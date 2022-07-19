package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author liuBing
 * @Classname UserFreezeRecordsEntity
 * @Description TODO
 * @Date 2021/10/11 13:48
 */
@Data
@TableName("user_freeze_records")
public class UserFreezeRecordsEntity {
    /**
     * 主键
     */
    private Long id;
    /**
     * 用户ucid
     */
    private String ucId;
    /**
     * 冻结时间
     */
    private Date freezeTime;
    /**
     * 解冻时长
     */
    private String thawTime;
    /**
     * 冻结项
     */
    private Integer freezeNum;
    /**
     * 时间格式 1 小时 2 天 3 周
     */
    private Integer timeFormat;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除 0 正常 1 删除
     */
    @TableLogic
    private Integer delFlag;
}
