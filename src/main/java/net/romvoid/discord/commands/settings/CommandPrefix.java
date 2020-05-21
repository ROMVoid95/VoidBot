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
package net.romvoid.discord.commands.settings;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;

import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandPrefix.
 */
public class CommandPrefix extends CommandHandler {
    
    /**
     * Instantiates a new command prefix.
     */
    public CommandPrefix() {
        super(new String[]{"prefix", "pr"}, CommandCategory.SETTINGS,
                new PermsNeeded("command.prefix", false, false),
                "Set the Server Prefix!", "<prefix>");
    }

    /**
     * Execute.
     *
     * @param p the p
     * @param userPermissions the user permissions
     * @return the message
     */
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation p, UserPermissions userPermissions) {
        if (p.getArgs().length <= 1) {
            MessageChannel ch = p.getMessage().getTextChannel();

            if (p.getArgs().length == 0) {
                VoidBot.getMySQL().updateGuildValue(p.getMessage().getGuild(), "prefix", ">>");
                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor("Prefix updated", null, p.getMessage().getGuild().getIconUrl());
                builder.setDescription(":white_check_mark: Successfully changed prefix to `>>`");
                ch.sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            } else {
                VoidBot.getMySQL().updateGuildValue(p.getMessage().getGuild(), "prefix", p.getArgs()[0]);
                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor("Prefix updated", null, p.getMessage().getGuild().getIconUrl());
                builder.setDescription(":white_check_mark: Successfully changed prefix to `" + p.getArgs()[0] + "`");
                ch.sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            }
        } else {
            return createHelpMessage();
        }

        return null;
    }
}
