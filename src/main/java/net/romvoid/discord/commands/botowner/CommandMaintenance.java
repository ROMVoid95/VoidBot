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

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.Colors;
import net.romvoid.discord.util.EmbedUtil;
import net.romvoid.discord.util.SafeMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandMaintenance.
 */
public class CommandMaintenance extends CommandHandler {

    /** The maintenance. */
    public static boolean maintenance = false;


    /**
     * Instantiates a new command maintenance.
     */
    public CommandMaintenance() {
        super(new String[]{"maintenance", "wartung"}, CommandCategory.BOT_OWNER, new PermsNeeded("command.maintenance", true, false), "Starts bot maintenance.", "<message for playing status>");
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
        if (parsedCommandInvocation.getArgs().length == 1) {
            if (parsedCommandInvocation.getArgs()[0].equalsIgnoreCase("false")) {
                disable();
                return EmbedUtil.message(EmbedUtil.success("Disabled maintenance", "Successfully disabled maintenance"));
            }
        }
        if (parsedCommandInvocation.getArgs().length < 1)
            return createHelpMessage();

        //Enabling with MaintenanceCommand
        VoidBot.getConfiguration().set("maintenance", "1");
        //Build Status message
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < parsedCommandInvocation.getArgs().length; i++) {
            msg.append(parsedCommandInvocation.getArgs()[i]).append(" ");
        }
        //Set playing status
        VoidBot.getConfiguration().set("playingStatus", msg.toString());
        enable();
        SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), new EmbedBuilder().setColor(Colors.COLOR_PRIMARY).setTitle("Activated Maintenance").setAuthor(parsedCommandInvocation.getAuthor().getName(), null, parsedCommandInvocation.getAuthor().getEffectiveAvatarUrl()).setDescription("Bot will only respond to owners.").build());
        return null;
    }

    /**
     * Disable.
     */
    public static void disable() {
        maintenance = false;
        VoidBot.getConfiguration().set("playingStatus", "0");
        VoidBot.getConfiguration().set("maintenance", "0");
        VoidBot.getJDA().getPresence().setGame(Game.playing("Maintenance is over"));
        VoidBot.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
    }

    /**
     * Enable.
     */
    public static void enable() {
        maintenance = true;
        VoidBot.getJDA().getPresence().setGame(Game.playing(VoidBot.getConfiguration().getString("playingStatus")));
        VoidBot.getJDA().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
    }
}

