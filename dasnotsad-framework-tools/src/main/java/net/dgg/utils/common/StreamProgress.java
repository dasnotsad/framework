package net.dgg.utils.common;

/**
 * @Description: Stream进度条
 * @Author Created by yan.x on 2019-05-05 .
 **/
public interface StreamProgress {

    /**
     * 开始
     */
    void start();

    /**
     * 进行中
     *
     * @param progressSize 已经进行的大小
     */
    void progress(long progressSize);

    /**
     * 结束
     */
    void finish();
}
