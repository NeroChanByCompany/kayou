/*
package com.nut.servicestation.app.kafka;


import com.nut.common.utils.JsonUtil;
import com.nut.servicestation.app.pojo.AreaStayInfoPojo;
import com.nut.servicestation.app.service.InOutAreaConsumerService;
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
 * @Description: 消费进出服务半径电子围栏信息
 * @Modified By:
 *//*

@Slf4j
@Component
public class SyncKafKaInOutAreaListener implements MessageListener<String, Object> {
    protected static final Logger logger = LoggerFactory.getLogger(SyncKafKaInOutAreaListener.class);

    @Autowired
    private InOutAreaConsumerService inOutAreaConsumerService;
    @Override
    public void onMessage(ConsumerRecord<String, Object> record) {
        log.info("[SyncKafKaInOutAreaListener] start ......");
        try {
            //获取通信号
            String terminalId = record.key();
            if (StringUtils.isEmpty(terminalId)){
                log.info("[InOutAreaConsumerService] This key is null");
                return;
            }

            String data = (String)record.value();
            if (StringUtils.isEmpty(data)) {
                log.info("[InOutAreaConsumerService] This value is null");
                return;
            }

            AreaStayInfoPojo pojo = JsonUtil.fromJson(data, AreaStayInfoPojo.class);
            log.info("[SyncKafKaInOutAreaListener] AreaStayInfoPojo : {}",pojo.toString());
            //数据完整性校验
            if (pojo.getAreaId() == null || pojo.getStartTime() == null){
                log.info("[InOutAreaConsumerService] This data is error");
                return;
            }
            inOutAreaConsumerService.inOutAreaConsumer(pojo, terminalId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[SyncKafKaInOutAreaListener] end ......");
    }
}
*/
