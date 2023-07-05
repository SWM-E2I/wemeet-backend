package com.e2i.wemeet.util.encryption;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptionUtils {

    private EncryptionUtils() {
    }

    public static String hashData(String data) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(data);
    }
}
