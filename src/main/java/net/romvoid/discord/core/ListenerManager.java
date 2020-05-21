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
package net.romvoid.discord.core;

import net.dv8tion.jda.core.JDABuilder;
import net.romvoid.discord.listener.AutochannelListener;
import net.romvoid.discord.listener.BotJoinListener;
import net.romvoid.discord.listener.BotLeaveListener;
import net.romvoid.discord.listener.ChannelDeleteListener;
import net.romvoid.discord.listener.MessageListener;
import net.romvoid.discord.listener.PortalListener;
import net.romvoid.discord.listener.ReadyListener;
import net.romvoid.discord.listener.RoleListener;
import net.romvoid.discord.listener.SelfMentionListener;
import net.romvoid.discord.listener.ServerLogHandler;
import net.romvoid.discord.listener.VerificationListener;

// TODO: Auto-generated Javadoc
/**
 * The Class ListenerManager.
 */
public class ListenerManager {

    /** The b. */
    private JDABuilder b;

    /**
     * Instantiates a new listener manager.
     *
     * @param builder the builder
     */
    public ListenerManager(JDABuilder builder) {
        this.b = builder;
        initListener();
    }

    /**
     * Inits the listener.
     */
    private void initListener() {
        b.addEventListener(new SelfMentionListener());
        b.addEventListener(new BotJoinListener());
        b.addEventListener(new ChannelDeleteListener());
        b.addEventListener(new BotLeaveListener());
        b.addEventListener(new PortalListener());
        b.addEventListener(new AutochannelListener());
        b.addEventListener(new VerificationListener());
        b.addEventListener(new ServerLogHandler());
        b.addEventListener(new ChannelDeleteListener());
        b.addEventListener(new RoleListener());
        b.addEventListener(new MessageListener());
        b.addEventListener(new ReadyListener());

    }
}
