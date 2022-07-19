package com.jac.app.job.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liuBing
 * @Classname UserMapper
 * @Description TODO 冻结用户定时任务
 * @Date 2021/8/9 14:37
 */
@Data
@TableName("user")
@Accessors(chain = true)
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId
    private Long id;
    /**
     * 用户中心ID
     */
    private String ucId;
    /**
     * 用户账号
     */
    private String accountName;
    /**
     *
     */
    private String ucPassword;
    /**
     *
     */
    private String password;
    /**
     * 1:司机app, 2:车主app, 3:tboss, 4:物流web
     */
    private Integer createType;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 用户电话
     */
    private String phone;
    /**
     * 组织机构代码
     */
    private String orgCode;
    /**
     * 客户标签
     */
    private String custTag;
    /**
     * 地域
     */
    private String region;
    /**
     * 省份/直辖市
     */
    private String provinceDesc;
    /**
     * 地级市
     */
    private String cityDesc;
    /**
     * 县级市/县
     */
    private String countyDesc;
    /**
     *
     */
    private String identityCard;
    /**
     * 驾照类型
     */
    private Integer drivingLicense;
    /**
     * 创建者ID
     */
    private String createUserId;
    /**
     * 车队版用当前车队ID
     */
    private String ownrCurTeamId;
    /**
     * 司机版用当前车队ID
     */
    private String drvrCurTeamId;
    /**
     * 司机版用当前车辆ID
     */
    private String drvrCurCarId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 最后登陆时间
     */
    private Date lastLogonTime;
    /**
     * 首次登录时间
     */
    private Date firstLoginTime;
    /**
     * 邀请人id
     */
    private String inviterId;
    /**
     * 客户APP用户头像url
     */
    private String userPicUrl;
    /**
     * 新手指引标签 0 老用户 1 新用户未完成新手指引 2 完成新手指引
     */
    private Integer noviceGuideTag;
    /**
     * 发送消息使用的key值
     */
    private String sendMessageKey;
    /**
     * 0-用户端，1-车队端
     */
    private String appType;
    /**
     * 0 android 1 ios
     */
    private String deviceType;
    /**
     * 签名
     */
    private String signature;
    /**
     * 性别
     */
    private String sex;
    /**
     * 兴趣
     */
    private String interest;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 驾龄
     */
    private Integer drivingAge;
    /**
     * 邮件
     */
    private String email;
    /**
     * 年收入单位（万元）
     */
    private Integer annualIncome;
    /**
     * 真名
     */
    private String realName;
    /**
     * 设备唯一标识
     */
    private String deviceId;
    /**
     * 信息是否已经补全, 1: 已经补全
     */
    private Integer infoOk;
    /**
     * 账户是否激活
     * 0 已激活
     * 1 已注销
     */
    private Integer status;
    /**
     * 用户注册ip
     */
    private String ip;
    /**
     * 用户冻结状态备注
     */
    private String remark;
    /**
     * 用户冻结次数
     */
    private Integer freezeNum;

}
