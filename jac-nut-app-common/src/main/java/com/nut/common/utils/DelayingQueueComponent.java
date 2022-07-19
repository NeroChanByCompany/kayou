package com.nut.common.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.entity.DelayMessageEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author liuBing
 * @Classname DelayingQueueComponent
 * @Description TODO 消息队列组件
 * @Date 2021/7/22 19:47
 */
@Slf4j
@Component
public class DelayingQueueComponent {

    /**
     * 可以不同业务用不同的key
     */
    public static final String QUEUE_NAME = "work_order:message:queue";

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 插入消息
     *
     * @param message
     * @return
     */
    @SneakyThrows
    public Boolean push(DelayMessageEntity message) {
        Boolean addFlag = redisTemplate.opsForZSet().add(QUEUE_NAME, JSON.toJSONString(message), message.getDelayTime());
        return addFlag;
    }

    /**
     * 更新消息过期时间
     *
     * @param message
     * @return
     */
    @SneakyThrows
    public Double update(DelayMessageEntity message) {
        Double updateFlag = redisTemplate.opsForZSet().incrementScore(QUEUE_NAME, JSON.toJSONString(message), message.getDelayTime());
        return updateFlag;
    }


    /**
     * 移除消息
     *
     * @param message
     * @return
     */
    @SneakyThrows
    public Boolean remove(DelayMessageEntity message) {
        Long remove = redisTemplate.opsForZSet().remove(QUEUE_NAME, JSON.toJSONString(message));
        return remove > 0 ? true : false;
    }


    /**
     * 拉取最新需要
     * 被消费的消息
     * rangeByScore 根据score范围获取 0-当前时间戳可以拉取当前时间及以前的需要被消费的消息
     *
     * @return
     */
    public List<DelayMessageEntity> pull() {
        Set<String> strings = redisTemplate.opsForZSet().rangeByScore(QUEUE_NAME, 0, System.currentTimeMillis());
        if (strings == null) {
            return null;
        }
        List<DelayMessageEntity> msgList = strings.stream().map(msg -> {
            DelayMessageEntity message = null;
            try {
                message = JSON.parseObject(msg,DelayMessageEntity.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return message;
        }).collect(Collectors.toList());
        return msgList;
    }
}
