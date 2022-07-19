package com.nut.driver.app.task;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.utils.JsonUtil;
import com.nut.common.entity.DelayMessageEntity;
import com.nut.driver.app.service.PushMessageService;
import com.nut.common.utils.DelayingQueueComponent;
import com.nut.driver.common.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liuBing
 * @Classname MessageConsumer
 * @Description TODO 定时消费
 * @Date 2021/7/22 13:43
 */
//todo 用于延迟队列，暂时功能隐藏
@Slf4j
//@Component
public class WorkOrderMessageConsumer {

    @Resource
    private DelayingQueueComponent queueComponent;

    @Autowired
    private PushMessageService pushMessageService;
    /**
     * 最后一次通知消息结束时间节点
     */
    @Value("${delayTime:3}")
    private Long delayTime;

    /**
     * 定时消费队列中的数据
     * zset会对score进行排序 让最早消费的数据位于最前
     * 拿最前的数据跟当前时间比较 时间到了则消费
     */
    //@Scheduled(cron = "*/1 * * * * *")
    public void consumer() throws JsonProcessingException {
        List<DelayMessageEntity> msgList = queueComponent.pull();
        if (null != msgList) {
            long current = System.currentTimeMillis();
            msgList.stream().forEach(msg -> {
                // 已超时的消息拿出来消费
                if (current >= msg.getDelayTime() && msg.getVersion().equals(1)) {
                    try {
                        log.info("消费消息：{}:消息创建时间：{},消费时间：{}", JSON.toJSONString(msg), msg.getCreateTime(), LocalDateTime.now());
                        //开始推送消息
                        DelayMessageEntity.MessageBody body = JsonUtil.fromJson(msg.getBody(), DelayMessageEntity.MessageBody.class);
                        pushMessageService.pushToStation(PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_ONE,body.getWildcard(),body.getMessageExtra(),body.getSendId(),body.getReceiveId(),PushStaticLocarVal.PUSH_SHOW_TYPE_WO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    queueComponent.remove(msg);
                    msg.setVersion(2);
                    msg.setCreateTime(LocalDateTime.now());
                    msg.setDelayTime(msg.getDelayTime()+(delayTime* CommonConstants.QUEUE_TIME));
                    queueComponent.push(msg);
                }
                // 已超时的消息拿出来消费
                if (current >= msg.getDelayTime() && msg.getVersion().equals(2)) {
                    try {
                        log.info("消费消息：{}:消息创建时间：{},消费时间：{}", JSON.toJSONString(msg), msg.getCreateTime(), LocalDateTime.now());
                        DelayMessageEntity.MessageBody body = JsonUtil.fromJson(msg.getBody(), DelayMessageEntity.MessageBody.class);
                        pushMessageService.pushToStation(PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_ONE,body.getWildcard(),body.getMessageExtra(),body.getSendId(),body.getReceiveId(),PushStaticLocarVal.PUSH_SHOW_TYPE_WO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    queueComponent.remove(msg);
                }
            });
        }
    }

}
