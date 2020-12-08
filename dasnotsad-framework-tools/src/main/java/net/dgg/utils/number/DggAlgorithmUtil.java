package net.dgg.utils.number;

import net.dgg.utils.exception.DggUtilException;

import java.util.Random;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/27 0027
 * @Description: 算法工具类
 */
public class DggAlgorithmUtil {

    /**
     * @param begin 最小数字（包含该数）
     * @param end   最大数字（不包含该数）
     * @param size  指定产生随机数的个数
     * @return
     * @description: 生成不重复随机数
     */
    public static int[] generateRandomNumber(int begin, int end, int size) {
        if (begin > end) {
            int temp = begin;
            begin = end;
            end = temp;
        }
        // 确保begin < end并且size不能大于该表示范围
        if ((end - begin) < size) {
            throw new DggUtilException("Size is larger than range between begin and end!");
        }
        // 种子你可以随意生成，但不能重复
        int[] seed = new int[end - begin];

        for (int i = begin; i < end; i++) {
            seed[i - begin] = i;
        }
        int[] ranArr = new int[size];
        Random ran = new Random();
        for (int i = 0; i < size; i++) {
            // 得到一个位置
            int pos = ran.nextInt(seed.length - i);
            // 得到那个位置的数值
            ranArr[i] = seed[pos];
            // 将最后一个未用的数字放到这里
            seed[pos] = seed[seed.length - 1 - i];
        }
        return ranArr;
    }

    /**
     * @param start 开始（包含）
     * @param stop  结束（包含）
     * @param step  步进
     * @return 整数列表
     * @description: 给定范围内的整数列表
     */
    public static int[] rangeInt(int start, int stop, int step) {
        if (start < stop) {
            step = Math.abs(step);
        } else if (start > stop) {
            step = -Math.abs(step);
        } else {// start == end
            return new int[]{start};
        }

        int size = Math.abs((stop - start) / step) + 1;
        int[] values = new int[size];
        int index = 0;
        for (int i = start; (step > 0) ? i <= stop : i >= stop; i += step) {
            values[index] = i;
            index++;
        }
        return values;
    }

    /**
     * @param number 阶乘数
     * @return
     * @description: 计算阶乘
     * <p>
     * n! = n * (n-1) * ... *
     * </p>
     */
    public static long factorial(long number) {
        if (number <= 1L) {
            return 1L;
        }
        return number * factorial(number - 1L);
    }

    /**
     * @param start 阶乘起始
     * @param end   阶乘结束
     * @return
     * @description: 计算阶乘
     * <p>
     * n! = n * (n-1) * ... * end
     * </p>
     */
    public static long factorial(long start, long end) {
        if (start < end) {
            return 0L;
        }
        if (start == end) {
            return 1L;
        }
        return start * factorial(start - 1, end);
    }

    public static void main(String[] args) {
        System.out.println(factorial(5));
    }

}
