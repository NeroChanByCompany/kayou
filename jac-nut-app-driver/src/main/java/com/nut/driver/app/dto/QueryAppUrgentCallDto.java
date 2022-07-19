package com.nut.driver.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 功能 紧急电话服务 App 查询
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-28 14:40
 * @Version: 1.0
 */
@Data
public class QueryAppUrgentCallDto implements Serializable {

    private String telId;

    private String tel;

    private String name;

    private String imgpath;

}
