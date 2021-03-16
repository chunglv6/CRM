package com.viettel.etc.utils.encoding;

import com.google.gson.Gson;
import com.viettel.etc.utils.Constants;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Formatter;
import java.util.Map;

@SuppressWarnings("restriction")
public class MomoEncoder {

    private final static Logger LOGGER = LoggerFactory.getLogger(MomoEncoder.class);
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    private static final String HMAC_SHA256 = "HmacSHA256";

    @SuppressWarnings("resource")
    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        try(Formatter formatter = new Formatter(sb)){
            for (byte b : bytes) {
                formatter.format("%02x", b);
            }
        };
        return sb.toString();
    }

    public static String signHmacSHA256(String data, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA256);
        Mac mac = Mac.getInstance(HMAC_SHA256);
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return toHexString(rawHmac);
    }

    public static String getSHA(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        for (byte b : bytes) {
            sb.append(HEX_CHARS[(((int) b & 0xFF) / 16) & 0x0F]);
            sb.append(HEX_CHARS[((int) b & 0xFF) % 16]);
        }
        return sb.toString();
    }

    public static String decode64(String s) {
        try {
            byte[] valueDecoded = Base64.decodeBase64(s.getBytes());
            return new String(valueDecoded);
        } catch (Exception e) {
            LOGGER.error("[decode64] Error: " + e);
            return "";
        }
    }

    public static String encode64(String s) {
        // encode data on your side using BASE64
        byte[] bytesEncoded = Base64.encodeBase64(s.getBytes());
        return new String(bytesEncoded);
    }

    public static String hashSHA256(String input) throws Exception {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(input.getBytes());
            BigInteger dis = new BigInteger(1, sha.digest());
            String result = dis.toString(16);
            if (!result.startsWith("0") && result.length() < 64) {
                result = "0" + result;
            }
            return result.toUpperCase();
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error("[hashSHA256] Error: " + ex);
            throw new Exception(ex);
        }
    }

    public static String hmacSha1(String value, String key) throws Exception {
        byte[] keyBytes = key.getBytes();
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, Constants.HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(Constants.HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(value.getBytes());
        return Base64.encodeBase64String(rawHmac);
    }

    public static String encryptRSA(byte[] dataBytes, String publicKey) throws Exception {
        // Note: You can use java.util.Base64 instead of sun.misc.*
        PublicKey pubk;
//        BASE64Decoder decoder = new BASE64Decoder();
//        BASE64Encoder encoder = new BASE64Encoder();
        java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
        java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();

        byte[] publicKeyBytes = decoder.decode(publicKey);
//        byte[] publicKeyBytes = decoder.decodeBuffer(publicKey);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        pubk = keyFactory.generatePublic(publicKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubk);
        return encoder.encodeToString(cipher.doFinal(dataBytes)).replace("\r", "");
//        return new String(encoder.encode(cipher.doFinal(dataBytes)).replace("\r", ""));
    }

    public static String decryptRSA(String encryptData, String privateKey) {
        try {
            byte[] privateKeyBytes = Base64.decodeBase64(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey prvk = keyFactory.generatePrivate(privateKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, prvk);
            return new String(cipher.doFinal(Base64.decodeBase64(encryptData)));
        } catch (Exception ex) {
            LOGGER.error("[DecryptRSA] Error: " + ex);
            return "";
        }
    }

    public static String generateRSA(Map<String, Object> rawData, String publicKey) throws Exception {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(rawData);
        byte[] testByte = jsonStr.getBytes(StandardCharsets.UTF_8);
        String hashRSA = MomoEncoder.encryptRSA(testByte, publicKey);
        return hashRSA;
    }

    public static String generateMoMoRSA(String rawData, String publicKey) throws Exception {
        byte[] testByte = rawData.getBytes(StandardCharsets.UTF_8);
        String hashRSA = MomoEncoder.encryptRSA(testByte, publicKey);
        return hashRSA;
    }

}
