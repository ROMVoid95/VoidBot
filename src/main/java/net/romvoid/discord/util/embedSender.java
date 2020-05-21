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
package net.romvoid.discord.util;


import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

// TODO: Auto-generated Javadoc
/**
 * The Class embedSender.
 */
public class embedSender {
    
    /**
     * Send embed.
     *
     * @param content the content
     * @param channel the channel
     * @param color the color
     */
    public static void sendEmbed(String content, MessageChannel channel, Color color){
        EmbedBuilder embed = new EmbedBuilder().setDescription(content).setColor(color);
        Message mymsg = channel.sendMessage(embed.build()).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mymsg.delete().queue();
            }
        }, 60000);
    }
    
    /**
     * Send embed with img.
     *
     * @param content the content
     * @param channel the channel
     * @param color the color
     * @param url the url
     */
    public static void sendEmbedWithImg(String content, MessageChannel channel, Color color, String url){
        EmbedBuilder embed = new EmbedBuilder().setDescription(content).setColor(color).setThumbnail(url);
        Message mymsg = channel.sendMessage(embed.build()).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mymsg.delete().queue();
            }
        }, 5000);
    }
    
    /**
     * Send permanent embed.
     *
     * @param content the content
     * @param channel the channel
     * @param color the color
     */
    public static void sendPermanentEmbed(String content, MessageChannel channel, Color color){
        EmbedBuilder embed = new EmbedBuilder().setDescription(content).setColor(color);
        channel.sendMessage(embed.build()).complete();

    }
}
 