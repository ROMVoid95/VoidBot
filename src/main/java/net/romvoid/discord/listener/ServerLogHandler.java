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
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.sql.ServerLogSQL;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class ServerLogHandler.
 */
public class ServerLogHandler extends ListenerAdapter {

    /** The ev join color. */
    private Color evJoinColor = new Color(49, 133, 224);
    
    /** The ev leave color. */
    private Color evLeaveColor = new Color(198, 224, 49);
    
    /** The ev ban color. */
    private Color evBanColor = new Color(224, 67, 0);
    
    /** The ev voice log. */
    private Color evVoiceLog = new Color(120, 68, 234);
    
    /** The ev role added. */
    private Color evRoleAdded = new Color(24, 188, 30);
    
    /** The ev role removed. */
    private Color evRoleRemoved = new Color(188, 57, 24);
    
    /** The ev command log. */
    private static Color evCommandLog = new Color(165, 100, 24);

    /** The banned users. */
    public static ArrayList<Long> bannedUsers = new ArrayList<>();

    /**
     * On guild member join.
     *
     * @param event the event
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!isEventEnabled(event.getGuild(), LogEventKeys.JOIN))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("New member joined the server", null, event.getMember().getUser().getAvatarUrl());
        embedBuilder.setDescription("**" + event.getMember().getEffectiveName() + " (" + event.getMember().getUser().getId() + ")** joined the server");
        embedBuilder.setColor(evJoinColor);
        sendLog(textChannel, embedBuilder);
    }

    /**
     * On guild member leave.
     *
     * @param event the event
     */
    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        if (bannedUsers.contains(event.getUser().getIdLong())) {
            return;
        }
        if (!isEventEnabled(event.getGuild(), LogEventKeys.BAN))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A member left the server", null, event.getMember().getUser().getAvatarUrl());
        embedBuilder.setDescription("**" + event.getMember().getEffectiveName() + " (" + event.getMember().getUser().getId() + ")** left the server");
        embedBuilder.setColor(evLeaveColor);
        sendLog(textChannel, embedBuilder);
    }

    /**
     * On guild ban.
     *
     * @param event the event
     */
    @Override
    public void onGuildBan(GuildBanEvent event) {
        if (!isEventEnabled(event.getGuild(), LogEventKeys.BAN))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;
        bannedUsers.add(event.getUser().getIdLong());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A member was banned", null, event.getUser().getAvatarUrl());
        embedBuilder.setDescription("**" + event.getUser().getName() + " (" + event.getUser().getId() + ")** was banned from the server");
        embedBuilder.setColor(evBanColor);
        sendLog(textChannel, embedBuilder);
    }

    /**
     * On guild voice join.
     *
     * @param event the event
     */
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (!isEventEnabled(event.getGuild(), LogEventKeys.VOICE))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A member created voice connection", null, event.getMember().getUser().getAvatarUrl());
        embedBuilder.setDescription("**" + event.getMember().getEffectiveName() + " (" + event.getMember().getUser().getId() + ")** joined `" + event.getChannelJoined().getName() + "`");
        embedBuilder.setColor(evVoiceLog);
        sendLog(textChannel, embedBuilder);
    }

    /**
     * On guild voice move.
     *
     * @param event the event
     */
    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if (!isEventEnabled(event.getGuild(), LogEventKeys.VOICE))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A member changed the channel", null, event.getMember().getUser().getAvatarUrl());
        embedBuilder.setDescription("**" + event.getMember().getEffectiveName() + " (" + event.getMember().getUser().getId() + ")** went from `" + event.getChannelLeft().getName() + "` to `" + event.getChannelJoined().getName() + "`");
        embedBuilder.setColor(evVoiceLog);
        sendLog(textChannel, embedBuilder);
    }

    /**
     * On guild voice leave.
     *
     * @param event the event
     */
    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (!isEventEnabled(event.getGuild(), LogEventKeys.VOICE))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A member closed voice connection", null, event.getMember().getUser().getAvatarUrl());
        embedBuilder.setDescription("**" + event.getMember().getEffectiveName() + " (" + event.getMember().getUser().getId() + ")** left `" + event.getChannelLeft().getName() + "`");
        embedBuilder.setColor(evVoiceLog);
        sendLog(textChannel, embedBuilder);
    }

    /**
     * On guild member role add.
     *
     * @param event the event
     */
    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if (!isEventEnabled(event.getGuild(), LogEventKeys.ROLE))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A member role was updated", null, event.getUser().getAvatarUrl());
        embedBuilder.setDescription("Added **" + event.getRoles().get(0).getName() + "** to **" + event.getMember().getEffectiveName() + " (" + event.getMember().getUser().getId() + ")**");
        embedBuilder.setColor(evRoleAdded);
        sendLog(textChannel, embedBuilder);
    }

    /**
     * On guild member role remove.
     *
     * @param event the event
     */
    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if (!isEventEnabled(event.getGuild(), LogEventKeys.ROLE))
            return;
        TextChannel textChannel = getLogChannel(event.getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A member role was updated", null, event.getUser().getAvatarUrl());
        embedBuilder.setDescription("Removed **" + event.getRoles().get(0).getName() + "** from **" + event.getMember().getEffectiveName() + " (" + event.getMember().getUser().getId() + ")**");
        embedBuilder.setColor(evRoleRemoved);
        sendLog(textChannel, embedBuilder);
    }

    /**
     * Log command.
     *
     * @param parsedCommandInvocation the parsed command invocation
     */
    public static void logCommand(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        if (!isEventEnabled(parsedCommandInvocation.getMessage().getGuild(), LogEventKeys.COMMAND))
            return;
        TextChannel textChannel = getLogChannel(parsedCommandInvocation.getMessage().getGuild());
        if (textChannel == null)
            return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("A command was executed", null);
        embedBuilder.setDescription("**" + parsedCommandInvocation.getMessage().getMember().getEffectiveName() + " (" + parsedCommandInvocation.getMessage().getMember().getUser().getId() + ")** executed `" + parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation() + "`");
        embedBuilder.setColor(evCommandLog);
        sendLog(textChannel, embedBuilder);
    }

    /**
     * Send log.
     *
     * @param channel the channel
     * @param builder the builder
     */
    public static void sendLog(TextChannel channel, EmbedBuilder builder) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        builder.setFooter(simpleDateFormat.format(new Date()), null);
        channel.sendMessage(builder.build()).queue();
    }

    /**
     * Gets the log channel.
     *
     * @param guild the guild
     * @return the log channel
     */
    private static TextChannel getLogChannel(Guild guild) {
        String entry = new ServerLogSQL(guild).get("channel");
        if (entry == null)
            return null;
        if (entry.equals("0"))
            return null;
        TextChannel textChannel;
        try {
            textChannel = guild.getTextChannelById(entry);
            return textChannel;
        } catch (NullPointerException ignored) {
            //channel deleted or something
        }
        return null;
    }

    /**
     * Checks if is event enabled.
     *
     * @param guild the guild
     * @param key the key
     * @return true, if is event enabled
     */
    public static boolean isEventEnabled(Guild guild, LogEventKeys key) {
        String entry = new ServerLogSQL(guild).get(key.getKey());
        try {
            return entry.equalsIgnoreCase("true");
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    /**
     * The Enum LogEventKeys.
     */
    public enum LogEventKeys {
        
        /** The join. */
        JOIN("ev_join", "Join"),
        
        /** The leave. */
        LEAVE("ev_leave", "Leave"),
        
        /** The command. */
        COMMAND("ev_command", "Command"),
        
        /** The ban. */
        BAN("ev_ban", "Ban"),
        
        /** The role. */
        ROLE("ev_role", "Role"),
        
        /** The voice. */
        VOICE("ev_voice", "Voice");


        /** The key. */
        private String key;
        
        /** The displayname. */
        private String displayname;

        /**
         * Instantiates a new log event keys.
         *
         * @param key the key
         * @param displayname the displayname
         */
        LogEventKeys(String key, String displayname) {
            this.key = key;
            this.displayname = displayname;
        }

        /**
         * Gets the key.
         *
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * Gets the displayname.
         *
         * @return the displayname
         */
        public String getDisplayname() {
            return displayname;
        }

        /**
         * Gets the all keys.
         *
         * @return the all keys
         */
        public static List<LogEventKeys> getAllKeys() {
            List<LogEventKeys> list = new ArrayList<>();
            list.add(JOIN);
            list.add(LEAVE);
            list.add(COMMAND);
            list.add(BAN);
            list.add(ROLE);
            list.add(VOICE);
            return list;
        }
    }
}
