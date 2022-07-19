package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author liuBing
 * @Classname CarMaintainRecordEntity
 * @Description TODO
 * @Date 2021/6/24 14:11
 */
@Data
@TableName("car_maintain_record")
public class CarMaintainRecordEntity {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 底盘号
     */
    private String carVin;

    /**
     * 车牌号
     */
    private String carNumber;
    /**
     *保养项目（逗号拼接）
     */
    private String maintenanceProgramStr;
    /**
     *保养时间
     */
    private Date maintainMileageTime;
    /**
     *保养里程
     */
    private Double maintainMileageMileage;

    /**
     * 保养更新来源：客户APP：1；服务APP：2；crm同步：3'
     */
    private Integer maintainSource;
    /**
     *'操作人
     */
    private String opUser;
    /**
     *操作时间
     */
    private Date optDate;
}
