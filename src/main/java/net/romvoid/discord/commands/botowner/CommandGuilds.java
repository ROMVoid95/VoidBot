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

import static net.romvoid.discord.util.EmbedUtil.info;
import static net.romvoid.discord.util.EmbedUtil.message;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandGuilds.
 */
public class CommandGuilds extends CommandHandler {
    
    /**
     * Instantiates a new command guilds.
     */
    public CommandGuilds() {
        super(new String[]{"guilds"}, CommandCategory.BOT_OWNER,
                new PermsNeeded("command.guilds", true, false),
                "Shows all Guilds the Bot is running on!", "guilds");
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
        StringBuilder runningOnServers = new StringBuilder();
        int count_server = 1;

        List<Guild> guild_sublist;
        int SideNumbInput = 1;
        if (parsedCommandInvocation.getArgs().length > 0) {
            SideNumbInput = Integer.parseInt(parsedCommandInvocation.getArgs()[0]);
            System.out.println(SideNumbInput);
        }

        if (VoidBot.getJDA().getGuilds().size() > 20) {
            guild_sublist = VoidBot.getJDA().getGuilds().subList((SideNumbInput - 1) * 20, (SideNumbInput - 1) * 20 + 20);
        } else {
            guild_sublist = VoidBot.getJDA().getGuilds();
        }


        int sideNumbAll;
        if (VoidBot.getJDA().getGuilds().size() >= 20) {
            for (Guild guild : guild_sublist) {
                runningOnServers.append("`\t " + (((SideNumbInput - 1) * 20) + count_server) + ". ").append(guild.getName()).append("(").append(guild.getId()).append(")`\n");
                count_server++;
            }
            sideNumbAll = VoidBot.getJDA().getGuilds().size() / 20;
        } else {
            for (Guild guild : guild_sublist) {
                runningOnServers.append("`\t " + count_server + ". ").append(guild.getName()).append("(").append(guild.getId()).append(")`\n");
                count_server++;
            }
            sideNumbAll = 1;
        }
        int sideNumb = SideNumbInput;
        return message(info("TheVoid running on following guilds", "`Total guilds: " + VoidBot.getJDA().getGuilds().size() + " - Side " + sideNumb + " / " + sideNumbAll + "`\n\n" + runningOnServers.toString()));
    }
}
