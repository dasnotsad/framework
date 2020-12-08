package net.dgg.utils.constant;

/**
 * @Description: 时间池
 * @Author Created by yan.x on 2019-05-09 .
 **/
public interface TimePool {
    /**
     * 有效期,10秒
     */
    int FAILURE_TIME_10S = 10;
    /**
     * 有效期,30秒
     */
    int FAILURE_TIME_30S = 30;
    /**
     * 有效期,1分钟
     */
    int FAILURE_TIME_1M = 60;
    /**
     * 有效期,10分钟
     */
    int FAILURE_TIME_5M = FAILURE_TIME_1M * 5;
    /**
     * 有效期,10分钟
     */
    int FAILURE_TIME_10M = FAILURE_TIME_1M * 10;
    /**
     * 有效期,1小时
     */
    int FAILURE_TIME_1H = FAILURE_TIME_1M * 60;
    /**
     * 有效期,1天
     */
    int FAILURE_TIME_24H = FAILURE_TIME_1H * 24;
    /**
     * 有效期,7天
     */
    int FAILURE_TIME_ONE_WEEKS = FAILURE_TIME_24H * 7;
    /**
     * 有效期,4个礼拜
     */
    int FAILURE_TIME_FOUR_WEEKS = FAILURE_TIME_ONE_WEEKS * 4;

    /**
     * 有效期,30天
     */
    int FAILURE_TIME_ONE_MONTHS = FAILURE_TIME_24H * 30;

    /**
     * 有效期,一季度
     */
    int FAILURE_TIME_FIRST_QUARTER = FAILURE_TIME_ONE_MONTHS * 3;

    /**
     * 有效期,六个月
     */
    int FAILURE_TIME_SIX_MONTHS = FAILURE_TIME_FIRST_QUARTER * 2;

    /**
     * 有效期,一年
     */
    int FAILURE_TIME_ONE_YEARS = FAILURE_TIME_SIX_MONTHS * 2;
}
