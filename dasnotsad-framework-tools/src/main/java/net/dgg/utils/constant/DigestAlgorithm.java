package net.dgg.utils.constant;

/**
 * describe: 摘要算法类型
 * since: 1.0
 * Created by hot.sun on 2018/4/13 .
 **/
public enum DigestAlgorithm {
    MD2("MD2"),
    MD5("MD5"),
    SHA("SHA"),
    SHA1("SHA-1"),
    SHA256("SHA-256"),
    SHA384("SHA-384"),
    SHA512("SHA-512");
    private String value;

    /**
     * 构造
     *
     * @param value 算法字符串表示
     */
    private DigestAlgorithm(String value) {
        this.value = value;
    }

    /**
     * 获取算法字符串表示
     *
     * @return 算法字符串表示
     */
    public String getValue() {
        return this.value;
    }
}