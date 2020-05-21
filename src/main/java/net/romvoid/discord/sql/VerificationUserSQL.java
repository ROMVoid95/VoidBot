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
package net.romvoid.discord.sql;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// TODO: Auto-generated Javadoc
/**
 * The Class VerificationUserSQL.
 */
public class VerificationUserSQL implements DatabaseGenerator {

    /** The connection. */
    private Connection connection;
    
    /** The my SQL. */
    private MySQL mySQL;
    
    /** The guild. */
    private Guild guild;
    
    /** The member. */
    private Member member;

    /**
     * Gets the guild.
     *
     * @return the guild
     */
    public Guild getGuild() {
        return guild;
    }

    /**
     * Gets the member.
     *
     * @return the member
     */
    public Member getMember() {
        return member;
    }

    /**
     * Instantiates a new verification user SQL.
     */
    public VerificationUserSQL() {
        this.mySQL = VoidBot.getMySQL();
        this.connection = MySQL.getCon();

    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public Message getMessage() {
        return guild.getTextChannelById(VoidBot.getMySQL().getVerificationValue(guild, "channelid")).getMessageById(Long.parseLong(this.get("messageid"))).complete();
    }

    /**
     * Instantiates a new verification user SQL.
     *
     * @param guild the guild
     * @param member the member
     */
    public VerificationUserSQL(Guild guild, Member member) {
        this.connection = MySQL.getCon();
        this.mySQL = VoidBot.getMySQL();
        this.guild = guild;
        this.member = member;
    }

    /**
     * From member.
     *
     * @param member the member
     * @return the verification user SQL
     */
    public static VerificationUserSQL fromMember(Member member) {

        return new VerificationUserSQL(member.getGuild(), member);
    }

    /**
     * Creates the table if not exist.
     */
    @Override
    public void createTableIfNotExist() {


        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                    "`verifyusers`" +
                    "(`id` INT NOT NULL AUTO_INCREMENT, " +
                    "`guildid` TEXT NOT NULL, " +
                    "`userid` TEXT NOT NULL, " +
                    "`messageid` TEXT NOT NULL," +
                    "PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    /**
     * Exist.
     *
     * @return true, if successful
     */
    public boolean exist() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM verifyusers WHERE userid = ?");
            ps.setString(1, this.getMember().getUser().getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Sets the.
     *
     * @param type the type
     * @param value the value
     */
    public void set(String type, String value) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE verifyusers SET " + type + " = '" + value + "' WHERE userid = ?");
            ps.setString(1, this.getMember().getUser().getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the.
     *
     * @param type the type
     * @return the string
     */
    public String get(String type) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM verifyusers WHERE `userid` = ?");
            ps.setString(1, this.getMember().getUser().getId());
            ResultSet rs = ps.executeQuery();
            // Only returning one result
            if (rs.next()) {
                return rs.getString(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Insert.
     *
     * @return true, if successful
     */
    public boolean insert() {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `verifyusers` (`guildid`, `userid`, `messageid`) VALUES (?,?,?)");
            ps.setLong(1, this.getGuild().getIdLong());
            ps.setLong(2, this.getMember().getUser().getIdLong());
            ps.setLong(3, this.getMessage().getIdLong());
            return true;
        } catch (SQLException e) {
            Logger.error(e);
            return false;
        }
    }
}
