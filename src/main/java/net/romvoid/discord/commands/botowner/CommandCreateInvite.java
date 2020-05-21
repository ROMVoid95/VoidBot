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

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.EmbedUtil;
import net.romvoid.discord.util.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandCreateInvite.
 */
public class CommandCreateInvite extends CommandHandler {

    /**
     * Instantiates a new command create invite.
     */
    public CommandCreateInvite() {
        super(new String[]{"createinvite", "minv", "createinv", "newinv"}, CommandCategory.BOT_OWNER, new PermsNeeded("command.createinvite", true, false), "Creates an invite", "<guildid>");
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
        try {
            return new MessageBuilder().append(parsedCommandInvocation.getMessage().getJDA().getGuildById(parsedCommandInvocation.getArgs()[0]).getTextChannels().get(0).createInvite().complete().getURL()).build();
        } catch (Exception ex) {
            Logger.info("Create Invite: " + ex.getMessage());
            return new MessageBuilder().setEmbed(EmbedUtil.error("Error!", "An error occurred!").build()).build();
        }
    }
}