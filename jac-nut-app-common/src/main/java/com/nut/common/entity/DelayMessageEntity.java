package com.nut.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.omg.CORBA.StringHolder;

import java.time.LocalDateTime;

/**
 * @author liuBing
 * @Classname DelayMessageEntity
 * @Description TODO
 * @Date 2021/7/22 19:48
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DelayMessageEntity {
    /**
     * 消息唯一标识
     */
    private String id;
    /**
     * 消息渠道 如 订单 支付 代表不同业务类型
     * 为消费时不同类去处理
     */
    private String channel;
    /**
     * 具体消息 json
     */
    private String body;

    /**
     * 延时时间 被消费时间  取当前时间戳+延迟时间
     */
    private Long delayTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 版本
     */
    private Integer version;

    @Data
    @Accessors(chain = true)
    public static class MessageBody {

        /**
         * 通配符
         */
        private String wildcard;
        /**
         * 自定义消息
         */
        private String messageExtra;
        /**
         * 发件人id
         */
        private String sendId;
        /**
         * 收件人id
         */
        private String receiveId;
    }


}
