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
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.sql.MySQL;
import net.romvoid.discord.sql.VerificationKickSQL;
import net.romvoid.discord.util.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

// TODO: Auto-generated Javadoc
/**
 * The Class VerificationKickHandler.
 */
public class VerificationKickHandler {

    /** The verify kicks. */
    static Map<Member, VerifyKick> verifyKicks = new HashMap<>();


    /**
     * The Class VerifyKick.
     */
    public static class VerifyKick {
        
        /** The guildid. */
        private final long guildid;
        
        /** The userid. */
        private final long userid;
        
        /** The kick text. */
        private final String kickText;
        
        /** The kick date. */
        private final long kickDate;
        
        /** The message id. */
        private final long messageId;
        
        /** The silent. */
        @SuppressWarnings("unused")
        private final boolean silent;
        
        /** The save. */
        @SuppressWarnings("unused")
        private final boolean save;
        
        /** The resolve task. */
        private final TimerTask resolveTask = new TimerTask() {
            @Override
            public void run() {
                schedule();
            }
        };

        /**
         * Instantiates a new verify kick.
         *
         * @param guild the guild
         * @param user the user
         * @param kickDate the kick date
         * @param kicktext the kicktext
         * @param messageid the messageid
         * @param silent the silent
         * @param save the save
         */
        public VerifyKick(Guild guild, Member user, Date kickDate, String kicktext, long messageid, boolean silent, boolean save) {
            this.guildid = guild.getIdLong();
            this.userid = user.getUser().getIdLong();
            this.messageId = messageid;
            this.kickDate = kickDate.getTime();
            this.kickText = kicktext;
            this.silent = silent;
            this.save = save;
            if (user.getUser().isBot())
                return;

            if (save)
                this.save();

            if (silent) return;
            Date now = new Date();
            verifyKicks.put(user, this);
            if (now.after(kickDate)) {
                this.schedule();
            } else {
                VoidBot.getTimer().schedule(resolveTask, new Date(this.kickDate));
            }
            //System.out.println(new SimpleDateFormat("HH:mm").format(this.kickDate));
        }

        /**
         * From member.
         *
         * @param member the member
         * @param silent the silent
         * @return the verify kick
         */
        public static VerifyKick fromMember(Member member, boolean silent) {
            return verifyKicks.get(member);
        }

        /**
         * Schedule.
         */
        private void schedule() {
            if (!verifyKicks.containsValue(this)) return;
            Guild guild = VoidBot.getJDA().getGuildById(this.guildid);
            Member member = guild.getMemberById(this.userid);
            if (member.getUser().isBot()) {
                verifyKicks.remove(this);
                return;
            }
            if (guild.getSelfMember().canInteract(member)) {
                member.getUser().openPrivateChannel().queue(c -> c.sendMessage(this.kickText.replace("%invite%", guild.getTextChannelById(VoidBot.getMySQL().getVerificationValue(guild, "channelid")).createInvite().setMaxUses(1).complete().getURL())).queue());
                guild.getController().kick(member).reason(this.kickText).queue();
            }
            VerificationKickSQL sql = new VerificationKickSQL(member.getUser(), member.getGuild());
            VoidBot.getJDA().getGuildById(guildid).getTextChannelById(VoidBot.getMySQL().getVerificationValue(guild, "channelid")).getMessageById(Long.parseLong(sql.get("message"))).complete().delete().queue();
            remove();
            verifyKicks.remove(this);
        }

        /**
         * Save.
         *
         * @return true, if successful
         */
        boolean save() {
            try {
                PreparedStatement saveStatement = MySQL.getCon().prepareStatement("INSERT INTO `verifykicks` (`guildid`,`userid`, `kickText`, `kicktime`, `message`) VALUES (?,?,?,?,?)");
                saveStatement.setLong(1, this.guildid);
                saveStatement.setLong(2, this.userid);
                saveStatement.setString(3, this.kickText);
                saveStatement.setLong(4, this.kickDate);
                saveStatement.setLong(5, this.messageId);
                saveStatement.execute();
            } catch (SQLException e) {
                Logger.error(e);
                return false;
            }
            return true;
        }

        /**
         * Removes the.
         *
         * @return true, if successful
         */
        public boolean remove() {
            try {
                PreparedStatement deleteStatement = MySQL.getCon().prepareStatement("DELETE FROM `verifykicks` WHERE `userid` = ? AND `guildid` = ?");
                deleteStatement.setLong(1, this.userid);
                deleteStatement.setLong(2, this.guildid);
                deleteStatement.execute();
                PreparedStatement deleteStatement2 = MySQL.getCon().prepareStatement("DELETE FROM `verifyusers` WHERE `userid` = ? AND `guildid` = ?");
                deleteStatement2.setLong(1, this.userid);
                deleteStatement2.setLong(2, this.guildid);
                deleteStatement2.execute();
                verifyKicks.remove(VoidBot.getJDA().getGuildById(this.guildid).getMemberById(this.userid));
            } catch (SQLException e) {
                Logger.error(e);
                return false;
            }
            return true;
        }

        /**
         * Exists.
         *
         * @return true, if successful
         */
        public boolean exists() {
            try {
                PreparedStatement ps = MySQL.getCon().prepareStatement("SELECT * FROM `verifykicks` WHERE `userid` = ? AND `guildidd` = ?");
                ps.setLong(1, this.userid);
                ps.setLong(2, this.guildid);
                ResultSet rs = ps.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                Logger.error(e);
                return false;
            }
        }

        /**
         * Exists.
         *
         * @param member the member
         * @return true, if successful
         */
        public static boolean exists(Member member) {
            try {
                PreparedStatement ps = MySQL.getCon().prepareStatement("SELECT * FROM `verifykicks` WHERE `userid` = ? AND `guildid` = ?");
                ps.setLong(1, member.getUser().getIdLong());
                ps.setLong(2, member.getGuild().getIdLong());
                ResultSet rs = ps.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                Logger.error(e);
                return false;
            }
        }

        /**
         * Gets the message id.
         *
         * @return the message id
         */
        public long getMessageId() {
            return messageId;
        }
    }

    /**
     * Load verify kicks.
     */
    public static void loadVerifyKicks() {
        try {
            PreparedStatement selectStatement = MySQL.getCon()
                    .prepareStatement("SELECT * FROM `verifykicks` ");
            ResultSet channelResult = selectStatement.executeQuery();
            while (channelResult.next()) {
                Guild guild = VoidBot.getJDA().getGuildById(channelResult.getString("guildid"));
                Member member = guild.getMember(VoidBot.getJDA().getUserById(channelResult.getString("userid")));
                Date date = new Date(Long.parseLong(channelResult.getString("kicktime")));
                String text = channelResult.getString("kickText");
                long messageId = Long.parseLong(channelResult.getString("message"));
                if (!member.getUser().isBot())
                    new VerifyKick(guild, member, date, text, messageId, false, false);

            }
        } catch (SQLException | NullPointerException e) {
            Logger.error("Could not load verifykicks.");
            Logger.error(e);
        }
    }
}
