package net.dgg.utils.constant;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/24 0024
 * @Description: 日期常量
 */
public enum DggDateFormatType {

    /**
     * 中杠【年月】格式
     */
    MONTH_FORMATTER("yyyy-MM"),
    /**
     * 中杠【年月日】格式
     */
    DATE_FORMATTER("yyyy-MM-dd"),
    /**
     * 中杠【年月日时分秒】格式
     */
    DATETIME_FORMATTER("yyyy-MM-dd HH:mm:ss"),

    /**
     * 连续【年月】格式
     */
    CONTINUITY_MONTH_FORMATTER("yyyyMM"),
    /**
     * 连续【年月日】格式
     */
    CONTINUITY_DATE_FORMATTER("yyyyMMdd"),
    /**
     * 连续【年月日时分秒】格式
     */
    CONTINUITY_TIMESTAMP_FORMATTER("yyyyMMddHHmmss"),
    /**
     * 连续【年月日时分秒毫秒】格式
     */
    CONTINUITY_MILLISECOND_FORMATTER("yyyyMMddHHmmssSSS"),

    /**
     * 反斜杠【年月】格式
     */
    BACKSLASH_MONTH_FORMATTER("yyyy/MM"),

    /**
     * 反斜杠【年月日】格式
     */
    BACKSLASH_DATE_FORMATTER("yyyy/MM/dd"),
    /**
     * 反斜杠【年月日时分秒】格式
     */
    BACKSLASH_DATETIME_FORMATTER("yyyy/MM/dd HH:mm:ss");

    private String value;

    public String getValue() {
        return value;
    }

    DggDateFormatType(String formatString) {
        this.value = formatString;
    }
}
