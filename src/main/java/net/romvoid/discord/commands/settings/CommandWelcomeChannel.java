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

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandWelcomeChannel.
 */
public class CommandWelcomeChannel extends CommandHandler {

    /**
     * Instantiates a new command welcome channel.
     */
    public CommandWelcomeChannel() {
        super(new String[]{"welcomechannel", "welchannel", "joinchannel"}, CommandCategory.SETTINGS,
                new PermsNeeded("command.channel", false, false),
                "Set the Server Welcome Channel!", "<#channel>", true);
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
        //Check if Channel got Mentioned
        if (parsedCommandInvocation.getMessage().getMentionedChannels().size() <= 0)
            return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(getParameterUsage()).build()).build();
        //Get the Mentioned Channel
        String ch = parsedCommandInvocation.getMessage().getMentionedChannels().get(0).getId();
        //Update MySql
        VoidBot.getMySQL().updateGuildValue(parsedCommandInvocation.getMessage().getGuild(), "channel", ch);
        return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(":white_check_mark: Successfully set the Event-Channel!").build()).build();
    }
}
