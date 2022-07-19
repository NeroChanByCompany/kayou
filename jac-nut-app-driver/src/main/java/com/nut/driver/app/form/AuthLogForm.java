package com.nut.driver.app.form;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuBing
 * @Classname authLogEntity
 * @Description TODO 用户实名认证日志表
 * @Date 2021/8/10 11:24
 */
@Data
@ApiModel("用户实名认证参数接收")
@Accessors(chain = true)
public class AuthLogForm extends BaseForm {
    /**
     * 主键
     */
    @ApiModelProperty(name = "id" , notes = "主键" , dataType = "String")
    private Long id;
    /**
     * 实名认证是否成功（0：成功，1：失败）
     */
    @ApiModelProperty(name = "isAuth" , notes = "实名认证是否成功（0：成功，1：失败）" , dataType = "String")
    private String isAuth;
    /**
     * 登记物联卡号
     */
    @ApiModelProperty(name = "msisdn" , notes = "登记物联卡号" , dataType = "String")
    private String msisdn;
    /**
     * 登记人姓名
     */
    @ApiModelProperty(name = "name" , notes = "登记人姓名" , dataType = "String")
    private String name;
    /**
     * 登记人身份证号
     */
    @ApiModelProperty(name = "idCard" , notes = "登记人身份证号" , dataType = "String")
    private String idCard;
    /**
     * 登记结果接收手机号
     */
    @ApiModelProperty(name = "phone" , notes = "登记结果接收手机号" , dataType = "String")
    private String phone;
    /**
     * 登记时间
     */
    @ApiModelProperty(name = "registerTime" , notes = "登记时间" , dataType = "Long")
    private Long registerTime;
    /**
     * 订单号
     */
    @ApiModelProperty(name = "orderNo" , notes = "订单号" , dataType = "String")
    private String orderNo;
    /**
     * 失败原因
     */
    @ApiModelProperty(name = "msg" , notes = "失败原因" , dataType = "String")
    private String msg;

    /**
     * 失败原因
     */
    @ApiModelProperty(name = "carVin" , notes = "车辆底盘号" , dataType = "String")
    private String carVin;

}
