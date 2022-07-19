package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 *
 * @author hcb
 * @email hcb@163.com
 * @date 2021-11-17 15:56:02
 */
@Data
@TableName("score_task_rule")
public class ScoreTaskRuleEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Long id;
	/**
	 * 渠道id 1 靑汽
	 */
	private Integer distId;
	/**
	 * 任务类型/周期,0:vip，1：每日任务，2新手任务（一次），3：长期任务（不限）
	 */
	private Long scoreType;
	/**
	 * 任务名称
	 */
	private String taskName;
	/**
	 * 任务类别（积分项） 服务端开发使用
	 */
	private String taskType;
	/**
	 * 规则明显，前端展示用
	 */
	private String ruleName;
	/**
	 * 任务说明
	 */
	private String taskExplain;
	/**
	 * 任务简介
	 */
	private String taskBrief;
	/**
	 * 奖励次数,999999:不限
	 */
	private Integer addScoreNum;
	/**
	 * 每次积分值
	 */
	private Integer addScore;
	/**
	 * 任务积分值上限,999999：无限
	 */
	private Integer scoreUp;
	/**
	 * 1：连续性，2：周期性,,0:无规则
	 */
	private Integer statisType;
	/**
	 * 是否可用	1：可用，2：不可用
	 */
	private Integer availFlag;
	/**
	 * 会员日是否加倍	1：是，2：不是
	 */
	private Integer vipFlag;
	/**
	 * 操作者id
	 */
	private String operateId;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 	1：app，2：论坛
	 */
	private Integer addFrom;
	/**
	 * 积分发放段
	 */
	private Integer pointDistribution;
	/**
	 * 模块：1 任务中心，2 个人中心，3 服务，4 论坛
	 */
	private Integer module;
	/**
	 * 规则的唯一标识值   1:注册,2:邦车,3:回复,4:发帖,5:分享,6:服务预约,7:服务评论
	 */
	private Integer ruleUnique;
	/**
	 * 是否删除   1：是，0：否
	 */
	private Integer delFlag;

}
