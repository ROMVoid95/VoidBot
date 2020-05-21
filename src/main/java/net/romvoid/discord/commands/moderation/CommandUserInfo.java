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
package net.romvoid.discord.commands.moderation;


import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.Colors;

import java.time.OffsetDateTime;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandUserInfo.
 */
public class CommandUserInfo extends CommandHandler {

    /**
     * Instantiates a new command user info.
     */
    public CommandUserInfo() {
        super(new String[]{"userinfo", "whois"}, CommandCategory.TOOLS, new PermsNeeded("command.userinfo", false, true), "Returns some information about the specified user", "[@User]");
    }

    /**
     * Execute.
     *
     * @param parsedCommandInvocation the parsed command invocation
     * @param userPermissions the user permissions
     * @return the message
     */
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        User info;
        String[] args = parsedCommandInvocation.getArgs();
        if (args.length > 0) {
            if (message.getMentionedUsers().size() > 0)
                info = message.getMentionedUsers().get(0);
            else {
                return createHelpMessage();
            }
        } else {
            info = message.getAuthor();
        }

        Member user = message.getGuild().getMember(info);
        StringBuilder rawRoles = new StringBuilder();
        user.getRoles().forEach(r -> rawRoles.append(r.getName()).append(", "));
        StringBuilder roles = new StringBuilder(rawRoles.toString());
        if (!user.getRoles().isEmpty())
            roles.replace(rawRoles.lastIndexOf(","), roles.lastIndexOf(",") + 1, "");
        EmbedBuilder userinfo = new EmbedBuilder();
        userinfo.setColor(Colors.COLOR_PRIMARY);
        userinfo.setTitle("User information of " + user.getUser().getName());
        userinfo.setThumbnail(info.getAvatarUrl());
        userinfo.addField("Nickname", user.getEffectiveName(), false);
        userinfo.addField("User id", info.getId(), false);
        userinfo.addField("Status", user.getOnlineStatus().toString().replace("_", ""), false);
        if (user.getGame() != null)
            userinfo.addField("Game", user.getGame().getName(), false);
        userinfo.addField("Guild join date", formatDate(user.getJoinDate()), false);
        userinfo.addField("Roles", "`" + roles.toString() + "`", false);
        userinfo.addField("Discord join date", formatDate(info.getCreationTime()), false);
        userinfo.addField("Avatar url", (info.getAvatarUrl() != null) ? info.getAvatarUrl() : "https://rubicon.fun", true);
        return new MessageBuilder().setEmbed(userinfo.build()).build();
    }

    /**
     * Format date.
     *
     * @param date the date
     * @return the string
     */
    public String formatDate(OffsetDateTime date) {
        return date.getMonthValue() + "/" + date.getDayOfMonth() + "/" + date.getYear();
    }
}
