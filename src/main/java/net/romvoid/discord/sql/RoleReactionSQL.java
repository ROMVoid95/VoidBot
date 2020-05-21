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
 * The Class RoleReactionSQL.
 */
public class RoleReactionSQL implements DatabaseGenerator {
	
	/** The guild. */
	private static Guild guild;
    
    /** The connection. */
    private static Connection connection;
    
    /** The my SQL. */
    private static MySQL mySQL;

    /**
     * Instantiates a new role reaction SQL.
     */
    public RoleReactionSQL() {
        RoleReactionSQL.mySQL = VoidBot.getMySQL();
        RoleReactionSQL.connection = MySQL.getCon();
    }
    
    /**
     * Gets the channel.
     *
     * @param type the type
     * @return the channel
     */
    public static String getChannel(String type) {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("SELECT" + type + " FROM rolereaction WHERE `guildid` = ?");
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
            PreparedStatement ps = connection.prepareStatement("UPDATE rolereaction SET " + type + "=? WHERE guildid=?");
            ps.setString(1, value);
            ps.setString(2, guild.getId());
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
                    "CREATE TABLE IF NOT EXISTS `rolereaction` (" +
                    "`channel` TEXT," +
                    "`message` TEXT," +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
}
