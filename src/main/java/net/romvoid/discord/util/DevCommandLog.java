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
package net.romvoid.discord.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandManager;

// TODO: Auto-generated Javadoc
/**
 * The Class DevCommandLog.
 *
 * @author ROMVoid
 */
public class DevCommandLog {

    /** The Constant channelId. */
    private static final long channelId = 559728923781496844L; //Dev

    /**
     * Log.
     *
     * @param invocation the invocation
     */
    public static void log(CommandManager.ParsedCommandInvocation invocation) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(
                "User: " + invocation.getAuthor().getName() + "(" + invocation.getAuthor().getId() + ")\n" +
                        "Guild: " + invocation.getGuild().getName() + "(" + invocation.getGuild().getId() + ")\n" +
                        "-------------------------\n" +
                        "Command: " + invocation.getCommandInvocation() + "\n" +
                        "Bot's ping: " + VoidBot.getJDA().getPing() + "ms");
        try {
            VoidBot.getJDA().getTextChannelById(channelId).sendMessage(builder.build()).queue();
        } catch (Exception ignore) {

        }
    }
}
