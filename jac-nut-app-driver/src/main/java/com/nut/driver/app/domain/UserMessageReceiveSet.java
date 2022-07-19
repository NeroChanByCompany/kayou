package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.domain
 * @Author: yzl
 * @CreateTime: 2021-06-30 11:10
 * @Version: 1.0
 */
@Data
public class UserMessageReceiveSet {
    private Long id;

    private String userId;

    private Integer messageType;

    private Integer type;

    private Integer stype;

    private Integer appType;

    private Date createTime;
}
