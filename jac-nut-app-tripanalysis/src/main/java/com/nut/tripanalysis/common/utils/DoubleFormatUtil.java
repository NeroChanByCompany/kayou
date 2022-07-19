package com.nut.tripanalysis.common.utils;

import java.text.DecimalFormat;

/**
 * double类型格式化工具类
 *
 * wangyang
 */
public class DoubleFormatUtil {

    private DoubleFormatUtil() {
    }

    static private DecimalFormat doubleFormat = new DecimalFormat("#.0");

    /**
     * 格式化为小数点后1位
     * @param value
     * @return
     */
    static public double formatDoubleValueTo1Bit(double value) {
        return Double.parseDouble(doubleFormat.format(value));
    }

}
