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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.util.DevCommandLog;
import net.romvoid.discord.util.EmbedUtil;
import net.romvoid.discord.util.GlobalBlacklist;
import net.romvoid.discord.util.Info;
import net.romvoid.discord.util.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandManager.
 */
public class CommandManager extends ListenerAdapter {
    
    /** The command associations. */
    private final Map<String, CommandHandler> commandAssociations = new HashMap<>();

    /**
     * Constructs and registers the command manager.
     */
    public CommandManager() {
        VoidBot.registerEventListener(this);
    }

    /**
     * Registers multiple CommandHandlers with their invocation aliases.
     *
     * @param commandHandlers the CommandHandlers to register.
     */
    public void registerCommandHandlers(CommandHandler... commandHandlers) {
        for (CommandHandler commandHandler : commandHandlers)
            registerCommandHandler(commandHandler);
    }

    /**
     * Registers a CommandHandler with it's invocation aliases.
     *
     * @param commandHandler the {@link CommandHandler} to be registered.
     */
    public void registerCommandHandler(CommandHandler commandHandler) {
        for (String invokeAlias : commandHandler.getCommandAliases())
            // only register if alias is not taken
            if (commandAssociations.containsKey(invokeAlias.toLowerCase()))
                Logger.warning("The '" + commandHandler.toString()
                        + "' CommandHandler tried to register the alias '" + invokeAlias
                        + "' which is already taken by the '" + commandAssociations.get(invokeAlias).toString()
                        + "' CommandHandler.");
            else
                commandAssociations.put(invokeAlias.toLowerCase(), commandHandler);
    }

    /**
     * On message received.
     *
     * @param event the event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        if (event.isFromType(ChannelType.PRIVATE)) return;

        super.onMessageReceived(event);
        ParsedCommandInvocation commandInvocation = parse(event.getMessage());
        //Send typing because it's useless
        if (commandInvocation != null && !event.getAuthor().isBot() && !event.getAuthor().isFake() && !event.isWebhookMessage()) {
            if (GlobalBlacklist.isOnBlacklist(event.getAuthor())) {
                event.getTextChannel().sendMessage(EmbedUtil.message(EmbedUtil.error("Blacklisted", "You are blacklisted from using TheVoid! ;)"))).queue(msg -> msg.delete().queueAfter(20, TimeUnit.SECONDS));
                return;
            }
            call(commandInvocation);
        }
    }

    /**
     * Call the CommandHandler for commandInvocation.
     *
     * @param parsedCommandInvocation the parsed message.
     */
    private void call(ParsedCommandInvocation parsedCommandInvocation) {
        CommandHandler commandHandler = getCommandHandler(parsedCommandInvocation.getCommandInvocation());
        Message response;
        if (commandHandler == null) {
            /*response = EmbedUtil.message(EmbedUtil.withTimestamp(EmbedUtil.error("Unknown command", "'" + parsedCommandInvocation.serverPrefix + parsedCommandInvocation.invocationCommand
                    + "' could not be resolved to a command.\nType '" + parsedCommandInvocation.serverPrefix
                    + "help' to get a list of all commands.")));*/
            return;
        } else {
            DevCommandLog.log(parsedCommandInvocation);
            response = commandHandler.call(parsedCommandInvocation);
        }

        // respond
        if (response != null)
            EmbedUtil.sendAndDeleteOnGuilds(parsedCommandInvocation.getMessage().getChannel(), response);

        // delete invocation message
        if (parsedCommandInvocation.getGuild() != null) {
            if (!parsedCommandInvocation.getGuild().getSelfMember().getPermissions(parsedCommandInvocation.getTextChannel()).contains(Permission.MESSAGE_MANAGE))
                return; // Do not try to delete message when bot is not allowed to
            parsedCommandInvocation.getMessage().delete().queue(null, msg -> {
            }); // suppress failure
        }
    }

    /**
     * Parses a raw message into command components.
     *
     * @param message the discord message to parse.
     * @return a {@link ParsedCommandInvocation} with the parsed arguments or null if the message could not be
     * resolved to a command.
     */
    private static ParsedCommandInvocation parse(Message message) {
        String prefix = null;
        // react to mention: '@botmention<majorcommand> [arguments]'
        if (message.getContentRaw().startsWith(VoidBot.getJDA().getSelfUser().getAsMention())) {
            prefix = VoidBot.getJDA().getSelfUser().getAsMention();
            // react to default prefix: '>>><majorcommand> [arguments]'
        } else if (message.getContentRaw().toLowerCase().startsWith(Info.BOT_DEFAULT_PREFIX.toLowerCase())) {
            prefix = message.getContentRaw().substring(0, Info.BOT_DEFAULT_PREFIX.length());
        }
        // react to custom server prefix: '<custom-server-prefix><majorcommand> [arguments...]'
        else if (message.getChannelType() == ChannelType.TEXT) { // ensure bot is on a server
            String serverPrefix = VoidBot.getMySQL().getGuildValue(message.getGuild(), "prefix");
            if (message.getContentRaw().toLowerCase().startsWith(serverPrefix.toLowerCase()))
                prefix = serverPrefix;
        }

        if (prefix != null) {
            // cut off command prefix
            String beheaded = message.getContentRaw().substring(prefix.length(), message.getContentRaw().length()).trim();
            // split arguments
            String[] allArgs = beheaded.split("\\s+");
            // create an array of the actual command arguments (exclude invocation arg)
            String[] args = new String[allArgs.length - 1];
            System.arraycopy(allArgs, 1, args, 0, args.length);
            return new ParsedCommandInvocation(message, prefix, allArgs[0], args);
        }
        // else
        return null; // = message is not a command
    }

    /**
     * Gets the command handler.
     *
     * @param invocationAlias the key property to the CommandHandler.
     * @return the associated CommandHandler or null if none is associated.
     */
    public CommandHandler getCommandHandler(String invocationAlias) {
        return commandAssociations.get(invocationAlias.toLowerCase());
    }

    /**
     * Gets the command associations.
     *
     * @return a clone of all registered command associations.
     */
    public Map<String, CommandHandler> getCommandAssociations() {
        return new HashMap<>(commandAssociations);
    }

    /**
     * The Class ParsedCommandInvocation.
     */
    public static final class ParsedCommandInvocation {

        /** The args new. */
        private final String[] argsNew;
        
        /** The command invocation. */
        private final String commandInvocation;
        
        /** The message. */
        private final Message message;
        
        /** The prefix. */
        private final String prefix;

        /**
         * Instantiates a new parsed command invocation.
         *
         * @param invocationMessage the invocation message
         * @param serverPrefix the server prefix
         * @param invocationCommand the invocation command
         * @param args the args
         */
        private ParsedCommandInvocation(Message invocationMessage, String serverPrefix, String invocationCommand, String[] args) {
            this.message = invocationMessage;
            this.prefix = serverPrefix;
            this.commandInvocation = invocationCommand;
            this.argsNew = args;
        }

        /**
         * Gets the message.
         *
         * @return the message
         */
        public Message getMessage() {
            return message;
        }

        /**
         * Gets the guild.
         *
         * @return the guild
         */
        public Guild getGuild() {
            return message.getGuild();
        }

        /**
         * Gets the args.
         *
         * @return the args
         */
        public String[] getArgs() {
            return argsNew;
        }

        /**
         * Gets the command invocation.
         *
         * @return the command invocation
         */
        public String getCommandInvocation() {
            return commandInvocation;
        }

        /**
         * Gets the prefix.
         *
         * @return the prefix
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * Gets the self member.
         *
         * @return the self member
         */
        public Member getSelfMember() {
            return message.getGuild().getSelfMember();
        }

        /**
         * Gets the author.
         *
         * @return the author
         */
        public User getAuthor() {
            return message.getAuthor();
        }

        /**
         * Gets the member.
         *
         * @return the member
         */
        public Member getMember() {
            return message.getMember();
        }

        /**
         * Gets the text channel.
         *
         * @return the text channel
         */
        public TextChannel getTextChannel() {
            return message.getTextChannel();
        }
    }
}
