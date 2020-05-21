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
package net.romvoid.discord.commands.general;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.EmbedUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandSay.
 */
public class CommandSay extends CommandHandler {

    /**
     * Instantiates a new command say.
     */
    public CommandSay() {
        super(new String[]{"say", "s"}, CommandCategory.GENERAL, new PermsNeeded("command.say", false, false), "Send a Message as the Bot!", "<Channel> <Message>");
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
        if (parsedCommandInvocation.getArgs().length < 2) {
            return createHelpMessage();
        }

        if (parsedCommandInvocation.getMessage().getMentionedChannels().size() == 0) {
            return createHelpMessage();
        }
        TextChannel textChannel = parsedCommandInvocation.getMessage().getMentionedChannels().get(0);
        if (!parsedCommandInvocation.getSelfMember().hasPermission(textChannel, Permission.MESSAGE_READ)) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "I have no permissions to write in this channel."));
        }
        String text = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation() + " #" + textChannel.getName(), "");
        if (!parsedCommandInvocation.getArgs()[0].contains("#")) {
            return EmbedUtil.message(EmbedUtil.error("No channel!", "Your first parameter must be a #channel."));
        }

        if (parsedCommandInvocation.getMessage().getMentionedUsers().size() >= 1) {
            for (User user : parsedCommandInvocation.getMessage().getMentionedUsers()) {
                Member member = parsedCommandInvocation.getGuild().getMember(user);
                text = text.replace("@" + member.getEffectiveName(), member.getAsMention());
            }
        }
        if (parsedCommandInvocation.getMessage().getMentionedRoles().size() >= 1) {
            for (Role role : parsedCommandInvocation.getMessage().getMentionedRoles()) {
                text = text.replace("@" + role.getName(), role.getAsMention());
            }
        }
        if (parsedCommandInvocation.getMessage().getMentionedChannels().size() >= 2) {
            for (int i = 1; i < parsedCommandInvocation.getMessage().getMentionedChannels().size(); i++) {
                TextChannel channel = parsedCommandInvocation.getMessage().getMentionedChannels().get(i);
                text = text.replace("#" + channel.getName(), channel.getAsMention());
            }
        }
        textChannel.sendMessage(text).queue();
        return new MessageBuilder().setEmbed(EmbedUtil.success("Successful", "Successfully sent message in " + textChannel.getAsMention()).build()).build();
    }
}
