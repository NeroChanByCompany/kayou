package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 车队表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 13:04:46
 */
@Data
@TableName("flt_fleet")
public class FltFleetEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增主键
	 */
	@TableId
	private Long id;
	/**
	 * 车队名称
	 */
	private String name;
	/**
	 * 车队头像
	 */
	private String avatar;
	/**
	 * 创建来源（1：APP；2：TBOSS）
	 */
	private Integer createType;
	/**
	 * TBOSS创建人ID
	 */
	private String tbossUserId;
	/**
	 * 是否企业客户
	 */
	private String isCompany;
	/**
	 * 企业名称
	 */
	private String companyName;
	/**
	 * 统一社会信用代码
	 */
	private String creditCode;
	/**
	 * 是否集团客户
	 */
	private String isGroup;
	/**
	 * 是否VIP
	 */
	private String isVip;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
