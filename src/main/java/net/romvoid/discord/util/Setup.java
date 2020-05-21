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
package net.romvoid.discord.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// TODO: Auto-generated Javadoc
/**
 * The Class Setup.
 */
public class Setup {

    /** The Constant sys_in. */
    private static final BufferedReader sys_in;

    static {
        InputStreamReader isr = new InputStreamReader(System.in);
        sys_in = new BufferedReader(isr);
    }

    /**
     * Prompt.
     *
     * @param req the req
     * @return the string
     */
    public static String prompt(String req) {
        String token;

        // prompt for token
        System.out.println("Enter The Bots " + req + ":");


        try {
            // read and trim line
            String line = sys_in.readLine();
            token = line.trim();

            return token;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        // RAGEQUIT
        System.out.println("Exiting");
        Runtime.getRuntime().exit(1);
        return null;
    }
}
