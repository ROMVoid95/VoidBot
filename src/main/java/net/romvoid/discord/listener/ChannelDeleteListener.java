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

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.util.Colors;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving channelDelete events.
 * The class that is interested in processing a channelDelete
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addChannelDeleteListener<code> method. When
 * the channelDelete event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ChannelDeleteEvent
 */
public class ChannelDeleteListener extends ListenerAdapter {

    /**
     * On text channel delete.
     *
     * @param e the e
     */
    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        String portalStatus = VoidBot.getMySQL().getGuildValue(e.getGuild(), "portal");
        if (portalStatus.equalsIgnoreCase("closed") || portalStatus.equalsIgnoreCase("waiting"))
            return;
        if (portalStatus.equalsIgnoreCase("open")) {
            TextChannel channel = e.getJDA().getTextChannelById(VoidBot.getMySQL().getPortalValue(e.getGuild(), "channelid"));
            if (e.getChannel().getId() != channel.getId())
                return;

            Guild partnerGuild = e.getJDA().getGuildById(VoidBot.getMySQL().getPortalValue(e.getGuild(), "partnerid"));
            VoidBot.getMySQL().deletePortal(e.getGuild());
            VoidBot.getMySQL().deletePortal(partnerGuild);
            VoidBot.getMySQL().updateGuildValue(e.getGuild(), "portal", "closed");
            VoidBot.getMySQL().updateGuildValue(partnerGuild, "portal", "closed");

            sendPortalNotification(e.getGuild(), "Portal was closed.");
            sendPortalNotification(partnerGuild, "Portal was closed on the other side.");
        }
    }

    /**
     * Send portal notification.
     *
     * @param guild the guild
     * @param message the message
     */
    private void sendPortalNotification(Guild guild, String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Portal Notification", null, guild.getIconUrl());
        builder.setDescription(message);
        builder.setColor(Colors.COLOR_PRIMARY);
        guild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(builder.build()).queue());
    }
}