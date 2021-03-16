package com.viettel.etc.utils.encoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/***
 * Hàm encoding & decoding đăng kiểm
 */
public class TripleDES {
    private final static Logger LOGGER = LoggerFactory.getLogger(TripleDES.class);
    static byte[] m_key = new byte[]
            {
                    1,
                    2,
                    3,
                    4,
                    5,
                    6,
                    7,
                    8,
                    9,
                    10,
                    11,
                    12,
                    13,
                    14,
                    15,
                    16,
                    17,
                    18,
                    19,
                    20,
                    21,
                    22,
                    23,
                    24
            };
    static byte[] m_iv = new byte[]
            {
                    8,
                    7,
                    6,
                    5,
                    4,
                    3,
                    2,
                    1
            };

    public static String encrypt(String message) {
        try {
            final SecretKey key = new SecretKeySpec(m_key, "DESede");
            final IvParameterSpec iv = new IvParameterSpec(m_iv);
            final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            final byte[] plainTextBytes = message.getBytes(StandardCharsets.UTF_8);
            final byte[] cipherText = cipher.doFinal(plainTextBytes);
            final String encodedCipherText = new sun.misc.BASE64Encoder().encode(cipherText);
            return encodedCipherText;
        } catch (java.security.InvalidAlgorithmParameterException e) {
            LOGGER.error("Invalid Algorithm", e);
            System.out.println("Invalid Algorithm");
        } catch (javax.crypto.NoSuchPaddingException e) {
            System.out.println("No Such Padding");
            LOGGER.error("No Such Padding", e);
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println("No Such Algorithm");
            LOGGER.error("No Such Algorithm", e);
        } catch (InvalidKeyException e) {
            System.out.println("Invalid Key");
            LOGGER.error("Invalid Key", e);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            LOGGER.error("BadPaddingException or IllegalBlockSizeException", e);
        }

        return null;
    }

    public static String decrypt(String message) {
        try {
            final SecretKey key = new SecretKeySpec(m_key, "DESede");
            final IvParameterSpec iv = new IvParameterSpec(m_iv);
            final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            decipher.init(Cipher.DECRYPT_MODE, key, iv);
            final byte[] encodedCipherText = new sun.misc.BASE64Decoder().decodeBuffer(message);
            final byte[] plainText = decipher.doFinal(encodedCipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (java.security.InvalidAlgorithmParameterException e) {
            LOGGER.error("Invalid Algorithm", e);
            System.out.println("Invalid Algorithm");
        } catch (javax.crypto.NoSuchPaddingException e) {
            System.out.println("No Such Padding");
            LOGGER.error("No Such Padding", e);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("No Such Algorithm");
            LOGGER.error("No Such Algorithm", e);
        } catch (InvalidKeyException | IOException e) {
            System.out.println("Invalid Key");
            LOGGER.error("Invalid Key", e);
        }
        return null;
    }
}
