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

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.romvoid.discord.VoidBot;

import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
/**
 * Generates common embed messages.
 */
public class EmbedUtil {
    /**
     * The default deletion interval.
     */
    private static final long defaultDeleteInterval = 60;
    /**
     * The default deletion interval TimeUnit.
     */
    private static final TimeUnit defaultDeleteIntervalTimeUnit = TimeUnit.SECONDS;

    /**
     * Creates an embedded message indicating a successful feature execution.
     *
     * @param title       the title of the embedded.
     * @param description a more detailed description.
     * @return the generated EmbedBuilder.
     */
    public static EmbedBuilder success(String title, String description) {
        return embed(":white_check_mark: " + title, description).setColor(Colors.COLOR_PRIMARY);
    }

    /**
     * Premium.
     *
     * @param title the title
     * @param description the description
     * @return the embed builder
     */
    public static EmbedBuilder premium(String title, String description) {
        return embed(":star: " + title, description).setColor(Colors.COLOR_PREMIUM);
    }

    /**
     * No premium.
     *
     * @return the embed builder
     */
    public static EmbedBuilder noPremium() {
        return premium("No Premium!", "Buy premium with rubys! - `>>premium buy`");
    }

    /**
     * Creates an embedded message with additional information.
     *
     * @param title       the title of the embedded.
     * @param description a more detailed description.
     * @return the generated EmbedBuilder.
     */
    public static EmbedBuilder info(String title, String description) {
        return embed(":information_source: " + title, description).setColor(Colors.COLOR_SECONDARY);
    }

    /**
     * Creates an embedded unknown error message.
     *
     * @return the generated EmbedBuilder.
     */
    public static EmbedBuilder error() {
        return error("Unknown error", "An unknown error occurred.");
    }

    /**
     * Creates an embedded error message.
     *
     * @param title       the title of the embedded.
     * @param description the error description.
     * @return the generated EmbedBuilder.
     */
    public static EmbedBuilder error(String title, String description) {
        return embed(":warning: " + title, description).setColor(Colors.COLOR_ERROR);
    }

    /**
     * Creates an embedded no permissions message.
     *
     * @return the generated EmbedBuilder.
     */
    public static EmbedBuilder no_permissions() {
        return embed(":warning: " + "No permissions", "You don't have the permission to execute this command!").setColor(Colors.COLOR_NO_PERMISSION);
    }


    /**
     * Creates an embedded message.
     *
     * @param title       the title of the embedded.
     * @param description the description (main message).
     * @return the generated EmbedBuilder.
     */
    public static EmbedBuilder embed(String title, String description) {
        return new EmbedBuilder().setTitle(title).setDescription(description);
    }

    /**
     * Attaches a timestamp to an embedded message.
     *
     * @param embedBuilder the EmbedBuilder to attach a timestamp to.
     * @return the given EmbedBuilder with a timestamp.
     */
    public static EmbedBuilder withTimestamp(EmbedBuilder embedBuilder) {
        return embedBuilder.setFooter(VoidBot.getNewTimestamp(), null);
    }

    /**
     * Converts an EmbedBuilder into a message.
     *
     * @param embedBuilder the EmbedBuilder that contains the embed data.
     * @return the compiled Message.
     */
    public static Message message(EmbedBuilder embedBuilder) {
        return new MessageBuilder().setEmbed(embedBuilder.build()).build();
    }

    /**
     * Always sends a message but deletes it only if the messageChannel is on a server.
     *
     * @param messageChannel the channel to send the message in.
     * @param message        the message to send.
     */
    public static void sendAndDeleteOnGuilds(MessageChannel messageChannel, Message message) {
        sendAndDeleteOnGuilds(messageChannel, message, defaultDeleteInterval, defaultDeleteIntervalTimeUnit);
    }

    /**
     * Always sends a message but deletes it only if the messageChannel is on a server.
     *
     * @param messageChannel         the channel to send the message in.
     * @param message                the message to send.
     * @param deleteInterval         the interval after that the message should be deleted if the channel is on a server.
     * @param deleteIntervalTimeUnit the TimeUnit of deleteInterval.
     */
    public static void sendAndDeleteOnGuilds(MessageChannel messageChannel, Message message, long deleteInterval, TimeUnit deleteIntervalTimeUnit) {
        sendAndDelete(messageChannel, message,
                // only delete message on servers.
                messageChannel.getType() == ChannelType.TEXT ? deleteInterval : -1,
                deleteIntervalTimeUnit);
    }

    /**
     * Sends a message and deletes it after an interval (if interval is >= 0).
     *
     * @param messageChannel         the channel to send the message in.
     * @param message                the message to send.
     * @param deleteInterval         the interval after that the message should be deleted. Message won't be deleted if
     *                               interval is < 0.
     * @param deleteIntervalTimeUnit the TimeUnit of deleteInterval.
     */
    public static void sendAndDelete(MessageChannel messageChannel, Message message, long deleteInterval, TimeUnit deleteIntervalTimeUnit) {
        messageChannel.sendMessage(message).queue(deleteInterval < 0
                // do nothing if interval < 0
                ? null
                // else: delete after interval
                : sentMessage -> sentMessage.delete().queueAfter(deleteInterval, deleteIntervalTimeUnit, null, ignored -> {
            //TODO evaluate exception, i.e. bot does not have permission
        }));
    }
}