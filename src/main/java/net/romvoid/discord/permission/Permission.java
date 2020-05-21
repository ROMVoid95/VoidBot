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
package net.romvoid.discord.permission;

import java.util.Objects;

// TODO: Auto-generated Javadoc
/**
 * Represents a permission.
 *
 * @author ROMVoid
 */
public class Permission {
    
    /** The Constant NEGATION_CHARACTER. */
    public static final char NEGATION_CHARACTER = '!';

    /** The permission string. */
    private final String permissionString;
    
    /** The negated. */
    private final boolean negated;

    /**
     * Constructs a Permission object.
     *
     * @param permissionString the permission node.
     * @param negated          whether the permission is negated.
     */
    public Permission(String permissionString, boolean negated) {
        this.permissionString = Objects.requireNonNull(permissionString);
        this.negated = negated;
    }

    /**
     * Gets the permission string.
     *
     * @return the permission String.
     */
    public String getPermissionString() {
        return permissionString;
    }

    /**
     * Checks if is negated.
     *
     * @return whether the permission is negated.
     */
    public boolean isNegated() {
        return negated;
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Permission // object type
                && permissionString.equalsIgnoreCase(((Permission) obj).permissionString) // permission string
                && negated == ((Permission) obj).negated; // negated
    }

    /**
     * Equals ignore negation.
     *
     * @param obj the obj
     * @return true, if successful
     */
    public boolean equalsIgnoreNegation(Object obj) {
        return obj instanceof Permission // object type
                && permissionString.equalsIgnoreCase(((Permission) obj).permissionString); // permission string
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return negated ? NEGATION_CHARACTER + permissionString : permissionString;
    }

    /**
     * Parses a {@link Permission} object from the serialized form.
     *
     * @param permissionAsString serialized permission {@link String}.
     * @return the {@link Permission} object.
     */
    public static Permission parse(String permissionAsString) {
        return permissionAsString.startsWith(String.valueOf(NEGATION_CHARACTER))
                ? new Permission(permissionAsString.substring(1), true)
                : new Permission(permissionAsString, false);
    }
}
