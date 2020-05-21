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
package net.romvoid.discord.listener;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.sql.ServerLogSQL;
import net.romvoid.discord.util.Logger;

// TODO: Auto-generated Javadoc
/**
 * If the TheVoidBot joins a new guild.
 *
 * @see BotJoinEvent
 */
public class BotJoinListener extends ListenerAdapter {

    /**
     * Creates the new guild in the database.
     *
     * @param event the event
     */
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        try {
            Guild g = event.getGuild();
            if (!VoidBot.getMySQL().ifGuildExits(event.getGuild())) {
                VoidBot.getMySQL().createGuildServer(g);
                new ServerLogSQL(event.getGuild());
            }
        } catch (Exception ex) {
            Logger.error(ex);
        }
    }
}