package com.jac.app.job.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author liuBing
 * @Classname UserMessageRecord
 * @Description TODO
 * @Date 2021/8/13 16:51
 */
@Data
@Accessors(chain = true)
public class UserMessageRecord {

    private Long id;

    private String messageId;

    private String title;

    private String content;

    private Integer pushShowType;

    private Integer messageCode;

    private Integer userVisible;

    private String receiverId;

    private Integer receiveAppType;

    private String senderId;

    private Long sendTime;

    private Integer readFlag;

    private String messageExtra;

    private Integer type;

    private Integer stype;

    private Integer receiveState;

    private Integer messageType;

    private String messageTypeName;

    private Date createTime;

    private Date updateTime;
}
