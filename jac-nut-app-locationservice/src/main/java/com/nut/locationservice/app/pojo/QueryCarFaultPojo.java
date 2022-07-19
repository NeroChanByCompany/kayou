package com.nut.locationservice.app.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigInteger;

/**
 * @Description: 故障类型
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:19
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class QueryCarFaultPojo {
    private String systemSource;
    private String systemType;
    private String typeModel;
    private String faultDes;
    private BigInteger spn;
    private BigInteger fmi;
    private String faultSolutions;
    private String symbolCode;
}
