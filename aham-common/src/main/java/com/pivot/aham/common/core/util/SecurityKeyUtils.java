/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.common.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import org.bouncycastle.util.encoders.Base64;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

/**
 * Utility class for creating Java security objects from PEM strings.
 */
public class SecurityKeyUtils {

    private static final String PEM_PUBLIC_START = "-----BEGIN PUBLIC KEY-----";
    private static final String PEM_PUBLIC_END = "-----END PUBLIC KEY-----";

    // PKCS#8 format
    private static final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
    private static final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";

    // PKCS#1 format
    private static final String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
    private static final String PEM_RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";

    /**
     * Create a RSA Private Ket from a PEM String. It supports PKCS#1 and PKCS#8
     * string formats
     *
     * @param pemPrivateKey RSA private key in PEM format
     * @return private key object
     * @throws java.security.GeneralSecurityException
     */
    public static PrivateKey parseRSAPrivateKey(final String pemPrivateKey) throws GeneralSecurityException {
        try {
            if (pemPrivateKey.contains(PEM_PRIVATE_START)) { // PKCS#8 format
                String privateKeyString = pemPrivateKey
                        .replace(PEM_PRIVATE_START, "")
                        .replace(PEM_PRIVATE_END, "")
                        .replaceAll("\\s", "");
                byte[] keyBytes = Base64.decode(privateKeyString.getBytes("UTF-8"));
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory fact = KeyFactory.getInstance("RSA");
                return fact.generatePrivate(keySpec);

            } else if (pemPrivateKey.contains(PEM_RSA_PRIVATE_START)) {  // PKCS#1 format
                String privateKeyString = pemPrivateKey
                        .replace(PEM_RSA_PRIVATE_START, "")
                        .replace(PEM_RSA_PRIVATE_END, "")
                        .replaceAll("\\s", "");

                DerInputStream derReader = new DerInputStream(Base64.decode(privateKeyString));

                DerValue[] seq = derReader.getSequence(0);

                if (seq.length < 9) {
                    throw new GeneralSecurityException("Could not parse a PKCS1 private key.");
                }

                // skip version seq[0];
                BigInteger modulus = seq[1].getBigInteger();
                BigInteger publicExp = seq[2].getBigInteger();
                BigInteger privateExp = seq[3].getBigInteger();
                BigInteger prime1 = seq[4].getBigInteger();
                BigInteger prime2 = seq[5].getBigInteger();
                BigInteger exp1 = seq[6].getBigInteger();
                BigInteger exp2 = seq[7].getBigInteger();
                BigInteger crtCoef = seq[8].getBigInteger();

                RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);

                KeyFactory factory = KeyFactory.getInstance("RSA");

                return factory.generatePrivate(keySpec);
            }
            throw new GeneralSecurityException("Not valid private key.");

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            throw new GeneralSecurityException(e);
        }
    }

    /**
     * Parse an X509 Cert from a PEM string
     *
     * @param certificateString PEM format
     * @return X.509 certificate object
     * @throws java.security.GeneralSecurityException
     */
    public static X509Certificate parseX509Certificate(final String certificateString) throws GeneralSecurityException {
        try {
            CertificateFactory f = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) f.generateCertificate(new ByteArrayInputStream(certificateString.getBytes("UTF-8")));
            return certificate;

        } catch (UnsupportedEncodingException e) {
            throw new GeneralSecurityException(e);
        }
    }

    /**
     * Creates a RSA Public Key from a PEM String
     *
     * @param pemPublicKey public key in PEM format
     * @return a RSA public key
     * @throws java.security.GeneralSecurityException
     */
    public static PublicKey parseRSAPublicKey(final String pemPublicKey) throws GeneralSecurityException {
        try {
            String publicKeyString = pemPublicKey.replace(PEM_PUBLIC_START, "")
                    .replace(PEM_PUBLIC_END, "").replaceAll("\\s", "");
            byte[] keyBytes = Base64.decode(publicKeyString.getBytes("UTF-8"));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new GeneralSecurityException(e);
        }
    }

    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] cipherText = encryptCipher.doFinal(plainText.getBytes("UTF-8"));

        return java.util.Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
        byte[] bytes = java.util.Base64.getDecoder().decode(cipherText);

        Cipher decriptCipher = Cipher.getInstance("RSA");
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(decriptCipher.doFinal(bytes), "UTF-8");
    }

    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes("UTF-8"));

        byte[] signature = privateSignature.sign();

        return java.util.Base64.getEncoder().encodeToString(signature);
    }

    public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes("UTF-8"));

        byte[] signatureBytes = java.util.Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }

}
