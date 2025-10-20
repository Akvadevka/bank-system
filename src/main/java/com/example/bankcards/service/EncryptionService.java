package com.example.bankcards.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionService {
    @Value("${app.encryption-key}")
    private String secretKey;

    private static final String ALGORITHM = "AES";

    private SecretKeySpec getKeySpec() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException("AES key must be 16 bytes long. Check your application.yml configuration.");
        }
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public String encrypt(String value) throws Exception {
        SecretKeySpec keySpec = getKeySpec();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }


    public String decrypt(String encryptedValue) throws Exception {
        SecretKeySpec keySpec = getKeySpec();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] originalBytes = Base64.getDecoder().decode(encryptedValue);
        byte[] decryptedBytes = cipher.doFinal(originalBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}