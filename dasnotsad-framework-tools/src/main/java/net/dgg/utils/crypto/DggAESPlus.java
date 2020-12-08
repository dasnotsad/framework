package net.dgg.utils.crypto;


import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密工具类
 */
public class DggAESPlus {


    /**
     * @param content 需要加密的内容
     * @param keyWord 加密密钥
     * @return byte[] 加密后的字节数组
     */
    private static byte[] encryptBin(String content, String keyWord) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(keyWord.getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("UTF-8");//content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param content  需要加密的内容
     * @param password 加密密钥
     * @return String 加密后的字符串
     */
    public static String encrypt(String content, String password) {
        return parseByte2HexStr(encryptBin(content, password));
    }

    /**
     * 解密
     *
     * @param content 待解密内容
     * @param keyWord 解密密钥
     * @return byte[]
     */
    public static byte[] decrypt(byte[] content, String keyWord) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(keyWord.getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param content 待解密内容(字符串)
     * @param keyWord 解密密钥
     * @return byte[]
     */
    public static String decrypt(String content, String keyWord) {
        try {
            return new String(decrypt(parseHexStr2Byte(content), keyWord), "UTF-8");
        } catch (Exception se) {
            se.printStackTrace();
        }
        return new String(decrypt(parseHexStr2Byte(content), keyWord));
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return String
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return byte[]
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) {
        String content = "FB27708E6BE308AF77DFF5E99CBD03D7AFEB7AF9039E299DF55DCA26B414F66FB0178675D1631BCB4AB8FF99DD84A95C5FC68FEA253D9DD90021986B799960D34B4EA75A48F557E2599F25FD93CE6D2482476086F623B83AC4BCCB1AD8E590132DA9D7CAC1913A315FF6502BE1A42813B0789595F1C69C025A7BBCE3F79A42547A00EB8989E9D8A92315306F716C78E6868045AC8A449A7CAB8A8F35E00601A908F21EE0814B8FCF860243760306079649B114604987E1F01CF8DD5179651A44925C913B467F77DE08B13626588119D7";
        String Key = "noka2jkl^swf@#$LKJds567q9KLJ^*1lkjsad";
        // 加密
        //System.out.println("加密前：" + content);
        //String encryptResult = encrypt(content, Key);
        //System.out.println("加密后：" + encryptResult);
        System.out.println("解密后：" + DggAESPlus.decrypt(content, Key));
        //System.out.println("解密后：" + decrypt(encryptResult, Key));
    }
}
