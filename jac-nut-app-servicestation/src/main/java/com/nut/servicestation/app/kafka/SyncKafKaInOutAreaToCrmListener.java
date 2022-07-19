/*
package com.nut.servicestation.app.kafka;


import com.nut.common.utils.JsonUtil;
import com.nut.servicestation.app.pojo.AreaStayInfoPojo;
import com.nut.servicestation.app.service.InOutAreaToCrmConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

*/
/**
 * @author: liubing
 * @Description: 消费进出服务半径电子围栏信息同步到CRM
 *//*

@Slf4j
@Component
public class SyncKafKaInOutAreaToCrmListener implements MessageListener<String, Object> {


    @Autowired
    private InOutAreaToCrmConsumerService inOutAreaToCrmConsumerService;
    @Override
    public void onMessage(ConsumerRecord<String, Object> record) {
        log.info("[SyncKafKaInOutAreaToCrmListener] start ......");
        try {
            //获取通信号
            String terminalId = record.key();
            log.info("[SyncKafKaInOutAreaToCrmListener] kafka key:{}", terminalId);
            if (StringUtils.isEmpty(terminalId)){
                log.info("[SyncKafKaInOutAreaToCrmListener] the key is empty!");
                return;
            }
            String data = (String)record.value();
            log.info("[SyncKafKaInOutAreaToCrmListener] kafka data:{}", data);
            if (StringUtils.isEmpty(data)) {
                log.info("[SyncKafKaInOutAreaToCrmListener] This value is empty!");
                return;
            }
            AreaStayInfoPojo pojo = JsonUtil.fromJson(data, AreaStayInfoPojo.class);
            log.info("[SyncKafKaInOutAreaToCrmListener] AreaStayInfoPojo : {}", pojo.toString());
            //数据完整性校验
            if (pojo == null || pojo.getAreaId() == null || pojo.gettId() == null || (pojo.getStartTime() == null && pojo.getEndTime() == null)){
                log.info("[SyncKafKaInOutAreaToCrmListener] This data is error");
                return;
            }
            inOutAreaToCrmConsumerService.inOutAreaToCrmConsumer(pojo, terminalId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[SyncKafKaInOutAreaToCrmListener] end ......");
    }
}
*/
