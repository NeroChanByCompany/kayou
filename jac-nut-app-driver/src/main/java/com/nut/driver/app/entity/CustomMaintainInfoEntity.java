package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author liuBing
 * @Classname CustomMaintainInfoEntity
 * @Description TODO
 * @Date 2021/6/24 14:40
 */
@Data
@TableName("custom_maintain_info")
public class CustomMaintainInfoEntity {

    private Long id;

    private String customMaintainName;

    private Integer customMaintainType;

    private String customMaintainDescribe;

    private String carId;

    private String carNumber;

    private String userId;

    private Integer maintainStatus;

    private Integer status;

    private String remarks;

    private Date createTime;

    private Date updateTime;

    private Integer appType;

    private String vin;

    private String teamId;
}
