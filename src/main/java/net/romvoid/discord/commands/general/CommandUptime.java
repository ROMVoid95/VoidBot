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

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.Info;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandUptime.
 */
public class CommandUptime extends CommandHandler {

    /**
     * Instantiates a new command uptime.
     */
    public CommandUptime() {
        super(new String[]{"uptime"}, CommandCategory.GENERAL, new PermsNeeded("command.uptime", false, true), "Get the Uptime of the Bot", "", false);
    }

    /**
     * Gets the time.
     *
     * @param date the date
     * @param pattern the pattern
     * @return the time
     */
    private String getTime(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * Gets the time diff.
     *
     * @param date1 the date 1
     * @param date2 the date 2
     * @return the time diff
     */
    private String getTimeDiff(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return diffDays + " d, " + parseTimeNumbs(diffHours) + " h, " + parseTimeNumbs(diffMinutes) + " min, " + parseTimeNumbs(diffSeconds) + " sec";
    }

    /**
     * Parses the time numbs.
     *
     * @param time the time
     * @return the string
     */
    private String parseTimeNumbs(long time) {
        String timeString = time + "";
        if (timeString.length() < 2)
            timeString = "0" + time;
        return timeString;
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
        parsedCommandInvocation.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(new Color(255, 71, 0))
                        .setDescription(":alarm_clock:   **UPTIME**")
                        .addField("Last restart", getTime(Info.lastRestart, "dd.MM.yyyy - HH:mm:ss (z)"), false)
                        .addField("Online since", getTimeDiff(new Date(), Info.lastRestart), false)
                        .build()
        ).queue();
        return null;
    }
}
