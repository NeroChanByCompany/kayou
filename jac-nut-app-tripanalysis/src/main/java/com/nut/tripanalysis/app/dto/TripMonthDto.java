package com.nut.tripanalysis.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 月行程数据查询接口--dto
 */
@Data
public class TripMonthDto implements Serializable {

	private String tripDate;  //  行程日期
	private int tripCount;  //  行程数

}
