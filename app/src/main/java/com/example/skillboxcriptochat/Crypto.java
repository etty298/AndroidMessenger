package com.example.skillboxcriptochat;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crypto {
    private static final String pass = "медвежьяпипирочка";
    private static SecretKeySpec keySpec;
    static {
        try {
            MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = pass.getBytes();
            shaDigest.update(bytes, 0, bytes.length);
            byte[] hash = shaDigest.digest();
            keySpec = new SecretKeySpec(hash, "AES");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String unEncryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(unEncryptedMessage.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(String decryptedMessage) throws Exception {
        byte[] ciphered = Base64.decode(decryptedMessage, Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] rawText = cipher.doFinal(ciphered);
        return new String(rawText, "UTF-8");
    }
}
