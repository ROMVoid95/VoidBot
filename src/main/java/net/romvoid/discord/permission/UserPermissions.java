/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2020
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
package net.romvoid.discord.permission;

import net.dv8tion.jda.core.entities.*;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.util.Info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Member/User-specific object used to query all permission-relevant variables.
 *
 * @author ROMVoid
 */
public class UserPermissions {
    
    /** The user id. */
    private final long userId;
    
    /** The guild id. */
    private final long guildId;

    /**
     * Construct a UserPermissions object.
     *
     * @param userId  the long id of the user whose permissions are reviewed.
     * @param guildId the id of the guild on which the permissions should apply.
     */
    public UserPermissions(long userId, long guildId) {
        this.userId = userId;
        this.guildId = guildId;
    }

    /**
     * Construct a UserPermissions object without a guild.
     *
     * @param userId the long id of the user whose permissions are reviewed.
     */
    public UserPermissions(long userId) {
        this(userId, -1);
    }

    /**
     * Convenience constructor.
     *
     * @param user  the user whose permissions are reviewed.
     * @param guild the guild on which the permissions should apply. If this is null, no guild will be specified.
     */
    public UserPermissions(User user, Guild guild) {
        this(user.getIdLong(), guild == null ? -1 : guild.getIdLong());
    }

    /**
     * Convenience constructor without a guild.
     *
     * @param user the user whose permissions are reviewed.
     */
    public UserPermissions(User user) {
        this(user, null);
    }

    /**
     * Convenience method returning the corresponding user, if available.
     *
     * @return the {@link User} corresponding to userId or null if this is not available.
     */
    public User getDiscordUser() {
        return VoidBot.getJDA() == null ? null : VoidBot.getJDA().getUserById(userId);
    }

    /**
     * Convenience method returning the corresponding guild, if available.
     *
     * @return the {@link Guild} corresponding to guildId or null if this is not available.
     */
    public Guild getDiscordGuild() {
        return VoidBot.getJDA() == null ? null : VoidBot.getJDA().getGuildById(guildId);
    }

    /**
     * Convenience method returning the corresponding member, if available.
     *
     * @return the {@link net.dv8tion.jda.core.entities.Member} corresponding to userId and guildId or null if this is
     * not available.
     */
    public Member getDiscordMember() {
        User discordUser = getDiscordUser();
        Guild discordGuild = getDiscordGuild();
        return discordUser == null || discordGuild == null ? null : discordGuild.getMember(discordUser);
    }

    /**
     * Gets the user id.
     *
     * @return the user id this object works with.
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Gets the guild id.
     *
     * @return the guild id this object works with. -1 if no guild was specified.
     */
    public long getGuildId() {
        return guildId;
    }

    /**
     * Checks if is bot author.
     *
     * @return whether the user is a bot author. Independent from guilds.
     */
    public boolean isBotAuthor() {
        return Arrays.asList(Info.BOT_AUTHOR_IDS).contains(userId);
    }

    /**
     * Checks if is server owner.
     *
     * @return whether the user is the owner of the specified guild. Also false if no guild was specified.
     */
    public boolean isServerOwner() {
        Member member = getDiscordMember();
        return member != null && member.isOwner();
    }

    /**
     * Gets the effective permissions.
     *
     * @return all effective permissions.
     * @see #hasPermissionNode(String) for permission checks.
     */
    public List<Permission> getEffectivePermissions() {
        PermissionManager manager = VoidBot.sGetPermissionManager();
        List<Permission> effectivePermissions = new ArrayList<>();
        for (PermissionTarget target : getPermissionTargets(null))
            for (Permission targetPermission : manager.getPermissions(target))
                // only add to effective if there is no equal permission string yet.
                if (effectivePermissions.stream()
                        .noneMatch(effectivePermission -> effectivePermission.equalsIgnoreNegation(targetPermission)))
                    effectivePermissions.add(targetPermission);
        return effectivePermissions;
    }

    /**
     * Checks for permission node.
     *
     * @param requiredPermissionNode the required permission node.
     * @return whether memberPermissionNodes contains requiredPermissionNode.
     */
    public boolean hasPermissionNode(String requiredPermissionNode) {
        return hasPermission(null, requiredPermissionNode);
    }

    /**
     * Checks for permission.
     *
     * @param context                used to check discord permissions in a channel.
     * @param requiredPermissionNode the required permission node.
     * @return whether memberPermissionNodes contains requiredPermissionNode.
     */
    public boolean hasPermission(Channel context, String requiredPermissionNode) {
        Permission permission = getEffectivePermissionEntry(context, requiredPermissionNode);
        // negated -> false (does not have perm), not negated -> true (has perm)
        return permission != null && !permission.isNegated();
    }

    /**
     * Iterates through all {@link PermissionTarget PermissionTargets} and returns the effective {@link Permission} entry.
     *
     * @param context                used to check discord permissions in a channel.
     * @param requiredPermissionNode the permission to query.
     * @return the effect
     */
    public Permission getEffectivePermissionEntry(Channel context, String requiredPermissionNode) {
        PermissionManager permissionManager = VoidBot.sGetPermissionManager();
        Permission effectivePermissionEntry = null;
        // check permissions
        List<PermissionTarget> permissionTargets = getPermissionTargets(context);
        for (int i = 0; effectivePermissionEntry == null && i < permissionTargets.size(); i++) {
            Permission permission = permissionManager.getPermission(permissionTargets.get(i), requiredPermissionNode);
            if (permission != null)
                effectivePermissionEntry = permission;
        }
        return effectivePermissionEntry;
    }

    /**
     * Gets the permission targets.
     *
     * @param context used to check discord permissions in a channel.
     * @return all {@link PermissionTarget PermissionTargets} that apply on this user in the order they should be
     * checked.
     */
    public List<PermissionTarget> getPermissionTargets(Channel context) {
        List<PermissionTarget> targets = new ArrayList<>();
        if (isMember()) {
            Member member = getDiscordMember();

            // add user target
            targets.add(new PermissionTarget(member));

            // add discord permission targets
            for (net.dv8tion.jda.core.Permission permission : context == null ? member.getPermissions() : member.getPermissions(context))
                targets.add(new PermissionTarget(member.getGuild(), permission));

            // add role targets
            List<Role> roleList = member.getRoles(); // member roles sorted from highest to lowest
            roleList.forEach(role -> targets.add(new PermissionTarget(role))); // add all roles
        }
        return targets;
    }

    /**
     * Checks if is member.
     *
     * @return whether this permission object can access member permission settings.
     */
    public boolean isMember() {
        return guildId != -1;
    }
}