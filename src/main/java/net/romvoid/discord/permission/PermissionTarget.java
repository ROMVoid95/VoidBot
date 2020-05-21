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

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

// TODO: Auto-generated Javadoc
/**
 * Specifies a permission target.
 */
public class PermissionTarget {
    
    /**
     * The Enum Type.
     */
    public enum Type {
        
        /** The user. */
        USER('u', "user"),
        
        /** The role. */
        ROLE('r', "role"),
        
        /** The discord permission. */
        DISCORD_PERMISSION('d', "discord permission");

        /** The identifier. */
        private final char identifier;

        /** The name. */
        private final String name;

        /**
         * Instantiates a new type.
         *
         * @param identifier the identifier
         * @param name the name
         */
        Type(char identifier, String name) {
            this.identifier = identifier;
            this.name = name;
        }

        /**
         * Gets the identifier.
         *
         * @return the identifier
         */
        public char getIdentifier() {
            return identifier;
        }

        /**
         * Gets the name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the by identifier.
         *
         * @param identifier the identifier
         * @return the by identifier
         */
        public static Type getByIdentifier(char identifier) {
            for (Type type : values())
                if (type.identifier == identifier)
                    return type;
            return null;
        }

    }

    /** The guild. */
    private final Guild guild;
    
    /** The type. */
    private final Type type;
    
    /** The id. */
    private final long id;

    /**
     * Instantiates a new permission target.
     *
     * @param guild the guild
     * @param type the type
     * @param id the id
     */
    public PermissionTarget(Guild guild, Type type, long id) {
        this.guild = guild;
        this.type = type;
        this.id = id;
    }

    /**
     * Overloading constructor for creating a user permission target.
     *
     * @param member the target user.
     */
    public PermissionTarget(Member member) {
        this(member.getGuild(), Type.USER, member.getUser().getIdLong());
    }

    /**
     * Overloading constructor for creating a role permission target.
     *
     * @param role the target role.
     */
    public PermissionTarget(Role role) {
        this(role.getGuild(), Type.ROLE, role.getIdLong());
    }

    /**
     * Overloading constructor for creating a discord-permission permission target.
     *
     * @param guild      the guild this permission should apply for.
     * @param permission the target discord-permission.
     */
    public PermissionTarget(Guild guild, Permission permission) {
        this(guild, Type.DISCORD_PERMISSION, permission.getOffset());
    }

    /**
     * Gets the guild.
     *
     * @return the guild
     */
    public Guild getGuild() {
        return guild;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public User getUser() {
        return type == Type.USER ? guild.getJDA().getUserById(id) : null;
    }

    /**
     * Gets the role.
     *
     * @return the role
     */
    public Role getRole() {
        return type == Type.ROLE ? guild.getRoleById(id) : null;
    }

    /**
     * Gets the permission.
     *
     * @return the permission
     */
    public Permission getPermission() {
        return type == Type.DISCORD_PERMISSION ? Permission.getFromOffset((int) id) : null;
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof PermissionTarget // object type
                && guild.equals(((PermissionTarget) obj).guild) // guild
                && type == ((PermissionTarget) obj).type // target type
                && id == ((PermissionTarget) obj).id; // id
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return type == Type.USER ? getUser().getName() // user name
                : (type == Type.ROLE ? getRole().getName() // or role name
                : getPermission().getName()); // or permission name
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return (exists() ? getName() : id)
                + " (" + type.getName() + ")"; // and type
    }

    /**
     * Checks whether the target exists in the given context (guild).
     *
     * @return {@code true} if the actual target exists and {@code false} otherwise.
     */
    public boolean exists() {
        switch (type) {
            case USER:
                User user = getUser();
                return user != null && guild.isMember(user);
            case ROLE:
                Role role = getRole();
                return role != null && role.getGuild().equals(guild);
            case DISCORD_PERMISSION:
                return Permission.getFromOffset((int) id) != Permission.UNKNOWN;
        }
        return false;
    }
}
