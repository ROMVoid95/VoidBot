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

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.EmbedUtil;
import net.romvoid.discord.util.StringUtil;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandClear.
 */
public class CommandClear extends CommandHandler {

    /**
     * Instantiates a new command clear.
     */
    public CommandClear() {
        super(new String[]{"clear", "purge"}, CommandCategory.MODERATION, new PermsNeeded("command.clear", false, false), "Clear the chat.", "<amount of messages> [@User]");
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
            return createHelpMessage();
        }

        if (!StringUtil.isNumeric(parsedCommandInvocation.getArgs()[0])) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "Parameter must be numeric."));
        }

        int messageAmount = Integer.parseInt(parsedCommandInvocation.getArgs()[0]);
        User user = (parsedCommandInvocation.getMessage().getMentionedUsers().size() == 1) ? parsedCommandInvocation.getMessage().getMentionedUsers().get(0) : null;

        if (messageAmount < 2) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "I can't delete less than 2 messages."));
        }

        if (messageAmount > 3000) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "Why do you want to clear more than 3000 messages??"));
        }

        int deletedMessagesSize = 0;
        List<Message> messagesToDelete;
        while (messageAmount != 0) {
            if (messageAmount > 100) {
                messagesToDelete = parsedCommandInvocation.getMessage().getTextChannel().getHistory().retrievePast(100).complete();
                messageAmount -= 100;
            } else {
                messagesToDelete = parsedCommandInvocation.getMessage().getTextChannel().getHistory().retrievePast(messageAmount).complete();
                messageAmount = 0;
            }
            messagesToDelete = messagesToDelete.stream().filter(message -> !message.getCreationTime().isBefore(OffsetDateTime.now().minusWeeks(2))).collect(Collectors.toList());
            if (user != null)
                messagesToDelete = messagesToDelete.stream().filter(message -> message.getAuthor() == user).collect(Collectors.toList());
            deletedMessagesSize += messagesToDelete.size();
            if (messagesToDelete.size() > 1)
                parsedCommandInvocation.getMessage().getTextChannel().deleteMessages(messagesToDelete).complete();
            else break;
        }
        return EmbedUtil.message(EmbedUtil.success("Cleared channel!", "Successfully cleared `" + deletedMessagesSize + "` messages"));
    }
}
