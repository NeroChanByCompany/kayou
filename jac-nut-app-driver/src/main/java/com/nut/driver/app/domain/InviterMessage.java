package com.nut.driver.app.domain;

import lombok.Data;

/**
 * @Description: 邀请人的身份与电话信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.domain
 * @Author: yzl
 * @CreateTime: 2021-08-26 19:20
 * @Version: 1.0
 */
@Data
public class InviterMessage {
    private String userType;
    private String phone;
}
