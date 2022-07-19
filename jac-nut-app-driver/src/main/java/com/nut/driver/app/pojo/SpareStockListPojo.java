package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description: 经销商服务站列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-28 16:39
 * @Version: 1.0
 */
@Data
public class SpareStockListPojo {
    /**
     * 主键
     */
    private Long id;

    private String distance;

    /**
     * 渠道类型：0.总经销 1.一级经销商 2.二级经销商 3.服务站
     */
    private String stockType;

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
    private Long stock;

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
}
