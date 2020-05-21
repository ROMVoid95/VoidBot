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

// TODO: Auto-generated Javadoc
/**
 * Permission requirements.
 *
 * @author ROMVoid
 */
public class PermsNeeded {
    
    /** The perms needed node. */
    private final String permsNeededNode;
    
    /** The is author exclusive. */
    private final boolean isAuthorExclusive;
    
    /** The is default. */
    private final boolean isDefault;

    /**
     * Constructs a new PermissionRequirements object.
     *
     * @param permsNeededNode 		the permission node that allows a user to pass the permission check.
     * @param isBotOnwerOnly      	whether this permission is exclusively granted for bot authors.
     * @param isDefault             whether users should have this permission by default (if no other permission entry covers it).
     */
    public PermsNeeded(String permsNeededNode, boolean isBotOnwerOnly, boolean isDefault) {
        this.permsNeededNode = permsNeededNode;
        this.isAuthorExclusive = isBotOnwerOnly;
        this.isDefault = isDefault;
    }

    /**
     * Checks whether the conditions set in this object are met by a user's permissions.
     *
     * @param userPermissions the user permissions access object.
     * @return true if all conditions are met, false otherwise.
     */
    public boolean coveredBy(UserPermissions userPermissions) {
        // bot authors have all permissions
        if (userPermissions.isBotAuthor())
            return true;

        // author exclusive permissions are not accessible for other users
        if (isAuthorExclusive)
            return false;

        // server owner has all perms on his server
        if (userPermissions.isServerOwner())
            return true;

        //MASTER permissions
        if (userPermissions.getEffectivePermissionEntry(null, "command.*") != null) {
            return true;
        }

        Permission effectiveEntry = userPermissions.getEffectivePermissionEntry(null, permsNeededNode);
        if (effectiveEntry == null) {
            // defaults
            return isDefault;
        } else
            // check permission
            return !effectiveEntry.isNegated();
    }

    /**
     * Gets the required permission node.
     *
     * @return the permission node that lets a user pass
     */
    public String getRequiredPermissionNode() {
        return permsNeededNode;
    }

    /**
     * Checks if is author exclusive.
     *
     * @return whether this permission is author exclusive.
     */
    public boolean isAuthorExclusive() {
        return isAuthorExclusive;
    }

    /**
     * Checks if is default.
     *
     * @return whether this permission is granted by default (if no other permission entry covers it).
     */
    public boolean isDefault() {
        return isDefault;
    }
}
