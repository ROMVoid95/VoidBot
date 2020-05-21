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
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.RestAction;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.command.UnavailableCommandHandler;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.sql.MySQL;
import net.romvoid.discord.util.Logger;

import static net.romvoid.discord.util.EmbedUtil.*;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
/**
 * Manages giveaways and handles user interaction.
 */
public class GiveawayHandler extends CommandHandler {
    /**
     * Chat-giveaway.
     */
    private class Giveaway {
        
        /** The text channel id. */
        private final long textChannelId;
        
        /** The message id. */
        private final long messageId;
        
        /** The prize. */
        private final String prize;
        
        /** The expiration date. */
        private final long expirationDate;
        
        /** The author id. */
        private final long authorId;
        
        /** The resolve task. */
        private final TimerTask resolveTask = new TimerTask() {
            @Override
            public void run() {
                resolve();
            }
        };
        
        /** The open. */
        private boolean open = true;

        /**
         * Constructs a giveaway object from the given parameters, ensures its singularity and schedules the resolving.
         *
         * @param textChannelId  the id of the text channel the giveaway message was posted in.
         * @param messageId      the message's id.
         * @param prize          the prize that will be won.
         * @param expirationDate the millis-date for the giveaway expiration.
         * @param authorId       the user id of the person giving away something.
         */
        private Giveaway(long textChannelId, long messageId, String prize, long expirationDate, long authorId) {
            this.textChannelId = textChannelId;
            this.messageId = messageId;
            this.prize = prize;
            this.expirationDate = expirationDate;
            this.authorId = authorId;

            // ensure there is not object of this giveaway yet to keep a single task
            if (giveaways.contains(this))
                throw new IllegalStateException("Giveaway object already exists for the specified message");
            giveaways.add(this);

            // schedule resolving
            VoidBot.getTimer().schedule(resolveTask, new Date(expirationDate));
        }

        /**
         * Prepare giveaway resolving. Actual resolving takes place in doResolve().
         */
        private void resolve() {
            // check whether the giveaway was resolved yet
            if (!open)
                throw new IllegalStateException("Giveaway is not open any more");

            getMessage().queue(message -> {
                MessageReaction participationReaction = null;
                for (MessageReaction reaction : message.getReactions()) {
                    if (reaction.getReactionEmote().getName().equalsIgnoreCase(PARTICIPATION_EMOTE)) {
                        participationReaction = reaction;
                        break;
                    }
                }
                if (participationReaction == null)
                    doResolve(new ArrayList<>(0));
                else
                    participationReaction.getUsers().queue(this::doResolve);
            });
        }

        /**
         * Resolves the giveaway with the given participants list.
         *
         * @param participants all participants. Participants will be checked with {@link #isEligible(User)}.
         */
        private void doResolve(List<User> participants) {
            // check whether the giveaway was resolved yet
            if (!open)
                throw new IllegalStateException("Giveaway is not open any more");

            User author = getAuthor();

            // remove ineligible participants
            if (participants != null)
                for (Iterator<User> iterator = participants.iterator(); iterator.hasNext(); )
                    if (!isEligible(iterator.next()))
                        iterator.remove();

            // prepare messages
            String winMessage, authorMessage;
            if (participants == null || participants.isEmpty()) { // no participants
                winMessage = "Nobody won `" + prize + "` from " + author.getAsMention() + " because " +
                        "nobody participated.";
                authorMessage = "Nobody participated in your giveaway for `" + prize + "` that ended recently.";
            } else {
                // select winner and send him a message
                User winner = participants.get((int) (Math.random() * participants.size()));
                winner.openPrivateChannel().queue(ch -> ch.sendMessage(message(giveawayEmbed("Congratulations!",
                        "You won `" + prize + "` from `" + author.getName() + '#' + author.getDiscriminator()
                                + "`. " + author.getName() + " has been notified and should send your prize very soon.")))
                        .queue());

                winMessage = winner.getAsMention() + " won `" + prize + "` from "
                        + author.getAsMention() + ". Congratulations!";
                authorMessage = '`' + winner.getName() + '#' + winner.getDiscriminator() + "` won your giveaway for `" +
                        prize + "`. Please send him/her the prize as soon as possible.";
            }

            // send messages to channel and giveaway author
            author.openPrivateChannel().queue(ch -> ch.sendMessage(
                    message(giveawayEmbed("Your giveaway has ended", authorMessage))
            ).queue());
            getTextChannel().sendMessage(
                    message(giveawayEmbed(author.getName() + "'s giveaway has ended", winMessage))
            ).queue();

            // edit original giveaway message
            getMessage().queue(message -> {
                MessageEmbed embed = message.getEmbeds().get(0);
                message.editMessage(new EmbedBuilder(embed)
                        .setDescription("~~" + embed.getDescription().replaceAll("~", "") + "~~")
                        .setFooter("This giveaway is over.", null)
                        .build()).queue();
            });

            close();
        }

        /**
         * Checks whether a user is eligible for participating in this giveaway.
         *
         * @param user the user who will be checked.
         * @return whether the user is eligible.
         */
        public boolean isEligible(User user) {
            return user != null // not null
                    && !user.isBot() // not a bot (including self-user)
                    && !user.isFake() // not a webhook / fake user
                    && user.getIdLong() != authorId; // not the author
        }

        /**
         * Gets the text channel id.
         *
         * @return the text channel id of the giveaway message.
         * @see #getTextChannel() for the text channel object.
         */
        public long getTextChannelId() {
            return textChannelId;
        }

        /**
         * Gets the message id.
         *
         * @return the message id of the giveaway message.
         * @see #getMessage() for the {@link Message} object.
         */
        public long getMessageId() {
            return messageId;
        }

        /**
         * Gets the prize.
         *
         * @return the giveaway prize.
         */
        public String getPrize() {
            return prize;
        }

        /**
         * Gets the expiration date.
         *
         * @return the giveaway expiration date in milliseconds.
         */
        @SuppressWarnings("unused")
        public long getExpirationDate() {
            return expirationDate;
        }

        /**
         * Gets the author id.
         *
         * @return the user id of there giveaway author.
         * @see #getAuthor() for the author {@link User} object.
         */
        @SuppressWarnings("unused")
        public long getAuthorId() {
            return authorId;
        }

        /**
         * Gets the guild.
         *
         * @return the guild where the giveaway was posted.
         */
        @SuppressWarnings("unused")
        public Guild getGuild() {
            return getTextChannel().getGuild();
        }

        /**
         * Gets the text channel.
         *
         * @return the text channel the giveaway was posted in.
         * @see #getTextChannelId()
         */
        public TextChannel getTextChannel() {
            return VoidBot.getJDA().getTextChannelById(textChannelId);
        }

        /**
         * Gets the message.
         *
         * @return a RestAction that requests the message from the text channel and message id.
         */
        public RestAction<Message> getMessage() {
            return getTextChannel().getMessageById(messageId);
        }

        /**
         * Gets the author.
         *
         * @return the User that posted the giveaway.
         * @see #getAuthorId()
         */
        public User getAuthor() {
            return VoidBot.getJDA().getUserById(authorId);
        }

        /**
         * Checks if is open.
         *
         * @return whether the giveaway is still open.
         */
        @SuppressWarnings("unused")
        public boolean isOpen() {
            return open;
        }

        /**
         * Inserts this giveaway into the database. Only call this once or there will be multiple instances in the database.
         *
         * @return whether the saving was successful.
         */
        public boolean save() {
            // check whether the giveaway was resolved yet
            if (!open)
                throw new IllegalStateException("Giveaway is not open any more");

            try {
                PreparedStatement insertStatement = MySQL.getCon().prepareStatement("INSERT INTO `giveaways-v1` (" +
                        "`textchannelid`, " +
                        "`messageid`," +
                        "`prize`," +
                        "`expirationdate`," +
                        "`authorid`) " +
                        "VALUES (?, ?, ?, ?, ?);");
                insertStatement.setLong(1, textChannelId);
                insertStatement.setLong(2, messageId);
                insertStatement.setString(3, prize);
                insertStatement.setLong(4, expirationDate);
                insertStatement.setLong(5, authorId);
                insertStatement.execute();
            } catch (SQLException e) {
                Logger.error("Could not save giveaway '" + toString() + "'.");
                Logger.error(e);
                return false;
            }
            return true;
        }

        /**
         * Closes this giveaway. Should only be called after resolving it.
         */
        public void close() {
            open = false;
            resolveTask.cancel();
            giveaways.remove(this);
            try {
                PreparedStatement deleteStatement = MySQL.getCon()
                        .prepareStatement("DELETE FROM `giveaways-v1` WHERE `textchannelid` = ? AND `messageid` = ?;");
                deleteStatement.setLong(1, textChannelId);
                deleteStatement.setLong(2, messageId);
                deleteStatement.execute();
            } catch (SQLException e) {
                Logger.error("Could not delete giveaway '" + toString() + "'.");
                Logger.error(e);
            }
        }

        /**
         * Equals.
         *
         * @param obj the obj
         * @return true, if successful
         */
        @Override
        public boolean equals(Object obj) {
            // obj must be a Giveaway object and have equal text channel & message
            return obj instanceof Giveaway && textChannelId == ((Giveaway) obj).textChannelId && messageId == ((Giveaway) obj).messageId;
        }
    }

    /** The Constant PARTICIPATION_EMOTE. */
    private static final String PARTICIPATION_EMOTE = "\ud83c\udfc6"; // golden trophy emote
    
    /** The Constant EMBED_COLOR. */
    private static final Color EMBED_COLOR = new Color(255, 215, 0);

    /**
     * Handles user interaction through events.
     */
    private final ListenerAdapter listener = new ListenerAdapter() {
        @Override
        public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
            if (!event.getUser().equals(VoidBot.getJDA().getSelfUser())) {
                Giveaway giveaway = getGiveawayById(event.getChannel().getIdLong(), event.getMessageIdLong());
                EmbedBuilder message;
                if (giveaway != null) {
                    User author = giveaway.getAuthor();
                    if (giveaway.isEligible(event.getUser()))
                        message = info("Giveaway participation", "You are now participating in a giveaway by `"
                                + author.getName() + '#' + author.getDiscriminator() + "` for `" + giveaway.getPrize() + "`.");
                    else {
                        event.getReaction().removeReaction(event.getUser()).queue();
                        message = error("Not eligible", "Sorry, you can not participate in this giveaway.");
                    }
                    event.getUser().openPrivateChannel().queue(ch -> ch.sendMessage(message.build()).queue());
                }
            }
        }

        @Override
        public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
            if (!event.getUser().equals(VoidBot.getJDA().getSelfUser())) {
                Giveaway giveaway = getGiveawayById(event.getChannel().getIdLong(), event.getMessageIdLong());
                if (giveaway != null && giveaway.isEligible(event.getUser())) {
                    User author = giveaway.getAuthor();
                    event.getUser().openPrivateChannel().queue(ch -> ch.sendMessage(info("Giveaway participation",
                            "You are not participating any more in a giveaway by `"
                                    + author.getName() + '#' + author.getDiscriminator() + "` for `"
                                    + giveaway.getPrize() + "`.").build()).queue());
                }
            }
        }
    };

    /** The giveaways. */
    private Set<Giveaway> giveaways = new HashSet<>();

    /**
     * Constructs, initializes and registers giveaway handling.
     */
    public GiveawayHandler() {
        super(new String[]{"giveaway", "giveaways"}, CommandCategory.ADMIN,
                new PermsNeeded("command.giveaway", false, false),
                "Creates an automated giveaway users can take part in by reacting.",
                "create <runtime-in-minutes> <prize...>");

        // ensure table existence
        try {
            MySQL.getCon().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `giveaways-v1` (" +
                            "`textchannelid` BIGINT SIGNED, " +
                            "`messageid` BIGINT SIGNED, " +
                            "`prize` VARCHAR(300), " +
                            "`expirationdate` BIGINT SIGNED, " +
                            "`authorid` BIGINT SIGNED" +
                            ");")
                    .execute();
        } catch (SQLException e) {
            Logger.error("Could not create giveaways table. Disabling giveaways.");
            Logger.error(e);
            VoidBot.getCommandManager().registerCommandHandler(new UnavailableCommandHandler(this));
            return;
        }

        // load giveaways
        try {
            for (TextChannel channel : VoidBot.getJDA().getTextChannels()) {
                PreparedStatement selectStatement = MySQL.getCon()
                        .prepareStatement("SELECT * FROM `giveaways-v1` WHERE `textchannelid` = ?;");
                selectStatement.setLong(1, channel.getIdLong());
                ResultSet channelResult = selectStatement.executeQuery();
                while (channelResult.next())
                    new Giveaway(channelResult.getLong("textchannelid"),
                            channelResult.getLong("messageid"),
                            channelResult.getString("prize"),
                            channelResult.getLong("expirationdate"),
                            channelResult.getLong("authorid"));
            }
        } catch (SQLException e) {
            Logger.error("Could not load giveaways, disabling them.");
            Logger.error(e);
            VoidBot.getCommandManager().registerCommandHandler(new UnavailableCommandHandler(this));
            return;
        }

        // register command and listeners if nothing went wrong
        VoidBot.getCommandManager().registerCommandHandler(this);
        VoidBot.registerEventListener(this.listener);
    }

    /**
     * Returns the giveaway with the specified identifiers, if it exists.
     *
     * @param textChannelId the {@link TextChannel} id.
     * @param messageId     the {@link Message} id.
     * @return the giveaway or null if there is no giveaway with the provided identifiers.
     */
    private Giveaway getGiveawayById(long textChannelId, long messageId) {
        for (Giveaway giveaway : giveaways)
            if (giveaway.getTextChannelId() == textChannelId && giveaway.getMessageId() == messageId)
                return giveaway;
        return null;
    }

    /**
     * Handles the 'giveaway' command.
     *
     * @param invocation the invocation
     * @param userPermissions the user permissions
     * @return the message
     */
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length == 0)
            return createHelpMessage(invocation);
        else {
            switch (invocation.getArgs()[0]) {
                case "create":
                    if (invocation.getArgs().length < 3)
                        return createHelpMessage(invocation);

                    // parse runtime
                    int runtime;
                    try {
                        runtime = Integer.parseInt(invocation.getArgs()[1]);
                        if (runtime < 0)
                            throw new IllegalArgumentException();
                    } catch (IllegalArgumentException e) {
                        return message(error("Invalid argument",
                                "The runtime must be an integer number greater than 0."));
                    }

                    // parse prize
                    StringBuilder prize = new StringBuilder(invocation.getArgs()[2]);
                    for (int i = 3; i < invocation.getArgs().length; i++)
                        prize.append(" ").append(invocation.getArgs()[i]);

                    // create giveaway
                    Giveaway giveaway = createGiveaway(invocation.getTextChannel().getIdLong(),
                            prize.toString(),
                            System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(runtime),
                            invocation.getAuthor().getIdLong());

                    return giveaway == null ? message(error()) : null; // message should not be deleted and is sent
                default:
                    return message(error("Invalid arguments", '`' + invocation.getArgs()[0] + "` is not a " +
                            "valid subcommand."));
            }
        }
    }

    /**
     * Creates and registers a new giveaway.
     *
     * @param textChannelId  the channel where the giveaway should take place.
     * @param prize          the giveaway prize.
     * @param expirationDate when the giveaway should end.
     * @param authorId       the user who started the giveaway.
     * @return the giveaway
     */
    public Giveaway createGiveaway(long textChannelId, String prize, long expirationDate, long authorId) {
        // create and send giveaway message
        Message message = VoidBot.getJDA().getTextChannelById(textChannelId).sendMessage(message(
                giveawayEmbed("Giveaway by " + VoidBot.getJDA().getUserById(authorId).getName(),
                        VoidBot.getJDA().getUserById(authorId).getAsMention() + " is giving away `" + prize + "`.")
                        .setFooter("Take part by reacting with the trophy", null)))
                .complete();
        message.addReaction(PARTICIPATION_EMOTE).queue();
        // create and giveaway
        Giveaway giveaway = new Giveaway(textChannelId, message.getIdLong(), prize, expirationDate, authorId);
        giveaway.save();
        return giveaway;
    }

    /**
     * Constructs an embed for giveaway messages.
     *
     * @param title       the embed title. A gift emote will be attached at the beginning.
     * @param description the embed description.
     * @return the configured EmbedBuilder.
     */
    private static EmbedBuilder giveawayEmbed(String title, String description) {
        return embed(":gift: " + title, description).setColor(EMBED_COLOR);
    }
}
