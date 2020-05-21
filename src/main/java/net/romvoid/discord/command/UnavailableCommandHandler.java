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

import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.EmbedUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class UnavailableCommandHandler.
 */
public class UnavailableCommandHandler extends CommandHandler {
    /**
     * Constructs an unavailable command handler out of original data.
     *
     * @param invocationAliases      the invocation commands (aliases). First entry is the 'main' alias.
     * @param category               the {@link CommandCategory} this command belongs to.
     * @param permissionRequirements all permission requirements a user needs to meet to execute a command.
     * @param description            a short command description.
     * @param parameterUsage         the usage message.
     */
    public UnavailableCommandHandler(String[] invocationAliases, CommandCategory category, PermsNeeded permissionRequirements, String description, String parameterUsage) {
        super(invocationAliases, category, permissionRequirements, description, parameterUsage);
    }

    /**
     * Constructs an unavailable command handler an unavailable command handler's data.
     *
     * @param unavailableCommandHandler the CommandHandler whose information will be used.
     */
    public UnavailableCommandHandler(CommandHandler unavailableCommandHandler) {
        super(unavailableCommandHandler.getCommandAliases(), unavailableCommandHandler.getCategory(),
                unavailableCommandHandler.getPermsNeeded(), unavailableCommandHandler.getDescription(),
                unavailableCommandHandler.getParameterUsage());
    }

    /**
     * Execute.
     *
     * @param invocation the invocation
     * @param permissions the permissions
     * @return the message
     */
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions permissions) {
        return EmbedUtil.message(EmbedUtil.error("Feature not available!",
                "Sorry, this feature is not available right now."));
    }
}
