package com.nut.driver.app.vo;/**
 * Created by Administrator on 2021/11/26.
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @version v1.0.0
 * @desc
 * @auther wangshuai
 * @create 2021/11/26
 * @Company esv
 */
@Data
public class SimOrderVo {

    @ApiModelProperty(value = "sim卡号")
    private BigDecimal simNumber;
    @ApiModelProperty(value = "支付金额")
    private BigDecimal realCost;
    @ApiModelProperty(value = "套餐名称")
    private String paName;
    @ApiModelProperty(value = "底盘号")
    private String chassisNumber;

    @ApiModelProperty(value = "车牌号")
    private String carNumber;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "订单状态")
    private String orderStatus;
    @ApiModelProperty(value = "套餐流量")
    private String pacFlow;
    @ApiModelProperty(value = "交易单号")
    private String orderNumber;

}
