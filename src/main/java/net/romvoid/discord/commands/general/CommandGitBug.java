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

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.EmbedUtil;
import net.romvoid.discord.util.Info;
import net.romvoid.discord.util.SafeMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandGitBug.
 */
public class CommandGitBug extends CommandHandler {
    
    /** The report map. */
    private static HashMap<Long, ReportHolder> reportMap = new HashMap<>();

    /**
     * Instantiates a new command git bug.
     */
    public CommandGitBug() {
        super(new String[]{"bug", "bugreport"}, CommandCategory.GENERAL, new PermsNeeded("command.bug", false, true), "Report an Bug", "<Bug title>");
    }

    /** The Constant ISSUE_HEADER. */
    private static final String ISSUE_HEADER = "<p><strong>Bugreport</strong><br><br><strong>Bug report by ";
    
    /** The Constant ISSUE_SUFFIX. */
    private static final String ISSUE_SUFFIX = " </strong><br><br><strong>Description</strong><br><br></p>";


    /**
     * Execute.
     *
     * @param parsedCommandInvocation the parsed command invocation
     * @param userPermissions the user permissions
     * @return the message
     */
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length<1){
            return createHelpMessage();
        }
        Message infoMessage = SafeMessage.sendMessageBlocking(parsedCommandInvocation.getTextChannel(), EmbedUtil.message(new EmbedBuilder().setTitle("Description....").setDescription("Please enter a short description.").setFooter("Will abort in 60 seconds.", null)));
        reportMap.put(parsedCommandInvocation.getAuthor().getIdLong(), new ReportHolder(
                parsedCommandInvocation.getTextChannel(),
                parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation() + " ", ""),
                parsedCommandInvocation.getAuthor(),
                infoMessage
        ));
        return null;
    }

    /**
     * Handle.
     *
     * @param event the event
     */
    public static void handle(MessageReceivedEvent event) {
        if (!reportMap.containsKey(event.getAuthor().getIdLong())) {
            return;
        }
        ReportHolder reportHolder = reportMap.get(event.getAuthor().getIdLong());
        if (event.getMessage().getContentDisplay().contains(reportHolder.title))
            return;
        if (!event.getTextChannel().getId().equals(reportHolder.textChannel.getId()))
            return;
        String description = event.getMessage().getContentDisplay();
        try {
        	
            GitHub gitHub = GitHub.connectUsingOAuth(Info.GITHUB_TOKEN);
            GHRepository repository = gitHub.getOrganization("TheVoidBot-Bot").getRepository("TheVoidBot");
            GHIssue issue = repository.createIssue(reportHolder.title).body(ISSUE_HEADER + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ISSUE_SUFFIX + description).label("Bug").label("Requires Testing").create();
            reportHolder.delete(issue.getHtmlUrl().toString());
            event.getMessage().delete().queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The Class ReportHolder.
     */
    private static class ReportHolder {
        
        /** The text channel. */
        private TextChannel textChannel;
        
        /** The title. */
        private String title;
        
        /** The author. */
        private User author;
        
        /** The info message. */
        private Message infoMessage;

        /** The timer. */
        private Timer timer;

        /**
         * Instantiates a new report holder.
         *
         * @param textChannel the text channel
         * @param title the title
         * @param author the author
         * @param infoMessage the info message
         */
        private ReportHolder(TextChannel textChannel, String title, User author, Message infoMessage) {
            this.textChannel = textChannel;
            this.title = title;
            this.author = author;
            this.infoMessage = infoMessage;

            //Abort
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    infoMessage.delete().queue();
                    SafeMessage.sendMessage(textChannel, EmbedUtil.message(EmbedUtil.error("Aborted!", "Aborted bug report.")));
                    reportMap.remove(author.getIdLong());
                }
            }, 60000);
        }

        /**
         * Delete.
         *
         * @param link the link
         */
        private void delete(String link) {
            reportMap.remove(author.getIdLong());
            timer.cancel();
            SafeMessage.sendMessage(textChannel, EmbedUtil.message(EmbedUtil.success("Success!", "Successfully reported bug. [Your Report](" + link + ")")));
            infoMessage.delete().queue();
        }
    }
}
