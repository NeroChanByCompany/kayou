package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 用户积分对象
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.dto
 * @Author: yzl
 * @CreateTime: 2021-06-21 14:16
 * @Version: 1.0
 */
@Data
public class IntegralDetailDto {

    /**
     * 数据主键
     */
    private Long id;

    /**
     * 业务主键
     */
    private String ucId;

    /**
     * 积分数量
     */
    private Integer integralCounts;

    /**
     * 积分状态
     */
    private Integer integralState;

    private Long updateTime;


}
