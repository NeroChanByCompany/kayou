package com.nut.driver.app.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author liuBing
 * @Classname QueryCustomMaintainItemCountPojo
 * @Description TODO
 * @Date 2021/6/24 14:37
 */
@Data
public class QueryCustomMaintainItemCountPojo {

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

    private Integer maintainCount;
}
