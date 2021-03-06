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
package net.romvoid.discord.commands.botowner;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.Configuration;
import net.romvoid.discord.util.EmbedUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandPlay.
 */
public class CommandPlay extends CommandHandler {

    /**
     * Instantiates a new command play.
     */
    public CommandPlay() {
        super(new String[]{"botplay", "status", "changestat"}, CommandCategory.BOT_OWNER, new PermsNeeded("command.botplay", true, false), "Change bot's playing status.", "<text>");
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
        Configuration configuration = VoidBot.getConfiguration();
        String configKey = "playingStatus";
        if (!configuration.has(configKey)) {
            configuration.set(configKey, "0");
        }
        if (parsedCommandInvocation.getArgs().length == 0) {
            configuration.set(configKey, "0");
            return null;
        }
        StringBuilder message = new StringBuilder();
        for (String s : parsedCommandInvocation.getArgs())
            message.append(s).append(" ");

        VoidBot.getConfiguration().set(configKey, message.toString());
        parsedCommandInvocation.getMessage().getJDA().getPresence().setGame(Game.playing(message.toString()));

        return new MessageBuilder().setEmbed(EmbedUtil.success("Status set!", "Successfully set the playing status!").build()).build();
    }
}
