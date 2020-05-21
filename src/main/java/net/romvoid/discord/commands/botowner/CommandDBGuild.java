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

import static net.romvoid.discord.util.EmbedUtil.*;

import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandDBGuild.
 */
public class CommandDBGuild extends CommandHandler {
    
    /**
     * Instantiates a new command DB guild.
     */
    public CommandDBGuild() {
        super(new String[]{"dbguild", "dbguilds"}, CommandCategory.BOT_OWNER,
                new PermsNeeded("command.dbguild", true, false),
                "Manage database guild entries.", "<add | remove | default> <ServerID>");
    }

    /**
     * Execute.
     *
     * @param parsedCommandInvocation the parsed command invocation
     * @param userPermissions the user permissions
     * @return the message
     */
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length == 2) {
            String option = parsedCommandInvocation.getArgs()[0];
            String serverID = parsedCommandInvocation.getArgs()[1];

            switch (option) {
                case "default":
                    VoidBot.getMySQL().deleteGuild(serverID);
                    VoidBot.getMySQL().createGuildServer(serverID);
                    return message(success("Server set to default", "The Server with the ID " + serverID + " has been set to default."));
                case "add":
                    VoidBot.getMySQL().createGuildServer(serverID);
                    return message(success("Server added", "The Server with the ID " + serverID + " has been added successfully."));
                case "remove":
                    VoidBot.getMySQL().deleteGuild(serverID);
                    return message(success("Server removed", "The Server with the ID " + serverID + " has been removed successfully."));
                default:
                    return message(error("Invalid parameter", option + " is not an valid parameter."));
            }
        } else {
            return message(error("Not enough arguments", "You forgot to add the option or server's ID."));
        }
    }

}
