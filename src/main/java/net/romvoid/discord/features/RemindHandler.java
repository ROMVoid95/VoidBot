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
package net.romvoid.discord.features;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.command.UnavailableCommandHandler;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.sql.MySQL;
import net.romvoid.discord.util.Colors;
import net.romvoid.discord.util.Logger;

import static net.romvoid.discord.util.EmbedUtil.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
/**
 * The Class RemindHandler.
 */
public class RemindHandler extends CommandHandler {

    /**
     * The Class Remind.
     */
    private class Remind {
        
        /** The text channel id. */
        private final long textChannelId;
        
        /** The message id. */
        private final long messageId;
        
        /** The remindmsg. */
        private final String remindmsg;
        
        /** The remindtime. */
        private final long remindtime;
        
        /** The author id. */
        private final long authorId;
        
        /** The resolve task. */
        private final TimerTask resolveTask = new TimerTask() {
            @Override
            public void run() {
                remind();
            }
        };
        
        /** The open. */
        private boolean open = true;


        /**
         * Constructs a Remind object from the given parameters, ensures its singularity and schedules the resolving.
         *
         * @param textChannelId the id of the text channel the remindme message was posted in.
         * @param messageId     the message's id.
         * @param remindmsg     the remind text that will be reminded to.
         * @param remindtime    the millis-date for the giveaway expiration.
         * @param authorId      the user id of the person who wants to be reminded away something.
         */

        private Remind(long textChannelId, long messageId, String remindmsg, long remindtime, long authorId) {
            this.textChannelId = textChannelId;
            this.messageId = messageId;
            this.remindmsg = remindmsg;
            this.remindtime = remindtime;
            this.authorId = authorId;

            // ensure there is not object of this giveaway yet to keep a single task
            if (reminders.contains(this))
                throw new IllegalStateException("Remind object already exists for the specified message");
            reminders.add(this);

            // schedule resolving
            VoidBot.getTimer().schedule(resolveTask, new Date(remindtime));
        }


        /**
         * Remind.
         */
        private void remind() {
            if (!open)
                throw new IllegalStateException("Remind is not open any more");

            PrivateChannel pc = getAuthor().openPrivateChannel().complete();
            pc.sendMessage(new EmbedBuilder()
                    .setColor(Colors.COLOR_SECONDARY)
                    .setAuthor(getAuthor().getName(), "http://rubicon.fun", getAuthor().getAvatarUrl())
                    .setTitle("Hey, " + getAuthor().getName() + "!")
                    .setDescription("You wanted to do following: " +
                            "\n```fix" +
                            "\n" + remindmsg + "```")
                    .build()
            ).queue();


            delete();
        }


        /**
         * Save.
         *
         * @return true, if successful
         */
        public boolean save() {
            // check whether the giveaway was resolved yet
            if (!open)
                throw new IllegalStateException("Remind is not open any more");

            try {
                PreparedStatement insertStatement = MySQL.getCon().prepareStatement("INSERT INTO `reminders-v1` (" +
                        "`textchannelid`, " +
                        "`messageid`," +
                        "`remindmsg`," +
                        "`remindtime`," +
                        "`authorid`) " +
                        "VALUES (?, ?, ?, ?, ?);");
                insertStatement.setLong(1, textChannelId);
                insertStatement.setLong(2, messageId);
                insertStatement.setString(3, remindmsg);
                insertStatement.setLong(4, remindtime);
                insertStatement.setLong(5, authorId);
                insertStatement.execute();
            } catch (SQLException e) {
                Logger.error("Could not save reminder '" + toString() + "'.");
                Logger.error(e);
                return false;
            }
            return true;
        }


        /**
         * Delete.
         */
        public void delete() {
            open = false;
            resolveTask.cancel();
            reminders.remove(this);
            try {
                PreparedStatement deleteStatement = MySQL.getCon()
                        .prepareStatement("DELETE FROM `reminders-v1` WHERE `textchannelid` = ? AND `messageid` = ?;");
                deleteStatement.setLong(1, textChannelId);
                deleteStatement.setLong(2, messageId);
                deleteStatement.execute();
            } catch (SQLException e) {
                Logger.error("Could not delete reminder '" + toString() + "'.");
                Logger.error(e);
            }
        }

        /**
         * Gets the text channel id.
         *
         * @return the text channel id
         */
        public long getTextChannelId() {
            return textChannelId;
        }

        /**
         * Gets the message id.
         *
         * @return the message id
         */
        public long getMessageId() {
            return messageId;
        }

        /**
         * Gets the author.
         *
         * @return the author
         */
        public User getAuthor() {
            return VoidBot.getJDA().getUserById(authorId);
        }


    }


    /** The reminders. */
    private Set<Remind> reminders = new HashSet<>();

    /**
     * Instantiates a new remind handler.
     */
    public RemindHandler() {
        super(new String[]{"remindme", "remind"}, CommandCategory.GENERAL, new PermsNeeded("command.remindme", false, true), "Get reminded of whatever you want", "create 9d Buy Cheese - you will get Reminded in 9 Days\n" +
                "create 5w Buy another Cheese! - you will get Reminded in 5 Weeks\n" +
                "create 1m Get this Message! - you will get Reminded in 5 Minutes\n" +
                "create 12mon Lol new Year! - you will get Reminded in 12 Months");
        try {
            MySQL.getCon().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `reminders-v1` (" +
                            "`textchannelid` BIGINT SIGNED, " +
                            "`messageid` BIGINT SIGNED, " +
                            "`remindmsg` VARCHAR(300), " +
                            "`remindtime` BIGINT SIGNED, " +
                            "`authorid` BIGINT SIGNED" +
                            ");")
                    .execute();
        } catch (SQLException e) {
            Logger.error("Could not create reminder table. Disabling giveaways.");
            Logger.error(e);
            VoidBot.getCommandManager().registerCommandHandler(new UnavailableCommandHandler(this));
            return;
        }

        try {
            for (TextChannel channel : VoidBot.getJDA().getTextChannels()) {
                PreparedStatement selectStatement = MySQL.getCon()
                        .prepareStatement("SELECT * FROM `reminders-v1` WHERE `textchannelid` = ?;");
                selectStatement.setLong(1, channel.getIdLong());
                ResultSet channelResult = selectStatement.executeQuery();
                while (channelResult.next())
                    new Remind(channelResult.getLong("textchannelid"),
                            channelResult.getLong("messageid"),
                            channelResult.getString("remindmsg"),
                            channelResult.getLong("remindtime"),
                            channelResult.getLong("authorid"));
            }
        } catch (SQLException e) {
            Logger.error("Could not load reminders, disabling them.");
            Logger.error(e);
            VoidBot.getCommandManager().registerCommandHandler(new UnavailableCommandHandler(this));
            return;
        }

        VoidBot.getCommandManager().registerCommandHandler(this);
    }

    /**
     * Gets the remind by id.
     *
     * @param textChannelId the text channel id
     * @param messageId the message id
     * @return the remind by id
     */
    @SuppressWarnings("unused")
    private Remind getRemindById(long textChannelId, long messageId) {
        for (Remind remind : reminders)
            if (remind.getTextChannelId() == textChannelId && remind.getMessageId() == messageId)
                return remind;
        return null;
    }

    /**
     * Execute.
     *
     * @param invocation the invocation
     * @param userPermissions the user permissions
     * @return the message
     */
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 1) {
            return createHelpMessage();
        } else {
            switch (invocation.getArgs()[0]) {
                case "create":
                    if (invocation.getArgs().length < 3)
                        return createHelpMessage(invocation);
                    TimeUnit timeunit = TimeUnit.MINUTES;
                    int runtime = 0;
                    double extra = 0;
                    try {
                        @SuppressWarnings("unused")
                        int time;
                        if (invocation.getArgs()[1].endsWith("m")) {
                            timeunit = TimeUnit.MINUTES;
                            runtime = Integer.parseInt(invocation.getArgs()[1].replace("m", ""));
                        } else if (invocation.getArgs()[1].endsWith("d")) {
                            timeunit = TimeUnit.DAYS;
                            runtime = Integer.parseInt(invocation.getArgs()[1].replace("d", ""));
                        } else if (invocation.getArgs()[1].endsWith("w")) {
                            extra = 604800000.002;
                            runtime = Integer.parseInt(invocation.getArgs()[1].replace("w", ""));
                        } else if (invocation.getArgs()[1].endsWith("mon")) {
                            extra = 2629745999.989;
                            runtime = Integer.parseInt(invocation.getArgs()[1].replace("mon", ""));
                        }

                        if (runtime < 0)
                            throw new IllegalArgumentException();
                    } catch (IllegalArgumentException e) {
                        return message(error("Invalid argument",
                                "The time must be an integer number greater than 0."));
                    }

                    StringBuilder prize = new StringBuilder(invocation.getArgs()[2]);
                    for (int i = 3; i < invocation.getArgs().length; i++)
                        prize.append(" ").append(invocation.getArgs()[i]);
                    if (extra == 0) {
                        Remind remind = createRemind(invocation.getTextChannel().getIdLong(),
                                prize.toString(),
                                (System.currentTimeMillis() + timeunit.toMillis(runtime)),
                                invocation.getAuthor().getIdLong());

                        return remind == null ? message(error()) : null;
                    } else {
                        Remind remind = createRemind(invocation.getTextChannel().getIdLong(),
                                prize.toString(),
                                (long) (System.currentTimeMillis() + runtime * extra),
                                invocation.getAuthor().getIdLong());
                        return remind == null ? message(error()) : null;
                    }
                default:
                    return createHelpMessage(invocation);
            }
        }
    }

    /**
     * Creates the remind.
     *
     * @param textChannelId the text channel id
     * @param prize the prize
     * @param expirationDate the expiration date
     * @param authorId the author id
     * @return the remind
     */
    public Remind createRemind(long textChannelId, String prize, long expirationDate, long authorId) {
        // create and send giveaway message
        Message message = VoidBot.getJDA().getTextChannelById(textChannelId).sendMessage(message(
                remindEmbed("Successfully set reminder!",
                        "I will remind you to do following: `" + prize + "`")))
                .complete();
        message.delete().queueAfter(10, TimeUnit.SECONDS);
        // create and giveaway
        Remind remind = new Remind(textChannelId, message.getIdLong(), prize, expirationDate, authorId);
        remind.save();
        return remind;
    }

    /**
     * Remind embed.
     *
     * @param title the title
     * @param description the description
     * @return the embed builder
     */
    private static EmbedBuilder remindEmbed(String title, String description) {
        return embed(":clock: " + title, description).setColor(Colors.COLOR_PRIMARY);
    }
}
