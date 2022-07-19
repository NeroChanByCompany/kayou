package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 字典表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-23 15:38:33
 */
@Data
@TableName("data_dict")
public class DataDictEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private String id;
	/**
	 * 
	 */
	private String code;
	/**
	 * 
	 */
	private Integer value;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private String keyType;
	/**
	 * 
	 */
	private Integer patternA;
	/**
	 * 
	 */
	private Integer patternB;
	/**
	 * 
	 */
	private Integer patternC;
	/**
	 * 
	 */
	private Integer patternD;
	/**
	 * 0：无效；1：有效
	 */
	private Integer availableFlag;

}
