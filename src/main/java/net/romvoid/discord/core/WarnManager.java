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
package net.romvoid.discord.core;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.romvoid.discord.sql.WarnSQL;
import net.romvoid.discord.util.Colors;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class WarnManager.
 *
 * @author ROMVoid
 */
public class WarnManager {

    /**
     * Adds the warn.
     *
     * @param warnedUser the warned user
     * @param guild the guild
     * @param executor the executor
     * @param reason the reason
     */
    public static void addWarn(User warnedUser, Guild guild, User executor, String reason) {
        Warn warn = new Warn(0, warnedUser, guild, executor, reason, new Date().getTime());
        new WarnSQL().addWarn(warn);
    }

    /**
     * Removes the warn.
     *
     * @param user the user
     * @param guild the guild
     * @param index the index
     */
    public static void removeWarn(User user, Guild guild, int index) {
        new WarnSQL().deleteWarn(user, guild, index);
    }

    /**
     * List warns.
     *
     * @param user the user
     * @param guild the guild
     * @return the embed builder
     */
    public static EmbedBuilder listWarns(User user, Guild guild) {
        String[] emotes = {
                ":one:",
                ":two:",
                ":three:",
                ":four:",
                ":five:",
                ":six:",
                ":seven:",
                ":eight:",
                ":nine:",
                ":keycap_ten:"
        };
        List<Warn> warnList = new WarnSQL().getWarns(user, guild);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(user.getName() + "'s warns", null, user.getAvatarUrl());
        embedBuilder.setColor(Colors.COLOR_PRIMARY);
        if (warnList.size() == 0) {
            embedBuilder.setDescription("User has no warns.");
        } else {
            int warnCount = 0;
            for (Warn warn : warnList) {
                embedBuilder.addField(emotes[warnCount] + " " + warn.getReason(), "Executor: " + warn.getExecutor().getName() + "\n" +
                        "Date: " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(warn.getDate()), false);
                warnCount++;
                if (warnCount == 10)
                    break;
            }
        }
        return embedBuilder;
    }

    /**
     * Checks if is warned.
     *
     * @param user the user
     * @param guild the guild
     * @return true, if is warned
     */
    public static boolean isWarned(User user, Guild guild) {
        List<Warn> warnList = new WarnSQL().getWarns(user, guild);
        if (warnList.size() == 0)
            return false;
        return true;
    }
}