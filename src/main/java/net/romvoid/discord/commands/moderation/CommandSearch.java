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
package net.romvoid.discord.commands.moderation;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.EmbedUtil;

import java.awt.*;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandSearch.
 */
public class CommandSearch extends CommandHandler {

    /**
     * Instantiates a new command search.
     */
    public CommandSearch() {
        super(new String[]{"search", "find"}, CommandCategory.TOOLS, new PermsNeeded("command.search", false, true), "Searches for users, roles and channels with a specified name.", "<query>");
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
        Message message = parsedCommandInvocation.getMessage();
        String[] args = parsedCommandInvocation.getArgs();
        TextChannel channel = message.getTextChannel();
        Guild guild = message.getGuild();

        if (args.length == 0) {
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "search <query>").build()).build();
        }
        StringBuilder query = new StringBuilder();
        for (String arg : args) {
            query.append(arg);
        }

        StringBuilder textchannels = new StringBuilder();
        StringBuilder voicechannels = new StringBuilder();
        StringBuilder members = new StringBuilder();
        StringBuilder roles = new StringBuilder();

        Message mymsg = channel.sendMessage(new EmbedBuilder().setColor(Color.cyan).setDescription("Collecting textchannels ...").build()).complete();

        guild.getTextChannels().forEach(i -> {
            if (i.getName().toLowerCase().contains(query.toString().toLowerCase()))
                textchannels.append(i.getName() + "(`" + i.getId() + "`)").append("\n");
        });

        mymsg.editMessage(new EmbedBuilder().setColor(Color.cyan).setDescription("Collecting voicechannels ...").build()).queue();


        guild.getVoiceChannels().forEach(i -> {
            if (i.getName().toLowerCase().contains(query.toString().toLowerCase()))
                voicechannels.append(i.getName() + "(`" + i.getId() + "`)").append("\n");
        });

        mymsg.editMessage(new EmbedBuilder().setColor(Color.cyan).setDescription("Collecting users ...").build()).queue();


        guild.getMembers().forEach(i -> {
            if (i.getUser().getName().toLowerCase().contains(query.toString().toLowerCase()) || i.getEffectiveName().toLowerCase().contains(query.toString().toLowerCase()))
                members.append(i.getUser().getName() + "(`" + i.getUser().getId() + "`)").append("\n");
        });

        mymsg.editMessage(new EmbedBuilder().setColor(Color.cyan).setDescription("Collecting roles ...").build()).queue();

        guild.getRoles().forEach(i -> {
            if (i.getName().toLowerCase().contains(query.toString().toLowerCase()))
                roles.append(i.getName() + "(`" + i.getId() + "`)").append("\n");
        });
        mymsg.delete().queue();
        try {
            EmbedBuilder results = new EmbedBuilder()
                    .setColor(Color.green)
                    .addField("**Textchannels**", textchannels.toString(), false)
                    .addField("**Voicechannles**", voicechannels.toString(), false)
                    .addField("**Members**", members.toString(), false)
                    .addField("**Roles**", roles.toString(), false);
            return new MessageBuilder().setEmbed(results.build()).build();
        } catch (IllegalArgumentException ex) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Error!", "Too many results!").build()).build();
        }

    }


}
