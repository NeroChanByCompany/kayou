package com.nut.locationservice.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * EOL系统未下线车辆表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-16 19:09:02
 */
@Data
@TableName("car_eol_system")
public class CarEolSystemEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车辆ID
     */
    @TableId
    private Long id;
    /**
     * vin
     */
    private String carVin;
    /**
     * 通信号
     */
    private String autoTerminal;
    /**
     * 终端来源(0:东风 1:F9)
     */
    private Integer tboxType;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 基础车型
     */
    private String carModelBase;
    /**
     * vin后8位
     */
    private String carVinSub;

}
