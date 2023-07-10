package com.e2i.wemeet.util.encryption;

import static com.e2i.wemeet.exception.ErrorCode.DATA_ENCRYPTION_ERROR;

import com.e2i.wemeet.exception.internal.InternalServerException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public abstract class EncryptionUtils {

    private EncryptionUtils() {
    }

    public static String hashData(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerException(DATA_ENCRYPTION_ERROR);
        }
    }

    public static String decodeHashData(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Arrays.toString(Base64.getDecoder().decode(encodedHash));
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerException(DATA_ENCRYPTION_ERROR);
        }
    }
}
