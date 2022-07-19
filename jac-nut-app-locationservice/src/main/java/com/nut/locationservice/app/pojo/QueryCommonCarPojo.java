package com.nut.locationservice.app.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigInteger;

/**
 * @Description: 车辆信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-17 09:32
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class QueryCommonCarPojo {
    //通讯号
    private BigInteger commId;
    //发动机类型
    private String engineType;
    //邮箱容量
    private String oilCapacity;
    //天然气or柴油车：0表示燃气，1表示燃油
    private Integer fuel;
}
