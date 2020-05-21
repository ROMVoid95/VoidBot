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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateNameEvent;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.Colors;
import net.romvoid.discord.util.Configuration;
import net.romvoid.discord.util.EmbedUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandRanks.
 */
public class CommandRanks extends CommandHandler {
    
    /**
     * Instantiates a new command ranks.
     */
    public CommandRanks() {
        super(new String[]{"role", "roles", "rank", "ranks"}, CommandCategory.MODERATION, new PermsNeeded("command.rank", false, false), "Easily create ranks, that users can assign herself", "<rolename>... <rolename>\n" +
                "add <rolename>\n" +
                "remove <rolename>", true);
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
        new File("data/ranks").mkdirs();
        File file = new File("data/ranks/" + message.getGuild().getId() + ".dat");
        Configuration ranks = new Configuration(file);
        //Fixes bug, that roles cannot be deleted
        ranks.set("bugfix", "true");
        if (file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (args.length == 0)
            return createHelpMessage();
        if (args[0].equals("add")) {
            if (args.length > 2)
                return new MessageBuilder().setEmbed(EmbedUtil.error("Incorect rolename", "Roles cannot contain spaces c:").build()).build();
            if (ranks.has(args[1]))
                return new MessageBuilder().setEmbed(EmbedUtil.error("Name used", "This name is already used").build()).build();
            Role role;
            try {
                role = message.getGuild().getController().createRole().setName(args[1]).complete();
            } catch (Exception error) {
                return new MessageBuilder().setEmbed(EmbedUtil.error("No permissions", "I'm not permitted to create roles c:").build()).build();
            }
            ranks.set(args[1], role.getId());
            return new MessageBuilder().setEmbed(EmbedUtil.success("Created rank", "Successfully created rank! When you update rolename I will automatically update it too in my config, but when the new rolename contains spaces I will remove it from my list").build()).build();
        } else if (args[0].equals("remove")) {
            if (args.length == 1)
                return createHelpMessage();
            if (args.length > 2)
                return new MessageBuilder().setEmbed(EmbedUtil.error("Incorect rolename", "Roles cannot contain spaces c:").build()).build();
            if (!ranks.has(args[1]))
                return new MessageBuilder().setEmbed(EmbedUtil.error("Name not used", "This rolename is not used").build()).build();
            ranks.unset(args[1]);
            return new MessageBuilder().setEmbed(EmbedUtil.success("Removed rank", "Successfully removed rank").build()).build();
        } else if (args[0].equals("list")) {
            StringBuilder ranksList = new StringBuilder();
            if (ranks.keySet().stream().filter(k -> !k.equals("bugfix")).collect(Collectors.toList()).isEmpty())
                return new MessageBuilder().setEmbed(EmbedUtil.info("No ranks", "There are no ranks on this server").build()).build();
            ranks.keySet().forEach(k -> {
                if (!k.equals("bugfix"))
                    ranksList.append(k).append(", ");
                ranksList.replace(ranksList.lastIndexOf(","), ranksList.lastIndexOf(",") + 1, "");
            });
            return new MessageBuilder().setEmbed(EmbedUtil.info("Ranks", "```" + ranksList.toString() + "```").build()).build();
        } else {
            StringBuilder added = new StringBuilder();
            StringBuilder removed = new StringBuilder();
            List<Role> toAdd = new ArrayList<>();
            List<Role> toRemove = new ArrayList<>();
            for (String arg : args) {
                if (ranks.has(arg) && !arg.equals("bugfix")) {
                    Role role = message.getGuild().getRoleById(ranks.getString(arg));
                    if (message.getMember().getRoles().contains(role)) {
                        toRemove.add(role);
                        removed.append(role.getName()).append(", ");
                    } else {
                        toAdd.add(role);
                        added.append(role.getName()).append(", ");
                    }
                }
            }
            if (!toAdd.isEmpty()) {
                message.getGuild().getController().addRolesToMember(message.getMember(), toAdd).queue();
                added.replace(added.lastIndexOf(","), added.lastIndexOf(",") + 1, "");
            }
            if (!toRemove.isEmpty()) {
                message.getGuild().getController().removeRolesFromMember(message.getMember(), toRemove).queue();
                removed.replace(removed.lastIndexOf(","), removed.lastIndexOf(",") + 1, "");
            }
            EmbedBuilder result = new EmbedBuilder();
            result.setColor(Colors.COLOR_PRIMARY);
            result.setTitle("Modified ranks");
            if (toAdd.isEmpty() && toRemove.isEmpty())
                return new MessageBuilder().setEmbed(EmbedUtil.error("Unknown roles", "You entered incorrect rolenames").build()).build();
            if (!added.toString().equals(""))
                result.addField("Added ranks", added.toString(), false);
            if (!removed.toString().equals(""))
                result.addField("Removed ranks", removed.toString(), false);
            return new MessageBuilder().setEmbed(result.build()).build();

        }
    }


    /**
     * Checks if is rank.
     *
     * @param role the role
     * @return true, if is rank
     */
    public static boolean isRank(Role role) {
        File file = new File("data/ranks/" + role.getGuild().getId() + ".dat");
        Configuration ranks = new Configuration(file);
        return ranks.has(role.getName());
    }

    /**
     * Handle role modification.
     *
     * @param event the event
     */
    public static void handleRoleModification(RoleUpdateNameEvent event) {
        File file = new File("data/ranks/" + event.getGuild().getId() + ".dat");
        Configuration ranks = new Configuration(file);
        Role role = event.getRole();
        if (!isRank(role)) return;
        if (role.getName().split(" ").length > 0) return;
        ranks.unset(event.getOldName());
        ranks.set(role.getName(), role.getId());
    }

    /**
     * Handle role deletion.
     *
     * @param event the event
     */
    public static void handleRoleDeletion(RoleDeleteEvent event) {
        File file = new File("data/ranks/" + event.getGuild().getId() + ".dat");
        Configuration ranks = new Configuration(file);
        Role role = event.getRole();
        if (!isRank(role)) return;
        ranks.unset(role.getName());
    }
}
