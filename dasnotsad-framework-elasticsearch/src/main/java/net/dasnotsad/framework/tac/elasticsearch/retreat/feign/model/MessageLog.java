package net.dasnotsad.framework.tac.elasticsearch.retreat.feign.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class MessageLog {

    private MessageType type;//消息MQ类型
    private ProducerParams data; // 消息体

    private String target_source;// 发往的目标数据源
    private Long delay_time;//将要发送的时间戳
    private String sys_code;//来自系统的sysCode
    private Long create_time;//创建时间
    private Long send_time;//成功投递的时间戳
    private String desc;//其它描述

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}