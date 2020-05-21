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

import java.util.stream.Collectors;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.RichPresence;
import net.romvoid.discord.VoidBot;
import net.romvoid.discord.util.Info;

// TODO: Auto-generated Javadoc
/**
 * The Class GameAnimator.
 */
public class GameAnimator {

    /** The t. */
    private static Thread t;
    
    /** The running. */
    private static boolean running = false;
    
    /** The current game. */
    private static int currentGame = 0;
    
    /** The game. */
    private static Game game;

    /** The Constant gameAnimations. */
    private static final String[] gameAnimations = {
            Info.BOT_NAME + " " + Info.BOT_VERSION,
            "Running on " + VoidBot.getJDA().getGuilds().size() + " servers",
            "Serving " + VoidBot.getJDA().getUsers().stream().filter(u -> u.isBot() == false).collect(Collectors.toList()).size() + " users"
            

    };
    
    /**
     * Sets the game.
     *
     * @return the game
     */
    public static Game setGame() {
    	game=RichPresence.playing("Galacticraft");
    	game.asRichPresence().getLargeImage();
		return game;
    	
    }

    /**
     * Start.
     */
    public static synchronized void start() {
        if (!VoidBot.getConfiguration().has("playingStatus")) {
            VoidBot.getConfiguration().set("playingStatus", "0");
        }
        if (!running) {
            t = new Thread(() -> {
                long last = 0;
                while (running) {
                    if (System.currentTimeMillis() >= last + 30000) {
                        if (VoidBot.getJDA().getStatus().isInit() == true) {
                            String playStat = VoidBot.getConfiguration().getString("playingStatus");
                            if (!playStat.equals("0") && !playStat.equals("")) {
                                VoidBot.getJDA().getPresence().setGame(Game.playing(playStat));
                                last = System.currentTimeMillis();
                            } else {
                                VoidBot.getJDA().getPresence().setGame(Game.playing("-help | " + gameAnimations[currentGame]));

                                if (currentGame == gameAnimations.length - 1)
                                    currentGame = 0;
                                else
                                    currentGame += 1;
                                last = System.currentTimeMillis();
                            }
                        }
                    }
                }
            });
            t.setName("GameAnimator");
            running = true;
            t.start();
        }
    }

    /**
     * Stop.
     */
    public static synchronized void stop() {
        if (running) {
            try {
                running = false;
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}