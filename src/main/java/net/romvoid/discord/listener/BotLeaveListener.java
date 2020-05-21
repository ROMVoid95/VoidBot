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

import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.util.Logger;


// TODO: Auto-generated Javadoc
/**
 * Listen if the TheVoidBot leaves a guild.
 *
 * @see BotLeaveEvent
 */
public class BotLeaveListener extends ListenerAdapter {

    /**
     * Removes the guild from the database.
     *
     * @param e the e
     */
    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        try {
            if (VoidBot.getMySQL().ifGuildExits(e.getGuild())) {
                VoidBot.getMySQL().deleteGuild(e.getGuild());
            }
        } catch (Exception ex) {
            Logger.error(ex);
        }
    }
}


