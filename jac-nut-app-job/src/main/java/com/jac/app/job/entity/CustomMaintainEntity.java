package com.jac.app.job.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author liuBing
 * @Classname CustomMaintainEntity
 * @Description TODO
 * @Date 2021/8/12 16:22
 */
@Data
@Accessors(chain = true)
@TableName("custom_maintain_info")
public class CustomMaintainEntity implements Serializable {
    private static final long serialVersionUID = -7980368130141099749L;

}
