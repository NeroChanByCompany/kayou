package com.nut.tools.app.pojo;

import lombok.Data;

/**
 * @author liuBing
 * @Classname NotReceiveUserInfoPojo
 * @Description TODO
 * @Date 2021/6/22 19:27
 */
@Data
public class NotReceiveUserInfoPojo {
    /**
     * 不接收消息的用户
     */
    private String userId;
    /**
     * 不接收消息的客户端
     */
    private Integer notReceiveAppType;
}
