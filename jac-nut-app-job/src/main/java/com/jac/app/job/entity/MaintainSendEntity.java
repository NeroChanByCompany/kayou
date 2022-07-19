package com.jac.app.job.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author liuBing
 * @Classname MaintainSendEntity
 * @Description TODO
 * @Date 2021/8/12 16:26
 */
@Data
@Accessors(chain = true)
public class MaintainSendEntity implements Serializable {
    private static final long serialVersionUID = -348143515029262819L;

    /**
     * 保养id
     */
    private Long maintainInfoId;

    /**
     * 需要推送的用户id
     */
    private String userId;
    /**
     * 需要推送的消息标题
     */
    private String title;
    /**
     * 需要推送的内容
     */
    private String content;

    /**
     * 保养时间还是保养里程
     * @return
     */
    private String type;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 消息类型
     */
    private String messageTypeName;
}
