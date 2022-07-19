package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * ��������¼��
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-29 15:50:21
 */
@Data
@TableName("phy_exa_record")
public class PhyExaRecordEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * ����ID
	 */
	@TableId
	private Long id;
	/**
	 * ����ID
	 */
	private String carId;
	/**
	 * ��������
	 */
	private Integer faultNumber;
	/**
	 * ���϶�Ӧ����ID����
	 */
	private String faultIds;
	/**
	 * ���ʱ��
	 */
	private Long phyExaTime;

}
