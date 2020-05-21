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

import java.awt.*;

// TODO: Auto-generated Javadoc
/**
 * Side-colors for embedded messages.
 *
 * @author ROMVoid
 * 
 * Need more Colors
 */
public class Colors {
    /**
     * Standard messages containing information like successful command responses.
     */
    public static Color COLOR_PRIMARY = new Color(88, 198, 33);
    /**
     * For messages containing additional information.
     */
    public static Color COLOR_SECONDARY = new Color(18, 109, 229);
    /**
     * For error messages that are not defined in further purpose.
     */
    public static Color COLOR_ERROR = new Color(229, 60, 18);
    /**
     * For permission-related error messages.
     */
    public static Color COLOR_NO_PERMISSION = new Color(75, 31, 94);
    /**
     * For (yet) unimplemented features.
     */
    public static Color COLOR_NOT_IMPLEMENTED = new Color(243, 156, 18);

    /** For Premium messages. */
    public static Color COLOR_PREMIUM = new Color(255, 215, 0);
}
