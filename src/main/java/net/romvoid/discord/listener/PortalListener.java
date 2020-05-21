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

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessage;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;
import net.romvoid.discord.VoidBot;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving portal events.
 * The class that is interested in processing a portal
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPortalListener<code> method. When
 * the portal event occurs, that object's appropriate
 * method is invoked.
 *
 * @see PortalEvent
 */
public class PortalListener extends ListenerAdapter {

    /**
     * On guild message received.
     *
     * @param e the e
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (!e.getAuthor().isBot()) {
            if (e.getChannel().getId().equals(VoidBot.getMySQL().getPortalValue(e.getGuild(), "channelid"))) {
                String status = VoidBot.getMySQL().getGuildValue(e.getGuild(), "portal");
                if (status.contains("open")) {
                    TextChannel otherChannel = e.getJDA().getTextChannelById(VoidBot.getMySQL().getPortalValue(e.getJDA().getGuildById(VoidBot.getMySQL().getPortalValue(e.getGuild(), "partnerid")), "channelid"));
                    try {
                        Webhook webhook = null;

                        for (Webhook hook : otherChannel.getWebhooks().complete()) {
                            if (hook.getName().equals("thevoid-portal-hook")) {
                                webhook = hook;
                                break;
                            }
                        }
                        if (webhook == null) {
                            webhook = otherChannel.createWebhook("rubicon-portal-hook").complete();
                        }
                        WebhookClientBuilder clientBuilder = webhook.newClient();
                        WebhookClient client = clientBuilder.build();

                        WebhookMessageBuilder builder = new WebhookMessageBuilder();
                        builder.setContent(e.getMessage().getContentDisplay().replace("@here", "@ here").replace("@everyone", "@ everyone"));
                        builder.setAvatarUrl(e.getAuthor().getAvatarUrl());
                        builder.setUsername(e.getAuthor().getName());
                        WebhookMessage message = builder.build();
                        client.send(message);
                        client.close();

                    } catch (NullPointerException fuck) {
                        fuck.printStackTrace();
                    }
                }
            }
        }
    }
}
