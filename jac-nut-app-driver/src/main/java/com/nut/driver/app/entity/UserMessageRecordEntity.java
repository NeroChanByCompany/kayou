package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 个人消息存储表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-27 13:01:24
 */
@Data
@Accessors(chain = true)
@TableName("user_message_record")
public class UserMessageRecordEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 消息id
	 */
	@TableId
	private Long id;
	/**
	 * 消息id
	 */
	private String messageId;
	/**
	 * 消息标题
	 */
	private String title;
	/**
	 * 消息内容
	 */
	private String content;
	/**
	 * 消息展示分类 1、车队消息，2、个人消息，3、工单消息，4、通知类消息
	 */
	private Integer pushShowType;
	/**
	 * 跳转编码
	 */
	private Integer messageCode;
	/**
	 * 用户是否可见，1：不可见，2：可见
	 */
	private Integer userVisible;
	/**
	 * 接收者id
	 */
	private String receiverId;
	/**
	 * 接收的客户端1、司机，2、车主，3、服务app,4:车机,5:管理员,6 :车主版和司机版
	 */
	private Integer receiveAppType;
	/**
	 * 发送者id
	 */
	private String senderId;
	/**
	 * 发送时间
	 */
	private Long sendTime;
	/**
	 * 已读未读 1：未读，2：已读
	 */
	private Integer readFlag;
	/**
	 * 附加字段 前端使用
	 */
	private String messageExtra;
	/**
	 * 大分类
	 */
	private Integer type;
	/**
	 * 小分类
	 */
	private Integer stype;
	/**
	 * 接收状态,0:不接收，1：接收
	 */
	private Integer receiveState;
	/**
	 * 消息分类
	 */
	private Integer messageType;
	/**
	 * 消息分类名称
	 */
	private String messageTypeName;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
