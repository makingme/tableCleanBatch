package com.clean.batch.extra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESCipherUtil {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final static String ALGORITHM = "AES/CBC/PKCS5Padding";

    // 128:16byte 256:32byte 필수 조건
    private final static String encryptKey = "1234qwerasdfzxcv";

    private final SecretKeySpec KEY_SPEC;

    private final IvParameterSpec IV_PARAM_SPEC;

    public AESCipherUtil() {
        IV_PARAM_SPEC = new IvParameterSpec(encryptKey.substring(0, 16).getBytes());
        KEY_SPEC = new SecretKeySpec(encryptKey.getBytes(), "AES");
    }

    public AESCipherUtil(String encryptKey) {
        IV_PARAM_SPEC = new IvParameterSpec(encryptKey.substring(0, 16).getBytes());
        KEY_SPEC = new SecretKeySpec(encryptKey.getBytes(), "AES");
    }

    public AESCipherUtil(String encryptKey, byte[] ivParams) {
        IV_PARAM_SPEC = new IvParameterSpec(ivParams);
        KEY_SPEC = new SecretKeySpec(encryptKey.getBytes(), "AES");
    }

    public static AESCipherUtil getInstance() {
        return AESCipherUtil.Singleton.instance;
    }

    private static class Singleton {
        private static final AESCipherUtil instance = new AESCipherUtil();
    }

    public String encrypt(String text){
        String encryptText = text;
        if (text ==null || text.trim().isEmpty()) {
            return "";
        }


        try {
            //Cipher 객체 인스턴스 획득
            Cipher encryptCipher = Cipher.getInstance(ALGORITHM);

            //Cipher 객체 초기화
            encryptCipher.init(Cipher.ENCRYPT_MODE, KEY_SPEC, IV_PARAM_SPEC);
            byte[] encrypted = encryptCipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            encryptText = Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            LOGGER.warn("<암호화 에러> = 암호화 중 에러 발생", e);
            return encryptText;
        }
        return encryptText;
    }

    public String decrypt(String encryptText){
        String plainText = encryptText;
        if (encryptText ==null || encryptText.trim().isEmpty()) {
            return plainText;
        }


        try {
            //Cipher 객체 인스턴스 획득
            Cipher decryptCipher = Cipher.getInstance(ALGORITHM);

            //Cipher 객체 초기화
            decryptCipher.init(Cipher.DECRYPT_MODE, KEY_SPEC, IV_PARAM_SPEC);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptText);
            byte[] decrypted = decryptCipher.doFinal(decodedBytes);
            plainText = new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.warn("<복호화 에러> = 복호화 중 에러 발생", e);
            return plainText;
        }
        return plainText;
    }

    public static void main(String[] args) throws Exception {
        if(args.length <1){
            System.out.println("암호화 할 문자열을 입력 하세요.");
            return;
        }
        String plainText = args[0];
        String encryptText = AESCipherUtil.getInstance().encrypt(plainText);
        System.out.println("=============== 암호화 결과 ===============");
        System.out.println(encryptText);
        System.out.printf("입력 문자열: [%s], 암호화된 문자열: [%s]", plainText, encryptText);
        System.out.println();
    }

}
