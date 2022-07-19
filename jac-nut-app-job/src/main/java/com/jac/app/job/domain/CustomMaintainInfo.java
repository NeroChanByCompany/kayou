package com.jac.app.job.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author liuBing
 * @Classname CustomMaintainInfo
 * @Description TODO
 * @Date 2021/8/12 16:32
 */
@Data
@Accessors(chain = true)
public class CustomMaintainInfo {

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

    //是否提示过 1、已推送 2、 未推送
    private Integer remind;
}
