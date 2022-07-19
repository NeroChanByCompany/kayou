package com.nut.truckingteam.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description: 获取消息模板推送消息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.form
 * @Author: yzl
 * @CreateTime: 2021-06-22 13:19
 * @Version: 1.0
 */
@NutFormValidator
@Data
public class PushMesForm extends BaseForm {
    /**
     * 非自定义消息，如果title、content 为空，则type、stype必填，只能为整数,查询消息模板
     */
    private String type;
    /**
     * 非自定义消息，如果title、content 为空，则type、stype必填，只能为整数,查询消息模板
     */
    private String stype;
    /**
     * 自定义消息（如果title、content 不为空）,有不接收推送设置功能时，notReceivePushType、notReceivePushStype 必填，只能为整数,查询消息模板
     */
    private String notReceivePushType;
    /**
     * 自定义消息,（如果title、content 不为空）有不接收推送设置功能时，notReceivePushType、notReceivePushStype 必填，只能为整数,查询消息模板
     */
    private String notReceivePushStype;
    /**
     * 如果title、content 不为空，则按赋值的title、content推送
     */
    private String title;
    private String content;
    /**
     * 通配符map(json 可转为map的json形式 : key:通配符，value：通配符的值)替换消息模板里的通配值
     */
    private String wildcardMap;
    /**
     * 扩展信息map(json 可转为map的json形式 : key:code，value：值)， 推送消息，页面跳转时需要的附加字段
     */
    private String messageExtra;
    /**
     * 消息分类：1：广播，2：指定消息推送
     */
    @NotNull(message = "消息分类不能为空")
    @NotBlank(message = "消息分类不能为空")
    @Pattern(regexp = RegexpUtils.ZERO_NEGATIVE_INTEGERS_REGEXP, message = "消息分类只能为整数")
    private String pushType;
    /**
     * 接收者用户id ,多个用户用逗号分隔,指定用户推送时，请填写 receiverId
     */
    private String receiverId;
    /**
     * 发送者用户id 系统为system
     */
    private String senderId;
    /**
     * 消息展示分类：1、车队消息，2、个人消息，3、工单消息，4、通知类消息
     */
    @NotNull(message = "消息展示分类不能为空")
    @NotBlank(message = "消息展示分类不能为空")
    @Pattern(regexp = RegexpUtils.ZERO_NEGATIVE_INTEGERS_REGEXP, message = "消息展示分类只能为整数")
    private String pushShowType;
    /**
     * 页面跳转code
     */
    @Pattern(regexp = RegexpUtils.ZERO_NEGATIVE_INTEGERS_REGEXP, message = "页面跳转code只能为整数")
    private String messageCode;
    /**
     * 产品标识
     */
    private String product;
    /**
     * 用户是否可见 1：不可见，不展示到消息里；2：可见，在消息模块里显示,默认可见
     */
    @Pattern(regexp = RegexpUtils.ZERO_NEGATIVE_INTEGERS_REGEXP, message = "用户是否可见只能为整数")
    private String isUserVisible;
    /**
     * 消息保存有效期 yyyy-MM-dd HH:mm:ss
     */
    private String expireTime;
    /**
     * 定时推送的标识 1：定时，2：时间间隔
     */
    @Pattern(regexp = RegexpUtils.ZERO_NEGATIVE_INTEGERS_REGEXP, message = "定时推送的标识只能为整数")
    private String pushTimeFlag;
    /**
     * 定时推送的次数
     */
    @Pattern(regexp = RegexpUtils.ZERO_NEGATIVE_INTEGERS_REGEXP, message = "定时推送的次数只能为整数")
    private String pushCount;
    /**
     * 定时推送的时间间隔 秒，定时推送的时间 yyyy-MM-dd HH:mm
     */
    private String pushTime;
    /**
     * 消息展示分类为1、车辆消息时，车辆ID必填 ,多个车辆ID用逗号分隔,车辆消息指定用户推送时，请填写 receiverId
     */
    private String carId;
    /**
     * 接收者角色：1：管理员，2：司机，3司机和管理员
     * 消息展示分类为1、车辆消息时，接收者角色必填
     */
    private Integer receiverRole;
    /**
     * 1：不调用爱心推推送，只存储消息。默认既推送又保存
     */
    private String noPushSaveOnly;

    /**
     * 消息分类（自定义消息必填）
     */
    private String messageType;

    /**
     * 消息分类名称（自定义消息必填）
     */
    private String messageTypeName;
}
