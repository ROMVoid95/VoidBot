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
package net.romvoid.discord.commands.admin;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandPortal.
 */
public class CommandPortal extends CommandHandler {

    /** The portal channel name. */
    private String portalChannelName = "thevoid-portal";
    
    /** The closed channel name. */
    private String closedChannelName = "closed-thevoid-portal";
    
    /** The invite file. */
    private File inviteFile = new File(VoidBot.getDataFolder() + "portal-invites.json");

    /**
     * Instantiates a new command portal.
     */
    public CommandPortal() {
        super(new String[]{"portal", "mirror", "phone"}, CommandCategory.ADMIN, new PermsNeeded("command.portal", false, false), "Create a portal and talk with users from other guilds.", "create\nclose\ninvite <serverid>\naccept <serverid>\ninfo");
    }

    /**
     * Execute.
     *
     * @param parsedCommandInvocation the parsed command invocation
     * @param userPermissions the user permissions
     * @return the message
     */
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length == 0) {
            return createHelpMessage(parsedCommandInvocation);
        }

        switch (parsedCommandInvocation.getArgs()[0].toLowerCase()) {
            case "open":
            case "o":
            case "create":
                if (parsedCommandInvocation.getArgs().length == 1) {
                    createPortalWithRandomGuild(parsedCommandInvocation);
                    return null;
                }
                return null;
            case "close":
                closePortal(parsedCommandInvocation);
                return null;
            case "invite":
                if (parsedCommandInvocation.getArgs().length == 2) {
                    inviteGuild(parsedCommandInvocation);
                } else {
                    return createHelpMessage();
                }
                return null;
            case "accept":
                if (parsedCommandInvocation.getArgs().length == 2) {
                    acceptInvite(parsedCommandInvocation);
                } else {
                    createHelpMessage();
                }
                return null;
            case "info":
                portalInfo(parsedCommandInvocation);
                return null;
            default:
                return createHelpMessage(parsedCommandInvocation);
        }
    }

    /**
     * Portal info.
     *
     * @param parsedCommandInvocation the parsed command invocation
     */
    private void portalInfo(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        String entry = VoidBot.getMySQL().getGuildValue(parsedCommandInvocation.getGuild(), "portal");
        TextChannel channel = parsedCommandInvocation.getTextChannel();
        if(entry.equals("open")){
            Guild guild = VoidBot.getJDA().getGuildById(VoidBot.getMySQL().getPortalValue(parsedCommandInvocation.getGuild(), "partnerid"));
            SafeMessage.sendMessage(channel, EmbedUtil.info("Connected to " + guild.getName(), "This server is currently connected to `" + guild.getName() + "`!").build(), 8);
        } else if (entry.equals("waiting")){
            SafeMessage.sendMessage(channel, EmbedUtil.error("Portal is waitng", "The portal is currently waiting to be accepted by the other side").build(), 5);
        } else if(entry.equals("closed")){
            SafeMessage.sendMessage(channel, EmbedUtil.error("Portal is closed", "The portal is currently closed").build(), 5);
        }

    }

    /**
     * Creates a portal with a random guild.
     *
     * @param parsedCommandInvocation parsedCommandInvocation
     */
    private void createPortalWithRandomGuild(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Guild messageGuild = parsedCommandInvocation.getMessage().getGuild();
        TextChannel messageChannel = parsedCommandInvocation.getMessage().getTextChannel();

        //Check if portal exists
        String oldGuildPortalEntry = VoidBot.getMySQL().getGuildValue(messageGuild, "portal");
        if (oldGuildPortalEntry.equals("open") || oldGuildPortalEntry.contains("waiting") || VoidBot.getMySQL().ifPortalExist(messageGuild)) {
            messageChannel.sendMessage(EmbedUtil.error("Portal error!", "Portal is already open.").build()).queue();
            return;
        }

        List<Guild> waitingGuilds = VoidBot.getMySQL().getGuildsByValue("portal", "waiting");
        if (waitingGuilds.size() == 0) {
            setGuildWaiting(messageGuild, messageChannel);
        } else {
            connectGuilds(messageGuild, waitingGuilds.get(0), messageChannel);
        }
    }

    /**
     * Invite guild.
     *
     * @param parsedCommandInvocation the parsed command invocation
     */
    private void inviteGuild(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Guild messageGuild = parsedCommandInvocation.getMessage().getGuild();
        TextChannel messageChannel = parsedCommandInvocation.getMessage().getTextChannel();
        if (!StringUtil.isNumeric(parsedCommandInvocation.getArgs()[1])) {
            parsedCommandInvocation.getMessage().getTextChannel().sendMessage(EmbedUtil.error("Portal error", "Invalid guild id.").build()).queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        if (parsedCommandInvocation.getArgs()[1].equalsIgnoreCase(messageGuild.getId())) {
            messageChannel.sendMessage(EmbedUtil.error("Portal error!", "Portal you can't invite yourself.").build()).queue();
            return;
        }

        //Check if portal exists
        String oldGuildPortalEntry = VoidBot.getMySQL().getGuildValue(messageGuild, "portal");
        if (oldGuildPortalEntry.equals("open") || oldGuildPortalEntry.contains("waiting")) {
            messageChannel.sendMessage(EmbedUtil.error("Portal error!", "Portal is already open.").build()).queue();
            return;
        }
        String guildId = parsedCommandInvocation.getArgs()[1];
        Guild guildTwo = null;
        try {
            guildTwo = parsedCommandInvocation.getMessage().getJDA().getGuildById(guildId);
        } catch (NullPointerException ignored) {
            // Guild doesn't exist
        }

        if (guildTwo == null) {
            parsedCommandInvocation.getMessage().getTextChannel().sendMessage(EmbedUtil.error("Portal error", "Invalid guild id.").build()).queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        if (VoidBot.getMySQL().getGuildValue(guildTwo, "portal").equals("waiting") || VoidBot.getMySQL().getGuildValue(guildTwo, "portal").equals("closed")) {
            EmbedBuilder builder = EmbedUtil.embed("Portal Invite", parsedCommandInvocation.getMessage().getGuild().getName() + "(" + parsedCommandInvocation.getMessage().getGuild().getId() + ") has sent you an portal invite.\n`Accept with >>>portal accept <serverid>`");
            builder.setFooter("Execute this command on your server", null);
            builder.setColor(Colors.COLOR_PRIMARY);
            guildTwo.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(builder.build()).queue());

            if (!inviteFile.exists()) {
                try {
                    inviteFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Configuration configuration = new Configuration(inviteFile);
            createInviteEntryIfNotExists(configuration, guildTwo);
            configuration.set(guildTwo.getId() + "." + messageGuild.getId(), 1);
            parsedCommandInvocation.getMessage().getTextChannel().sendMessage(EmbedUtil.success("Portal Invite sent", "Successfully sent an portal invite to " + guildTwo.getName()).build()).queue();
        }
    }

    /**
     * Accept invite.
     *
     * @param parsedCommandInvocation the parsed command invocation
     */
    private void acceptInvite(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Guild guildOne = parsedCommandInvocation.getMessage().getGuild();
        if (guildOne == null) {
            parsedCommandInvocation.getMessage().getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(EmbedUtil.error("Portal error!", "You only can execute this command on your server.").build()).queue());
            return;
        }
        Guild guildTwo;

        if (!StringUtil.isNumeric(parsedCommandInvocation.getArgs()[1])) {
            parsedCommandInvocation.getMessage().getTextChannel().sendMessage(EmbedUtil.error("Portal error", "Invalid guild id!").build()).queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        try {
            guildTwo = parsedCommandInvocation.getMessage().getJDA().getGuildById(parsedCommandInvocation.getArgs()[1]);
        } catch (NullPointerException ex) {
            parsedCommandInvocation.getMessage().getTextChannel().sendMessage(EmbedUtil.error("Portal error", "Invalid guild id!").build()).queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        if (guildTwo == null) {
            parsedCommandInvocation.getMessage().getTextChannel().sendMessage(EmbedUtil.error("Portal error", "Invalid guild id!").build()).queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        Configuration configuration = new Configuration(inviteFile);
        createInviteEntryIfNotExists(configuration, guildTwo);

        if (configuration.has(guildOne.getId() + "." + guildTwo.getId())) {
            connectGuilds(guildOne, guildTwo, parsedCommandInvocation.getMessage().getTextChannel());
            configuration.set(guildOne.getId() + "." + guildTwo.getId(), null);
        } else {
            parsedCommandInvocation.getMessage().getTextChannel().sendMessage(EmbedUtil.error("Portal error!", "You don't have an invite from this guild!").build()).queue();
        }
    }

    /**
     * Create portals at two guilds.
     *
     * @param guildOne message guild
     * @param guildTwo guild with waiting status
     * @param messageChannel the message channel
     */
    private void connectGuilds(Guild guildOne, Guild guildTwo, TextChannel messageChannel) {
        //Channel creation and waiting check
        try {
            TextChannel channelOne;
            TextChannel channelTwo;
            if (guildOne.getTextChannelsByName(portalChannelName, true).size() == 0) {
                if (guildOne.getMemberById(VoidBot.getJDA().getSelfUser().getId()).getPermissions().contains(Permission.MANAGE_CHANNEL)) {
                    channelOne = (TextChannel) guildOne.getController().createTextChannel(portalChannelName).complete();
                } else {
                    messageChannel.sendMessage(EmbedUtil.error("Portal Error!", "I need the `MANAGE_CHANNEL` permissions or you create yourself a channel called `rubicon-portal`.").setFooter(guildOne.getName(), null).build()).queue();
                    return;
                }
            } else {
                channelOne = guildOne.getTextChannelsByName(portalChannelName, true).get(0);
            }
            if (guildTwo.getTextChannelsByName(portalChannelName, true).size() == 0) {
                if (guildTwo.getMemberById(VoidBot.getJDA().getSelfUser().getId()).getPermissions().contains(Permission.MANAGE_CHANNEL)) {
                    channelTwo = (TextChannel) guildTwo.getController().createTextChannel(portalChannelName).complete();
                } else {
                    guildTwo.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(EmbedUtil.error("Portal Error!", "I need the `MANAGE_CHANNEL` permissions or you create yourself a channel called `rubicon-portal`.").setFooter(guildTwo.getName(), null).build()).queue());
                    VoidBot.getMySQL().updateGuildValue(guildTwo, "portal", "closed");
                    setGuildWaiting(guildOne, messageChannel);
                    return;
                }
            } else {
                channelTwo = guildTwo.getTextChannelsByName(portalChannelName, true).get(0);
            }
            //Update Database Values
            VoidBot.getMySQL().updateGuildValue(guildOne, "portal", "open");
            VoidBot.getMySQL().createPortal(guildOne, guildTwo, channelOne);
            VoidBot.getMySQL().updateGuildValue(guildTwo, "portal", "open");
            VoidBot.getMySQL().createPortal(guildTwo, guildOne, channelTwo);

            channelOne.getManager().setTopic("Connected to: " + guildTwo.getName()).queue();
            channelTwo.getManager().setTopic("Connected to: " + guildOne.getName()).queue();

            //Send Connected Message
            sendConnectedMessage(channelOne, channelTwo);
        } catch (Exception ignored) {
            Logger.error(ignored);
        }
    }

    /**
     * Send connected message.
     *
     * @param channelOne the channel one
     * @param channelTwo the channel two
     */
    private void sendConnectedMessage(TextChannel channelOne, TextChannel channelTwo) {
        //GuildOne Message
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Connection established with " + channelTwo.getGuild().getName(), null, channelTwo.getGuild().getIconUrl());
        embedBuilder.setDescription(":white_check_mark: Successfully created and connected portals.");
        Message message1 = channelOne.sendMessage(embedBuilder.build()).complete();
        channelOne.pinMessageById(message1.getId()).queue();

        //GuildTwo Message
        embedBuilder.setAuthor("Connection established with " + channelOne.getGuild().getName(), null, channelOne.getGuild().getIconUrl());
        embedBuilder.setDescription(":white_check_mark: Successfully created and connected portals.");
        Message message2 = channelTwo.sendMessage(embedBuilder.build()).complete();
        channelTwo.pinMessageById(message2.getId()).queue();
    }

    /**
     * Sets the guild waiting.
     *
     * @param g the g
     * @param messageChannel the message channel
     */
    private void setGuildWaiting(Guild g, TextChannel messageChannel) {
        VoidBot.getMySQL().updateGuildValue(g, "portal", "waiting");
        if (g.getTextChannelsByName(portalChannelName, true).size() == 0)
            messageChannel.sendMessage(EmbedUtil.success("Portal opened", "Successfully opened portal.\nA portal-channel will be created as soon as another server opens a portal.").build()).queue();
        else
            messageChannel.sendMessage(EmbedUtil.success("Portal opened", "Successfully opened portal.\nA message will be sent to the portal channel as soon as a partner is found.").build()).queue();
        return;
    }

    /**
     * Close portal.
     *
     * @param parsedCommandInvocation the parsed command invocation
     */
    private void closePortal(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        JDA jda = parsedCommandInvocation.getMessage().getJDA();
        Guild messageGuild = parsedCommandInvocation.getMessage().getGuild();
        TextChannel messageChannel = parsedCommandInvocation.getMessage().getTextChannel();

        //Check if portal exists
        String oldGuildPortalEntry = VoidBot.getMySQL().getGuildValue(messageGuild, "portal");
        if (oldGuildPortalEntry.equals("closed")) {
            messageChannel.sendMessage(EmbedUtil.error("Portal error!", "Portal is already closed").build()).queue();
            return;
        }
        if (oldGuildPortalEntry.equals("waiting")) {
            VoidBot.getMySQL().updateGuildValue(messageGuild, "portal", "closed");
            messageChannel.sendMessage(EmbedUtil.success("Portal", "Successful closed portal request.").build()).queue();
            return;
        }
        Guild partnerGuild = jda.getGuildById(VoidBot.getMySQL().getPortalValue(messageGuild, "partnerid"));

        //Close Channels
        TextChannel channelOne = null;
        TextChannel channelTwo = null;
        try {
            channelOne = jda.getTextChannelById(VoidBot.getMySQL().getPortalValue(messageGuild, "channelid"));
            channelTwo = jda.getTextChannelById(VoidBot.getMySQL().getPortalValue(partnerGuild, "channelid"));
        } catch (NullPointerException ignored) {
            //Channels doesn't exist
        }

        if (channelOne != null)
            channelOne.getManager().setName(closedChannelName).queue();
        if (channelTwo != null)
            channelTwo.getManager().setName(closedChannelName).queue();

        //Close and delete DB Portal
        VoidBot.getMySQL().updateGuildValue(messageGuild, "portal", "closed");
        VoidBot.getMySQL().deletePortal(messageGuild);
        VoidBot.getMySQL().updateGuildValue(partnerGuild, "portal", "closed");
        VoidBot.getMySQL().deletePortal(partnerGuild);

        EmbedBuilder portalClosedMessage = new EmbedBuilder();
        portalClosedMessage.setAuthor("Portal closed!", null, jda.getSelfUser().getEffectiveAvatarUrl());
        portalClosedMessage.setDescription("Portal was closed. Create a new one with `" + parsedCommandInvocation.getPrefix() + "portal create`");
        portalClosedMessage.setColor(Colors.COLOR_ERROR);

        channelOne.sendMessage(portalClosedMessage.build()).queue();
        portalClosedMessage.setDescription("Portal was closed. Create a new one with `" + parsedCommandInvocation.getPrefix() + "portal create`");
        channelTwo.sendMessage(portalClosedMessage.build()).queue();

        channelOne.getManager().setTopic("Portal closed").queue();
        channelTwo.getManager().setTopic("Portal closed").queue();
    }

    /**
     * Creates the invite entry if not exists.
     *
     * @param configuration the configuration
     * @param g the g
     */
    private void createInviteEntryIfNotExists(Configuration configuration, Guild g) {
        if (!configuration.has(g.getId() + ".state")) {
            configuration.set(g.getId() + ".state", "enabled");
        }
    }
}