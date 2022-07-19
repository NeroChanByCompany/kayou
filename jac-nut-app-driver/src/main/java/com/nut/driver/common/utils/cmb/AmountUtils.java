package com.nut.driver.common.utils.cmb;

import java.math.BigDecimal;

/**
 * @description: 金额处理工具
 * @author: MengJinyue
 * @create: 2021-04-27 10:16
 **/
public class AmountUtils {
    /**
     * 元转分，确保price保留两位有效数字
     * @return
     */
    public static Integer changeY2F(BigDecimal price) {
        if (price == null){
            return null;
        }
        BigDecimal bigDecimal = price.setScale(2);
        return bigDecimal.multiply(new BigDecimal(100)).intValue();
    }

    /**
     * 分转元，转换为bigDecimal
     *
     * @return
     */
    public static BigDecimal changeF2Y(int price) {
        return BigDecimal.valueOf(Long.valueOf(price)).divide(new BigDecimal(100));
    }
}
