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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.util.Colors;

import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving selfMention events.
 * The class that is interested in processing a selfMention
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSelfMentionListener<code> method. When
 * the selfMention event occurs, that object's appropriate
 * method is invoked.
 *
 * @see SelfMentionEvent
 */
public class SelfMentionListener extends ListenerAdapter {

    /** The emojis. */
    private final String[] EMOJIS = {"\uD83C\uDDF7", "\uD83C\uDDFA", "\uD83C\uDDE7", "\uD83C\uDDEE", "\uD83C\uDDE8", "\uD83C\uDDF4", "\uD83C\uDDF3"};

    /**
     * On guild message received.
     *
     * @param e the e
     */
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getMessage().getMentionedUsers().contains(e.getJDA().getSelfUser())) {
            if (!e.getMessage().getContentDisplay().replaceFirst("@", "").equals(e.getGuild().getSelfMember().getEffectiveName()))
                return;
            Message message = e.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(Colors.COLOR_SECONDARY)
                            .setAuthor(e.getJDA().getSelfUser().getName(), null, e.getJDA().getSelfUser().getAvatarUrl())
                            .setDescription("Hey, I am TheVoidBot")
                            .addField("**Prefix**", "`" + VoidBot.getMySQL().getGuildValue(e.getGuild(), "prefix") + "`", false)
                            .addField("**Documentation**", "[thevoidbot.com](https://thevoidbot.com)", false)
                            .build()
            ).complete();
            for (String emoji : EMOJIS) {
                message.addReaction(emoji).queue();
            }
            if (!e.getGuild().getSelfMember().getPermissions(e.getChannel()).contains(Permission.MESSAGE_MANAGE))
                return; // Do not try to delete message when bot is not allowed to
            message.delete().queueAfter(5, TimeUnit.MINUTES);
            e.getMessage().delete().queue();
        }
    }

}
