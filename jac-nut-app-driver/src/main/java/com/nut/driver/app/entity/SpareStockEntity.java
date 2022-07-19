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
 * @date 2021-06-28 16:17:56
 */
@Data
@TableName("hy_spare_stock")
public class SpareStockEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * Dms代码
	 */
	private String dmsId;
	/**
	 * 渠道类型：0.总经销 1.一级经销商 2.二级经销商 3.服务站
	 */
	private String stockType;
	/**
	 * 仓库代码
	 */
	private String stockCode;
	/**
	 * 仓库名称
	 */
	private String stockName;
	/**
	 * 仓库地址
	 */
	private String stockAddress;
	/**
	 * 联系人
	 */
	private String linkPerson;
	/**
	 * 联系电话
	 */
	private String linkPhone;
	/**
	 * 备件代码
	 */
	private String sparePartCode;
	/**
	 * 备件名称
	 */
	private String sparePartName;
	/**
	 * 库存数量
	 */
	private Integer stock;
	/**
	 * 供应商代码
	 */
	private String supplierCode;
	/**
	 * 供应商名称
	 */
	private String supplierName;
	/**
	 * 经度
	 */
	private Long longitude;
	/**
	 * 纬度
	 */
	private Long latitude;
	/**
	 * 同步时间
	 */
	private Date syncTime;

}
