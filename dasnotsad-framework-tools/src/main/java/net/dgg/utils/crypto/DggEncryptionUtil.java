package net.dgg.utils.crypto;


import net.dgg.utils.constant.DigestAlgorithm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * describe: 加密工具类{@link DigestAlgorithm}
 * 使用Java自带的MessageDigest类
 * since: 1.0
 * Created by hot.sun on 2018/4/13 .
 **/
public final class DggEncryptionUtil {

    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    /**
     * 使用位运算符，将加密后的数据转换成16进制(默认小写)
     *
     * @param source          : 需要加密的字符串
     * @param digestAlgorithm : 加密类型 （MD5 和 SHA）
     * @return
     */
    public static String encodeHex(String source, DigestAlgorithm digestAlgorithm) {
        return encodeHex(source, digestAlgorithm, true);
    }

    /**
     * 使用位运算符，将加密后的数据转换成16进制
     *
     * @param source          : 需要加密的字符串
     * @param digestAlgorithm : 加密类型 （MD5 和 SHA）
     * @param toLowerCase     <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return
     */
    public static String encodeHex(String source, DigestAlgorithm digestAlgorithm, boolean toLowerCase) {
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlgorithm.getValue());
            byte[] encryptStr = md.digest(source.getBytes()); // 获得密文完成哈希计算,产生128 位的长整数
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对每一个字节,转换成 16 进制字符的转换
                byte byte0 = encryptStr[i]; // 取第 i 个字节
                str[k++] = toLowerCase ? DIGITS_LOWER[byte0 >>> 4 & 0xf] : DIGITS_UPPER[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
                str[k++] = toLowerCase ? DIGITS_LOWER[byte0 & 0xf] : DIGITS_UPPER[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            return new String(str); // 换后的结果转换为字符串
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 采用格式化的方式加密字符串
     *
     * @param source          : 需要加密的字符串
     * @param digestAlgorithm : 加密类型 （MD5 和 SHA）
     * @return
     */
    public static String encodeHexFormat(String source, DigestAlgorithm digestAlgorithm) {
        StringBuilder sb = new StringBuilder();
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(digestAlgorithm.getValue());
            md5.update(source.getBytes());
            for (byte b : md5.digest()) {
                sb.append(String.format("%02X", b)); // 10进制转16进制，X 表示以十六进制形式输出，02 表示不足两位前面补0输出
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用算法，将加密后的数据转换成16进制
     *
     * @param source          : 需要加密的字符串
     * @param digestAlgorithm : 加密类型 （MD5 和 SHA）
     * @return
     */
    public static String encodeHex1(String source, DigestAlgorithm digestAlgorithm) {
        return encodeHex1(source, digestAlgorithm, true);
    }

    /**
     * 使用算法，将加密后的数据转换成16进制
     *
     * @param source          : 需要加密的字符串
     * @param digestAlgorithm : 加密类型 （MD5 和 SHA）
     * @param toLowerCase     <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return
     */
    public static String encodeHex1(String source, DigestAlgorithm digestAlgorithm, boolean toLowerCase) {
        StringBuilder sb = new StringBuilder();
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(digestAlgorithm.getValue());
            md5.update(source.getBytes());
            byte[] encryptStr = md5.digest();
            for (int i = 0; i < encryptStr.length; i++) {
                int iRet = encryptStr[i];
                if (iRet < 0) {
                    iRet += 256;
                }
                int iD1 = iRet / 16;
                int iD2 = iRet % 16;
                sb.append((toLowerCase ? DIGITS_LOWER[iD1] : DIGITS_UPPER[iD1]) + "" + (toLowerCase ? DIGITS_LOWER[iD2] : DIGITS_UPPER[iD2]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("encodeHex md5--> " + encodeHex("小奎", DigestAlgorithm.MD5, false));
        System.out.println("encodeHex --> " + encodeHex("小奎", DigestAlgorithm.SHA, false));
        System.err.println("------------------------------------------------------------");
        System.out.println("encodeHexFormat md5--> " + encodeHexFormat("小奎", DigestAlgorithm.MD5));
        System.out.println("encodeHexFormat --> " + encodeHexFormat("小奎", DigestAlgorithm.SHA));
        System.err.println("------------------------------------------------------------");
        System.out.println("encodeHex1 md5--> " + encodeHex1("小奎", DigestAlgorithm.MD5, false));
        System.out.println("encodeHex1 --> " + encodeHex1("小奎", DigestAlgorithm.SHA, false));
        System.err.println("------------------------------------------------------------");
    }
}