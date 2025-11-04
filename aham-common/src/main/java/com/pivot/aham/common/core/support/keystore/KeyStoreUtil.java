package com.pivot.aham.common.core.support.keystore;

import org.springframework.util.ResourceUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import lombok.extern.slf4j.Slf4j;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月21日
 */
@Slf4j
public class KeyStoreUtil {

    public static PrivateKey getPrivateKey(String cerPath, String password) {
        FileInputStream fileInputStream = null;
        PrivateKey privateKey = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            System.out.println("keystore type=" + keyStore.getType());

            fileInputStream = new FileInputStream(ResourceUtils.getFile("classpath:" + cerPath));
            keyStore.load(fileInputStream, password.toCharArray());
            fileInputStream.close();

            Enumeration<String> enumeration = keyStore.aliases();

            if (enumeration.hasMoreElements()) {
                String keyAlias = enumeration.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
                privateKey = (PrivateKey) keyStore.getKey(keyAlias, password.toCharArray());
            }

        } catch (KeyStoreException e) {
            log.error("获取keyStore错误", e);
        } catch (FileNotFoundException e) {
            log.error("获取证书文件错误", e);
        } catch (CertificateException e) {
            log.error("证书错误", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("算法错误", e);
        } catch (IOException e) {
            log.error("证书文件流错误", e);
        } catch (UnrecoverableKeyException e) {
            log.error("解析私钥异常", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.error("证书文件流关闭错误", e);
                }
            }
        }
        return privateKey;
    }

    public static PublicKey getPublicKey(String cerPath, String password) {
        FileInputStream fileInputStream = null;
        PublicKey publicKey = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            fileInputStream = new FileInputStream(ResourceUtils.getFile("classpath:" + cerPath));
            keyStore.load(fileInputStream, password.toCharArray());
            fileInputStream.close();

            Enumeration<String> enumeration = keyStore.aliases();

            if (enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();
                Certificate certificate = keyStore.getCertificate(alias);
                publicKey = certificate.getPublicKey();
            }

        } catch (KeyStoreException e) {
            log.error("获取keyStore错误", e);
        } catch (FileNotFoundException e) {
            log.error("获取证书文件错误", e);
        } catch (CertificateException e) {
            log.error("证书错误", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("算法错误", e);
        } catch (IOException e) {
            log.error("证书文件流错误", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.error("证书文件流关闭错误", e);
                }
            }
        }
        return publicKey;
    }

    public static Certificate getCertificate(String cerPath, String password) {
        FileInputStream fileInputStream = null;
        Certificate certificate = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            fileInputStream = new FileInputStream(ResourceUtils.getFile("classpath:" + cerPath));
            keyStore.load(fileInputStream, password.toCharArray());
            fileInputStream.close();

            Enumeration<String> enumeration = keyStore.aliases();

            if (enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();
                certificate = keyStore.getCertificate(alias);
            }

        } catch (KeyStoreException e) {
            log.error("获取keyStore错误", e);
        } catch (FileNotFoundException e) {
            log.error("获取证书文件错误", e);
        } catch (CertificateException e) {
            log.error("证书错误", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("算法错误", e);
        } catch (IOException e) {
            log.error("证书文件流错误", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.error("证书文件流关闭错误", e);
                }
            }
        }
        return certificate;
    }

    /**
     * 获取证书签名
     *
     * @param cert
     * @return
     */
    public static String getThumbprint(Certificate cert) {
        MessageDigest md = null;
        byte[] digest = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] der = new byte[0];
        try {
            der = cert.getEncoded();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        if (md != null) {
            md.update(der);
            digest = md.digest();
        }
        String digestHex = DatatypeConverter.printHexBinary(digest);
        return digestHex.toLowerCase();
    }

}
