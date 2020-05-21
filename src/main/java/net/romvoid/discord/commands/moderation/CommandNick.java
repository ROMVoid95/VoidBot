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

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.EmbedUtil;

import java.util.ArrayList;
import java.util.Arrays;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandNick.
 */
public class CommandNick extends CommandHandler {
    
    /**
     * Instantiates a new command nick.
     */
    public CommandNick() {
        super(new String[]{"nick", "nickname", "name"}, CommandCategory.TOOLS, new PermsNeeded("command.nick", false, false), "Easily nick yourself or others", "[@User] <nickname/reset>", false);
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
        String[] args = parsedCommandInvocation.getArgs();
        if (args.length < 1) {
            return createHelpMessage();
        }
        Member member;
        if (!message.getMentionedUsers().isEmpty())
            member = message.getGuild().getMember(message.getMentionedUsers().get(0));
        else
            member = message.getMember();
        String oldName = member.getEffectiveName();
        String nickname = String.join(" ", new ArrayList<>(Arrays.asList(args).subList(1, args.length))).replace(member.getEffectiveName(), "").replace("@", "");
        if (!message.getGuild().getSelfMember().canInteract(member) || !message.getGuild().getSelfMember().hasPermission(Permission.NICKNAME_MANAGE))
            return new MessageBuilder().setEmbed(EmbedUtil.error("No permission", "Sorry but TheVoid has no permission to change " + member.getAsMention() + "'s nickname").build()).build();
        if (nickname.length() > 32) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Nickname to long", "Your nickname can not be longer than 32 chars").build()).build();
        }
        if (nickname.equals("reset")) {
            message.getGuild().getController().setNickname(member, member.getUser().getName()).queue();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Reset Nickname", "Succesfully reset " + member.getAsMention() + "'s nickname").build()).build();
        } else {
            message.getGuild().getController().setNickname(member, nickname).queue();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Changed nickname", "Successfully changed nickname of " + oldName + " to `" + nickname + "`").build()).build();
        }
    }
}
