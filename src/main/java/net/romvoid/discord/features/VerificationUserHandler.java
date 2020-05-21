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

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.commands.admin.CommandVerification;
import net.romvoid.discord.sql.MySQL;
import net.romvoid.discord.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class VerificationUserHandler.
 */
public class VerificationUserHandler {

    /** The users. */
    public static HashMap<Member, VerifyUser> users = new HashMap<>();
    
    /** The connection. */
    static Connection connection = MySQL.getCon();

    /**
     * The Class VerifyUser.
     */
    public static class VerifyUser {
        
        /** The guildid. */
        private final long guildid;
        
        /** The userid. */
        private final long userid;
        
        /** The messageid. */
        private final long messageid;
        
        /** The resolve task. */
        @SuppressWarnings("unused")
        private final Runnable resolveTask = new Runnable() {
            @Override
            public void run() {
                run();
            }
        };

        /**
         * Instantiates a new verify user.
         *
         * @param member the member
         * @param message the message
         */
        public VerifyUser(Member member, Message message) {
            this.guildid = member.getGuild().getIdLong();
            this.userid = member.getUser().getIdLong();
            this.messageid = message.getIdLong();
            CommandVerification.users.put(message, member.getUser());
            this.save();
            users.put(member, this);
            CommandVerification.users.put(message, member.getUser());
        }

        /**
         * From member.
         *
         * @param member the member
         * @return the verify user
         */
        public static VerifyUser fromMember(Member member) {
            return users.get(member);
        }


        /**
         * Save.
         *
         * @return true, if successful
         */
        private boolean save() {
            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO `verifyusers` (`guildid`, `userid`, `messageid`) VALUES (?,?,?)");
                ps.setLong(1, this.guildid);
                ps.setLong(2, this.userid);
                ps.setLong(3, this.messageid);
                ps.execute();
                return true;
            } catch (SQLException e) {
                Logger.error(e);
                return false;
            }
        }

        /**
         * Removes the.
         *
         * @return true, if successful
         */
        public boolean remove() {
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM `verifyusers` WHERE `userid` =? AND guildid =?");
                ps.setLong(1, this.userid);
                ps.setLong(2, this.guildid);
                ps.execute();
                return true;
            } catch (SQLException e) {
                Logger.error(e);
                return false;
            }
        }

    }

    /**
     * Load verify user.
     */
    public static void loadVerifyUser() {
        try {
            PreparedStatement selectStatement = MySQL.getCon()
                    .prepareStatement("SELECT * FROM `verifyusers` ");
            ResultSet channelResult = selectStatement.executeQuery();
            while (channelResult.next()) {
                Guild guild = VoidBot.getJDA().getGuildById(channelResult.getString("guildid"));
                Member member = guild.getMemberById(channelResult.getLong("userid"));
                Message message = guild.getTextChannelById(VoidBot.getMySQL().getVerificationValue(guild, "channelid")).getMessageById(channelResult.getString("messageid")).complete();
                new VerifyUser(member, message);
            }
        } catch (SQLException e) {
            Logger.error("Could not load verifykicks!");
            Logger.error(e);
        }
    }
}
