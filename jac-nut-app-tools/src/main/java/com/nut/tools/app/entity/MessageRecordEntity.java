package com.nut.tools.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author liuBing
 * @Classname MessageRecordEntity
 * @Description TODO
 * @Date 2021/6/22 19:42
 */
@Data
@TableName("car_message_record")
public class MessageRecordEntity {


    private Long id;

    private String messageId;

    private String title;

    private String content;

    private Integer messageCode;

    private Integer userVisible;

    private String carId;

    private Integer receiverRole;

    private Integer receiveAppType;

    private Long sendTime;

    private String messageExtra;

    private Integer type;

    private Integer stype;

    private Integer pushShowType;

    private String receiverId;

    private String senderId;

    private Integer readFlag;

    private Integer receiveState;

    private Long createTimestamp;

    private Date createTime;

    private Date updateTime;

    private Integer messageType;

    private String messageTypeName;

}
