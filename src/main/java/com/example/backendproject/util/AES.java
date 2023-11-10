package com.example.backendproject.util;

import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class AES {
    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_TAG_LENGTH = 16;

    public static String encrypt(String strToEncrypt, String secret, String ivBase64)
    {
        try
        {
            byte[] strToEncryptBytes = strToEncrypt.getBytes("UTF-8");

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), secret.getBytes(), 65536, AES_KEY_SIZE);
            SecretKey secretKey = factory.generateSecret(spec);

            byte[] iv = decodeIV(ivBase64);

            return Base64.getEncoder().encodeToString(encrypt(strToEncryptBytes, secretKey, iv));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String strToDecrypt, String secret, String ivBase64) {
        try
        {
            byte[] strToDecryptBytes = Base64.getDecoder().decode(strToDecrypt);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), secret.getBytes(), 65536, AES_KEY_SIZE);
            SecretKey secretKey = factory.generateSecret(spec);

            byte[] iv = decodeIV(ivBase64);

            return decrypt(strToDecryptBytes, secretKey, iv);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encrypt(byte[] plaintext, SecretKey key, byte[] IV) throws Exception
    {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

        // Perform Encryption
        byte[] cipherText = cipher.doFinal(plaintext);

        return cipherText;
    }

    public static String decrypt(byte[] cipherText, SecretKey key, byte[] IV) throws Exception
    {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

        // Perform Decryption
        byte[] decryptedText = cipher.doFinal(cipherText);

        return new String(decryptedText);
    }

    public static String generateIV(int length) {
        byte[] IV = new byte[length];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        System.out.println("IV: " + Arrays.toString(IV));
        String ivBase64 = Base64.getEncoder().encodeToString(IV);
        System.out.println("ivBase64: " + ivBase64);
        return ivBase64;
    }

    public static byte[] decodeIV(String ivBase64) {
        return Base64.getDecoder().decode(ivBase64);
    }

    public static String generateSecret(int length) {
        String secret = RandomStringUtils.randomAlphanumeric(length);
        System.out.println("secret: " + secret);
        return secret;
    }

    public static void main(String[] args) throws Exception
    {
        String ivBase64 = generateIV(12);
//        String ivBase64 = "5WUO3jBK14p4GbMO";

        String secret = generateSecret(200);
//        String secret = "R8L!OIsS6N0M@cg#d_8mkI28so0so8tLpbvx1UhteGCos+3vKeMyufdneuS_u^wod3R8sOKeds|ZkG-OC6YG9@F^8Uii5yWucg";
//
//        String plainText = "This is a plain text which need to be encrypted by Java AES 256 GCM Encryption Algorithm";
//
//        String cipherText = encrypt(plainText, secret, ivBase64);
//        System.out.println("Encrypted Text : " + cipherText);
//
//        String decryptedText = decrypt(cipherText, secret, ivBase64);
//        System.out.println("DeCrypted Text : " + decryptedText);
    }
}
