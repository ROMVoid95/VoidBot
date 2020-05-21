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

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.GuildController;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.EmbedUtil;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandMoveAll.
 */
public class CommandMoveAll extends CommandHandler {
    
    /**
     * Instantiates a new command move all.
     */
    public CommandMoveAll() {
        super(new String[]{"moveall", "mvall", "mva"}, CommandCategory.MODERATION, new PermsNeeded("command.moveall", false, false), "Move all members in your channel into another channel.", "<#channel>", false);
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
        String[] args = parsedCommandInvocation.getArgs();
        Message message = parsedCommandInvocation.getMessage();
        Guild guild = parsedCommandInvocation.getMessage().getGuild();
        if (args.length == 0) {
            return createHelpMessage(parsedCommandInvocation);
        }
        if (!message.getMember().getVoiceState().inVoiceChannel())
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not connected", "Please connect to a voice channel to use this command").build()).build();

        String name;
        name = message.getContentRaw().replace(parsedCommandInvocation.getCommandInvocation(), "");
        name = name.replace(parsedCommandInvocation.getPrefix(), "");
        name = name.substring(1);
        List<VoiceChannel> channels = message.getGuild().getVoiceChannelsByName(name, true);
        if (channels.isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.error("Channel not found", "This channel doesen't exist").build()).build();
        VoiceChannel channel = channels.get(0);
        if (channel.equals(message.getMember().getVoiceState().getChannel()))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Same channel", "You are already connected to that channel").build()).build();
        GuildController controller = message.getGuild().getController();
        if (!guild.getSelfMember().hasPermission(Permission.VOICE_MOVE_OTHERS)) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Cannot move you!", "Cannot move all members in the Channel").build()).build();
        }
        message.getMember().getVoiceState().getChannel().getMembers().forEach(m -> {
            if (!parsedCommandInvocation.getSelfMember().canInteract(m))
                return;
            controller.moveVoiceMember(m, channel).queue();
        });
        return new MessageBuilder().setEmbed(EmbedUtil.success("Connected", "Connected all users in your channel to `" + channel.getName() + "`").build()).build();
    }
}
