package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-23 16:51:40
 */
@Data
@TableName("work_order_operate")
public class WorkOrderOperateEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增主键
	 */
	@TableId
	private Long id;
	/**
	 * 工单号
	 */
	private String woCode;
	/**
	 * 工单操作状态
	 */
	private Integer operateCode;
	/**
	 * 操作唯一标识,关联图片表用
	 */
	private String operateId;
	/**
	 * 对服务APP不可见标志	1：隐藏，默认0
	 */
	private Integer isHiddenToApp;
	/**
	 * 服务类型
	 */
	private Integer serviceType;
	/**
	 * 处理方式
	 */
	private Integer dealType;
	/**
	 * 费用类型
	 */
	private Integer chargeType;
	/**
	 * 付费方式
	 */
	private Integer payType;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 照片总数量
	 */
	private Integer photoNum;
	/**
	 * 服务记录显示标题
	 */
	private String title;
	/**
	 * 服务记录显示内容
	 */
	private String textJson;
	/**
	 * 服务记录显示内容(只在tboss和app有显示区别时使用)
	 */
	private String textJsonTb;
	/**
	 * 是否隐藏1：隐藏，默认0
	 */
	private Integer hiddenFlg;
	/**
	 * 经度
	 */
	private String longitude;
	/**
	 * 纬度
	 */
	private String latitude;
	/**
	 * 操作者id
	 */
	private String userId;
	/**
	 * 外出救援次数
	 */
	private Integer timesRescueNumber;
	/**
	 * 完成状态（0:未完成，1：已完成）
	 */
	private Integer finishedStatus;
	/**
	 * 保修保内分次提交标识（0:否，1：是）
	 */
	private Integer subTwiceMark;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
