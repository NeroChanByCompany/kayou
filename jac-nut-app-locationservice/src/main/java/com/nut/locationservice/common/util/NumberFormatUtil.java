package com.nut.locationservice.common.util;


import java.math.BigDecimal;

/**
 * 数字转换工具类
 *
 */

public class NumberFormatUtil {

    /**
     * 取小数点后N位
     * @param data
     * @param num 位数（目前采用理想范围内的1-6位判断，临时使用）
     * @return 格式化数据
     */
    public static double getDoubleValueData(Double data ,Integer num) {
        java.text.DecimalFormat df = null;
        if (data == null) return 0d;
        if (null != num) {
            if (num == 1) {
                df = new java.text.DecimalFormat("#0.0");
            } else if (num == 2) {
                df = new java.text.DecimalFormat("#0.00");
            } else if (num == 3) {
                df = new java.text.DecimalFormat("#0.000");
            } else if (num == 4) {
                df = new java.text.DecimalFormat("#0.0000");
            } else if (num == 5) {
                df = new java.text.DecimalFormat("#0.00000");
            } else if (num == 6) {
                df = new java.text.DecimalFormat("#0.000000");
            } else {
                df = new java.text.DecimalFormat("#0.0");
            }
        } else {
            df = new java.text.DecimalFormat("#0.0");
        }
        try {
            String d = data == null ? "0" : df.format(data) + "";
            return Double.valueOf(d);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0d;
    }

    /**
     * 经纬度装换
     * @param number
     * @return
     */
    public static double getLatitudeOrLongitude(int number) {
        return number * 0.000001;
    }

    /**
     * 剩余油量转换
     * @param oilwear 位置云返回的油量百分比
     *        oilCapacity 邮箱容量
     * @return
     */
    public static double getOilwear(double oilwear,double oilCapacity) {
        return (oilwear * oilCapacity)/10000;
    }
    /**
     * 整车里程and油量转换
     * @param statusValue 待处理值
     * @return
     */
    public static double getStatusValue(long statusValue) {
        return statusValue*0.01;
    }

    public static double formatNumber(double src,int validBit) {
        double result = 0;
        try {
            BigDecimal b = new BigDecimal(src);
            result = b.setScale(validBit,BigDecimal.ROUND_HALF_UP).doubleValue();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
