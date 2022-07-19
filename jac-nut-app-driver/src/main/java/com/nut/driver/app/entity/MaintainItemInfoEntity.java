package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author liuBing
 * @Classname MaintainItemInfoEntity
 * @Description TODO
 * @Date 2021/6/24 15:32
 */
@Data
@TableName("maintain_item_info")
public class MaintainItemInfoEntity {
    private Integer maintainItemId;

    private String maintainItemName;

    private String createUser;

    private String updateUser;

    private Date createTime;

    private Date updateTime;
}
