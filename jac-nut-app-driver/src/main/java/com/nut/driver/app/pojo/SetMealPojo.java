package com.nut.driver.app.pojo;/**
 * Created by Administrator on 2021/11/25.
 */

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version v1.0.0
 * @desc
 * @auther wangshuai
 * @create 2021/11/25
 * @Company esv
 */
@Data
public class SetMealPojo {
    //套餐描述
    private String setMealContent;
    //套餐名称
    private String setMealTitle;
    //创建时间
    private String createAt;
    //创建人
    private String createBy;
    //流量单位
    private String dataUsageUnit;
    //流量大小
    private Integer dataUsageSize;
    //删除标记
    private Integer deleted;
    //有效期类型
    private String periodType;
    //套餐价格
    private BigDecimal price;
    //套餐类型
    private String setMealType;

    private String setMealTypeName;
    //套餐状态名称
    private String statusName;
    private String id;
    //套餐类型名称
    private String periodTypeName;
    //套餐状态
    private String status;

}
