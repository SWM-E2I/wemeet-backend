package com.e2i.wemeet.util;

import java.security.SecureRandom;

public abstract class RandomCodeUtils {

    private static final SecureRandom random = new SecureRandom();

    private RandomCodeUtils() {
    }

    public static String crateCredential() {
        int code = random.nextInt(900_000) + 100_000;
        return String.valueOf(code);
    }

    public static String createMemberCode() {
        int code = random.nextInt(9000) + 1000;
        return String.valueOf(code);
    }
}
