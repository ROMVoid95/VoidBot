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

import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.romvoid.discord.commands.moderation.CommandRanks;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving role events.
 * The class that is interested in processing a role
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addRoleListener<code> method. When
 * the role event occurs, that object's appropriate
 * method is invoked.
 *
 * @see RoleEvent
 */
public class RoleListener extends ListenerAdapter {

    /**
     * On role update name.
     *
     * @param event the event
     */
    @Override
    public void onRoleUpdateName(RoleUpdateNameEvent event) {
        CommandRanks.handleRoleModification(event);
    }

    /**
     * On role delete.
     *
     * @param event the event
     */
    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        CommandRanks.handleRoleDeletion(event);
    }
}
