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
import net.dv8tion.jda.core.entities.User;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// TODO: Auto-generated Javadoc
/**
 * The Class MemberSQL.
 *
 * @author ROMVoid
 */
public class MemberSQL implements DatabaseGenerator {

    /** The connection. */
    private Connection connection;
    
    /** The my SQL. */
    private MySQL mySQL;
    
    /** The member. */
    private Member member;
    
    /** The user. */
    private User user;

    /**
     * Uses for database generation.
     */
    public MemberSQL() {
        mySQL = VoidBot.getMySQL();
        connection = MySQL.getCon();
    }

    /**
     * User fromUser(User user, Guild guild) or fromMember(Member member) method.
     *
     * @param member the member
     * @see MemberSQL
     */
    public MemberSQL(Member member) {
        this.member = member;
        this.user = member.getUser();
        this.mySQL = VoidBot.getMySQL();
        this.connection = MySQL.getCon();

        create();
    }

    /**
     * Instantiates a new member SQL.
     *
     * @param member the member
     * @param mySQL the my SQL
     */
    private MemberSQL(Member member, MySQL mySQL) {
        this.member = member;
        this.user = member.getUser();
        this.mySQL = VoidBot.getMySQL();
        this.connection = MySQL.getCon();
    }

    /**
     * From member.
     *
     * @param member the member
     * @return the member SQL
     */
    public static MemberSQL fromMember(Member member) {
        return new MemberSQL(member, VoidBot.getMySQL());
    }

    /**
     * From user.
     *
     * @param user the user
     * @param guild the guild
     * @return the member SQL
     */
    public static MemberSQL fromUser(User user, Guild guild) {
        return new MemberSQL(guild.getMember(user), VoidBot.getMySQL());
    }


    /**
     * Exist.
     *
     * @return true, if successful
     */
    //User Stuff
    public boolean exist() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM members WHERE userid = ? AND serverid = ?");
            ps.setString(1, user.getId());
            ps.setString(2, member.getGuild().getId());
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
            if (!exist())
                create();
            PreparedStatement ps = connection.prepareStatement("UPDATE members SET " + type + " = '" + value + "' WHERE userid = ? AND serverid = ?");
            ps.setString(1, user.getId());
            ps.setString(2, member.getGuild().getId());
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
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM members WHERE userid = ? AND serverid = ?");
            ps.setString(1, user.getId());
            ps.setString(2, member.getGuild().getId());
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
     * Creates the.
     */
    public void create() {
        if (exist())
            return;
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO members(`id`, `userid`, `serverid`, `permissionlevel`, `level`, `points`) VALUES (0, ?, ?, ?, ?, ?)");
            ps.setString(1, user.getId());
            ps.setString(2, member.getGuild().getId());
            if (member.isOwner())
                ps.setString(3, "3");
            else
                ps.setString(3, "0");
            ps.setString(4, "0");
            ps.setString(5, "0");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the user SQL.
     *
     * @return the user SQL
     */
    public UserSQL getUserSQL() {
        return UserSQL.fromUser(this.user);
    }

    /**
     * Creates the table if not exist.
     */
    @Override
    public void createTableIfNotExist() {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("" +
                    "CREATE TABLE IF NOT EXISTS `members` (" +
                    "  `id` INT(250) NOT NULL AUTO_INCREMENT," +
                    "  `userid` VARCHAR(50) NOT NULL," +
                    "  `serverid` VARCHAR(50) NOT NULL," +
                    "  `permissionlevel` VARCHAR(50) NOT NULL," +
                    "  `level` VARCHAR(50) NOT NULL," +
                    "  `points` VARCHAR(50) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=3918 DEFAULT CHARSET=utf8;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
}
