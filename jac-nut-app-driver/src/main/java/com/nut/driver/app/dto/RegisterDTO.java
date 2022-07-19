package com.nut.driver.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: APP端注册返回信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.dto
 * @Author: yzl
 * @CreateTime: 2021-06-15 17:18
 * @Version: 1.0
 */
@Data
public class RegisterDTO implements Serializable {

    private Long id;
    // 用户ID
    private String userId;

    // 电话
    private String mobile;

    // token凭证
    private String token;
}
