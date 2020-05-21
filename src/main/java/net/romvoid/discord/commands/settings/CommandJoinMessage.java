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
package net.romvoid.discord.commands.settings;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.EmbedUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandJoinMessage.
 */
public class CommandJoinMessage extends CommandHandler {
    
    /**
     * Instantiates a new command join message.
     */
    public CommandJoinMessage() {
        super(new String[]{"joinmsg", "joinmessage"}, CommandCategory.SETTINGS,
                new PermsNeeded("command.joinmsg", false, false),
                "Set the server's join message!", "<Message(%user% for username, %guild% for guildname)>\ndisable/off", true);
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
        if (parsedCommandInvocation.getArgs().length == 0)
            return createHelpMessage();
        String content = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation() + " ", "");
        if (content.equalsIgnoreCase("disable") || content.equalsIgnoreCase("false") || content.equalsIgnoreCase("0") || content.equalsIgnoreCase("off")) {
            VoidBot.getMySQL().updateGuildValue(parsedCommandInvocation.getMessage().getGuild(), "joinmsg", "0");
            return new MessageBuilder().setEmbed(EmbedUtil.success("Disabled!", "Succesfully disabled joinmessages.").build()).build();
        }
        VoidBot.getMySQL().updateGuildValue(parsedCommandInvocation.getMessage().getGuild(), "joinmsg", content);
        return new MessageBuilder().setEmbed(EmbedUtil.success("Enabled!", "Successfully set message to `" + content + "`.").build()).build();
    }
}