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
import java.sql.SQLException;

import net.romvoid.discord.VoidBot;
import net.romvoid.discord.util.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class MinecraftSQL.
 */
public class MinecraftSQL implements DatabaseGenerator {

    /** The connection. */
    private Connection connection;
    
    /** The my SQL. */
    private MySQL mySQL;

    /**
     * Instantiates a new minecraft SQL.
     */
    public MinecraftSQL() {
        this.mySQL = VoidBot.getMySQL();
        this.connection = MySQL.getCon();
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
                    "CREATE TABLE IF NOT EXISTS `mincraft` (" +
                    "`id` INT(250) NOT NULL AUTO_INCREMENT," +
                    "`uuid` TEXT," +
                    "`playername` TEXT," +
                    "`awaitingaprooval` TEXT, " +
                    " PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=3918 DEFAULT CHARSET=utf8;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
}
