package com.e2i.wemeet.util.encryption;

public interface TwoWayEncryption {
    String encrypt(String plainText);
    String decrypt(String cipherText);
}
