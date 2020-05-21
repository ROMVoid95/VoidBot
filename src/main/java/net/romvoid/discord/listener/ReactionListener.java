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

import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.commands.admin.CommandAutochannel;
import net.romvoid.discord.commands.admin.CommandVerification;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving reaction events.
 * The class that is interested in processing a reaction
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addReactionListener<code> method. When
 * the reaction event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ReactionEvent
 */
public class ReactionListener extends ListenerAdapter {

    /**
     * On message reaction add.
     *
     * @param event the event
     */
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().equals(VoidBot.getJDA().getSelfUser()))
            return;
        CommandAutochannel.handleReaction(event);
        CommandVerification.handleReaction(event);
    }

    /**
     * On message reaction remove.
     *
     * @param event the event
     */
    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {

    }
}
