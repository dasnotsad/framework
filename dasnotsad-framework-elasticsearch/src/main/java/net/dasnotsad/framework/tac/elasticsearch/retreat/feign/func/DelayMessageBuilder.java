package net.dasnotsad.framework.tac.elasticsearch.retreat.feign.func;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.dasnotsad.framework.tac.elasticsearch.annotation.RestClientContainer;
import net.dasnotsad.framework.tac.elasticsearch.retreat.feign.model.*;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.common.io.stream.OutputStreamStreamOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 延迟队列消息生成器
 *
 * @author liuliwei
 */
@Slf4j
@Component
public class DelayMessageBuilder {

    @Autowired
    private RestClientContainer containers;

    @Lazy
    @Qualifier("ribbonRestTemplate")
    @Autowired(required = false)
    private RestTemplate restTemplate;

    public void createRetreatRecord(int whichDataSource, WriteRequest request, String sysCode, OperationType operationType, long delayTime, String desc) {
        RetreatRecord rr = new RetreatRecord();
        try (ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
             OutputStreamStreamOutput out = new OutputStreamStreamOutput(outBuffer)) {
            request.writeTo(out);
            rr.setDatasourceType(DatasourceType.ELASTICSEARCH);
            rr.setDatasourceUri(containers.getNodes(whichDataSource));
            rr.setOperationType(operationType);
            rr.setOperationContent(outBuffer.toByteArray());
            rr.setSysCode(sysCode);
            rr.setCreateTime(System.currentTimeMillis());
            rr.setDesc(desc);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        MessageLog msg = createMessageLog(rr, sysCode, delayTime);

        JsonResponse fres = this.doPostJson("http://tac-delaymq/pushtask.do", msg);
        if (ResponseCode.CODE_00.equals(fres.getCode()))
            log.info("********************delayMessageFeign success return:{}", fres);
        else
            log.error("********************delayMessageFeign failure return:{}", fres);
    }

    private JsonResponse doPostJson(String url, Object params) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity httpEntity = new HttpEntity(JSON.toJSONString(params), headers);
        return restTemplate.postForObject(url, httpEntity, JsonResponse.class);
    }

    public MessageLog createMessageLog(RetreatRecord rr, String sysCode, long delayTime) {
        // 延迟时间
        long recordDelayTime = System.currentTimeMillis() + (delayTime * 1000L);
        rr.setDelayTime(delayTime);
        // 消费者对象
        ProducerParams rabbitParams = new ProducerParams();
        rabbitParams.setExchangeName(RabbitmqProperties.RETREAT_EXCHANGE_NAME);
        rabbitParams.setQueueName(RabbitmqProperties.RETREAT_QUEUE_NAME);
        rabbitParams.setData(rr);

        // 延迟服务API接口参数
        MessageLog msg = new MessageLog();
        msg.setType(MessageType.rabbitmq);
        msg.setData(rabbitParams);
        msg.setDelay_time(recordDelayTime);
        msg.setSys_code(sysCode);
        msg.setDesc("补偿操作：" + rr.getDesc());
        return msg;
    }
}