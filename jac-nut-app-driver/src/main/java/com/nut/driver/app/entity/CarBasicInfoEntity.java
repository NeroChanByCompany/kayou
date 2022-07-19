package com.nut.driver.app.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuBing
 * @Classname CarBasicInfo
 * @Description TODO 车辆基础信息接口
 * @Date 2021/8/3 13:41
 */
@Data
@Accessors(chain = true)
public class CarBasicInfoEntity {
    /**
     * sim卡号
     */
    private String simId;
    /**
     * iccId
     */
    private String iccId;
    /**
     * 订单号
     */
    private String orderNo;
}
