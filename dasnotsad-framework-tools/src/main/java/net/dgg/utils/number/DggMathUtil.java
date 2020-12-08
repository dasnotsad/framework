package net.dgg.utils.number;

import java.math.BigDecimal;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/26 0026
 * @Description: 精确计算工具类
 */
public class DggMathUtil {

    private static BigDecimal verifyBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 加法操作
     */
    public static BigDecimal addition(BigDecimal v1, BigDecimal v2, BigDecimal... value) {
        BigDecimal result = verifyBigDecimal(v1).add(verifyBigDecimal(v2));
        if (value != null && value.length != 0) {
            for (BigDecimal v : value) {
                result = result.add(verifyBigDecimal(v));
            }
        }
        return result;
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 减法操作(v1 - v2)
     */
    public static BigDecimal subtraction(BigDecimal v1, BigDecimal v2, BigDecimal... value) {
        BigDecimal result = verifyBigDecimal(v1).subtract(verifyBigDecimal(v2));
        if (value != null && value.length != 0) {
            for (BigDecimal v : value) {
                result = result.subtract(verifyBigDecimal(v));
            }
        }
        return result;
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 乘法操作
     */
    public static BigDecimal multiplication(BigDecimal v1, BigDecimal v2, BigDecimal... value) {
        BigDecimal result = verifyBigDecimal(v1).multiply(verifyBigDecimal(v2));
        if (value != null && value.length != 0) {
            for (BigDecimal v : value) {
                result = result.multiply(verifyBigDecimal(v));
            }
        }
        return result;
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 除法操作(v1 / v2) 默认保留两位有效位数
     */
    public static BigDecimal division(BigDecimal v1, BigDecimal v2, BigDecimal... value) {
        return division(v1, v2, 2, value);
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param scale 保留位数
     * @param value 其它
     * @return
     * @description: 除法操作(v1 / v2)
     */
    public static BigDecimal division(BigDecimal v1, BigDecimal v2, int scale, BigDecimal... value) {
        if (v2 == null) {
            v2 = BigDecimal.ONE;
        }
        if (scale < 0) {
            scale = 0;
        }
        BigDecimal result = verifyBigDecimal(v1).divide(v2, scale, BigDecimal.ROUND_HALF_UP);
        if (value != null && value.length != 0) {
            for (BigDecimal v : value) {
                if (v == null) {
                    v = BigDecimal.ONE;
                }
                result = result.divide(v, scale, BigDecimal.ROUND_HALF_UP);
            }
        }
        return result;
    }

    /**
     * @param value 数值数组
     * @return
     * @description: 求平均操作
     */
    public static BigDecimal average(BigDecimal... value) {
        if (value != null && value.length != 0) {
            return division(addition(BigDecimal.ZERO, BigDecimal.ZERO, value), BigDecimal.valueOf(value.length));
        }
        return BigDecimal.ZERO;
    }

    /**********************************【BigDecimal计算结束】******************************************************/

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 加法操作
     */
    public static BigDecimal addition(double v1, double v2, double... value) {
        BigDecimal result = BigDecimal.valueOf(v1).add(BigDecimal.valueOf(v2));
        if (value != null && value.length != 0) {
            for (double v : value) {
                result = result.add(BigDecimal.valueOf(v));
            }
        }
        return result;
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 减法操作(v1 - v2)
     */
    public static BigDecimal subtraction(double v1, double v2, double... value) {
        BigDecimal result = BigDecimal.valueOf(v1).subtract(BigDecimal.valueOf(v2));
        if (value != null && value.length != 0) {
            for (double v : value) {
                result = result.subtract(BigDecimal.valueOf(v));
            }
        }
        return result;
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 乘法操作
     */
    public static BigDecimal multiplication(double v1, double v2, double... value) {
        BigDecimal result = BigDecimal.valueOf(v1).multiply(BigDecimal.valueOf(v2));
        if (value != null && value.length != 0) {
            for (double v : value) {
                result = result.multiply(BigDecimal.valueOf(v));
            }
        }
        return result;
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 除法操作(v1 / v2) 默认保留两位有效位数
     */
    public static BigDecimal division(double v1, double v2, double... value) {
        return division(v1, v2, 2, value);
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param scale 保留位数
     * @param value 其它
     * @return
     * @description: 除法操作(v1 / v2)
     */
    public static BigDecimal division(double v1, double v2, int scale, double... value) {
        if (scale < 0) {
            scale = 0;
        }
        BigDecimal result = BigDecimal.valueOf(v1).divide(BigDecimal.valueOf(v2), scale, BigDecimal.ROUND_HALF_UP);
        if (value != null && value.length != 0) {
            for (double v : value) {
                result = result.divide(BigDecimal.valueOf(v), scale, BigDecimal.ROUND_HALF_UP);
            }
        }
        return result;
    }

    /**
     * @param value 数值数组
     * @return
     * @description: 求平均操作
     */
    public static BigDecimal average(double... value) {
        if (value != null && value.length != 0) {
            return division(addition(0, 0, value), BigDecimal.valueOf(value.length));
        }
        return BigDecimal.ZERO;
    }

    /**********************************【BigDecimal计算结束】******************************************************/

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 加法操作
     */
    public static BigDecimal addition(String v1, String v2, String... value) {
        BigDecimal result = verifyBigDecimal(new BigDecimal(v1)).add(verifyBigDecimal(new BigDecimal(v2)));
        if (value != null && value.length != 0) {
            for (String v : value) {
                result = result.add(verifyBigDecimal(new BigDecimal(v)));
            }
        }
        return result;
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 减法操作(v1 - v2)
     */
    public static BigDecimal subtraction(String v1, String v2, String... value) {
        BigDecimal result = verifyBigDecimal(new BigDecimal(v1)).subtract(verifyBigDecimal(new BigDecimal(v2)));
        if (value != null && value.length != 0) {
            for (String v : value) {
                result = result.subtract(verifyBigDecimal(new BigDecimal(v)));
            }
        }
        return result;
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 乘法操作
     */
    public static BigDecimal multiplication(String v1, String v2, String... value) {
        BigDecimal result = verifyBigDecimal(new BigDecimal(v1)).multiply(verifyBigDecimal(new BigDecimal(v2)));
        if (value != null && value.length != 0) {
            for (String v : value) {
                result = result.multiply(verifyBigDecimal(new BigDecimal(v)));
            }
        }
        return result;
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 除法操作(v1 / v2) 默认保留两位有效位数
     */
    public static BigDecimal division(String v1, String v2, String... value) {
        return division(v1, v2, 2, value);
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param scale 保留位数
     * @param value 其它
     * @return
     * @description: 除法操作(v1 / v2)
     */
    public static BigDecimal division(String v1, String v2, int scale, String... value) {
        if (scale < 0) {
            scale = 0;
        }
        BigDecimal result = verifyBigDecimal(new BigDecimal(v1)).divide(verifyBigDecimal(new BigDecimal(v2)), scale, BigDecimal.ROUND_HALF_UP);
        if (value != null && value.length != 0) {
            for (String v : value) {
                result = result.divide(verifyBigDecimal(new BigDecimal(v)), scale, BigDecimal.ROUND_HALF_UP);
            }
        }
        return result;
    }

    /**
     * @param value 数值数组
     * @return
     * @description: 求平均操作
     */
    public static BigDecimal average(String... value) {
        if (value != null && value.length != 0) {
            return division(addition("0", "0", value), BigDecimal.valueOf(value.length));
        }
        return BigDecimal.ZERO;
    }

}
