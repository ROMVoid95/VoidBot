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

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.romvoid.discord.util.Info;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving ready events.
 * The class that is interested in processing a ready
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addReadyListener<code> method. When
 * the ready event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ReadyEvent
 */
public class ReadyListener extends ListenerAdapter {

    /**
     * On ready.
     *
     * @param event the event
     */
    @Override
    public void onReady(ReadyEvent event) {
        StringBuilder sb = new StringBuilder();
        event.getJDA().getGuilds()
                .forEach(guild -> sb.append("|  - \"" + guild.getName() + "\" - \n| \t ID:    "
                        + guild.getId() + " \n| \t Owner: " + guild.getOwner().getUser().getName()
                        + "#" + guild.getOwner().getUser().getDiscriminator() + " \n|\n"));

        System.out.println(String.format("\n\n"
                + "#--------------------------------------------------------------------------------\n"
                + "| %s - v.%s (JDA: v.%s)\n"
                + "#--------------------------------------------------------------------------------\n"
                + "| Running on %s guilds: \n" + "%s"
                + "#--------------------------------------------------------------------------------\n\n",
                 "VoidBot", Info.BOT_VERSION, "3.8.3_464",
                event.getJDA().getGuilds().size(), sb.toString()));

    }
}
