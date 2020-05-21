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
package net.romvoid.discord.commands.botowner;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandGuildData.
 */
public class CommandGuildData extends CommandHandler {

    /**
     * Instantiates a new command guild data.
     */
    public CommandGuildData() {
        super(new String[]{"guild-data"}, CommandCategory.BOT_OWNER, new PermsNeeded("command.guilddata", true, false), "Saves guild data in a file. Only works on TheVoid Server.", "");
    }

    /**
     * Execute.
     *
     * @param parsedCommandInvocation the parsed command invocation
     * @param userPermissions the user permissions
     * @return the message
     */
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getGuild().getIdLong() != 538530739017220107L)
            return null;
        new Thread(() -> {
            StringBuilder builder = new StringBuilder();
            for (Guild guild : VoidBot.getJDA().getGuilds()) {
                builder.append(guild.getName() + "(" + guild.getId() + ") [" + guild.getMembers().size() + "]\n");
            }
            File tempFile = new File("data/temp", "guilddata.txt");
            try {
                tempFile.createNewFile();
                FileWriter writer = new FileWriter(tempFile);
                writer.write(builder.toString());
                writer.flush();
                writer.close();
                parsedCommandInvocation.getTextChannel().sendFile(tempFile).complete().delete().queueAfter(20, TimeUnit.SECONDS);
                tempFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        return null;
    }
}
