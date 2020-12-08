package net.dgg.utils.number;

import net.dgg.utils.exception.DggUtilException;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/26 0026
 * @Description: 整型工具类
 */
public class DggIntUtil {

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 加法操作
     */
    public static int addition(int v1, int v2, int... value) {
        int result = v1 + v2;
        if (value != null && value.length != 0) {
            for (int v : value) {
                result += v;
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
    public static int subtraction(int v1, int v2, int... value) {
        int result = v1 - v2;
        if (value != null && value.length != 0) {
            for (int v : value) {
                result -= v;
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
    public static int multiplication(int v1, int v2, int... value) {
        int result = v1 * v2;
        if (value != null && value.length != 0) {
            for (int v : value) {
                result *= v;
            }
        }
        return result;
    }

    /**
     * @param v1    操作数1
     * @param v2    操作数2
     * @param value 其它
     * @return
     * @description: 除法操作(v1 / v2)
     */
    public static int division(int v1, int v2, int... value) {
        if (v2 == 0) {
            throw new DggUtilException("除数不能为0");
        }
        int result = v1 / v2;
        if (value != null && value.length != 0) {
            for (int v : value) {
                if (v == 0) {
                    throw new DggUtilException("除数不能为0");
                }
                result /= v;
            }
        }
        return result;
    }
}
