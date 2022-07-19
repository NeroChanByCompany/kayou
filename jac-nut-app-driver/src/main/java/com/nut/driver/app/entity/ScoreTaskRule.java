package com.nut.driver.app.entity;

import lombok.Data;

/**
 * @Description: 积分实体类
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.entity
 * @Author: yzl
 * @CreateTime: 2021-06-15 19:09
 * @Version: 1.0
 */
@Data
public class ScoreTaskRule {

    /**
     * 每次积分值
     * 1:注册
     * 2:绑车
     * 3:回复
     * 4:发帖
     * 5:分享
     * 6:服务预约
     * 7:服务评论
     */
    private Long ruleUnique;

    private Integer addScore;

    private Integer availFlag;

    private Integer pointDistribution;
}
