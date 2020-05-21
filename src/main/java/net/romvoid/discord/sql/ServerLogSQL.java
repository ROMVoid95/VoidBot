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
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// TODO: Auto-generated Javadoc
/**
 * The Class ServerLogSQL.
 *
 * @author ROMVoid
 */
public class ServerLogSQL implements DatabaseGenerator {
    
    /** The guild. */
    private Guild guild;
    
    /** The connection. */
    private Connection connection;
    
    /** The my SQL. */
    private MySQL mySQL;

    /**
     * Uses for database generation.
     */
    public ServerLogSQL() {
        this.mySQL = VoidBot.getMySQL();
        this.connection = MySQL.getCon();
    }

    /**
     * Instantiates a new server log SQL.
     *
     * @param guild the guild
     */
    public ServerLogSQL(Guild guild) {
        this.guild = guild;
        this.mySQL = VoidBot.getMySQL();
        this.connection = MySQL.getCon();

        create();
    }

    /**
     * Gets the.
     *
     * @param type the type
     * @return the string
     */
    public String get(String type) {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM serverlog WHERE `guildid` = ?");
            ps.setString(1, guild.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(type);
            }
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }

    /**
     * Sets the.
     *
     * @param type the type
     * @param value the value
     */
    public void set(String type, String value) {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("UPDATE serverlog SET " + type + "=? WHERE guildid=?");
            ps.setString(1, value);
            ps.setString(2, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    /**
     * Creates the.
     */
    public void create() {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM serverlog WHERE guildid = ?");
            checkStatement.setString(1, guild.getId());
            ResultSet checkResult = checkStatement.executeQuery();
            if (checkResult.next())
                return;
            PreparedStatement ps = connection.prepareStatement("INSERT INTO serverlog (guildid, channel, ev_join, ev_leave, ev_command, ev_ban, ev_voice, ev_role) VALUES (?, '0', 'false', 'false', 'false', 'false', 'false', 'false')");
            ps.setString(1, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
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
                    "CREATE TABLE IF NOT EXISTS `serverlog` (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `guildid` varchar(50) NOT NULL,\n" +
                    "  `channel` varchar(50) NOT NULL,\n" +
                    "  `ev_join` varchar(50) NOT NULL,\n" +
                    "  `ev_leave` varchar(50) NOT NULL,\n" +
                    "  `ev_command` varchar(50) NOT NULL,\n" +
                    "  `ev_ban` varchar(50) NOT NULL,\n" +
                    "  `ev_voice` varchar(50) NOT NULL,\n" +
                    "  `ev_role` varchar(50) NOT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
}
