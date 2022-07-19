package com.nut.driver.app.form;/**
 * Created by Administrator on 2021/11/25.
 */

import com.nut.common.base.BaseForm;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @version v1.0.0
 * @desc
 * @auther wangshuai
 * @create 2021/11/25
 * @Company esv
 */
@Data
public class BuySetMealForm extends BaseForm {
    //支付金额
    private BigDecimal orderAmount;
    //用户手机号，车辆接口返回
    private String phone;
    //终端通信号，车辆接口返回 autoTerminal
    private String simNum;
    //终端ID 车辆接口返回 terminalId
    private String simId;
    //套餐ID，套餐查询接口 返回 ID
    private String pacId;
    //套餐名称,套餐查询接口 返回 setMealTitl
    private String paName;
    //套餐类别,套餐查询接口 返回 套餐类型
    private String pacType;
    //套餐流量,套餐查询接口 返回 dataUsageSize
    private String pacFlow;
    //套餐售价,套餐查询接口 返回 price
    private BigDecimal pacPrice;
    //套餐有效时间,套餐查询接口 返回 periodTypeName
    private String actiTime;
    //套餐描述,套餐查询接口 返回 setMealContent
    private String pacDesc;


}
