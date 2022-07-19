package com.jac.app.job.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author liuBing
 * @Classname DateUtils
 * @Description TODO 时间日期工具类
 * @Date 2021/6/7 19:10
 */
public class DateUtils {


    private DateUtils(){}

    /**
     * 格式(yyyyMMddHHmmss)
     */
    public static final String DF_YMDHMS = "yyyyMMddHHmmss";
    /**
     * 格式(yyyyMMdd)
     */
    public static final String DF_YMD = "yyyyMMdd";

    /**
     * 将yyyyMMDDhhmmss时间字符串转换Date
     * @param datetime 指定时间 格式为yyyyMMDDhhmmss
     * @return
     */
    public static Date formatString(String datetime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DateUtils.DF_YMDHMS);
        LocalDateTime ldt = LocalDateTime.parse(datetime,dtf);
        Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }

    /**
     * 取得系统时间
     * @param format 格式(使用"DF_YMD_HMS"等类型)
     * @param date 指定时间，null为当前时间
     * @return
     */
    public static String getSystemTime(String format, Date date) {
        if (StringUtils.isEmpty(format)) {
            return null;
        }
        Date tempDate = date;
        if (tempDate == null)
            tempDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String systemDate = simpleDateFormat.format(tempDate);
        return systemDate;
    }

    /**
     * 取得系统时间
     * @param format 格式(使用"DF_YMD_HMS"等类型)
     * @return
     */
    public static String getSystemTime(String format) {
        if (StringUtils.isEmpty(format)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String systemDate = simpleDateFormat.format(new Date());
        return systemDate;
    }

    /**
     * 时间是否相等
     * @param dt1
     * @param dt2
     * @return
     */
    public static Boolean sameDate(Date dt1 , Date dt2 )
    {
        LocalDate ld1 = new LocalDate(new DateTime(dt1));
        LocalDate ld2 = new LocalDate(new DateTime(dt2));
        return ld1.equals( ld2 );
    }

    /**
     * 当前时间是否小于首保时间
     * @param dt1
     * @param dt2
     * @return
     */
    public static Boolean lessDate(Date dt1 , Date dt2 )
    {
        LocalDate ld1 = new LocalDate(new DateTime(dt1));
        LocalDate ld2 = new LocalDate(new DateTime(dt2));
        return ld1.isBefore(ld2);
    }

    /**
     * 在当前时间基础上添加n天
     * @param firstMonth
     * @return
     */
    public static Date plusMonths(Date createTime,int firstMonth) {
        LocalDate ld1 = new LocalDate(new DateTime(createTime));
        Date date = ld1.plusMonths(firstMonth).toDate();
        return date;
    }
}
