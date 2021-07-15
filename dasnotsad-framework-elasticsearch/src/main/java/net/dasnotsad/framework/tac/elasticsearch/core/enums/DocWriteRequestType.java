package net.dasnotsad.framework.tac.elasticsearch.core.enums;

import org.springframework.util.StringUtils;

/**
 * @Description: es写入请求类型（增、删、改）
 * @Author Created by dgg-wxw on 2021-01-27 .
 **/
public enum DocWriteRequestType {
    /**
     *
     */
    INDEX_REQUEST("index_request"),
    UPDATE_REQUEST("update_request"),
    DELETE_REQUEST("delete_request");

    private final String code;

    DocWriteRequestType(String code) {
        this.code = code;
    }

    public static DocWriteRequestType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (DocWriteRequestType value : values()) {
            if (value.getCode().equalsIgnoreCase(code)) {
                return value;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

}
