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
package net.romvoid.discord.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.commands.admin.CommandVerification;
import net.romvoid.discord.commands.botowner.CommandMaintenance;
import net.romvoid.discord.listener.ServerLogHandler;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.Colors;
import net.romvoid.discord.util.EmbedUtil;
import net.romvoid.discord.util.Info;
import net.romvoid.discord.util.Logger;

import static net.romvoid.discord.util.EmbedUtil.info;
import static net.romvoid.discord.util.EmbedUtil.message;

import java.util.ArrayList;
import java.util.Arrays;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandHandler.
 */
public abstract class CommandHandler {
    
    /** The command aliase. */
    private final String[] commandAliase;
    
    /** The category. */
    private final CommandCategory category;
    
    /** The perms needed. */
    private final PermsNeeded permsNeeded;
    
    /** The description. */
    private String description;
    
    /** The parameter usage. */
    private final String parameterUsage;
    
    /** The disabled. */
    private boolean disabled = false;

    /**
     * Constructs a new CommandHandler.
     *
     * @param commandAliases      the invocation commands (aliases). First entry is the 'main' alias.
     * @param category               the {@link CommandCategory} this command belongs to.
     * @param permsNeeded all permission requirements a user needs to meet to execute a command.
     * @param description            a short command description.
     * @param parameterUsage         the usage message.
     */
    public CommandHandler(String[] commandAliases, CommandCategory category,
                             PermsNeeded permsNeeded, String description, String parameterUsage) {
        this.commandAliase = commandAliases;
        this.category = category;
        this.permsNeeded = permsNeeded;
        this.description = description;
        this.parameterUsage = parameterUsage;
    }

    /**
     * Instantiates a new command handler.
     *
     * @param invocationAliases the invocation aliases
     * @param category the category
     * @param permissionRequirements the permission requirements
     * @param description the description
     * @param parameterUsage the parameter usage
     * @param disabled the disabled
     */
    public CommandHandler(String[] invocationAliases, CommandCategory category,
                             PermsNeeded permissionRequirements, String description, String parameterUsage, boolean disabled) {
        this.commandAliase = invocationAliases;
        this.category = category;
        this.permsNeeded = permissionRequirements;
        this.description = description;
        this.parameterUsage = parameterUsage;
        this.disabled = disabled;
    }

    /**
     * Checks permission, safely calls the execute method and ensures response.
     *
     * @param parsedCommandInvocation the parsed command invocation.
     * @return a response that will be sent and deleted by the caller.
     */
    public Message call(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        if (disabled) {
            return new MessageBuilder().setEmbed(EmbedUtil.info("Command disabled", "Command is currently disabled.").setFooter("TheVoid Dev Team", null).build()).build();
        }
        if (CommandMaintenance.maintenance) {
            ArrayList<Long> authors = new ArrayList<>(Arrays.asList(Info.BOT_AUTHOR_IDS));
            if (!authors.contains(parsedCommandInvocation.getAuthor().getIdLong())) {
                return EmbedUtil.message(EmbedUtil.info("Maintenance!", "Bots maintenance is enabled. Please be patient."));
            }
        }
        UserPermissions userPermissions = new UserPermissions(parsedCommandInvocation.getMessage().getAuthor(),
                parsedCommandInvocation.getMessage().getGuild());
        // check permission
        if (permsNeeded.coveredBy(userPermissions)) {
            // execute command
            try {
                ServerLogHandler.logCommand(parsedCommandInvocation);
                return execute(parsedCommandInvocation, userPermissions);
            } catch (Exception e) { // catch exceptions in command and provide an answer
                Logger.error("Unknown error during the execution of the '" + parsedCommandInvocation.getCommandInvocation() + "' command. ");
                Logger.error(e);
                return new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setAuthor("Error", null, VoidBot.getJDA().getSelfUser().getEffectiveAvatarUrl())
                        .setDescription("An unknown error occured while executing your command.")
                        .setColor(Colors.COLOR_ERROR)
                        .setFooter(VoidBot.getNewTimestamp(), null)
                        .build()).build();
            }
        } else
            // respond with 'no-permission'-message
            return new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setAuthor("Missing permissions", null, VoidBot.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .setDescription("You are not permitted to execute this command.")
                    .setColor(Colors.COLOR_NO_PERMISSION)
                    .setFooter(VoidBot.getNewTimestamp(), null)
                    .build()).build();
    }

    /**
     * Method to be implemented by actual command handlers.
     *
     * @param parsedCommandInvocation the command arguments with prefix and command head removed.
     * @param userPermissions         an object to query the invoker's permissions.
     * @return a response that will be sent and deleted by the caller.
     */
    protected abstract Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions);

    /**
     * Gets the command aliases.
     *
     * @return all aliases this CommandHandler wants to listen to.
     */
    public String[] getCommandAliases() {
        return commandAliase;
    }

    /**
     * Gets the category.
     *
     * @return the category this command belongs to.
     */
    public CommandCategory getCategory() {
        return category;
    }

    /**
     * Gets the perms needed.
     *
     * @return the permission requirements a user needs to meet to execute a command.
     */
    public PermsNeeded getPermsNeeded() {
        return permsNeeded;
    }

    /**
     * Gets the description.
     *
     * @return the short description of this command.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Updates the description of the command.
     *
     * @param description the new command description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Use createHelpMessage for a full help message.
     *
     * @return the parameter usage String.
     */
    public String getParameterUsage() {
        return parameterUsage;
    }

    /**
     * Generates a usage message for this command with the default prefix and alias.
     *
     * @return the generated Message.
     */
    public Message createHelpMessage() {
        return createHelpMessage(Info.BOT_DEFAULT_PREFIX, commandAliase[0]);
    }

    /**
     * Generates a usage message for this command.
     *
     * @param invocation data source for prefix and alias to use in the Message.
     * @return the generated Message.
     */
    public Message createHelpMessage(CommandManager.ParsedCommandInvocation invocation) {
        return createHelpMessage(invocation.getPrefix(), invocation.getCommandInvocation());
    }

    /**
     * Generates a usage message for this command.
     *
     * @param serverPrefix which prefix should be used in this message?
     * @param aliasToUse   which alias should be used in this message?
     * @return the message
     */
    public Message createHelpMessage(String serverPrefix, String aliasToUse) {
        StringBuilder usage = new StringBuilder();
        for (String part : getParameterUsage().split("\n")) {
            usage.append(serverPrefix + aliasToUse + " " + part + "\n");
        }
        if (this instanceof CommandVerification) {
            if (CommandVerification.showInspired) {
                setDescription("Let your members accept rules before posting messages.");
            } else {
                setDescription("Let your members accept rules before posting messages\n\nThis feature is partially inspired by [Flashbot](https://flashbot.de)");
            }
        }
        return message(info('\'' + aliasToUse + "' command help", getDescription())
                .addField("Aliases", String.join(", ", getCommandAliases()), false)
                .addField("Usage", usage.toString(), false)
                .addField("Permission", permsNeeded.getRequiredPermissionNode(), false));
    }
}
