package net.dgg.utils.desensitization;


import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 电话号码加密工具类
 * @Author Created by yan.x on 2019-08-20 .
 **/
public class DggPhoneDecodeUtil {
    private final String CRYPT_KEY = "DGG99978998765432166dgg9";
    private SecretKey secretKey = null;
    private static final String ALGORITHM = "DESede";

    public void init() throws UnsupportedEncodingException {
        byte[] decodeKeyBytes = build3DesKey(CRYPT_KEY);
        secretKey = new SecretKeySpec(decodeKeyBytes, ALGORITHM);
    }

    private byte[] build3DesKey(String keyStr) throws UnsupportedEncodingException {
        byte[] key = new byte[24];
        byte[] temp = keyStr.getBytes("UTF-8");

        /**
         * 执行数组拷贝
         * System.arraycopy(源数组，从源数组哪里开始拷贝，目标数组，拷贝多少位)
         */
        if (key.length > temp.length) {
            System.arraycopy(temp, 0, key, 0, temp.length);
        } else {
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        return key;
    }

    /**
     * 加密
     *
     * @param src
     * @return
     */
    public String encode(String src) {
        try {
            byte[] srcBytes = src.getBytes("UTF-8");
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            // 初始化为加密模式
            c1.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] targetBytes = c1.doFinal(srcBytes);
            return Base64.encode(targetBytes);
        } catch (Exception e) {
            e.toString();
        }
        return "";
    }

    /**
     * 解密
     *
     * @param encodeStr
     * @return
     */
    public String decode(String encodeStr) {
        try {
            byte[] encodeBytes = Base64.decode(encodeStr);
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            // 初始化为解密模式
            c1.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] srcBytes = c1.doFinal(encodeBytes);
            return new String(srcBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
