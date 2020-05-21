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

import static net.romvoid.discord.util.EmbedUtil.info;
import static net.romvoid.discord.util.EmbedUtil.message;

import java.util.stream.Collectors;

import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;

// TODO: Auto-generated Javadoc
/**
 * Handles the 'statistics' command which responds with some statistics about this bot.
 *
 * @author ROMVoid
 */
public class CommandStatistics extends CommandHandler {
    /**
     * Constructs the 'statistics' command handler.
     */
    public CommandStatistics() {
        super(new String[]{"statistics", "statistic", "stats"}, CommandCategory.GENERAL,
                new PermsNeeded("command.statistics", false, true),
                "Shows some statistics about this bot.", "");
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
        Message message = message(info(VoidBot.getJDA().getSelfUser().getName() + "'s statistics", null)
                .addField("Total servers", String.valueOf(VoidBot.getJDA().getGuilds().size()), true)
                .addField("Total users", String.valueOf(VoidBot.getJDA().getUsers().stream()
                        .filter(u -> !u.isBot()).collect(Collectors.toList()).size()), true));
        parsedCommandInvocation.getMessage().getTextChannel().sendMessage(message).queue();
        return null;
    }
}
