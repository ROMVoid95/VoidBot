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
package net.romvoid.discord.util;

import java.util.Date;

import net.romvoid.discord.VoidBot;

// TODO: Auto-generated Javadoc
/**
 * The Class Info.
 */
public class Info {

    /** The Constant BOT_DEFAULT_PREFIX. */
    public final static String BOT_DEFAULT_PREFIX = "--";
    
    /** The Constant BOT_NAME. */
    public final static String BOT_NAME = "TheVoidBot";
    
    /** The Constant BOT_VERSION. */
    public final static String BOT_VERSION = "0.2.2";
    
    /** The Constant BOT_WEBSITE. */
    public final static String BOT_WEBSITE = "https://thevoidbot.com";
    
    /** The Constant BOT_GITHUB. */
    public final static String BOT_GITHUB = "https://github.com/ROMVoid95/TheVoidBot";
    
    /** The Constant COMMUNITY_SERVER. */
    public final static String COMMUNITY_SERVER = "538530739017220107";
    
    /** The Constant COMMUNITY_STAFF_ROLE. */
    public final static String COMMUNITY_STAFF_ROLE = "540793542675529728";
    
    /** The Constant PREMIUM_ROLE. */
    public final static String PREMIUM_ROLE = "540834946831351808";
    
    /** The Constant CONFIG_FILE. */
    public final static String CONFIG_FILE = "config.json";

    /** The Constant GITHUB_TOKEN. */
    public static final String GITHUB_TOKEN = VoidBot.getConfiguration().getString("git_token");
    
    /** The last restart. */
    public static Date lastRestart;

    /**
     * Bot author long ids.
     */
    public final static Long[] BOT_AUTHOR_IDS = {
            393847930039173131L  // ROMVoid
    };

    /** The Constant MYSQL_HOST. */
    /* MySQL login */
    public final static String MYSQL_HOST = VoidBot.getConfiguration().getString("mysql_host");
    
    /** The Constant MYSQL_PORT. */
    public final static String MYSQL_PORT = VoidBot.getConfiguration().getString("mysql_port");
    
    /** The Constant MYSQL_USER. */
    public final static String MYSQL_USER = VoidBot.getConfiguration().getString("mysql_user");
    
    /** The Constant MYSQL_PASSWORD. */
    public final static String MYSQL_PASSWORD = VoidBot.getConfiguration().getString("mysql_password");
    
    /** The Constant MYSQL_DATABASE. */
    public final static String MYSQL_DATABASE = VoidBot.getConfiguration().getString("mysql_database");
}
