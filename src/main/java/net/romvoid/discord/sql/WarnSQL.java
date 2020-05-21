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
import net.dv8tion.jda.core.entities.User;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.core.Warn;
import net.romvoid.discord.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class WarnSQL.
 *
 * @author ROMVoid
 */
public class WarnSQL implements DatabaseGenerator {

    /** The connection. */
    private Connection connection;
    
    /** The my SQL. */
    private MySQL mySQL;

    /**
     * Instantiates a new warn SQL.
     */
    public WarnSQL() {
        this.mySQL = VoidBot.getMySQL();
        this.connection = MySQL.getCon();
    }

    /**
     * Gets the warns.
     *
     * @param user the user
     * @param guild the guild
     * @return the warns
     */
    public List<Warn> getWarns(User user, Guild guild) {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM warns WHERE warnedUser = ? AND serverid=?");
            ps.setString(1, user.getId());
            ps.setString(2, guild.getId());
            ResultSet rs = ps.executeQuery();
            List<Warn> warns = new ArrayList<>();
            while (rs.next()) {
                warns.add(Warn.parseWarn(
                        rs.getString("id"),
                        rs.getString("warnedUser"),
                        rs.getString("serverid"),
                        rs.getString("executor"),
                        rs.getString("reason"),
                        rs.getString("date")));
            }
            return warns;
        } catch (SQLException e) {
            Logger.error(e);
        }
        return new ArrayList<>();
    }

    /**
     * Adds the warn.
     *
     * @param warn the warn
     */
    public void addWarn(Warn warn) {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO warns VALUES (0, ?, ?, ?, ?, ?)");
            ps.setString(1, warn.getWarnedUser().getId());
            ps.setString(2, warn.getGuild().getId());
            ps.setString(3, warn.getExecutor().getId());
            ps.setString(4, warn.getReason());
            ps.setString(5, String.valueOf(warn.getDate()));
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    /**
     * Delete warn.
     *
     * @param user the user
     * @param guild the guild
     * @param index the index
     */
    public void deleteWarn(User user, Guild guild, int index) {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM warns WHERE id=?");
            ps.setInt(1, getWarns(user, guild).get(index).getId());
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
                    "CREATE TABLE IF NOT EXISTS `warns` (" +
                    "  `id` INT(11) NOT NULL AUTO_INCREMENT," +
                    "  `warnedUser` VARCHAR(50) NOT NULL," +
                    "  `serverid` VARCHAR(50) NOT NULL," +
                    "  `executor` VARCHAR(50) NOT NULL," +
                    "  `reason` TEXT NOT NULL," +
                    "  `date` VARCHAR(100) NOT NULL," +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
}
