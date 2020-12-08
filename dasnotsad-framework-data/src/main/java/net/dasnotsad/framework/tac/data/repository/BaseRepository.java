package net.dasnotsad.framework.tac.data.repository;

/**
 * @Description: 基础的资源管理器接口
 * @Author Created by HOT SUN on 2020/7/22 .
 **/
public interface BaseRepository<T, ID> {

    /**
     * 默认批次提交数量
     */
    int DEFAULT_BATCH_SIZE = 1000;

}
