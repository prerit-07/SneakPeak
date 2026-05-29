package com.sneakpeak.streetpeak.common;

import java.security.SecureRandom;
import java.util.Locale;

public final class SlugUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private SlugUtil() {
    }

    public static String usernameFromEmail(String email) {
        String prefix = email == null ? "user" : email.split("@")[0];
        String cleaned = prefix.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "");
        if (cleaned.length() < 3) {
            cleaned = "user";
        }
        return cleaned.length() > 28 ? cleaned.substring(0, 28) : cleaned;
    }

    public static String shortSuffix() {
        return Integer.toString(RANDOM.nextInt(9000) + 1000);
    }
}
