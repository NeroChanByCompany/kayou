package com.nut.driver.app.dto;


import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class BrandInfoDto implements Serializable {

    // 品牌id
    private BigInteger brandId;
    // 品牌名称
    private String brandName;

}
