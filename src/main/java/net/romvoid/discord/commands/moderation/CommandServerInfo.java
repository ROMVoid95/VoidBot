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
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.Colors;

import java.time.OffsetDateTime;


// TODO: Auto-generated Javadoc
/**
 * The Class CommandServerInfo.
 */
public class CommandServerInfo extends CommandHandler {

    /**
     * Instantiates a new command server info.
     */
    public CommandServerInfo() {
        super(new String[]{"serverinfo", "guild", "guildinfo"}, CommandCategory.TOOLS, new PermsNeeded("command.serverinfo", false, true), "Returns some information about the current/an other server", "[serverid]");
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
        boolean guildexist = Guildexist(parsedCommandInvocation);
        if (guildexist) {
            Guild guild = VoidBot.getJDA().getGuildById(parsedCommandInvocation.getArgs()[0]);
            StringBuilder rawRoles = new StringBuilder();
            guild.getRoles().forEach(r -> rawRoles.append(r.getName()).append(", "));
            StringBuilder roles = new StringBuilder(rawRoles.toString());
            roles.replace(rawRoles.lastIndexOf(","), roles.lastIndexOf(",") + 1, "");
            EmbedBuilder serverInfo = new EmbedBuilder();
            serverInfo.setColor(Colors.COLOR_PRIMARY);
            serverInfo.setTitle(":desktop: Serverinfo of " + guild.getName());
            serverInfo.setThumbnail(guild.getIconUrl());
            serverInfo.addField("ID", "`" + guild.getId() + "`", false);
            serverInfo.addField("Guildname", "`" + guild.getName() + "`", false);
            serverInfo.addField("Server region", guild.getRegion().toString(), false);
            serverInfo.addField("Members", String.valueOf(guild.getMembers().size()), false);
            serverInfo.addField("Textchannels", String.valueOf(guild.getTextChannels().size()), false);
            serverInfo.addField("Voicechannels", String.valueOf(guild.getVoiceChannels().size()), false);
            serverInfo.addField("Roles", String.valueOf(guild.getRoles().size()) + "\n ```" + roles.toString() + "```", false);
            serverInfo.addField("Server owner", guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator(), false);
            if (hasicon(guild)) {
                serverInfo.addField("Server icon url", guild.getIconUrl(), false);
            }
            serverInfo.addField("Server Creation Date", formatDate(guild.getCreationTime()), false);
            return new MessageBuilder().setEmbed(serverInfo.build()).build();
        } else {
            Guild guild = parsedCommandInvocation.getGuild();
            StringBuilder rawRoles = new StringBuilder();
            guild.getRoles().forEach(r -> rawRoles.append(r.getName()).append(", "));
            StringBuilder roles = new StringBuilder(rawRoles.toString());
            roles.replace(rawRoles.lastIndexOf(","), roles.lastIndexOf(",") + 1, "");
            EmbedBuilder serverInfo = new EmbedBuilder();
            serverInfo.setColor(Colors.COLOR_PRIMARY);
            serverInfo.setTitle(":desktop: Serverinfo of " + guild.getName());
            serverInfo.setThumbnail(guild.getIconUrl());
            serverInfo.addField("ID", "`" + guild.getId() + "`", false);
            serverInfo.addField("Guildname", "`" + guild.getName() + "`", false);
            serverInfo.addField("Server region", guild.getRegion().toString(), false);
            serverInfo.addField("Members", String.valueOf(guild.getMembers().size()), false);
            serverInfo.addField("Textchannels", String.valueOf(guild.getTextChannels().size()), false);
            serverInfo.addField("Voicechannels", String.valueOf(guild.getVoiceChannels().size()), false);
            serverInfo.addField("Roles", String.valueOf(guild.getRoles().size()) + "\n ```" + roles.toString() + "```", false);
            serverInfo.addField("Server owner", guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator(), false);
            if (hasicon(guild)) {
                serverInfo.addField("Server icon url", guild.getIconUrl(), false);
            }
            serverInfo.addField("Server Creation Date", formatDate(guild.getCreationTime()), false);
            return new MessageBuilder().setEmbed(serverInfo.build()).build();
        }


    }

    /**
     * Format date.
     *
     * @param date the date
     * @return the string
     */
    public String formatDate(OffsetDateTime date) {
        return date.getMonthValue() + "/" + date.getDayOfMonth() + "/" + date.getYear();
    }

    /**
     * Guildexist.
     *
     * @param parsedCommandInvocation the parsed command invocation
     * @return true, if successful
     */
    public boolean Guildexist(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        try {
            Guild guild = VoidBot.getJDA().getGuildById(parsedCommandInvocation.getArgs()[0]);
            guild.getName();
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {
            return false;

        }
        return true;
    }

    /**
     * Hasicon.
     *
     * @param g the g
     * @return true, if successful
     */
    public boolean hasicon(Guild g) {
        try {
            String url = g.getIconUrl();
            url.toLowerCase();
        } catch (NullPointerException ignored) {
            return false;

        }
        return true;
    }

}
