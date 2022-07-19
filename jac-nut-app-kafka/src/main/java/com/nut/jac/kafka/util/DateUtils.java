package com.nut.jac.kafka.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author liuBing
 * @Classname DateUtils
 * @Description TODO 时间日期工具类
 * @Date 2021/6/7 19:10
 */
public class DateUtils {

    private DateUtils(){}
    public static final String time_pattern = "yyyy-MM-dd HH:mm:ss";

    /**
     * 取得当前日期
     *
     * @return Date:当前日期
     */
    public static Date getNowDate(String timeType) {
        Date now = null;
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat(timeType);
            now = dateFormat.parse(dateFormat.format(new Date()));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return now;
    }
}
