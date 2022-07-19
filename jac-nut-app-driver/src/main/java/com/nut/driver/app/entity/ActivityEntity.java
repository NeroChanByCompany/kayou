package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author liuBing
 * @Classname ActivityEntity
 * @Description TODO
 * @Date 2021/7/7 11:02
 */
@Data
@TableName("activity")
public class ActivityEntity {


    private Integer id;
    private String activeCode;
    private String activeName;
    private Date startTime;
    private Date endTime;
    private String remark;
    private Date createTime;
    @TableField(exist = false)
    private Integer type;

}
