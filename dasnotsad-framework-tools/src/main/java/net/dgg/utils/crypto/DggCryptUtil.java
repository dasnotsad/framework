package net.dgg.utils.crypto;

import net.dgg.utils.constant.DigestAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * @Description: 加密解密工具类
 * @Author Created by yan.x on 2020-09-08 .
 **/
public final class DggCryptUtil {
    protected static Logger logger = LoggerFactory.getLogger(DggCryptUtil.class);

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * Base64编码
     *
     * @param text 明文
     * @return Base64编码后的字符串
     */
    public static String encodeBase64(String text) {
        return Base64.encodeBase64String(text.getBytes());
    }

    /**
     * Base64解码
     *
     * @param text Base64编码后的字符串
     * @return 明文字符串
     */
    public static String decodeBase64(String text) {
        return new String(Base64.decodeBase64(text));
    }

    /**
     * MD5加密
     *
     * @param text 明文
     * @return MD5密文字符串
     */
    public static String md5(String text) {
        return DggEncryptionUtil.encodeHex(text, DigestAlgorithm.MD5, true);
    }

    /**
     * AES加密
     *
     * @param content 明文
     * @param key     密钥
     * @return 密文
     */
    public static String encryptAES(String content, String key) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            byte[] bt = content.getBytes("UTF-8");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));
            byte[] result = cipher.doFinal(bt);
            return Base64.encodeBase64String(result);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage());
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage());
        } catch (BadPaddingException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * AES解密
     *
     * @param content 密文
     * @param key     密钥
     * @return 明文
     */
    public static String decryptAES(String content, String key) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage());
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage());
        } catch (BadPaddingException e) {
            logger.error(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private static SecretKeySpec getSecretKey(final String password) throws NoSuchAlgorithmException {
        KeyGenerator key = KeyGenerator.getInstance(KEY_ALGORITHM);
        key.init(128, new SecureRandom(password.getBytes()));
        SecretKey secretKey = key.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }

}