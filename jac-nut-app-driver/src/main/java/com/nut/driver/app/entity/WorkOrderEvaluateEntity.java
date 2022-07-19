package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 工单评价表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-23 14:59:00
 */
@Data
@TableName("work_order_evaluate")
public class WorkOrderEvaluateEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 工单号
	 */
	private String woCode;
	/**
	 * 服务站id
	 */
	private Long stationId;
	/**
	 * 总体满意程度星级
	 */
	private Integer wholeStar;
	/**
	 * 评价标签
	 */
	private String reviewLabel;
	/**
	 * 其他补充标签
	 */
	private String otherLabel;
	/**
	 * 本次服务类型（null:免费；其他:自费）
	 */
	private String cost;
	/**
	 * 是否再次光临（否：0 - 是：1）
	 */
	private Integer comeAgain;
	/**
	 * 不满意的流程（数据字典A080）
	 */
	private String discontent;
	/**
	 * 其他建议
	 */
	private String content;
	/**
	 * 评价人id
	 */
	private Long userId;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 评价类型，10: 系统自动评价, 20: 用户评价
	 */
	private Integer evaluateType;

}
