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
package net.romvoid.discord.commands.general;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.Colors;
import net.romvoid.discord.util.Info;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandInvite.
 */
public class CommandInvite extends CommandHandler {

    /**
     * Instantiates a new command invite.
     */
    public CommandInvite() {
        super(new String[]{"invite", "inv"}, CommandCategory.GENERAL, new PermsNeeded("command.invite", false, true), "Gives you the invite-link of the bot.", "");
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
    	
    	String invite = VoidBot.getJDA().asBot().getInviteUrl(Permission.getPermissions(2080898295L));
    	
    	System.out.println(invite);
    	
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_SECONDARY);
        builder.setAuthor(Info.BOT_NAME + " - Invite", null, parsedCommandInvocation.getMessage().getJDA().getSelfUser().getAvatarUrl());
        builder.setDescription("[Invite TheVoid](https://discordapp.com/oauth2/authorize?client_id=528635696001318928&permissions=2080898295&scope=bot)\n" +
                "[Join Support Server](https://discord.gg/UrHvXY9)");
        return new MessageBuilder().setEmbed(builder.build()).build();
    }


}
