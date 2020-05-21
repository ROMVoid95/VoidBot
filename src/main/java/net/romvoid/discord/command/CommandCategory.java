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
package net.romvoid.discord.command;

// TODO: Auto-generated Javadoc
/**
 * The Enum CommandCategory.
 */
public enum CommandCategory {

    /** The test. */
    TEST("test", "Test"),
    
    /** The general. */
    GENERAL("general", "General"),
    
    /** The moderation. */
    MODERATION("mod", "Moderation"),
    
    /** The admin. */
    ADMIN("admin", "Admin"),
    
    /** The guild owner. */
    GUILD_OWNER("guildOwner", "Server Owner"),
    
    /** The bot owner. */
    BOT_OWNER("botOwner", "Bot Owner"),
    
    /** The tools. */
    TOOLS("tools", "Tools"),
    
    /** The settings. */
    SETTINGS("settings", "Settings");


    /** The id. */
    private String id;
    
    /** The displayname. */
    private String displayname;

    /**
     * Instantiates a new command category.
     *
     * @param id the id
     * @param displayname the displayname
     */
    CommandCategory(String id, String displayname) {
        this.id = id;
        this.displayname = displayname;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the displayname.
     *
     * @return the displayname
     */
    public String getDisplayname() {
        return displayname;
    }
}