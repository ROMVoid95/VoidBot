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
import net.romvoid.discord.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// TODO: Auto-generated Javadoc
/**
 * The Class VerificationKickSQL.
 */
public class VerificationKickSQL implements DatabaseGenerator {

    /** The connection. */
    private Connection connection;
    
    /** The my SQL. */
    private MySQL mySQL;
    
    /** The user. */
    private User user;
    
    /** The guild. */
    @SuppressWarnings("unused")
    private Guild guild;

    /**
     * Uses for database generation.
     */
    public VerificationKickSQL() {
        this.mySQL = VoidBot.getMySQL();
        this.connection = MySQL.getCon();
    }

    /**
     * Instantiates a new verification kick SQL.
     *
     * @param user the user
     * @param guild the guild
     */
    public VerificationKickSQL(User user, Guild guild) {
        this.mySQL = VoidBot.getMySQL();
        this.connection = MySQL.getCon();
        this.user = user;
        this.guild = guild;

    }

    /**
     * Exist.
     *
     * @return true, if successful
     */
    public boolean exist() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM verifykicks WHERE userid = ?");
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
            PreparedStatement ps = connection.prepareStatement("UPDATE verifykicks SET " + type + " = '" + value + "' WHERE userid = ?");
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
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM verifykicks WHERE `userid` = ?");
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
     * Creates the table if not exist.
     */
    @Override
    public void createTableIfNotExist() {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                    "`verifykicks` " +
                    "( `id` INT NOT NULL AUTO_INCREMENT ," +
                    " `guildid` TEXT NOT NULL ," +
                    " `userid` TEXT NOT NULL ," +
                    " `kickText` TEXT NOT NULL ," +
                    " `kickTime` TEXT NOT NULL," +
                    " `message` TEXT NOT NULL, " +
                    " PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
}
