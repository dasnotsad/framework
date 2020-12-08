package net.dasnotsad.framework.tac.elasticsearch.retreat.feign.model;

/**
 * Desc:   返回状态枚举
 * Author: liuliwei
 * Date:   2019/7/29
 **/
public enum ResponseCode {

    /**
     * 成功
     */
    CODE_00,
    /**
     * 失败
     */
    CODE_01,

    /**
     * fallback
     */
    CODE_98,

    /**
     * 发生异常
     */
    CODE_99;
}