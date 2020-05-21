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

import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.EmbedUtil;
import net.romvoid.discord.util.GlobalBlacklist;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandGlobalBlacklist.
 */
public class CommandGlobalBlacklist extends CommandHandler {

    /**
     * Instantiates a new command global blacklist.
     */
    public CommandGlobalBlacklist() {
        super(new String[]{"globalblacklist", "gbl"}, CommandCategory.BOT_OWNER, new PermsNeeded("command.globalblacklist", true, false), "Ban a user from TheVoid.", "<@User>");
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
        if (parsedCommandInvocation.getMessage().getMentionedUsers().size() == 1) {
            return createHelpMessage();
        }
        switch (parsedCommandInvocation.getArgs()[0]) {
            case "add":
                GlobalBlacklist.addToBlacklist(parsedCommandInvocation.getMessage().getMentionedUsers().get(0));
                return EmbedUtil.message(EmbedUtil.success("Success!", "Successfuly added " + parsedCommandInvocation.getMessage().getMentionedUsers().get(0).getName() + " to global blacklist."));
            case "remove":
                GlobalBlacklist.removeFromBlacklist(parsedCommandInvocation.getMessage().getMentionedUsers().get(0));
                return EmbedUtil.message(EmbedUtil.success("Success!", "Successfuly removed " + parsedCommandInvocation.getMessage().getMentionedUsers().get(0).getName() + " from the global blacklist."));
        }
        return createHelpMessage();
    }
}
