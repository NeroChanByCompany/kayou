package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description: 查询所有消息分类的未读消息数量
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-27 11:28
 * @Version: 1.0
 */
@Data
public class QueryMessageCountPojo {
    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 未读消息数量
     */
    private Integer count;
}
