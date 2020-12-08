package net.dgg.utils.number;


import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @Description 金额工具类
 * @Version 1.0.0
 * @Author Created by yan.x on 2019-04-29 .
 * @Copyright © dgg group.All Rights Reserved.
 **/
public final class DggAmountUtil {
    private DggAmountUtil() {
    }

    private static String HanDigiStr[] = new String[]{"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

    private static String HanDiviStr[] = new String[]{"", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟"};

    private final static double max = 99999999999999.999;

    private final static double min = -99999999999999.999;

    /**
     * 将货币转换为大写形式
     *
     * @param money 传入的数据
     * @return String 返回的人民币大写形式字符串
     */
    public static final String numToRMBStr(double money) {
        String signStr = "";
        String tailStr = "";
        long fraction, integer;
        int jiao, fen;
        if (money < 0) {
            money = -money;
            signStr = "负";
        }
        if (money > max || money < min) {
            return "数值位数过大!";
        }
        // 四舍五入到分
        long temp = Math.round(money * 100);
        integer = temp / 100;
        fraction = temp % 100;
        jiao = (int) fraction / 10;
        fen = (int) fraction % 10;
        if (jiao == 0 && fen == 0) {
            tailStr = "整";
        } else {
            tailStr = HanDigiStr[jiao];
            if (jiao != 0) {
                tailStr += "角";
            }
            // 零元后不写零几分
            if (integer == 0 && jiao == 0) {
                tailStr = "";
            }
            if (fen != 0) {
                tailStr += HanDigiStr[fen] + "分";
            }
        }
        // 下一行可用于非正规金融场合，0.03只显示“叁分”而不是“零元叁分”
        return signStr + positiveIntegerToHanStr(String.valueOf(integer)) + "元" + tailStr;
    }

    /**
     * 将货币转换为大写形式(类内部调用)<br>
     * 输入字符串必须正整数，只允许前导空格(必须右对齐)，不宜有前导零
     *
     * @param money
     * @return String
     */
    private static String positiveIntegerToHanStr(String money) {
        // 输入字符串必须正整数，只允许前导空格(必须右对齐)，不宜有前导零
        String rmbStr = "";
        boolean lastzero = false;
        // 亿、万进位前有数值标记
        boolean hasvalue = false;
        int len, n;
        len = money.length();
        if (len > 15) {
            return "数值过大!";
        }
        for (int i = len - 1; i >= 0; i--) {
            if (money.charAt(len - i - 1) == ' ') {
                continue;
            }
            n = money.charAt(len - i - 1) - '0';
            if (n < 0 || n > 9) {
                return "输入含非数字字符!";
            }
            if (n != 0) {
                if (lastzero) {
                    // 若干零后若跟非零值，只显示一个零
                    rmbStr += HanDigiStr[0];
                }
                // 除了亿万前的零不带到后面
                // 如十进位前有零也不发壹音用此行
                // 十进位处于第一位不发壹音
                if (!(n == 1 && i % 4 == 1 && i == len - 1)) {
                    rmbStr += HanDigiStr[n];
                }
                // 非零值后加进位，个位为空
                rmbStr += HanDiviStr[i];
                // 置万进位前有值标记
                hasvalue = true;
            } else {
                // 亿万之间必须有非零值方显示万
                if (i % 8 == 0 || i % 8 == 4 && hasvalue) {
                    // “亿”或“万”
                    rmbStr += HanDiviStr[i];
                }
            }
            if (i % 8 == 0) {
                // 万进位前有值标记逢亿复位
                hasvalue = false;
            }
            lastzero = n == 0 && i % 4 != 0;
        }
        if (rmbStr.length() == 0) {
            // 输入空字符或"0"，返回"零"
            return HanDigiStr[0];
        }
        return rmbStr;
    }

    /**
     * 将字符串"元"转换成"分"
     *
     * @param money : 金额(xx.00)
     * @return
     */
    public static String DollarConvertCent(String money) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        StringBuffer sb = df.format(Double.parseDouble(money),
                new StringBuffer(), new java.text.FieldPosition(0));
        int idx = sb.toString().indexOf(".");
        sb.deleteCharAt(idx);
        for (; sb.length() != 1; ) {
            if (sb.charAt(0) == '0') {
                sb.deleteCharAt(0);
            } else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * 将字符串"分"转换成"元"（长格式），如：100分被转换为1.00元。
     *
     * @param money
     * @return
     */
    public static String CentConvertDollar(String money) {
        if ("".equals(money) || money == null) {
            return "";
        }
        long l;
        if (money.length() != 0) {
            if (money.charAt(0) == '+') {
                money = money.substring(1);
            }
            l = Long.parseLong(money);
        } else {
            return "";
        }
        boolean negative = false;
        if (l < 0) {
            negative = true;
            l = Math.abs(l);
        }
        money = Long.toString(l);
        if (money.length() == 1) {
            return (negative ? ("-0.0" + money) : ("0.0" + money));
        }
        if (money.length() == 2) {
            return (negative ? ("-0." + money) : ("0." + money));
        } else {
            return (negative ? ("-" + money.substring(0, money.length() - 2) + "." + money
                    .substring(money.length() - 2)) : (money.substring(0,
                    money.length() - 2)
                    + "." + money.substring(money.length() - 2)));
        }
    }

    /**
     * 将字符串"分"转换成"元"（短格式），如：100分被转换为1元。
     *
     * @param money
     * @return
     */
    public static String CentConvertDollarShort(String money) {
        String ss = CentConvertDollar(money);
        ss = "" + Double.parseDouble(ss);
        if (ss.endsWith(".0")) {
            return ss.substring(0, ss.length() - 2);
        }
        if (ss.endsWith(".00")) {
            return ss.substring(0, ss.length() - 3);
        } else {
            return ss;
        }
    }

    /**
     * 金额格式化
     *
     * @param bd
     * @return
     */
    public static String toString(BigDecimal bd, String defaultString) {
        if (bd == null || bd.doubleValue() == 0) {
            return defaultString;
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(bd);
    }
}
