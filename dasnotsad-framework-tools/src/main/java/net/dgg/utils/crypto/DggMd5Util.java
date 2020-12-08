package net.dgg.utils.crypto;

import net.dgg.utils.constant.DigestAlgorithm;

/**
 * @Description: md5加密工具类
 * @Author Created by yan.x on 2020-09-08 .
 **/
public class DggMd5Util {

    /**
     * MD5加密(默认大写)
     *
     * @param source
     * @return
     */
    public static String encodeMD5(String source) {
        return encodeMD5(source, false);
    }

    /**
     * MD5加密
     *
     * @param source
     * @param toLowerCase :<code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return
     */
    public static String encodeMD5(String source, boolean toLowerCase) {
        return DggEncryptionUtil.encodeHex(source, DigestAlgorithm.MD5, toLowerCase);
    }

}
