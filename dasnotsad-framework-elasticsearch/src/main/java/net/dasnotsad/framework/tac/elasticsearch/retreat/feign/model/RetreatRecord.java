package net.dasnotsad.framework.tac.elasticsearch.retreat.feign.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @Author: linhongda
 * @Date: 2019/11/6 0006
 * @Description:
 */
@Data
public class RetreatRecord {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 数据源类型
     */
    private DatasourceType datasourceType;

    /**
     * 数据源URI
     */
    private String datasourceUri;

    /**
     * 操作类型
     */
    private OperationType operationType;

    /**
     * 操作语句
     */
    private byte[] operationContent;

    /**
     * 系统码
     */
    private String sysCode;

    /**
     * 执行状态
     */
    private RecordStatus operaStatus;

    /**
     * 重试次数
     */
    private Integer retryNum;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 最后修改时间
     */
    private Long last_modify;

    /**
     * 成功执行时间
     */
    private Long operaTime;

    /**
     * 备注信息
     */
    private String desc;

    /**
     * 延时时间
     */
    private Long delayTime;

    /**
     * 版本
     */
    private long version;

    @Override
    public String toString(){
        return JSON.toJSONString(this);
    }
}
