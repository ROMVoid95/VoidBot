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
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.EmbedUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandRole.
 */
public class CommandRole extends CommandHandler {
    
    /**
     * Instantiates a new command role.
     */
    public CommandRole() {
        super(new String[]{"mod-role"}, CommandCategory.MODERATION, new PermsNeeded("command.role", false, false), "Easily add or remove roles to users", "add/remove <@User> <role>", true);
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
        if (args.length < 2 || message.getMentionedUsers().isEmpty())
            return createHelpMessage();
        GuildController controller = message.getGuild().getController();
        Member member = message.getGuild().getMember(message.getMentionedUsers().get(0));

        //Get Role
        int userNameArgsLength = message.getMentionedUsers().get(0).getAsMention().split(" ").length;
        String rolename = args[userNameArgsLength + 1];

        if (member.getGuild().getRolesByName(rolename, true).isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.error("Unknown role", "That role doesn't exist").build()).build();
        Role role = member.getGuild().getRolesByName(rolename, true).get(0);
        Member issuer = message.getMember();
        if (!issuer.canInteract(role))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not permitted", "You are not permitted to assign or remove that role").build()).build();
        if (!message.getGuild().getSelfMember().canInteract(role))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not permitted", "I'm not permitted to assign or remove that role").build()).build();

        if (args[0].equals("add")) {
            controller.addRolesToMember(member, role).queue();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Assigned role", "Successfully asigned role `" + role.getName() + "` to " + member.getAsMention()).build()).build();
        } else if (args[0].equals("remove")) {
            controller.removeRolesFromMember(member, role).queue();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Removed role", "Successfully removed role `" + role.getName() + "` from " + member.getAsMention()).build()).build();
        } else
            return createHelpMessage();
    }
}
