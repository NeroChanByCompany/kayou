package com.nut.tripanalysis.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询某车队下全部车型信息-dto
 */
@Data
public class ModelInfoDto implements Serializable {

	/**
	 * 车型ID
	 */
	private String modelId;

	/**
	 * 车型名称
	 */
	private String modelName;

	/**
	 * 数量
	 */
	private Integer carNum = 0;

}
