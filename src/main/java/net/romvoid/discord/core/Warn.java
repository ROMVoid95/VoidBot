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

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.romvoid.discord.VoidBot;

// TODO: Auto-generated Javadoc
/**
 * The Class Warn.
 */
public class Warn {

    /** The id. */
    private final int id;
    
    /** The warned user. */
    private final User warnedUser;
    
    /** The guild. */
    private final Guild guild;
    
    /** The executor. */
    private final User executor;
    
    /** The reason. */
    private final String reason;
    
    /** The date. */
    private final long date;

    /**
     * Instantiates a new warn.
     *
     * @param id the id
     * @param warnedUser the warned user
     * @param guild the guild
     * @param executor the executor
     * @param reason the reason
     * @param date the date
     */
    public Warn(int id, User warnedUser, Guild guild, User executor, String reason, long date) {
        this.id = id;
        this.warnedUser = warnedUser;
        this.guild = guild;
        this.executor = executor;
        this.reason = reason;
        this.date = date;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the warned user.
     *
     * @return the warned user
     */
    public User getWarnedUser() {
        return warnedUser;
    }

    /**
     * Gets the guild.
     *
     * @return the guild
     */
    public Guild getGuild() {
        return guild;
    }

    /**
     * Gets the executor.
     *
     * @return the executor
     */
    public User getExecutor() {
        return executor;
    }

    /**
     * Gets the reason.
     *
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public long getDate() {
        return date;
    }

    /**
     * Parses the warn.
     *
     * @param id the id
     * @param warnedUser the warned user
     * @param guildid the guildid
     * @param executor the executor
     * @param reason the reason
     * @param date the date
     * @return the warn
     */
    public static Warn parseWarn(String id, String warnedUser, String guildid, String executor, String reason, String date) {
        int pId = Integer.parseInt(id);
        User pWarnedUser = VoidBot.getJDA().getUserById(warnedUser);
        Guild guild = VoidBot.getJDA().getGuildById(guildid);
        User pExecutor = VoidBot.getJDA().getUserById(executor);
        long pDate = Long.valueOf(date);
        return new Warn(pId, pWarnedUser, guild, pExecutor, reason, pDate);
    }
}
