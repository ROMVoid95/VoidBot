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
package net.romvoid.discord.commands.admin;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.command.CommandCategory;
import net.romvoid.discord.command.CommandHandler;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.features.VerificationKickHandler;
import net.romvoid.discord.features.VerificationUserHandler;
import net.romvoid.discord.permission.PermsNeeded;
import net.romvoid.discord.permission.UserPermissions;
import net.romvoid.discord.util.EmbedUtil;
import net.romvoid.discord.util.SafeMessage;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandVerification.
 */
public class CommandVerification extends CommandHandler {

    /** The setups. */
    public static HashMap<Guild, VerificationSetup> setups = new HashMap<>();
    
    /** The settingslist. */
    private static HashMap<Guild, VerificationSettings> settingslist = new HashMap<>();
    
    /** The users. */
    public static HashMap<Message, User> users = new HashMap<>();

    /** The show inspired. */
    public static boolean showInspired = false;

    /**
     * Instantiates a new command verification.
     */
    public CommandVerification() {
        super(new String[]{"verification", "verify"}, CommandCategory.ADMIN, new PermsNeeded("command.verification", false, false), "Let you members accept rules before posting messages\n\nThis feature is partially inspired by [Flashbot](https://flashbot.de)", "setup\ndisable");
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
        String[] args = parsedCommandInvocation.getArgs();
        if (args.length == 0) {
            return createHelpMessage();
        }
        if (args[0].equalsIgnoreCase("disable"))
            return disableVerification(parsedCommandInvocation);
        else if (args[0].equalsIgnoreCase("setup"))
            return enableVerification(parsedCommandInvocation);
        else
            return createHelpMessage();

    }

    /**
     * Disable verification.
     *
     * @param parsedCommandInvocation the parsed command invocation
     * @return the message
     */
    private Message disableVerification(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        if (!VoidBot.getMySQL().verificationEnabled(message.getGuild())) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not enabled", "Verification System is not enabled on this guild").build()).build();
        }

        VoidBot.getMySQL().deleteGuildVerification(message.getGuild());

        return new MessageBuilder().setEmbed(EmbedUtil.success("Disabled", "Successfully disabled verification system").build()).build();
    }

    /**
     * Enable verification.
     *
     * @param parsedCommandInvocation the parsed command invocation
     * @return the message
     */
    private Message enableVerification(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Message message = parsedCommandInvocation.getMessage();
        if (VoidBot.getMySQL().verificationEnabled(message.getGuild())) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Already enabled", "Verification System is already enabled on this guild").build()).build();
        }
        MessageEmbed embed = EmbedUtil.info("Step 1 - Confirm setup", "Please react with :white_check_mark: \n **Requirements:** \n - Channel for the verification messages \n - Role to be added to the users \n - Custom accept emoji").build();
        Message setupmsg = message.getTextChannel().sendMessage(embed).complete();
        setupmsg.addReaction("âœ…").queue();
        setupmsg.addReaction("â�Œ").queue();
        VerificationSetup setup = new VerificationSetup(parsedCommandInvocation, setupmsg);
        setups.put(message.getGuild(), setup);
        users.put(setupmsg, message.getAuthor());
        return null;
    }

    /**
     * The Class VerificationSetup.
     */
    public class VerificationSetup {
        
        /** The message. */
        public Message message;
        
        /** The author. */
        public User author;
        
        /** The guild. */
        Guild guild;
        
        /** The step. */
        public int step;

        /**
         * Instantiates a new verification setup.
         *
         * @param parsedCommandInvocation the parsed command invocation
         * @param message the message
         */
        VerificationSetup(CommandManager.ParsedCommandInvocation parsedCommandInvocation, Message message) {
            this.message = message;
            this.author = parsedCommandInvocation.getMessage().getAuthor();
            this.guild = parsedCommandInvocation.getMessage().getGuild();
            this.step = 1;
        }
    }

    /**
     * The Class VerificationSettings.
     */
    public static class VerificationSettings {

        /** The channel. */
        public TextChannel channel;
        
        /** The verifytext. */
        public String verifytext;
        
        /** The verifiedtext. */
        public String verifiedtext;
        
        /** The role. */
        public Role role;
        
        /** The emote. */
        public MessageReaction.ReactionEmote emote;
        
        /** The kicktime. */
        public int kicktime;
        
        /** The kicktext. */
        public String kicktext;

        /**
         * Instantiates a new verification settings.
         *
         * @param verificationChannel the verification channel
         * @param verifytext the verifytext
         * @param verifiedtext the verifiedtext
         * @param verifiedrole the verifiedrole
         * @param kicktime the kicktime
         * @param kicktext the kicktext
         * @param emote the emote
         */
        VerificationSettings(TextChannel verificationChannel, String verifytext, String verifiedtext, Role verifiedrole, int kicktime, String kicktext, MessageReaction.ReactionEmote emote) {
            this.channel = verificationChannel;
            this.verifytext = verifytext;
            this.verifiedtext = verifiedtext;
            this.role = verifiedrole;
            this.emote = emote;
            this.kicktime = kicktime;
            this.kicktext = kicktext;
        }
    }

    /**
     * Handle reaction.
     *
     * @param event the event
     */
    public static void handleReaction(MessageReactionAddEvent event) {
        Message message = null;
        try {
            message = event.getTextChannel().getMessageById(event.getMessageId()).complete();
        } catch (InsufficientPermissionException ignored) {
        }
        if (message == null) return;

        if (!message.getAuthor().equals(event.getJDA().getSelfUser())) return;
        if (!event.getUser().equals(users.get(message))) return;
        if (VoidBot.getMySQL().verificationEnabled(event.getGuild())) {
            TextChannel channel = event.getGuild().getTextChannelById(VoidBot.getMySQL().getVerificationValue(event.getGuild(), "channelid"));
            if (event.getTextChannel().equals(channel)) {
                event.getReaction().removeReaction().queue();
                String emote = VoidBot.getMySQL().getVerificationValue(event.getGuild(), "emote");
                if (!emote.equals(event.getReactionEmote().getName()) && !emote.equals(event.getReactionEmote().getId()))
                    return;
                Role verfied = event.getGuild().getRoleById(VoidBot.getMySQL().getVerificationValue(event.getGuild(), "roleid"));
                if (!event.getGuild().getSelfMember().canInteract(verfied)) {
                    event.getTextChannel().sendMessage(EmbedUtil.error("Error!", "I can not assign roles that are higher than my role.").build()).queue();
                }

                event.getGuild().getController().addRolesToMember(event.getMember(), verfied).queue();
                VerificationUserHandler.VerifyUser.fromMember(event.getMember()).remove();
                message.getReactions().forEach(r -> {
                    r.removeReaction().queue();
                });
                message.editMessage(VoidBot.getMySQL().getVerificationValue(event.getGuild(), "verifiedtext").replace("%user%", event.getUser().getAsMention()).replace("%guild%", event.getGuild().getName())).queue();
                message.getReactions().forEach(r -> {
                    r.getUsers().forEach(u -> {
                        r.removeReaction(u).queue();
                    });
                });
                message.delete().queueAfter(5, TimeUnit.SECONDS);
                VerificationKickHandler.VerifyKick.fromMember(event.getMember(), true).remove();
            }
        } else {
            if (!setups.containsKey(event.getGuild())) return;
            if (!setups.get(event.getGuild()).author.equals(event.getUser())) return;
            if (!event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE)) {
                message.editMessage(EmbedUtil.error("Aborted", "Bot has not the Permissions MESSAGE_MANAGE.Please contact the Owner for the Permissions").build()).queue();
                return;
            }
            event.getReaction().removeReaction(event.getUser()).queue();
            message.getReactions().forEach(r -> r.removeReaction().queue());
            String emote = event.getReactionEmote().getName();
            if (setups.get(event.getGuild()).step == 4)
                setupStepFour(event);
            else if (emote.equalsIgnoreCase("âœ…"))
                message.editMessage(EmbedUtil.info("Step 2 - Channel", "Please mention the channel where verification messages should me posted").build()).queue();
            else if (emote.equalsIgnoreCase("â�Œ"))
                message.editMessage(EmbedUtil.error("Aborted", "Successfully aborted setup").build()).queue();
        }
    }

    /**
     * Setup step one.
     *
     * @param message the message
     * @param response the response
     */
    public static void setupStepOne(Message message, Message response) {
        if (response.getMentionedChannels().isEmpty()) {
            setups.remove(message.getGuild());
            message.delete().queue();
            return;
        }
        VerificationSettings settings = new VerificationSettings(response.getMentionedChannels().get(0), null, null, null, 0, null, null);
        settingslist.put(message.getGuild(), settings);

        message.editMessage(EmbedUtil.info("Step 3 - Verify message", "Please enter the text of the message that'll be sent to new users. (Use `%user%` to mention the user and `%guild%` for the servername)").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
    }

    /**
     * Setup step two.
     *
     * @param message the message
     * @param response the response
     */
    public static void setupStepTwo(Message message, Message response) {
        if (response.getContentDisplay().length() > 1048) {
            SafeMessage.sendMessage(message.getTextChannel(), EmbedUtil.message(EmbedUtil.error("Too long", "Your message can't be longer than 1048 chars")), 4);
            return;
        }
        VerificationSettings settings = settingslist.get(message.getGuild());
        settings.verifytext = response.getContentDisplay();
        settingslist.replace(message.getGuild(), settings);
        message.editMessage(EmbedUtil.info("Step 4 - Verified message", "Please enter the message that should be sent when a user accepted rules (Use `%user%` to mention the user and `%guild%` for the servername)").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
    }

    /**
     * Setup step three.
     *
     * @param message the message
     * @param response the response
     */
    public static void setupStepThree(Message message, Message response) {
        if (response.getContentDisplay().length() > 1048) {
            SafeMessage.sendMessage(message.getTextChannel(), EmbedUtil.message(EmbedUtil.error("Too long", "Your message can't be longer than 1048 chars")), 4);
            return;
        }
        VerificationSettings settings = settingslist.get(message.getGuild());
        settings.verifiedtext = response.getContentDisplay();
        settingslist.replace(message.getGuild(), settings);
        message.editMessage(EmbedUtil.info("Step 4 - Verified emote", "Please react with the verify emote. Emote must be a **custom** emote from **your** server.").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
    }

    /**
     * Sets the up step four.
     *
     * @param event the new up step four
     */
    public static void setupStepFour(MessageReactionAddEvent event) {
        Message message = event.getTextChannel().getMessageById(event.getMessageId()).complete();
        VerificationSettings settings = settingslist.get(event.getGuild());
        MessageReaction.ReactionEmote emote = event.getReactionEmote();
        //System.out.println(event.getReactionEmote().getEmote().isManaged());
        try {


            if (!event.getReactionEmote().getEmote().isManaged()) {
                if (!event.getGuild().getEmotes().contains(emote.getEmote())) {
                    SafeMessage.sendMessage(message.getTextChannel(), EmbedUtil.message(EmbedUtil.error("Unsupported emote", "You can only use global or custom emotes of your server")), 4);
                    return;
                }
            }
        } catch (NullPointerException ignored) {

        }
        settings.emote = emote;
        settingslist.replace(event.getGuild(), settings);
        message.editMessage(EmbedUtil.info("Step 5 - Verified role", "Please mention the role that should be added to user").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
        event.getReaction().removeReaction(event.getUser()).queue();
    }

    /**
     * Setup step five.
     *
     * @param message the message
     * @param response the response
     */
    public static void setupStepFive(Message message, Message response) {
        VerificationSettings settings = settingslist.get(message.getGuild());
        if (response.getMentionedRoles().isEmpty()) {
            setups.remove(message.getGuild());
            settingslist.remove(message.getGuild());
            message.delete().queue();
            return;
        }
        settings.role = response.getMentionedRoles().get(0);
        settingslist.replace(message.getGuild(), settings);
        message.editMessage(EmbedUtil.info("Step 6 - Time", "Please enter the time (minutes) after that the user should be kicked when he does not accept the rules").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);

    }

    /**
     * Setup step six.
     *
     * @param message the message
     * @param response the response
     */
    public static void setupStepSix(Message message, Message response) {
        int kicktime;
        try {
            kicktime = Integer.parseInt(response.getContentDisplay());
        } catch (NumberFormatException e) {
            SafeMessage.sendMessageBlocking(message.getTextChannel(), EmbedUtil.message(EmbedUtil.error("Invalid number", "Please enter a valid number")));
            return;
        }
        VerificationSettings settings = settingslist.get(message.getGuild());
        if (kicktime == 0) {
            settings.kicktext = "NULL";
            message.editMessage(EmbedUtil.success("Saved!", "Successfully enabled verification").build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            setups.remove(message.getGuild());
            settingslist.remove(message.getGuild());
            VoidBot.getMySQL().createVerification(settings);
            return;
        }
        settings.kicktime = kicktime;
        message.editMessage(EmbedUtil.info("Step 7 - Kick message", "Please enter the message that'll be sent to the user after he got kicked use `%invite%` to embed a custom invite link to verification channel (1 user limited) or `%guild%` for the servername").build()).queue();
        VerificationSetup setup = setups.get(message.getGuild());
        setup.step++;
        setups.replace(message.getGuild(), setup);
    }

    /**
     * Setup step seven.
     *
     * @param message the message
     * @param response the response
     */
    public static void setupStepSeven(Message message, Message response) {
        if (response.getContentDisplay().length() > 1048) {
            SafeMessage.sendMessage(message.getTextChannel(), EmbedUtil.message(EmbedUtil.error("Too long", "Your message can't be longer than 1048 chars")), 4);
            return;
        }
        VerificationSettings settings = settingslist.get(message.getGuild());
        settings.kicktext = response.getContentDisplay();
        settingslist.replace(message.getGuild(), settings);
        VoidBot.getMySQL().createVerification(settings);
        message.delete().queue();
        message.getTextChannel().sendMessage((EmbedUtil.success("Saved!", "Successfully enabled verification").build())).queue();
        setups.remove(message.getGuild());
        settingslist.remove(message.getGuild());
    }

    /**
     * Toggle inspired.
     */
    public static void toggleInspired() {
        if (showInspired) {
            showInspired = false;
        } else {
            showInspired = true;
        }
    }
}
