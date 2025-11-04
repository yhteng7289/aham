/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.common.core.support.security;

import javax.crypto.Cipher;
import java.security.SecureRandom;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.BadPaddingException;
import lombok.extern.slf4j.Slf4j;

/**
 * This class shows how to securely perform AES encryption in GCM mode, with 256
 * bits key size.
 */
@Slf4j
public class SecuredGCMUsage {

    public static int AES_KEY_SIZE = 256;
    public static int IV_SIZE = 96;
    public static int TAG_BIT_LENGTH = 128;
    public static String ALGO_TRANSFORMATION_STRING = "AES/GCM/PKCS5Padding";

    public static void main(String args[]) {
        String messageToEncrypt = "ABCD";

        byte[] aadData = "random".getBytes(); // Any random data can be used as tag. Some common examples could be domain name...

        // Use different key+IV pair for encrypting/decrypting different parameters
        // Generating Key
        SecretKey aesKey = null;
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES"); // Specifying algorithm key will be used for 
            keygen.init(AES_KEY_SIZE); // Specifying Key size to be used, Note: This would need JCE Unlimited Strength to be installed explicitly 
            aesKey = keygen.generateKey();
        } catch (NoSuchAlgorithmException noSuchAlgoExc) {
            log.error("Key being request is for AES algorithm, but this cryptographic algorithm is not available in the environment " + noSuchAlgoExc);
            System.exit(1);
        }

        // Generating IV
        byte iv[] = new byte[IV_SIZE];
        SecureRandom secRandom = new SecureRandom();
        secRandom.nextBytes(iv); // SecureRandom initialized using self-seeding

        // Initialize GCM Parameters
        GCMParameterSpec gcmParamSpec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);

        byte[] encryptedText = aesEncrypt(messageToEncrypt, aesKey, gcmParamSpec, aadData);

        log.info("Encrypted Text = " + Base64.getEncoder().encodeToString(encryptedText));

        byte[] decryptedText = aesDecrypt(encryptedText, aesKey, gcmParamSpec, aadData); // Same key, IV and GCM Specs for decryption as used for encryption.

        log.info("Decrypted text " + new String(decryptedText));

        // Make sure not to repeat Key + IV pair, for encrypting more than one plaintext.
        secRandom.nextBytes(iv);
    }

    public static byte[] aesEncrypt(String message, SecretKey aesKey, GCMParameterSpec gcmParamSpec, byte[] aadData) {
        Cipher c = null;

        try {
            c = Cipher.getInstance(ALGO_TRANSFORMATION_STRING); // Transformation specifies algortihm, mode of operation and padding
        } catch (NoSuchAlgorithmException noSuchAlgoExc) {
            log.error("Exception while encrypting. Algorithm being requested is not available in this environment " + noSuchAlgoExc);
            System.exit(1);
        } catch (NoSuchPaddingException noSuchPaddingExc) {
            log.error("Exception while encrypting. Padding Scheme being requested is not available this environment " + noSuchPaddingExc);
            System.exit(1);
        }

        try {
            c.init(Cipher.ENCRYPT_MODE, aesKey, gcmParamSpec, new SecureRandom());
        } catch (InvalidKeyException invalidKeyExc) {
            log.error("Exception while encrypting. Key being used is not valid. It could be due to invalid encoding, wrong length or uninitialized " + invalidKeyExc);
            System.exit(1);
        } catch (InvalidAlgorithmParameterException invalidAlgoParamExc) {
            log.info("Exception while encrypting. Algorithm parameters being specified are not valid " + invalidAlgoParamExc);
            System.exit(1);
        }

        try {
            c.updateAAD(aadData); // add AAD tag data before encrypting
        } catch (IllegalArgumentException illegalArgumentExc) {
            log.info("Exception thrown while encrypting. Byte array might be null " + illegalArgumentExc);
            System.exit(1);
        } catch (IllegalStateException illegalStateExc) {
            log.info("Exception thrown while encrypting. CIpher is in an illegal state " + illegalStateExc);
            System.exit(1);
        } catch (UnsupportedOperationException unsupportedExc) {
            log.info("Exception thrown while encrypting. Provider might not be supporting this method " + unsupportedExc);
            System.exit(1);
        }

        byte[] cipherTextInByteArr = null;
        try {
            cipherTextInByteArr = c.doFinal(message.getBytes());
        } catch (IllegalBlockSizeException illegalBlockSizeExc) {
            log.info("Exception while encrypting, due to block size " + illegalBlockSizeExc);
            System.exit(1);
        } catch (BadPaddingException badPaddingExc) {            
            log.info("Exception while encrypting, due to padding scheme " + badPaddingExc);
            System.exit(1);
        }

        return cipherTextInByteArr;
    }

    public static byte[] aesDecrypt(byte[] encryptedMessage, SecretKey aesKey, GCMParameterSpec gcmParamSpec, byte[] aadData) {
        Cipher c = null;

        try {
            c = Cipher.getInstance(ALGO_TRANSFORMATION_STRING); // Transformation specifies algortihm, mode of operation and padding
        } catch (NoSuchAlgorithmException noSuchAlgoExc) {
            log.info("Exception while decrypting. Algorithm being requested is not available in environment " + noSuchAlgoExc);
            System.exit(1);
        } catch (NoSuchPaddingException noSuchAlgoExc) {
            log.info("Exception while decrypting. Padding scheme being requested is not available in environment " + noSuchAlgoExc);
            System.exit(1);
        }

        try {
            c.init(Cipher.DECRYPT_MODE, aesKey, gcmParamSpec, new SecureRandom());
        } catch (InvalidKeyException invalidKeyExc) {
            log.info("Exception while encrypting. Key being used is not valid. It could be due to invalid encoding, wrong length or uninitialized " + invalidKeyExc);
            System.exit(1);
        } catch (InvalidAlgorithmParameterException invalidParamSpecExc) {
            log.info("Exception while encrypting. Algorithm Param being used is not valid. " + invalidParamSpecExc);
            System.exit(1);
        }

        try {
            c.updateAAD(aadData); // Add AAD details before decrypting
        } catch (IllegalArgumentException illegalArgumentExc) {
            log.info("Exception thrown while encrypting. Byte array might be null " + illegalArgumentExc);
            System.exit(1);
        } catch (IllegalStateException illegalStateExc) {
            log.info("Exception thrown while encrypting. CIpher is in an illegal state " + illegalStateExc);
            System.exit(1);
        }

        byte[] plainTextInByteArr = null;
        try {
            plainTextInByteArr = c.doFinal(encryptedMessage);
        } catch (IllegalBlockSizeException illegalBlockSizeExc) {
            log.info("Exception while decryption, due to block size " + illegalBlockSizeExc);
            System.exit(1);
        } catch (BadPaddingException badPaddingExc) {
            badPaddingExc.printStackTrace();
            log.info("Exception while decryption, due to padding scheme " + badPaddingExc);
            System.exit(1);
        }

        return plainTextInByteArr;
    }
}
