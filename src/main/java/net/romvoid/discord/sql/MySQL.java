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
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.commands.admin.CommandVerification;
import net.romvoid.discord.util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class MySQL.
 */
public class MySQL {

    /** The connection. */
    private static Connection connection;
    
    /** The host. */
    private String host;
    
    /** The port. */
    private String port;
    
    /** The user. */
    private String user;
    
    /** The password. */
    private String password;
    
    /** The database. */
    private String database;

    /**
     * Instantiates a new my SQL.
     *
     * @param host     Host of MySQL server
     * @param port     Port of MySQL server
     * @param user     User of MySQL database
     * @param password Password of MySQL user
     * @param dbname   Name of MySQL database
     * @throws NullPointerException the null pointer exception
     */
    public MySQL(String host, String port, String user, String password, String dbname) throws NullPointerException {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = dbname;
    }

    /**
     * Connect.
     *
     * @return MySQL connection
     */
    public MySQL connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", this.user, this.password);
            Logger.info("MySQL connection success");
        } catch (SQLException e) {
            Logger.error(e);
            Logger.error("MySQL connection failed");
            Logger.info("Shutdown application...");
            System.exit(1);
        }
        return this;
    }

    /**
     * Disconnect.
     *
     * @return the my SQL
     */
    public MySQL disconnect() {
        try {
            connection.close();
            System.out.println("disconnected from MYSQL");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Gets the con.
     *
     * @return the con
     */
    public static Connection getCon() {
        return connection;
    }

    /**
     * Gets the string.
     *
     * @param table the table
     * @param key the key
     * @param where the where
     * @param wherevalue the wherevalue
     * @return Value of the given key
     */
    public String getString(String table, String key, String where, String wherevalue) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM ? WHERE ?=?");
            ps.setString(1, table);
            ps.setString(2, where);
            ps.setString(3, wherevalue);
            ResultSet rs = ps.executeQuery();
            // Only returning one result
            if (rs.next())
                return rs.getString(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the string.
     *
     * @param table      Tablename
     * @param key        column name
     * @param value      value
     * @param where the where
     * @param wherevalue the wherevalue
     * @return null
     */
    public MySQL setString(String table, String key, String value, String where, String wherevalue) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE ? SET ?=? WHERE ?=?");
            ps.setString(1, table);
            ps.setString(2, key);
            ps.setString(3, value);
            ps.setString(4, where);
            ps.setString(5, wherevalue);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Execute prepared statement.
     *
     * @param ps the ps
     * @return the my SQL
     */
    public MySQL executePreparedStatement(PreparedStatement ps) {
        try {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Execute prepared statements.
     *
     * @param statements the statements
     * @return the my SQL
     */
    public MySQL executePreparedStatements(PreparedStatement... statements) {
        for (PreparedStatement statement : statements) {
            try {
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * If role exist.
     *
     * @param role the role
     * @return true, if successful
     */
    //Role Stuff
    public boolean ifRoleExist(Role role) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM roles WHERE roleid = ?");
            ps.setString(1, role.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update role value.
     *
     * @param role the role
     * @param type the type
     * @param value the value
     * @return the my SQL
     */
    public MySQL updateRoleValue(Role role, String type, String value) {
        try {
            if (connection.isClosed())
                connect();
            if (!ifRoleExist(role))
                createRole(role);
            PreparedStatement ps = connection.prepareStatement("UPDATE roles SET " + type + " = '" + value + "' WHERE role = '" + role.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Gets the role value.
     *
     * @param role the role
     * @param type the type
     * @return the role value
     */
    public String getRoleValue(Role role, String type) {
        createRoleIfNecessary(role);
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM roles WHERE `roleid` = ?");
            ps.setString(1, role.getId());
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
     * Creates a Role if it is not already in the database. Used to ensure data.
     *
     * @param role the Role to check and create.
     * @return the my SQL
     */
    private MySQL createRoleIfNecessary(Role role) {
        if (!ifRoleExist(role))
            createRole(role);
        return this;
    }

    /**
     * Creates the role.
     *
     * @param role the role
     * @return the my SQL
     */
    public MySQL createRole(Role role) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `roles`(`roleid`, `permissions`) VALUES (?, '')");
            ps.setString(1, String.valueOf(role.getId()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * If portal exist.
     *
     * @param guild the guild
     * @return true, if successful
     */
    //Portal Stuff
    public boolean ifPortalExist(Guild guild) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM portal WHERE guildid = ?");
            ps.setString(1, guild.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update portal value.
     *
     * @param guild the guild
     * @param type the type
     * @param value the value
     * @return the my SQL
     */
    public MySQL updatePortalValue(Guild guild, String type, String value) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("UPDATE portal SET " + type + " = '" + value + "' WHERE guildid = ?");
            ps.setString(1, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Gets the portal value.
     *
     * @param guild the guild
     * @param type the type
     * @return the portal value
     */
    public String getPortalValue(Guild guild, String type) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM portal WHERE `guildid` = ?");
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

    /**
     * Creates the portal.
     *
     * @param guild the guild
     * @param otherguild the otherguild
     * @param channel the channel
     * @return the my SQL
     */
    public MySQL createPortal(Guild guild, Guild otherguild, TextChannel channel) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `portal`(`guildid`, `partnerid`, `channelid`) VALUES (?, ?,?)");
            ps.setString(1, String.valueOf(guild.getId()));
            ps.setString(2, String.valueOf(otherguild.getId()));
            ps.setString(3, String.valueOf(channel.getId()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Delete portal.
     *
     * @param guild the guild
     * @return the my SQL
     */
    public MySQL deletePortal(Guild guild) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `portal` WHERE `guildid` = ?");
            ps.setString(1, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Gets the guilds by value.
     *
     * @param type the type
     * @param value the value
     * @return the guilds by value
     */
    //Guild Stuff
    public List<Guild> getGuildsByValue(String type, String value) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds where `" + type + "` = ?");
            ps.setString(1, value);
            List<Guild> guilds = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                guilds.add(VoidBot.getJDA().getGuildById(rs.getString("serverid")));
            }
            return guilds;
        } catch (SQLException ex) {
            Logger.error(ex);
        }
        return null;
    }

    /**
     * If guild exits.
     *
     * @param guild the guild
     * @return true, if successful
     */
    public boolean ifGuildExits(Guild guild) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE serverid =?");
            ps.setString(1, guild.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update guild value.
     *
     * @param guild the guild
     * @param type the type
     * @param value the value
     * @return the my SQL
     */
    public MySQL updateGuildValue(Guild guild, String type, String value) {
        try {
            if (connection.isClosed())
                connect();
            if (!ifGuildExits(guild))
                createGuildServer(guild);
            PreparedStatement ps = connection.prepareStatement("UPDATE guilds SET " + type + " = '" + value + "' WHERE serverid = ?");
            ps.setString(1, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Gets the guild value.
     *
     * @param guild the guild
     * @param type the type
     * @return the guild value
     */
    public String getGuildValue(Guild guild, String type) {
        createGuildIfNecessary(guild);
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE `serverid` = ?");
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

    /**
     * Creates a guild if it is not already in the database. Used to ensure data.
     *
     * @param guild the Guild to check and create.
     * @return the my SQL
     */
    private MySQL createGuildIfNecessary(Guild guild) {
        if (!ifGuildExits(guild))
            createGuildServer(guild);
        return this;
    }

    /**
     * Creates the guild server.
     *
     * @param guild the guild
     * @return the my SQL
     * @see GuildSQL
     */
    public MySQL createGuildServer(Guild guild) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `guilds`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, `logchannel`, `autorole`, `portal`, `welmsg`, `autochannels`, `blacklist`, `whitelist`) VALUES (?, '0', '>>', 'Welcome %user% on %guild%', 'Bye %user%', '0', '0', 'closed', '0', '', '', '')");
            ps.setString(1, String.valueOf(guild.getIdLong()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Creates the guild server.
     *
     * @param serverID the server ID
     * @return the my SQL
     */
    public MySQL createGuildServer(String serverID) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `guilds`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, `logchannel`, `autorole`, `portal`, `welmsg`, `autochannels`, `blacklist`) VALUES (?, '0', '>>', 'Welcome %user% on %guild%', 'Bye %user%', '0', '0', 'closed', '0', '0', '')");
            ps.setString(1, serverID);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Delete guild.
     *
     * @param guild the guild
     * @return the my SQL
     */
    public MySQL deleteGuild(Guild guild) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `guilds` WHERE `serverid` = ?");
            ps.setString(1, guild.getId());
            ps.execute();
            PreparedStatement ps2 = connection.prepareStatement("DELETE FROM `members` WHERE `serverid` = ?");
            ps2.setString(1, guild.getId());
            ps2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Delete guild.
     *
     * @param serverID the server ID
     * @return the my SQL
     */
    public MySQL deleteGuild(String serverID) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `guilds` WHERE `serverid` = ?");
            ps.setString(1, serverID);
            ps.execute();
            PreparedStatement ps2 = connection.prepareStatement("DELETE FROM `members` WHERE `serverid` = ?");
            ps2.setString(1, serverID);
            ps2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Delete guild verification.
     *
     * @param g the g
     * @return the my SQL
     */
    public MySQL deleteGuildVerification(Guild g) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `verifications` WHERE `guildid` =?");
            ps.setString(1, g.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Gets the verification value.
     *
     * @param g the g
     * @param key the key
     * @return the verification value
     */
    public String getVerificationValue(Guild g, String key) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `verifications` WHERE `guildid` = ?");
            ps.setString(1, g.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates the verification.
     *
     * @param settings the settings
     * @return the my SQL
     */
    public MySQL createVerification(CommandVerification.VerificationSettings settings) {
        String kicktext = "0";
        if (settings.kicktext != null)
            kicktext = settings.kicktext;
        String emote;
        if (settings.emote.getId() != null)
            emote = settings.emote.getId();
        else
            emote = settings.emote.getName();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `verifications` (`guildid`, `channelid`, `roleid`, `text`, `verifiedtext`, `kicktime`, `kicktext`, `emote`) VALUES ( ?, ?, ?,?,?,?,?,?);");
            ps.setString(1, settings.channel.getGuild().getId());
            ps.setString(2, settings.channel.getId());
            ps.setString(3, settings.role.getId());
            ps.setString(4, settings.verifytext);
            ps.setString(5, settings.verifiedtext);
            ps.setString(6, String.valueOf(settings.kicktime));
            ps.setString(7, kicktext);
            ps.setString(8, emote);
            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Verification enabled.
     *
     * @param g the g
     * @return true, if successful
     */
    public boolean verificationEnabled(Guild g) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `verifications` WHERE `guildid` = ?");
            ps.setString(1, g.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if is whitelisted.
     *
     * @param channel the channel
     * @return true, if is whitelisted
     */
    public boolean isWhitelisted(TextChannel channel) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE `serverid` = ?");
            ps.setString(1, channel.getGuild().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString("blacklist").equals("") || rs.getString("blacklist").contains(channel.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {

        }
        return false;
    }

    /**
     * Checks if is channel whitelisted.
     *
     * @param channel the channel
     * @return true, if is channel whitelisted
     */
    public boolean isChannelWhitelisted(TextChannel channel) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE `serverid` = ?");
            ps.setString(1, channel.getGuild().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString("blacklist").contains(channel.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error(e);
        } catch (NullPointerException ignored) {

        }
        return false;
    }

    /**
     * Checks if is blacklisted.
     *
     * @param channel the channel
     * @return true, if is blacklisted
     */
    public boolean isBlacklisted(TextChannel channel) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE `serverid` = ?");
            ps.setString(1, channel.getGuild().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString("blacklist").contains(channel.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {

        }
        return false;
    }

    /**
     * Prepare statement.
     *
     * @param sql the sql
     * @return the prepared statement
     * @throws SQLException the SQL exception
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return MySQL.connection.prepareStatement(sql);
    }
}
