package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 版本更新说明表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 14:17:55
 */
@Data
@TableName("version")
public class VersionEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 生效范围（车队APP：1 - 司机APP：2）
	 */
	private Integer appCode;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 类型（文本：1 - 链接：2 - 文件：3）
	 */
	private Integer type;
	/**
	 * 说明
	 */
	private String explainValue;
	/**
	 * 应用状态（停用：0 - 启用：1）
	 */
	private Integer applyStatus;
	/**
	 * 修改人
	 */
	private String updateUser;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 创建人
	 */
	private String createUser;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
