package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 工单客户关系中间表实体类
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.domain
 * @Author: yzl
 * @CreateTime: 2021-06-22 10:45
 * @Version: 1.0
 */
@Data
public class RelationWorkOrderUser {
    private Long id;

    private String ucId;

    private String woCode;

    private String phone;

    private String appType;

    private Integer workState;

    private Date createTime;

    private Date updateTime;

}
