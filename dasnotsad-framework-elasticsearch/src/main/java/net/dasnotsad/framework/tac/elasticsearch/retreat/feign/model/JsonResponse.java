package net.dasnotsad.framework.tac.elasticsearch.retreat.feign.model;

import lombok.Data;

/**
 * Desc:   统一Response
 * Author: liuliwei
 * Date:   2019/7/29
 **/
@Data
public class JsonResponse {

    private Object data;//返回数据
    private ResponseCode code;//状态
    private String msg;//描述

    /**
     * @description: 返回默认的成功响应
     * @return JsonResponse
     */
    public static JsonResponse success(){
        JsonResponse response = new JsonResponse();
        response.setCode(ResponseCode.CODE_00);
        response.setMsg("success");
        return response;
    }

    /**
     * @description: 返回默认的成功响应
     * @param msg 成功描述
     * @return JsonResponse
     */
    public static JsonResponse success(String msg){
        JsonResponse response = new JsonResponse();
        response.setCode(ResponseCode.CODE_00);
        response.setMsg(msg);
        return response;
    }

    /**
     * @description: 返回默认的失败响应
     * @return JsonResponse
     */
    public static JsonResponse fail(){
        JsonResponse response = new JsonResponse();
        response.setCode(ResponseCode.CODE_99);
        response.setMsg("fail");
        return response;
    }

    /**
     * @description: 返回默认的失败响应
     * @param msg 失败描述
     * @return JsonResponse
     */
    public static JsonResponse fail(String msg){
        JsonResponse response = new JsonResponse();
        response.setCode(ResponseCode.CODE_99);
        response.setMsg(msg);
        return response;
    }

    /**
     * @description: fallBack返回
     * @return JsonResponse
     */
    public static JsonResponse fallBack(){
        JsonResponse response = new JsonResponse();
        response.setCode(ResponseCode.CODE_98);
        response.setMsg("服务忙，请稍后再试...");
        return response;
    }
}