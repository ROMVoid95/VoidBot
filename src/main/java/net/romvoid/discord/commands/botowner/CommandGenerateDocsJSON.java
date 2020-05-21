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
package net.romvoid.discord.commands.botowner;

import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.Info;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandGenerateDocsJSON.
 */
public class CommandGenerateDocsJSON extends CommandHandler {

    /**
     * Instantiates a new command generate docs JSON.
     */
    public CommandGenerateDocsJSON() {
        super(new String[]{"generate-docs", "gendocs"}, CommandCategory.BOT_OWNER, new PermsNeeded("command.generate-docs", true, false), "Generate json file fou our docs.", "");
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
        //Generate JSON File for website
        int i = 0;
        StringBuilder out = new StringBuilder();
        List<CommandHandler> allCommands = new ArrayList<>();
        for (CommandHandler commandHandler : VoidBot.getCommandManager().getCommandAssociations().values()) {
            if (!allCommands.contains(commandHandler))
                allCommands.add(commandHandler);
        }
        for (CommandHandler commandHandler : allCommands) {
            if (commandHandler.getCategory().equals(CommandCategory.BOT_OWNER))
                continue;
            StringBuilder usage = new StringBuilder();
            for (String part : commandHandler.getParameterUsage().split("\n")) {
                if (commandHandler.getParameterUsage().split("\n").length > 1) {
                    usage.append(Info.BOT_DEFAULT_PREFIX + commandHandler.getCommandAliases()[0] + " " + part + "<br>");
                } else
                    usage.append(Info.BOT_DEFAULT_PREFIX + commandHandler.getCommandAliases()[0] + " " + part + "");
            }
            out.append("{\n\"id\":\"" + i + "\",\"name\":\"" + commandHandler.getCommandAliases()[0] + "\",\n" +
                    "\t\"command\":\"" + Info.BOT_DEFAULT_PREFIX + commandHandler.getCommandAliases()[0] + "\",\n" +
                    "\t\"description\":\"" + commandHandler.getDescription() + "\",\n" +
                    "\t\"category\":\"" + commandHandler.getCategory().getId() + "\",\n" +
                    "\t\"aliases\":\"" + String.join(",", commandHandler.getCommandAliases()) + "\",\n" +
                    "\t\"permissions\":\"" + commandHandler.getPermsNeeded().getRequiredPermissionNode() + "\",\n" +
                    "\t\"usage\":\"" + usage + "\"\n},\n");
            i++;
        }
        try {
            File file = createCommandListFile();
            FileWriter writer = new FileWriter(file);
            String json = out.toString();
            String cutted = json.substring(0, json.length() - 2);
            cutted = "[" + cutted + "]";
            writer.write(cutted);
            writer.flush();
            writer.close();
            parsedCommandInvocation.getTextChannel().sendFile(file).complete();
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates the command list file.
     *
     * @return the file
     */
    private File createCommandListFile() {
        new File("data/temp").mkdirs();
        File file = new File("command-list.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
