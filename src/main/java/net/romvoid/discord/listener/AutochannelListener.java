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

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.romvoid.discord.VoidBot;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving autochannel events.
 * The class that is interested in processing a autochannel
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addAutochannelListener<code> method. When
 * the autochannel event occurs, that object's appropriate
 * method is invoked.
 *
 * @see AutochannelEvent
 */
public class AutochannelListener extends ListenerAdapter {

    /**
     * On voice channel delete.
     *
     * @param e the e
     */
    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent e) {
        String oldEntry = VoidBot.getMySQL().getGuildValue(e.getGuild(), "autochannels");
        if (oldEntry != null)
            if (oldEntry.contains(e.getChannel().getId())) {
                String newEntry = oldEntry.replace(e.getChannel().getId() + ",", "");
                VoidBot.getMySQL().updateGuildValue(e.getGuild(), "autochannels", newEntry);
            }
    }

    /**
     * On guild voice join.
     *
     * @param e the e
     */
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
        VoiceChannel ch = e.getChannelJoined();
        if (isAutoChannel(e.getGuild(), ch)) {
            VoiceChannel newChannel = (VoiceChannel) e.getGuild().getController().createCopyOfChannel(ch).setName(ch.getName() + " [AC]").complete();
            e.getGuild().getController().moveVoiceMember(e.getMember(), newChannel).queue();
        }
    }

    /**
     * On guild voice move.
     *
     * @param e the e
     */
    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent e) {
        VoiceChannel ch = e.getChannelJoined();
        if (isAutoChannel(e.getGuild(), ch)) {
            VoiceChannel newChannel = (VoiceChannel) e.getGuild().getController().createCopyOfChannel(ch).setName(ch.getName() + " [AC]").complete();
            e.getGuild().getController().moveVoiceMember(e.getMember(), newChannel).queue();
        }
        if (e.getChannelLeft().getMembers().size() == 0) {
            if (e.getChannelLeft().getName().contains("[AC]")) {
                e.getChannelLeft().delete().queue();
            }
        }
    }

    /**
     * On guild voice leave.
     *
     * @param e the e
     */
    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
        if (e.getChannelLeft().getMembers().size() == 0) {
            if (e.getChannelLeft().getName().contains("[AC]")) {
                e.getChannelLeft().delete().queue();
            }
        }
    }

    /**
     * Checks if is auto channel.
     *
     * @param g the g
     * @param ch the ch
     * @return true, if is auto channel
     */
    private boolean isAutoChannel(Guild g, Channel ch) {
        String oldEntry = VoidBot.getMySQL().getGuildValue(g, "autochannels");
        if (oldEntry != null)
            if (oldEntry.contains(ch.getId())) {
                return true;
            }
        return false;
    }
}
