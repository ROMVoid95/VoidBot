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

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
/**
 * The Class SafeMessage.
 */
public class SafeMessage {

    /**
     * Send message.
     *
     * @param textChannel the text channel
     * @param message the message
     */
    public static void sendMessage(TextChannel textChannel, Message message) {
        if (hasPermissions(textChannel))
            textChannel.sendMessage(message).queue();
    }

    /**
     * Send message.
     *
     * @param textChannel the text channel
     * @param message the message
     * @param deleteTime the delete time
     */
    public static void sendMessage(TextChannel textChannel, Message message, int deleteTime) {
        if (hasPermissions(textChannel))
            textChannel.sendMessage(message).queue(msg -> msg.delete().queueAfter(deleteTime, TimeUnit.SECONDS));
    }

    /**
     * Send message blocking.
     *
     * @param textChannel the text channel
     * @param message the message
     * @return the message
     */
    public static Message sendMessageBlocking(TextChannel textChannel, Message message) {
        if (hasPermissions(textChannel))
            return textChannel.sendMessage(message).complete();
        return null;
    }

    /**
     * Send message.
     *
     * @param textChannel the text channel
     * @param message the message
     */
    public static void sendMessage(TextChannel textChannel, String message) {
        if (hasPermissions(textChannel))
            textChannel.sendMessage(message).queue();
    }

    /**
     * Send message.
     *
     * @param textChannel the text channel
     * @param message the message
     * @param deleteTime the delete time
     */
    public static void sendMessage(TextChannel textChannel, String message, int deleteTime) {
        if (hasPermissions(textChannel) && hasDeletePermission(textChannel))
            textChannel.sendMessage(message).queue(msg -> msg.delete().queueAfter(deleteTime, TimeUnit.SECONDS));
        else if (hasPermissions(textChannel))
            textChannel.sendMessage(message).queue();

    }

    /**
     * Send message.
     *
     * @param textChannel the text channel
     * @param build the build
     */
    public static void sendMessage(TextChannel textChannel, MessageEmbed build) {
        if (hasPermissions(textChannel))
            textChannel.sendMessage(build).queue();
    }

    /**
     * Send message.
     *
     * @param textChannel the text channel
     * @param build the build
     * @param deleteTime the delete time
     */
    public static void sendMessage(TextChannel textChannel, MessageEmbed build, int deleteTime) {
        if (hasPermissions(textChannel) && hasDeletePermission(textChannel))
            textChannel.sendMessage(build).queue(msg -> msg.delete().queueAfter(deleteTime, TimeUnit.SECONDS));
        else if (hasPermissions(textChannel))
            textChannel.sendMessage(build).queue();
    }

    /**
     * Send message blocking.
     *
     * @param textChannel the text channel
     * @param message the message
     * @return the message
     */
    public static Message sendMessageBlocking(TextChannel textChannel, String message) {
        if (hasPermissions(textChannel))
            return textChannel.sendMessage(message).complete();
        return null;
    }


    /**
     * Checks for permissions.
     *
     * @param channel the channel
     * @return true, if successful
     */
    private static boolean hasPermissions(TextChannel channel) {
        if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_READ) && channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE))
            return true;
        channel.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(EmbedUtil.error("Permission Error!", "The bot need the `MESSAGE_READ` and `MESSAGE_WRITE` permissions in the ``" + channel.getName() + "` channel to run without errors.").build()));
        return false;
    }

    /**
     * Checks for delete permission.
     *
     * @param channel the channel
     * @return true, if successful
     */
    private static boolean hasDeletePermission(TextChannel channel) {
        if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE))
            return true;
        channel.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(EmbedUtil.error("Permission Error!", "The bot need the `MESSAGE_READ` and `MESSAGE_MANAGE` permissions in the ``" + channel.getName() + "` channel to run without errors.").build()));
        return false;
    }


}
