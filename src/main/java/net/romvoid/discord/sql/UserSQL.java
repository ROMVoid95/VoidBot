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
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class UserSQL.
 *
 * @author ROMVoid
 */
public class UserSQL implements DatabaseGenerator {

    /** The connection. */
    private Connection connection;
    
    /** The my SQL. */
    private MySQL mySQL;
    
    /** The user. */
    private User user;

    /**
     * Uses for database generation.
     */
    public UserSQL() {
        this.mySQL = VoidBot.getMySQL();
        this.connection = MySQL.getCon();
    }

    /**
     * Instantiates a new user SQL.
     *
     * @param user the user
     */
    public UserSQL(User user) {
        this.user = user;
        this.mySQL = VoidBot.getMySQL();
        this.connection = MySQL.getCon();

        create();
    }

    /**
     * Instantiates a new user SQL.
     *
     * @param user the user
     * @param mySQL the my SQL
     * @param connection the connection
     */
    private UserSQL(User user, MySQL mySQL, Connection connection) {
        this.user = user;
        this.mySQL = mySQL;
        this.connection = connection;
    }

    /**
     * From user.
     *
     * @param user the user
     * @return the user SQL
     */
    public static UserSQL fromUser(User user) {
        return new UserSQL(user, VoidBot.getMySQL(), MySQL.getCon());
    }

    /**
     * From member.
     *
     * @param member the member
     * @return the user SQL
     */
    public static UserSQL fromMember(Member member) {
        return fromUser(member.getUser());
    }


    /**
     * Exist.
     *
     * @return true, if successful
     */
    //User Stuff
    public boolean exist() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE userid = ?");
            ps.setString(1, user.getId());
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
            PreparedStatement ps = connection.prepareStatement("UPDATE users SET " + type + " = '" + value + "' WHERE userid = ?");
            ps.setString(1, user.getId());
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
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE `userid` = ?");
            ps.setString(1, user.getId());
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
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users(`id`, `userid`, `bio`, `money`, `premium`) VALUES (0, ?, ?, ?, ?)");
            ps.setString(1, user.getId());
            ps.setString(2, "No bio set.");
            ps.setString(3, "1000");
            ps.setString(4, "false");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if is premium.
     *
     * @return true, if is premium
     */
    public boolean isPremium() {
        String entry = get("premium");
        if (entry.equalsIgnoreCase("false")) {
            return false;
        }
        Date expiry = new Date(Long.parseLong(this.get("premium")));
        Date now = new Date();
        if (expiry.before(now)) {
            this.set("premium", "false");
            return false;
        }
        return true;
    }

    /**
     * Gets the premium expiry date.
     *
     * @return the premium expiry date
     */
    public Date getPremiumExpiryDate() {
        if (!this.isPremium())
            return null;
        return new Date(Long.parseLong(this.get("premium")));
    }

    /**
     * Format expiry date.
     *
     * @return the string
     */
    public String formatExpiryDate() {
        if (!this.isPremium())
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(this.getPremiumExpiryDate());
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public User getUser() {
        return VoidBot.getJDA().getUserById(this.get("userid"));
    }

    /**
     * Gets the member.
     *
     * @param guild the guild
     * @return the member
     */
    public Member getMember(Guild guild) {
        return guild.getMember(this.getUser());
    }

    /**
     * Gets the member SQL.
     *
     * @param guild the guild
     * @return the member SQL
     */
    public MemberSQL getMemberSQL(Guild guild) {
        return MemberSQL.fromUser(this.user, guild);
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
                    "CREATE TABLE IF NOT EXISTS `users` (" +
                    "  `id` INT(250) NOT NULL AUTO_INCREMENT," +
                    "  `userid` VARCHAR(50) NOT NULL," +
                    "  `bio` TEXT NOT NULL," +
                    "  `money` VARCHAR(250)," +
                    "  `premium` VARCHAR(50) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=3918 DEFAULT CHARSET=utf8;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }


}
