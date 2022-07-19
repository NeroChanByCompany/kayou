package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author liuBing
 * @Classname CustomMaintainItemEntity
 * @Description TODO
 * @Date 2021/6/24 15:22
 */
@Data
@TableName("custom_maintain_item")
public class CustomMaintainItemEntity {

    private Long id;

    private Long maintainId;

    private Integer maintainItemId;

}
