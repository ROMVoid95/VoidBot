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
package net.romvoid.discord.permission;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.util.Colors;
import net.romvoid.discord.util.StringUtil;

import static net.romvoid.discord.util.EmbedUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Auto-generated Javadoc
/**
 * Handles the 'permission' command which interfaces the permissions system in a discord command.
 *
 * @author ROMVoid
 */
public class PermissionCommandHandler extends CommandHandler {
    
    /** The Constant LIST_PERMISSIONS. */
    private static final PermsNeeded MODIFY_PERMISSIONS = new PermsNeeded(
            "permissions.modify", false, false),
            LIST_PERMISSIONS = new PermsNeeded("permissions.list", false, false);

    /**
     * Initializes the command handler.
     */
    protected PermissionCommandHandler() {
        super(new String[]{"permission", "permit", "permissions", "perm", "perms"}, CommandCategory.ADMIN,
                new PermsNeeded("command.permission", false, false),
                "Allows modifying and listing permissions.",
                "allow/deny <@User/@Role/dp:id> <command-permission>\n" +
                        "list <@User/@Role/dp:id> <command>");
    }

    /**
     * Execute.
     *
     * @param invocation the invocation
     * @param userPermissions the user permissions
     * @return the message
     */
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        try {
            Message message = invocation.getMessage();
            if (invocation.getArgs().length == 0)
                return createHelpMessage(invocation);

            //Define subcommand
            String subCommand = invocation.getArgs()[0];

            if (subCommand.equalsIgnoreCase("show-discord-permissions") || subCommand.equalsIgnoreCase("discord-permissions") || subCommand.equalsIgnoreCase("dp")) {
                StringBuilder discordPermissions = new StringBuilder("— `ID`  `Name`\n");
                for (net.dv8tion.jda.core.Permission permission : net.dv8tion.jda.core.Permission.values())
                    discordPermissions.append("  \u2022 `").append(String.format("%02d", permission.getOffset())).append("`  ").append(permission.getName()).append("\n");
                return message(info("List of discord permissions", discordPermissions.toString()));
            }

            if (invocation.getArgs().length < 2)
                return message(error("Missing arguments", "Some parameters seem to be missing. Use `" +
                        invocation.getPrefix() + invocation.getCommandInvocation() + "` for usage information."));

            User mentionedUser = null;
            Role mentionedRole = null;
            String discordPermission = null;
            String targetString = invocation.getArgs()[1];
            if (message.getMentionedUsers().size() == 1)
                mentionedUser = message.getMentionedUsers().get(0);
            else if (message.getMentionedRoles().size() == 1)
                mentionedRole = message.getMentionedRoles().get(0);
            else
                discordPermission = targetString;

            PermissionTarget target;
            if (discordPermission != null)
                target = parseDiscordPermissionTarget(invocation.getGuild(), discordPermission);
            else
                target = mentionedRole == null ? new PermissionTarget(invocation.getGuild(), PermissionTarget.Type.USER, mentionedUser.getIdLong()) : new PermissionTarget(invocation.getGuild(), PermissionTarget.Type.ROLE, mentionedRole.getIdLong());
            if (!target.exists())
                return message(error("Target does not exit", "`" + target.toString() + "` is not on this server!"));

            if (subCommand.equalsIgnoreCase("list")) {
                //Check Permissions
                if (!LIST_PERMISSIONS.coveredBy(userPermissions))
                    return message(no_permissions());
                List<CommandHandler> filteredCommandList = VoidBot.getCommandManager().getCommandAssociations().values().stream().filter(commandHandler -> commandHandler.getCategory() != CommandCategory.BOT_OWNER).collect(Collectors.toList());
                List<Permission> permissionEntries = VoidBot.sGetPermissionManager().getPermissions(target);
                StringBuilder deniedPermissionString = new StringBuilder();
                StringBuilder allowedPermissionString = new StringBuilder();
                ArrayList<CommandHandler> alreadyAdded = new ArrayList<>();
                for (CommandHandler commandHandler : filteredCommandList) {
                    if (alreadyAdded.contains(commandHandler))
                        continue;
                    List<Permission> commandPermissions = permissionEntries.stream().filter(permission -> permission.getPermissionString().replaceFirst("!", "").equalsIgnoreCase(commandHandler.getPermsNeeded().getRequiredPermissionNode())).collect(Collectors.toList());
                    if (commandPermissions.size() == 0) {
                        if (!commandHandler.getPermsNeeded().isDefault()) {
                            deniedPermissionString.append("`").append(commandHandler.getCommandAliases()[0]).append("` ");
                            alreadyAdded.add(commandHandler);
                            continue;
                        }
                    } else {
                        if (commandPermissions.get(0).isNegated()) {
                            deniedPermissionString.append("`").append(commandHandler.getCommandAliases()[0]).append("` ");
                            alreadyAdded.add(commandHandler);
                            continue;
                        }
                    }
                    allowedPermissionString.append("`" + commandHandler.getCommandAliases()[0] + "` ");
                    alreadyAdded.add(commandHandler);
                }
                EmbedBuilder listBuilder = new EmbedBuilder();
                listBuilder.setColor(Colors.COLOR_SECONDARY);
                listBuilder.setTitle(":information_source: Permission list");
                listBuilder.setDescription("Permissions of " + target.toString());
                listBuilder.addField("Allowed Permissions", allowedPermissionString.toString(), true);
                listBuilder.addField("Denied Permissions", deniedPermissionString.toString(), true);
                listBuilder.setFooter("NOTICE - Permissions always starts with command. | This command is a pre-version.", null);
                return message(listBuilder);
            }

            if (invocation.getArgs().length < 3)
                return message(error("Missing arguments", "Some parameters seem to be missing. Use `" +
                        invocation.getPrefix() + invocation.getCommandInvocation() + "` for usage information."));
            if (!MODIFY_PERMISSIONS.coveredBy(userPermissions))
                return message(no_permissions());

            String permissionString = invocation.getArgs()[2];

            if (subCommand.equalsIgnoreCase("allow") || subCommand.equalsIgnoreCase("add")) {
                // Check Permissions
                return VoidBot.sGetPermissionManager().addPermission(target, Permission.parse(permissionString))
                        ? message(success("Updated permissions", "Successfully allowed `" +
                        permissionString + "` for `" + target.toString() + "`."))
                        : message(error("Entry already exists", "There already is a `" +
                        Permission.parse(permissionString).getPermissionString() + "` entry for `" +
                        target.toString() + "` on this guild. Use `" + invocation.getPrefix() +
                        invocation.getCommandInvocation() + " list " + (targetString) +
                        "` to get a list of permission entries for this target."));
            } else if (subCommand.equalsIgnoreCase("deny") || subCommand.equalsIgnoreCase("remove")) {
                if (!VoidBot.sGetPermissionManager().removePermission(target, Permission.parse(permissionString)))
                    VoidBot.sGetPermissionManager().addPermission(target, Permission.parse("!" + permissionString));
                return message(success("Updated permissions", "Successfully denied `" +
                        permissionString + "` for `" + target.toString() + "`."));
            }
            return null;
        } catch (
                IllegalArgumentException e)

        {
            return message(error("Invalid arguments", e.getMessage() + " Use `" +
                    invocation.getPrefix() + invocation.getCommandInvocation() + "` for a " + "command manual."));
        }

    }

    /**
     * Parses a {@link net.dv8tion.jda.core.Permission Discord Permission} out of user input string.
     *
     * @param guild the guild
     * @param input the user input
     * @return the parsed {@link PermissionTarget}.
     * @throws IllegalArgumentException the illegal argument exception
     */
    private PermissionTarget parseDiscordPermissionTarget(Guild guild, String input) throws IllegalArgumentException {
        String[] splitted = input.split(":");
        if (splitted.length == 1)
            throw new IllegalArgumentException("`" + input + "` is not a valid permission id.");
        if (!StringUtil.isNumeric(splitted[1]))
            throw new IllegalArgumentException("`" + input + "` must be a numeric parameter.");
        int id = Integer.parseInt(splitted[1]);
        return new PermissionTarget(guild, PermissionTarget.Type.DISCORD_PERMISSION, id);
    }

    /**
     * Creates the help message.
     *
     * @param serverPrefix the server prefix
     * @param aliasToUse the alias to use
     * @return the message
     */
    @Override
    public Message createHelpMessage(String serverPrefix, String aliasToUse) {
        StringBuilder usage = new StringBuilder();
        for (String part : getParameterUsage().split("\n"))
            usage.append(serverPrefix + aliasToUse + " " + part + "\n");
        return message(info('\'' + aliasToUse + "' command help", getDescription())
                .addField("Aliases", String.join(", ", getCommandAliases()), false)
                .addField("Usage", usage.toString(), false)
                .addField("Parameters",
                        "`command-permission`\n" +
                                "Permission of the command. If you want to add a command\n" +
                                "you have to use `command.yourCommand`\n" +
                                "All command-permissions are displayed next to the commands at our [documentation](http://rubicon.fun)", false));
    }
}
