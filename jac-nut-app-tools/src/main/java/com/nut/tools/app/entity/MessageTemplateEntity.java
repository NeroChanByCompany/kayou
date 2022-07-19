package com.nut.tools.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author liuBing
 * @Classname MessageTemplateEntity
 * @Description TODO
 * @Date 2021/6/22 19:28
 */
@Data
@TableName("message_template")
public class MessageTemplateEntity {
    private String templateId;

    private String title;//标题

    private String content;//消息内容

    private Integer type;//分类

    private String words;//额外的参数

    private String code;//代码

    private Integer receiveType;//接收者类型

    private Integer stype;//分类

    private Integer messageType;// 消息分类

    private String messageTypeName;// 消息分类名称
}
