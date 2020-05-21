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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
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
 * The Class CommandInfo.
 */
public class CommandInfo extends CommandHandler {

    /** The arr supporter. */
    private String[] arrSupporter = {"Greatly Needed"};

    /**
     * Instantiates a new command info.
     */
    public CommandInfo() {
        super(new String[]{"info", "inf", "version"}, CommandCategory.GENERAL, new PermsNeeded("command.info", false, true), "Shows some information about the bot!", "");
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
        //Set some Vars
        Message message = parsedCommandInvocation.getMessage();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_PRIMARY);
        builder.setAuthor(Info.BOT_NAME + " - Information", message.getJDA().getSelfUser().getEffectiveAvatarUrl());
        StringBuilder authors = new StringBuilder();

        //Append on StringBuilder
        for (long authorId : Info.BOT_AUTHOR_IDS) {
            User authorUser = VoidBot.getJDA().getUserById(authorId);
            if (authorUser == null)
                authors.append(authorId).append("\n");
            else
                authors.append(authorUser.getName()).append("#").append(authorUser.getDiscriminator()).append("\n");
        }
        //Set the Embed Values
        builder.addField("Bot Name", Info.BOT_NAME, true);
        builder.addField("Bot Version", Info.BOT_VERSION, true);
        builder.addField("Website", "[Link](" + Info.BOT_WEBSITE + ")", true);
        builder.addField("Bot Invite", "[Invite TheVoid](https://discordapp.com/api/oauth2/authorize?client_id=559722702248869903&permissions=2080763121&scope=bot)", true);
        builder.addField("Github Link", "[Github Link](" + Info.BOT_GITHUB + ")", true);
        builder.addField("Patreon Link", "[TheVoid Dev](https://www.patreon.com/romvoid)", true);
        builder.addField("Support Server", "[Link](https://discord.gg/Bsd5hCM)", true);
        builder.addField("Donators", String.join("\n", arrSupporter), true);
        builder.addField("Devs", authors.toString(), false);
        return new MessageBuilder().setEmbed(builder.build()).build();
    }

}
