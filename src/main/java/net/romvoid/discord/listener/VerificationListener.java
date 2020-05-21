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
package net.romvoid.discord.listener;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.commands.admin.CommandVerification;
import net.romvoid.discord.features.VerificationKickHandler;
import net.romvoid.discord.features.VerificationUserHandler;
import net.romvoid.discord.util.SafeMessage;
import net.romvoid.discord.util.StringUtil;

import java.util.Calendar;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving verification events.
 * The class that is interested in processing a verification
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addVerificationListener<code> method. When
 * the verification event occurs, that object's appropriate
 * method is invoked.
 *
 * @see VerificationEvent
 */
public class VerificationListener extends ListenerAdapter {

    /**
     * On guild message received.
     *
     * @param event the event
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!CommandVerification.setups.containsKey(event.getGuild())) return;
        if (!CommandVerification.setups.get(event.getGuild()).author.equals(event.getAuthor())) return;
        CommandVerification.VerificationSetup setup = CommandVerification.setups.get(event.getGuild());
        Message message = setup.message;
        Message response = event.getMessage();
        response.delete().queue();
        if (setup.step == 1)
            CommandVerification.setupStepOne(message, response);
        else if (setup.step == 2)
            CommandVerification.setupStepTwo(message, response);
        else if (setup.step == 3)
            CommandVerification.setupStepThree(message, response);
        else if (setup.step == 5)
            CommandVerification.setupStepFive(message, response);
        else if (setup.step == 6)
            CommandVerification.setupStepSix(message, response);
        else if (setup.step == 7)
            CommandVerification.setupStepSeven(message, response);
    }

    /**
     * Deactivate verification when channel got deleted.
     *
     * @param event the event
     */
    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        if (!VoidBot.getMySQL().verificationEnabled(event.getGuild())) return;
        if (!VoidBot.getMySQL().getVerificationValue(event.getGuild(), "channelid").equals(event.getChannel().getId()))
            return;

        VoidBot.getMySQL().deleteGuildVerification(event.getGuild());
    }

    /**
     * On guild member join.
     *
     * @param event the event
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getUser().isBot()) return;
        if (!VoidBot.getMySQL().verificationEnabled(event.getGuild())) return;
        if (event.getUser().isBot())
            return;
        TextChannel channel = event.getGuild().getTextChannelById(VoidBot.getMySQL().getVerificationValue(event.getGuild(), "channelid"));
        Message message = SafeMessage.sendMessageBlocking(channel, VoidBot.getMySQL().getVerificationValue(event.getGuild(), "text").replace("%user%", event.getUser().getAsMention()).replace("%guild%", event.getGuild().getName()));
        CommandVerification.users.put(message, event.getUser());

        String emoteRaw = VoidBot.getMySQL().getVerificationValue(event.getGuild(), "emote");
        if (!StringUtil.isNumeric(emoteRaw))
            message.addReaction(emoteRaw).queue();
        else
            message.addReaction(event.getJDA().getEmoteById(emoteRaw)).queue();
        int delay = Integer.parseInt(VoidBot.getMySQL().getVerificationValue(event.getGuild(), "kicktime"));
        if (delay == 0) return;
        new VerificationUserHandler.VerifyUser(event.getMember(), message);
        new VerificationKickHandler.VerifyKick(event.getGuild(), event.getMember(), getKickTime(delay), VoidBot.getMySQL().getVerificationValue(event.getGuild(), "kicktext").replace("%guild%", event.getGuild().getName()), message.getIdLong(), false, true);
    }

    /**
     * On guild member leave.
     *
     * @param event the event
     */
    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        if (event.getUser().isBot()) return;
        if (VerificationKickHandler.VerifyKick.exists(event.getMember())) {
            VerificationKickHandler.VerifyKick kick = VerificationKickHandler.VerifyKick.fromMember(event.getMember(), true);
            event.getJDA().getTextChannelById(VoidBot.getMySQL().getVerificationValue(event.getGuild(), "channelid")).getMessageById(kick.getMessageId()).complete().delete().queue();
            kick.remove();
        }
    }


    /**
     * Gets the kick time.
     *
     * @param mins the mins
     * @return the kick time
     */
    private Date getKickTime(int mins) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minutes = calendar.get(Calendar.MINUTE) + mins;
        calendar.set(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
}
