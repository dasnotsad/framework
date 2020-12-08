package net.dasnotsad.framework.tac.elasticsearch.retreat.feign.model;

public enum MessageStatus {

    wait,//等待中
    queueing,//排队中（已经进入时间轮）
    success,//投递成功
    error;//发生错误
}