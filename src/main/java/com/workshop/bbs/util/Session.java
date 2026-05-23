package com.workshop.bbs.util;

import com.workshop.bbs.model.User;

/**
 * Simple session holder — stores the currently logged-in user.
 */
public class Session {
    private static User currentUser;

    public static void setUser(User user) { currentUser = user; }
    public static User getUser() { return currentUser; }
    public static boolean isLoggedIn() { return currentUser != null; }
    public static void clear() { currentUser = null; }
}
