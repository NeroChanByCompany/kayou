package com.jac.app.job.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liuBing
 * @Classname WorkOrderLogEntity
 * @Description TODO
 * @Date 2021/8/16 9:54
 */
@Data
@Accessors(chain = true)
@TableName("work_order_log")
public class WorkOrderLogEntity implements Serializable {
    private static final long serialVersionUID = 2978688281978524511L;
    /**
     * 主键
     */
    private Long id;

    /**
     * '工单号'
     */
    private String woCode;

    /**
     * 工单上报dms是否成功 0成功 1失败
     */
    private Integer status;

    /**
     * 工单上报异常信息
     */
    private String message;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
