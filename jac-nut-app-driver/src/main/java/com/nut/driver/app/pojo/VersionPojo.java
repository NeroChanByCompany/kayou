package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description: 帮助手册
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-28 14:22
 * @Version: 1.0
 */
@Data
public class VersionPojo {
    /**
     * 手册ID
     */
    private String manualId;
    /**
     * 手册标题
     */
    private String manualTitle;
    /**
     * 手册类型（1：文本 - 2：链接）
     */
    private Integer manualType;

    /**
     * 手册内容
     */
    private String manualValue;
}
