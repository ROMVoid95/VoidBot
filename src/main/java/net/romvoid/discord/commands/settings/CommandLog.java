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
package net.romvoid.discord.commands.settings;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.listener.ServerLogHandler;
import net.romvoid.discord.listener.ServerLogHandler.LogEventKeys;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.sql.ServerLogSQL;
import net.romvoid.discord.util.Colors;
import net.romvoid.discord.util.EmbedUtil;

import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandLog.
 */
public class CommandLog extends CommandHandler {

    /** The parsed command invocation. */
    private CommandManager.ParsedCommandInvocation parsedCommandInvocation;

    /** The server log SQL. */
    private ServerLogSQL serverLogSQL;
    
    /** The args. */
    private String[] args;

    /**
     * Instantiates a new command log.
     */
    public CommandLog() {
        super(new String[]{"log", "logsettings"}, CommandCategory.SETTINGS, new PermsNeeded("command.log", false, false), "Enable/Disable log settings", "list\n" +
                "channel <#channel>\n" +
                "join <enable/disable>\n" +
                "leave <enable/disable>\n" +
                "command <enable/disable>\n" +
                "ban <enable/disable>\n" +
                "role <enable/disable>\n" +
                "voice <enable/disable>");
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
        this.parsedCommandInvocation = parsedCommandInvocation;
        this.args = parsedCommandInvocation.getArgs();
        this.serverLogSQL = new ServerLogSQL(parsedCommandInvocation.getMessage().getGuild());
        if (args.length == 0)
            return createHelpMessage();
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            return EmbedUtil.message(generateList());
        } else if (args.length == 2) {
            switch (args[0]) {
                case "channel":
                    if (parsedCommandInvocation.getMessage().getMentionedChannels().size() != 1)
                        return EmbedUtil.message(EmbedUtil.error("Error!", "You have to mention `one` channel."));
                    serverLogSQL.set("channel", parsedCommandInvocation.getMessage().getMentionedChannels().get(0).getId());
                    return EmbedUtil.message(EmbedUtil.success("Success!", "Successfully set logchannel to `" + parsedCommandInvocation.getMessage().getMentionedChannels().get(0).getName() + "`"));
                case "join":
                    return EmbedUtil.message(handleEventUpdate(LogEventKeys.JOIN, args[1]));
                case "leave":
                    return EmbedUtil.message(handleEventUpdate(LogEventKeys.LEAVE, args[1]));
                case "command":
                    return EmbedUtil.message(handleEventUpdate(LogEventKeys.COMMAND, args[1]));
                case "ban":
                    return EmbedUtil.message(handleEventUpdate(LogEventKeys.BAN, args[1]));
                case "role":
                    return EmbedUtil.message(handleEventUpdate(LogEventKeys.ROLE, args[1]));
                case "voice":
                    return EmbedUtil.message(handleEventUpdate(LogEventKeys.VOICE, args[1]));
            }
        }
        return createHelpMessage();
    }

    /**
     * Handle event update.
     *
     * @param event the event
     * @param option the option
     * @return the embed builder
     */
    private EmbedBuilder handleEventUpdate(ServerLogHandler.LogEventKeys event, String option) {
        if (option.equalsIgnoreCase("true") || option.equalsIgnoreCase("enable")) {
            serverLogSQL.set(event.getKey(), "true");
            return EmbedUtil.success("Success!", "Successfully **enabled** " + event.getDisplayname().toLowerCase() + " logging");
        } else if (option.equalsIgnoreCase("false") || option.equalsIgnoreCase("disable")) {
            serverLogSQL.set(event.getKey(), "false");
            return EmbedUtil.success("Success!", "Successfully **disabled** " + event.getDisplayname().toLowerCase() + " logging");
        } else
            return EmbedUtil.error("Error!", "Wrong arguments. Use `enable` or `disable`");
    }

    /**
     * Generate list.
     *
     * @return the embed builder
     */
    private EmbedBuilder generateList() {
        HashMap<LogEventKeys, Boolean> eventStats = new HashMap<>();

        for (LogEventKeys event : LogEventKeys.getAllKeys()) {
            String entry = serverLogSQL.get(event.getKey());
            if (entry.equalsIgnoreCase("true"))
                eventStats.put(event, true);
            else
                eventStats.put(event, false);
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_PRIMARY);
        builder.setAuthor("List of log events", null, parsedCommandInvocation.getMessage().getGuild().getIconUrl());

        for (Map.Entry entry : eventStats.entrySet()) {
            builder.addField(((LogEventKeys) entry.getKey()).getDisplayname() + " Event", ((boolean) entry.getValue()) ? "enabled" : "disabled", false);
        }
        builder.setDescription("Enable or disable an log event with `" + parsedCommandInvocation.getPrefix() + "log <event> <enable/disable>`");
        return builder;
    }
}
