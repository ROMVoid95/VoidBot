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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.dv8tion.jda.core.entities.Guild;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.util.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class GuildSQL.
 */
public class GuildSQL implements DatabaseGenerator {

    /** The connection. */
    private Connection connection;
    
    /** The my SQL. */
    private MySQL mySQL;
    
    /** The guild. */
    private Guild guild;

    /**
     * Creates the table if not exist.
     */
    @Override
    public void createTableIfNotExist() {
        try {
            if (connection.isClosed())
                mySQL.connect();     
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `guilds`" +
                    "(`serverid` VARCHAR(100) , " +
                    "`prefix` VARCHAR (25)," +
                    "`joinmsg` TEXT," +
                    "`leavemsg` TEXT," +
                    "`channel` TEXT," +
                    "`logchannel` TEXT," +
                    "`autorole` TEXT," +
                    "`portal` VARCHAR (250)," +
                    "`welmsg` TEXT," +
                    "`autochannels` VARCHAR (250)," +
                    "`cases` INT (11)," +
                    "`blacklist` TEXT," +
                    "`whitelist` TEXT);");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
    
    /**
     * Instantiates a new guild SQL.
     */
    public GuildSQL() {
        mySQL = VoidBot.getMySQL();
        connection = MySQL.getCon();
    }

    /**
     * Instantiates a new guild SQL.
     *
     * @param guild the guild
     * @param mySQL the my SQL
     */
    private GuildSQL(Guild guild, MySQL mySQL) {
        this.guild = guild;
        this.mySQL = VoidBot.getMySQL();
        this.connection = MySQL.getCon();
    }

    /**
     * From guild.
     *
     * @param guild the guild
     * @return the guild SQL
     */
    public static GuildSQL fromGuild(Guild guild) {
        return new GuildSQL(guild, VoidBot.getMySQL());
    }


    /**
     * Exist.
     *
     * @return true, if successful
     */
    public boolean exist() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE serverid = ?");
            ps.setString(1, guild.getId());
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
            PreparedStatement ps = connection.prepareStatement("UPDATE guilds SET " + type + " = '" + value + "' WHERE serverid = ?");
            ps.setString(1, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the.
     */
    private void create() {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `guilds`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, `logchannel`, `autorole`, `portal`, `welmsg`, `autochannels`, `blacklist`,`lvlmsg`, `whitelist`) VALUES (?, '0', '>>', 'Welcome %user% on %guild%', 'Bye %user%', '0', '0', 'closed', '0', '', '', '')");
            ps.setString(1, String.valueOf(guild.getIdLong()));
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
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE serverid = ?");
            ps.setString(1, guild.getId());
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

//    public boolean enabledWhitelist() {
//        return !get("whitelist").equals("");
//    }
//
//    public boolean enabledBlacklist() {
//        return !get("blacklist").equals("");
//    }
//
//    public boolean isBlacklisted(TextChannel channel) {
//        return get("blacklist").contains(channel.getId());
//    }
//
//    public boolean isWhitelisted(TextChannel channel) {
//        return get("whitelist").contains(channel.getId());
//    }

}