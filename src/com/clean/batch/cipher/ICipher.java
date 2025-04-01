package com.clean.batch.cipher;

public interface ICipher {
    String encrypt(String text) throws SecurityException;
    String decrypt(String encryptText) throws SecurityException;
}
