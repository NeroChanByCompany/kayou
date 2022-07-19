package com.jac.app.job.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author liuBing
 * @Classname UserFreezeConfigEntity
 * @Description TODO
 * @Date 2021/10/11 13:45
 */
@Data
@TableName("user_freeze_config")
public class UserFreezeConfigEntity {
    /**
     * 主键
     */
    private Long id;
    /**
     * 冻结名称
     */
    private String freezeName;
    /**
     * 解冻时间
     */
    private String thawTime;
    /**
     * 时间格式 1 小时 2 天 3 周
     */
    private Integer timeFormat;
    /**
     * 冻结具体配置值，和用户冻结次数相关
     */
    private Integer freezeNum;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
