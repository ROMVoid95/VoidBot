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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
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
 * The Class CommandHelp.
 */
public class CommandHelp extends CommandHandler {

    /**
     * Instantiates a new command help.
     */
    public CommandHelp() {
        super(new String[]{"help", "usage", "?", "command", "manual", "man"}, CommandCategory.GENERAL,
                new PermsNeeded("command.help", false, true),
                "Shows the command manual.", "[command]");
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
        if (parsedCommandInvocation.getArgs().length == 0) {
            // show complete command manual
            parsedCommandInvocation.getMessage().getTextChannel().sendMessage(new MessageBuilder().setEmbed(generateFullHelp(parsedCommandInvocation).build()).build()).queue();
            return null;
        } else {
            CommandHandler handler = VoidBot.getCommandManager().getCommandHandler(parsedCommandInvocation.getArgs()[0]);
            return handler == null
                    // invalid command
                    ? new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(Colors.COLOR_ERROR)
                    .setTitle(":warning: Invalid command")
                    .setDescription("There is no command named '" + parsedCommandInvocation.getArgs()[0] + "'. Use `"
                            + parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation()
                            + "` to get a full command list.")
                    .build()).build()
                    // show command help for a single command
                    : handler.createHelpMessage(Info.BOT_DEFAULT_PREFIX, parsedCommandInvocation.getArgs()[0]);
        }
    }

    /**
     * Generate full help.
     *
     * @param invocation the invocation
     * @return the embed builder
     */
    private EmbedBuilder generateFullHelp(CommandManager.ParsedCommandInvocation invocation) {
        EmbedBuilder builder = new EmbedBuilder();
        List<CommandHandler> filteredCommandList = VoidBot.getCommandManager().getCommandAssociations().values().stream().filter(commandHandler -> commandHandler.getCategory() != CommandCategory.BOT_OWNER).collect(Collectors.toList());

        ArrayList<String> alreadyAdded = new ArrayList<>();

        StringBuilder listGeneral = new StringBuilder();
        for (CommandHandler commandHandler : filteredCommandList) {
            if (commandHandler.getCategory() == CommandCategory.GENERAL && !alreadyAdded.contains(commandHandler.getCommandAliases()[0])) {
                alreadyAdded.add(commandHandler.getCommandAliases()[0]);
                listGeneral.append("`").append(commandHandler.getCommandAliases()[0]).append("` ");
            }
        }
        StringBuilder listModeration = new StringBuilder();
        for (CommandHandler commandHandler : filteredCommandList) {
            if (commandHandler.getCategory() == CommandCategory.MODERATION && !alreadyAdded.contains(commandHandler.getCommandAliases()[0])) {
                alreadyAdded.add(commandHandler.getCommandAliases()[0]);
                listModeration.append("`").append(commandHandler.getCommandAliases()[0]).append("` ");
            }
        }
        StringBuilder listAdmin = new StringBuilder();
        for (CommandHandler commandHandler : filteredCommandList) {
            if (commandHandler.getCategory() == CommandCategory.ADMIN && !alreadyAdded.contains(commandHandler.getCommandAliases()[0])) {
                alreadyAdded.add(commandHandler.getCommandAliases()[0]);
                listAdmin.append("`").append(commandHandler.getCommandAliases()[0]).append("` ");
            }
        }
        StringBuilder listSettings = new StringBuilder();
        for (CommandHandler commandHandler : filteredCommandList) {
            if (commandHandler.getCategory() == CommandCategory.SETTINGS && !alreadyAdded.contains(commandHandler.getCommandAliases()[0])) {
                alreadyAdded.add(commandHandler.getCommandAliases()[0]);
                listSettings.append("`").append(commandHandler.getCommandAliases()[0]).append("` ");
            }
        }
        StringBuilder listTools = new StringBuilder();
        for (CommandHandler commandHandler : filteredCommandList) {
            if (commandHandler.getCategory() == CommandCategory.TOOLS && !alreadyAdded.contains(commandHandler.getCommandAliases()[0])) {
                alreadyAdded.add(commandHandler.getCommandAliases()[0]);
                listTools.append("`").append(commandHandler.getCommandAliases()[0]).append("` ");
            }
        }

        builder.setTitle(":information_source: TheVoid command manual");
        builder.setDescription("Use `" + invocation.getPrefix() + "help <command>` to get a more information about a command.");
        builder.setColor(Colors.COLOR_SECONDARY);
        builder.setFooter("Loaded a total of "
                + new HashSet<>(VoidBot.getCommandManager().getCommandAssociations().values()).size()
                + " commands.", null);

        //Add Categories
        builder.addField("General", listGeneral.toString(), false);
        builder.addField("Moderation", listModeration.toString(), false);
        builder.addField("Admin", listAdmin.toString(), false);
        builder.addField("Settings", listSettings.toString(), false);
        builder.addField("Tools", listTools.toString(), false);
        return builder;
    }
}
