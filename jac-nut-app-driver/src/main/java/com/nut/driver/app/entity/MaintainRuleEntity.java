package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author liuBing
 * @Classname MaintainRuleEntity
 * @Description TODO
 * @Date 2021/8/23 13:58
 */
@Data
@Accessors(chain = true)
@TableName("maintain_rule")
public class MaintainRuleEntity {
    private Long id;
    /**
     * '车型结构号'
     */
    private String structureNum;
    /**
     * '总成类别'
     */
    private Integer sumType;
    /**
     * '保养内容
     */
    private String maintainContent;
    /**
     * '保养类别'
     */
    private Integer maintainType;
    /**
     * '首保里程(单位：KM)'
     */
    private Integer firstMileage;
    /**
     * '首保时间(月份间隔)'
     */
    private Integer firstMonth;
    /**
     * '定保里程(单位KM)'
     */
    private Integer routineMileage;
    /**
     * '定保时间(月份间隔)'
     */
    private Integer routineMonth;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新时间
     */
    private Date updateDate;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 更新人
     */
    private String updateUser;
    /**
     * 逻辑删除标识：0-未删除、1-已删除
     */
    @TableLogic
    private Integer deleted;
}
