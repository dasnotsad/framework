package net.dgg.utils.database;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/10/21 0021
 * @Description: 特殊字符处理
 */
public class DggSqlHandleUtil {

    /**
     * 将like语句中的特殊字符转义
     *
     * @param sqlStr 原始sql字符串
     * @return 转义后的字符
     */
    public static String likeSqlHandle(String sqlStr) {
        String result = sqlStr;
        if (sqlStr.indexOf("\\") != -1) {
            result = sqlStr.replaceAll("\\\\", "\\\\\\\\\\\\\\\\");
        }
        if (sqlStr.indexOf("_") != -1) {
            result = result.replaceAll("_", "\\\\_");
        }
        if (sqlStr.indexOf("%") != -1) {
            result = result.replaceAll("%", "\\\\%");
        }
        if (sqlStr.indexOf("'") != -1) {
            result = result.replaceAll("'", "\\\\'");
        }
        return result;
    }
}
