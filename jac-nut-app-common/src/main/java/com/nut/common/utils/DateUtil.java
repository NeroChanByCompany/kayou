package com.nut.common.utils;

import org.joda.time.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @Description: 日期工具类
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.utils
 * @Author: yzl
 * @CreateTime: 2021-06-15 13:13
 * @Version: 1.0
 */
@Slf4j
public class DateUtil {
    public static final String date_pattern = "yyyy-MM-dd";

    public static final String time_pattern = "yyyy-MM-dd HH:mm:ss";

    public static final String miroSeconds_pattern = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String getDate_pattern_dot = "yyyy.MM.dd";

    public static final String time_pattern_dot = "yyyy.MM.dd HH:mm:ss";

    public static final String time_pattern_min = "yyyy.MM.dd HH:mm";

    public static final String date_s_pattern = "yyyyMMdd";

    public static final String date_st_pattern = "yyyy/MM/dd HH:mm:ss";

    public static final String date_st_pattern_yyyyMMddHHmmss = "yyyyMMddHHmmss";

    public static final String date_st_miroSeconds_pattern = "yyyyMMddHHmmssSSS";

    public static final String date_st_pattern_yyyyMMddHHmm = "yyyyMMddHHmm";

    public static final String date_st_pattern_yyyyMM = "yyyyMM";

    public static final int DIFFNOWDATE_EQUAL = 3;

    public static final int CONVERT_TO_SECOND = 3600;

    public static final String TIME_PATTERN_MINUTE = "yyyy-MM-dd HH:mm";

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(date_pattern).format(date);
    }

    public static String formatSimDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(date_s_pattern).format(date);
    }

    public static String formatDateDot(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(getDate_pattern_dot).format(date);
    }

    public static String format(String pattern, Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String formatDate(String pattern, Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String formatTime(Date date) {
        if (date == null) {
            return "";
        }
        return getDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static Date parseDate(String datestr) {
        try {
            return new SimpleDateFormat(date_pattern).parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Date parseDate(String datestr, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Date parseDateDot(String datestr) {
        try {
            return new SimpleDateFormat(getDate_pattern_dot).parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Date parseTime(String time) {
        Date result = null;
        if (time == null || "".equals(time)) {
            return null;
        }

        try {
            result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Date parseTimeDot(String time) {
        Date result = null;
        if (time == null || "".equals(time)) {
            return null;
        }

        try {
            result = new SimpleDateFormat(time_pattern_dot).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Date add(Date date, int mount, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, mount);
        return cal.getTime();
    }

    /**
     * date 2 - date1
     *
     * @param type {@link Calendar}
     */
    public static int diff(Date date1, Date date2, int type) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        int d1 = cal.get(type);
        cal.setTime(date2);
        return cal.get(type) - d1;
    }

    /**
     * date 2 - date1
     *
     * @deprecated 跨年计算天数时返回值不正确，请不要使用该方法
     */
    @Deprecated
    public static int diffByDay(String date1, String date2) {
        int type = Calendar.DAY_OF_YEAR;
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.parseDateDot(date1));
        int d1 = cal.get(type);
        int year1 = cal.get(Calendar.YEAR);
        cal.setTime(DateUtil.parseDateDot(date2));
        int year2 = cal.get(Calendar.YEAR);
        if (year1 == year2) {
            return cal.get(type) - d1;
        } else {
            return (year2 - year1) * 365 + cal.get(type) - d1 + 2;
        }
    }

    /**
     * 计算date2与date1相隔天数
     * <pre>
     *    date2         date1
     * 2020-08-05 与 2020-08-04 相隔    1 天
     * 2020-08-05 与 2020-08-05 相隔    0 天
     * 2020-08-05 与 2020-08-05 相隔  731 天
     * 2020-08-05 与 2020-08-06 相隔   -1 天
     * 2020-08-05 与 2020-08-05 相隔 -730 天
     * </pre>
     *
     * @param date1     较小日期
     * @param date2     较大日期
     * @param connector 日期格式的连接符："-", ".", "", "/"等
     * @return 相隔天数，date2小于date1时返回负数。<b>注意：格式解析失败将返回0</b>
     */
    public static long diffByDay(String date1, String date2, String connector) {
        StringBuilder sb = new StringBuilder(date_s_pattern);
        sb.insert(4, connector);
        sb.insert(7, connector);
        String datePattern = sb.toString();
        Date d1 = parseDate(date1, datePattern);
        Date d2 = parseDate(date2, datePattern);
        long daysBetween = 0;
        if (d1 != null && d2 != null) {
            daysBetween = (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24);
        }
        return daysBetween;
    }

    /**
     * date自然周中的那一天
     *
     * @param date 时间YYYY.MM.DD
     */
    public static int dayInWeek(String date) {
        int type = Calendar.DAY_OF_WEEK;
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.parseDateDot(date));
        int d1 = cal.get(type);
        return d1;
    }

    /**
     * date自然周中的那一天
     */
    public static String dayInWeek(Date date) {
        int type = Calendar.DAY_OF_WEEK;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int d = cal.get(type);
        String dayOfWeek = "";
        switch (d) {
            case 1:
                dayOfWeek = "周日";
                break;
            case 2:
                dayOfWeek = "周一";
                break;
            case 3:
                dayOfWeek = "周二";
                break;
            case 4:
                dayOfWeek = "周三";
                break;
            case 5:
                dayOfWeek = "周四";
                break;
            case 6:
                dayOfWeek = "周五";
                break;
            case 7:
                dayOfWeek = "周六";
                break;
            default:
                dayOfWeek = "";
        }
        return dayOfWeek;
    }

    /**
     * date自然月中的那一天
     *
     * @param date 时间YYYY.MM.DD
     */
    public static int dayInMonth(String date) {
        int type = Calendar.DAY_OF_MONTH;
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.parseDateDot(date));
        int d1 = cal.get(type);
        return d1;
    }

    /**
     * 一个月最大的一天
     *
     * @param date 时间YYYY.MM.DD
     * @return int 天数
     */
    public static int maxDayMonth(String date) {
        int type = Calendar.DATE;
        Calendar cal = Calendar.getInstance();
        int year = Integer.parseInt(date.substring(0, 4));
        cal.set(Calendar.YEAR, year);
        int month = Integer.parseInt(date.substring(5, 7).replace("0", "")) - 1;
        cal.set(Calendar.MONTH, month);
        int d1 = cal.getActualMaximum(type);
        return d1;
    }

    /**
     * 一年中有多少个自然周
     *
     * @param date 时间YYYY.12.31
     *             <p/>
     *             处于一年中第几个自然周
     * @param date 时间YYYY.MM.DD
     */
    public static int yearMoreWeek(String date) {
        Date time = DateUtil.addDay(date, 1);
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setMinimalDaysInFirstWeek(7);
        cal.setTime(time);
        int d1 = cal.get(Calendar.WEEK_OF_YEAR);
        return d1;
    }

    /**
     * 根据传递过来的日期，转换为对应的 年、所在周的格式
     *
     * @return 返回 202021
     */
    public static int yearofWeekStr(String startWeekDay) {
        Date time = DateUtil.addDay(startWeekDay, 1);
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setMinimalDaysInFirstWeek(7);
        cal.setTime(time);
        String weekNumber = cal.get(Calendar.WEEK_OF_YEAR) + "";
        String week = startWeekDay.substring(0, 4) + weekNumber;
        if (weekNumber.equals("52") && startWeekDay.substring(5, 7).equals("01")) {
            week = String.valueOf(Integer.parseInt(startWeekDay.substring(0, 4)) - 1) + weekNumber;
        }
        if (weekNumber.length() == 1) {
            week = startWeekDay.substring(0, 4) + "0" + weekNumber;
        }

        return Integer.parseInt(week);
    }

    public static String timeStr(long time) {
        Date date = new Date(time);
        return getDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String timeStr2(long time) {
        Date date = new Date(time);
        return getDateFormat(date_pattern).format(date);
    }

    public static SimpleDateFormat getDateFormat(String pattern) {
        SimpleDateFormat sim = new SimpleDateFormat(pattern);
        sim.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        // sim.setTimeZone(TimeZone.getTimeZone("Asia/ShangHai"));
        return sim;
    }

    /**
     * 字符串转LONG
     */
    public static Long strTimeChangeLong(String time) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calBegin = new GregorianCalendar();
        try {
            calBegin.setTime(format.parse(time));
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        long beginTime = calBegin.getTimeInMillis();
        return beginTime;
    }

    /**
     * 取得当前日期
     *
     * @return Date:当前日期
     */
    public static Date getNowDate() {
        Date now = null;
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat(getDate_pattern_dot);
            now = dateFormat.parse(dateFormat.format(new Date()));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return now;
    }

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

    public static Date getDate(Date date, String pattern) {
        Date result = null;
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat(pattern);
            result = dateFormat.parse(dateFormat.format(date));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return result;
    }

    /**
     * 增加日期中某类型的某数值。如增加日期
     *
     * @param date     日期
     * @param dateType 类型
     * @param amount   数值
     * @return 计算后日期
     */
    public static Date addInteger(Date date, int dateType, int amount) {
        Date myDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(dateType, amount);
            myDate = calendar.getTime();
        }
        return myDate;
    }

    /**
     * long转String
     */
    public static Date transferLongToDate(Long millSec) {
        Date date = null;
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat(getDate_pattern_dot);
            date = dateFormat.parse(dateFormat.format(new Date(millSec)));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return date;
    }

    /**
     * long转String
     */
    public static Date transferLongToDate(Long millSec, String pattern) {
        Date date = null;
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat(pattern);
            date = dateFormat.parse(dateFormat.format(new Date(millSec)));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return date;
    }

    /**
     * 增加日期的月份。失败返回null。
     *
     * @param date        日期
     * @param monthAmount 增加数量。可为负数
     * @return 增加月份后的日期字符串
     */
    public static Date addMonth(Date date, int monthAmount) {
        return addInteger(date, Calendar.MONTH, monthAmount);
    }

    /**
     * 增加日期的月份。失败返回null。
     *
     * @param date      日期
     * @param dayAmount 增加数量。可为负数
     * @return 增加月份后的日期字符串
     */
    public static Date addDay(String date, int dayAmount) {
        return addInteger(DateUtil.parseDateDot(date), Calendar.DAY_OF_YEAR, dayAmount);
    }

    /**
     * 增加日期的月份。失败返回null。
     *
     * @param date      日期
     * @param dayAmount 增加数量。可为负数
     * @return 增加月份后的日期字符串
     */
    public static Date addDay(Date date, int dayAmount) {
        return addInteger(date, Calendar.DAY_OF_YEAR, dayAmount);
    }

    /**
     * 当前周的第一天
     */
    public static String firstDayOfCurrentWeek(String date) {
        return formatDateDot(addDay(date, 1 - dayInWeek(date)));
    }

    /**
     * 当前周的最后一天
     */
    public static String lastDayOfCurrentWeek(String date) {
        return formatDateDot(addDay(date, 7 - dayInWeek(date)));
    }

    /**
     * 下一周的第一天
     */
    public static String firstDayOfNextWeek(String date) {
        return formatDateDot(addDay(date, 8 - dayInWeek(date)));
    }

    /**
     * 下一周的最后一天
     */
    public static String lastDayOfNextWeek(String date) {
        return formatDateDot(addDay(date, 14 - dayInWeek(date)));
    }

    /**
     * 当前日期到当前周最后日期的日期范围(不完整周,向后)
     */
    public static String currentWeekRangeTail(String date) {
        date = date.substring(0, 10);
        return date + " - " + lastDayOfCurrentWeek(date);
    }

    /**
     * 当前日期到当前周最后日期的日期范围(不完整周,向前)
     */
    public static String currentWeekRangeHead(String date) {
        date = date.substring(0, 10);
        return firstDayOfCurrentWeek(date) + " - " + date;
    }

    /**
     * 当前周的日期范围
     */
    public static String currentWeekRange(String date) {
        date = date.substring(0, 10);
        return firstDayOfCurrentWeek(date) + " - " + lastDayOfCurrentWeek(date);
    }

    /**
     * 下一周的日期范围
     */
    public static String nextWeekRange(String date) {
        date = date.substring(0, 10);
        return firstDayOfNextWeek(date) + " - " + lastDayOfNextWeek(date);
    }

    /**
     * 增加i周后的日期范围
     */
    public static String addWeekRange(String date, int n) {
        date = date.substring(0, 10);
        return firstDayOfNextNWeek(date, n) + " - " + lastDayOfNextNWeek(date, n);
    }

    /**
     * 下n周的第一天
     */
    public static String firstDayOfNextNWeek(String date, int n) {
        return formatDateDot(addDay(date, 7 * n + 1 - dayInWeek(date)));
    }

    /**
     * 下n周的最后一天
     */
    public static String lastDayOfNextNWeek(String date, int n) {
        return formatDateDot(addDay(date, 7 * (n + 1) - dayInWeek(date)));
    }

    /**
     * 把week封装成日期范围
     *
     * @param date 格式：202120（2021年第20周）
     */
    public static String parseToWeekRange(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int week = Integer.parseInt(date.substring(4, 6));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
        Date weekStart = calendar.getTime();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        Date weekEnd = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(weekStart) + " - " + sdf.format(weekEnd);
    }

    /**
     * date1 与 date2 中包含独立的月数
     */
    public static int containAloneMonths(String date1, String date2) {
        date1 = date1.substring(0, date1.lastIndexOf("."));
        date2 = date2.substring(0, date2.lastIndexOf("."));
        DateTime dt1 = DateTime.parse(date1, DateTimeFormat.forPattern("yyyy.MM"));
        DateTime dt2 = DateTime.parse(date2, DateTimeFormat.forPattern("yyyy.MM"));
        return Months.monthsBetween(dt1, dt2).getMonths() + 1;
    }

    /**
     * 当前周的第一天
     */
    public static String firstDayOfCurrentMonth(String date) {
        return date.substring(0, 8) + "01";
    }

    /**
     * 当前周的最后一天
     */
    public static String lastDayOfCurrentMonth(String date) {
        String day = String.valueOf(maxDayMonth(date));
        if (day.length() == 2) {
            return date.substring(0, 8) + day;
        } else {
            return date.substring(0, 8) + "0" + day;
        }
    }

    /**
     * 下一周的第一天
     */
    public static String firstDayOfNextMonth(String date) {
        return firstDayOfCurrentMonth(formatDateDot(addMonth(parseDateDot(date), 1)));
    }

    /**
     * 下一周的最后一天
     */
    public static String lastDayOfNextMonth(String date) {
        return lastDayOfCurrentMonth(formatDateDot(addMonth(parseDateDot(date), 1)));
    }

    /**
     * 下n周的第一天
     */
    public static String firstDayOfNextNMonth(String date, int n) {
        return firstDayOfCurrentMonth(formatDateDot(addMonth(parseDateDot(date), n)));
    }

    /**
     * 下n周的最后一天
     */
    public static String lastDayOfNextNMonth(String date, int n) {
        return lastDayOfCurrentMonth(formatDateDot(addMonth(parseDateDot(date), n)));
    }

    /**
     * 当前日期到当前月最后日期的日期范围(不完整月,向后)
     */
    public static String currentMonthRangeTail(String date) {
        return date + " - " + lastDayOfCurrentMonth(date);
    }

    /**
     * 当前日期到当前月最后日期的日期范围(不完整月,向前)
     */
    public static String currentMonthRangeHead(String date) {
        return firstDayOfCurrentMonth(date) + " - " + date;
    }

    /**
     * 当前月范围
     */
    public static String currentMonthRange(String date) {
        date = date.substring(0, 7) + ".01";
        return firstDayOfCurrentMonth(date) + " - " + lastDayOfCurrentMonth(date);
    }

    /**
     * 增加i月后的日期范围
     */
    public static String addMonthRange(String date, int n) {
        return firstDayOfNextNMonth(date, n) + " - " + lastDayOfNextNMonth(date, n);
    }

    /**
     * 下个月的第一天
     *
     * @param date YYYY.MM.DD
     * @return YYYY.MM.DD
     */
    public static String firstMonthDay(String date) {
        // 自然月区域
        int inDay = DateUtil.dayInMonth(date);
        String endDay =
                DateUtil.format(DateUtil.getDate_pattern_dot,
                        DateUtil.addDay(date, DateUtil.maxDayMonth(date) - inDay));
        return DateUtil.format(DateUtil.getDate_pattern_dot, DateUtil.addDay(endDay, 1));
    }

    /**
     * 下周的第一天
     *
     * @param date YYYY.MM.DD
     * @return YYYY.MM.DD
     */
    public static String firstWeekDay(String date) {
        // 自然周区域
        int weekInDay = DateUtil.dayInWeek(date);
        String endDay = DateUtil.format(DateUtil.getDate_pattern_dot, DateUtil.addDay(date, 7 - weekInDay));
        return DateUtil.format(DateUtil.getDate_pattern_dot, DateUtil.addDay(endDay, 1));
    }

    /**
     * 判断日期字符串是否为日期
     */
    public static boolean isDateCheck(String dateStr, String formatStr) {
        if (StringUtil.isEmpty(dateStr)) {
            return true;
        }
        try {
            String formatedDate = DateUtil.format(formatStr, DateUtil.parseDate(dateStr, formatStr));
            if (formatedDate.equals(dateStr)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 时间段格式设置
     *
     * @param time 秒
     * @return twentyFourHour 12小时制
     */
    public static String twentyFourHour(Integer time) {
        String hour = String.valueOf(time / 3600);
        hour = hour.length() == 2 ? hour : "0" + hour;
        String minute = String.valueOf((time % 3600) / 60);
        minute = minute.length() == 2 ? minute : "0" + minute;
        String second = String.valueOf((time % 3600) % 60);
        second = second.length() == 2 ? second : "0" + second;
        String twentyFourHour = hour + ":" + minute + ":" + second;
        return twentyFourHour;
    }

    /**
     * 时间段格式设置
     *
     * @param time 秒
     * @return ta 00:00~00:59
     */
    public static String convStringToHour(Integer time) {
        String ta = "";
        if (time < 10) {
            ta = "0" + String.valueOf(time) + ":00~0" + String.valueOf(time) + ":59";
        } else {
            ta = String.valueOf(time) + ":00~" + String.valueOf(time) + ":59";
        }
        return ta;
    }

    /**
     * 获取今天第1秒时间戳
     *
     * @return 时间戳
     */
    public static Long firstSecondToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today.getTimeInMillis();
    }

    /**
     * 日期格式转换
     *
     * @param datestr YYYYmmdd
     * @param type    ./-
     * @return Date
     */
    public static Date parseDateDot(String datestr, String type) {
        try {
            StringBuffer str = new StringBuffer(datestr);
            str.insert(4, type);
            str.insert(7, type);
            if (type.equals(".")) {
                return new SimpleDateFormat(getDate_pattern_dot).parse(str.toString());
            }
            if (type.equals("-")) {
                return new SimpleDateFormat(date_pattern).parse(str.toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 与当前日期比较
     *
     * @param dateStr 比较时间
     * @return 1：大于当前日期，2：小于当前日期，3：等于当前日期
     */
    public static int diffNowDate(String dateStr) {
        try {
            Date dEndDate = new Date();
            if (!dateStr.contains(".") && !dateStr.contains("-")) {
                StringBuilder str = new StringBuilder(dateStr);
                str.insert(4, ".");
                str.insert(7, ".");
                dEndDate = DateUtil.parseDateDot(str.toString());
            } else if (dateStr.contains(".")) {
                dEndDate = DateUtil.parseDateDot(dateStr);
            } else if (dateStr.contains("-")) {
                dEndDate = DateUtil.parseDate(dateStr);
            }

            Date nowDate = DateUtil.getNowDate();
            if (dEndDate != null && nowDate != null) {
                if (dEndDate.getTime() > nowDate.getTime()) {
                    // 日期大于当前日期
                    return 1;
                } else if (dEndDate.getTime() < nowDate.getTime()) {
                    // 日期小于当前日期
                    return 2;
                } else { // dEndDate.getTime() == nowDate.getTime()
                    // 日期等于当前日期
                    return 3;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 与当前日期比较
     *
     * @param datestr 比较时间
     * @return 1：大于当前日期，2：小于当前日期，3：等于当前日期
     */
    public static String dateStr(String datestr, String type) {
        StringBuffer str = new StringBuffer(datestr);
        if (datestr.indexOf(".") == -1 && datestr.indexOf("-") == -1) {
            str.insert(4, type);
            str.insert(7, type);
        }
        return str.toString();
    }

    /**
     * 与当前日期比较
     *
     * @param datestr 比较时间
     * @return 1：大于当前日期，2：小于当前日期，3：等于当前日期
     */
    public static Date dateD(String datestr, String type) {
        StringBuffer str = new StringBuffer(datestr);
        Date date = new Date();
        if (datestr.indexOf(".") == -1 && datestr.indexOf("-") == -1) {
            str.insert(4, type);
            str.insert(7, type);
            if (datestr.indexOf(".") != -1) {
                date = DateUtil.parseDateDot(datestr);
            } else if (datestr.indexOf("-") != -1) {
                date = DateUtil.parseDate(datestr);
            }
        }
        return date;
    }

    /**
     * 判断今天、昨天、前天
     *
     * @param date 格式要求2021-01-01 08:08:08
     * @return 今天、昨天、前台 否则返回日期
     */
    public static String parseDateToday(Date date) {
        String ret = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long create = date.getTime();
        Calendar now = Calendar.getInstance();
        long ms =
                1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now
                        .get(Calendar.SECOND));// 毫秒数
        long ms_now = now.getTimeInMillis();
        if (ms_now - create < ms) {
            ret = "今天";
        } else if (ms_now - create < (ms + 24 * 3600 * 1000)) {
            ret = "昨天";
        } else if (ms_now - create < (ms + 24 * 3600 * 1000 * 2)) {
            ret = "前天";
        } else {
            ret = sdf.format(date);
        }
        return ret;

    }

    /**
     * 判断输入日期是：今天、昨天，若比昨天还早且在本周内，则返回周几，若非本周内，则返回年月日
     *
     * @param dateStr      待判断的日期字符串
     * @param inputFormat  带判断日期字符串的格式
     * @param outPutFormat 要输出的日期字符串的格式
     * @param subFormat    显示为今天、昨天或周几时 带的时分秒格式，若为空，则不带时分秒
     * @return System.out.println(DateUtil.parseQqDate ( " 2020 - 12 - 21 22 : 10 : 10 ", " yyyy - MM - dd HH : mm :
     *ss ", " yyyy / MM / dd HH : mm "
     *, " HH : mm "));
     */
    public static String parseQqDate(String dateStr, String inputFormat, String outPutFormat, String subFormat) {
        String time00 = "00:00:00";
        if (dateStr.indexOf(time00) != -1) {
            // 解决 当天0点被计算成昨天的bug
            dateStr = dateStr.replaceAll("00:00:00", "00:00:01");
        }
        String ret = "";
        SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
        Date date = DateUtil.parseDate(dateStr, inputFormat);
        long create = date.getTime();
        Calendar now = Calendar.getInstance();
        long ms =
                1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now
                        .get(Calendar.SECOND));// 毫秒数

        long ms_now = now.getTimeInMillis();

        String subStr = "";
        if (StringUtils.isNotEmpty(subFormat)) {
            subStr = " " + DateUtil.format(subFormat, date);
        }
        System.out.println(ms_now - create);
        if (ms_now - create < ms) {
            ret = "今天" + subStr;
        } else if (ms_now - create < (ms + 24 * 3600 * 1000)) {
            ret = "昨天" + subStr;
        } else {
            if (DateUtil.isThisWeek(date)) {
                ret = DateUtil.dayInWeek(date) + subStr;
            } else {
                sdf = new SimpleDateFormat(outPutFormat);
                ret = sdf.format(date);
            }
        }
        return ret;

    }

    /**
     * 判断选择的日期是否是本周
     */
    public static boolean isThisWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(date);
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    /**
     * 与当前时间相差多少秒
     *
     * @param date yyyy-MM-dd HH:mm:ss
     * @return 秒数
     */
    public static Long diffNowDateTime(String date) {
        Date b = parseDate(date, time_pattern);
        return (System.currentTimeMillis() - b.getTime()) / 1000;
    }

    /**
     * 取得当前日期(yyyyMMddHHmmss)
     *
     * @return String:当前日期
     */
    public static String getNowDate_yyyyMMddHHmmss() {
        SimpleDateFormat dateFormat = null;
        dateFormat = new SimpleDateFormat(date_st_pattern_yyyyMMddHHmmss);
        String now = dateFormat.format(new Date());
        return now;
    }


    /**
     * 取得指定格式当前日期
     *
     * @return String:当前日期
     */
    public static String getFormatNowDate(String pattern) {
        SimpleDateFormat dateFormat = null;
        dateFormat = new SimpleDateFormat(pattern);
        String now = dateFormat.format(new Date());
        return now;
    }

    /**
     * 取得当前日期(yyyyMMddHHmmss)
     *
     * @return String:当前日期
     */
    public static String getNowDate_yyyyMMdd() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(date_s_pattern);
        String now = dateFormat.format(new Date());
        return now;
    }

    /**
     * 取某天零点时间
     */
    public static Long getZeroTime(long time) {
        String dateStr = formatDate(new Date(time));
        return DateUtil.parseDate(dateStr + " 00:00:00", DateUtil.time_pattern).getTime();
    }

    /**
     * 取得当前日期(yyyyMMddHHmm)
     *
     * @return String:当前日期
     */
    public static String getNowDate_yyyyMMddHHmm() {
        SimpleDateFormat dateFormat = null;
        dateFormat = new SimpleDateFormat(date_st_pattern_yyyyMMddHHmm);
        String now = dateFormat.format(new Date());
        return now;
    }

    /**
     * 取得当前日期(yyyyMMddHHmm)
     *
     * @return String:当前日期
     */
    public static String getNowDate_date_st_partten() {
        SimpleDateFormat dateFormat = null;
        dateFormat = new SimpleDateFormat(date_st_pattern);
        String now = dateFormat.format(new Date());
        return now;
    }

    /**
     * 取得指定两日期之间的连续日期
     *
     * @param start 开始日期yyyyMMdd
     * @param end   结束日期yyyyMMdd
     * @return 日期数组[yyyyMMdd, ...]
     */
    public static String[] getDateArrayBetween(String start, String end) {
        try {
            Date sDate = parseDate(start, date_s_pattern);
            Date eDate = parseDate(end, date_s_pattern);
            if (sDate == null || eDate == null || sDate.compareTo(eDate) > 0) {
                return new String[0];
            }
            List<String> ret = new ArrayList<>();
            while (sDate.compareTo(eDate) <= 0) {
                ret.add(formatSimDate(sDate));
                sDate = addDay(sDate, 1);
            }
            return ret.toArray(new String[ret.size()]);
        } catch (Exception e) {
            return new String[0];
        }
    }

    /**
     * @param time yyyy-MM-dd HH:mm:ss
     * @return java.lang.String
     * @Description: 转换年月日
     * @method: getTimeStr
     */
    public static String getTimeStr(String time) {
        if (StringUtil.isEmpty(time)) {
            return "";
        }
        String[] dataList = time.split(" ");
        String[] day = dataList[0].replace("0", "").split("-");
        String[] hourList = dataList[1].split(":");
        String data = day[0] + "年" + day[1] + "月" + day[2] + "日";
        String hour = hourList[0].replace("0", "");
        if (StringUtil.isNotEmpty(hour)) {
            data += hour + "时";
        } else {
            data += "0时";
        }
        String minute = hourList[1].replace("00", "0");
        if (StringUtil.isNotEmpty(minute)) {
            data += minute + "分";
        }
        return data;
    }

    /**
     * 与当前时间差装换为小时
     */
    public static Long secondConversionToHour(String time) {
        Long diffTime = diffNowDateTime(time);
        if (diffTime != null) {
            diffTime = diffTime / CONVERT_TO_SECOND;
        } else {
            return 0L;
        }
        return diffTime;
    }

    /**
     * 时间格式转换为yyyy-MM-dd HH:mm
     */
    public static String getDateToMinute(String time) {

        String date = "";
        if (StringUtil.isEmpty(time)) {
            return date;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_PATTERN_MINUTE);
        Date sDate = parseDate(time, time_pattern);
        date = dateFormat.format(sDate);

        return date;
    }

    /**
     * 时间戳转时间
     */
    public static String timeStampToDate(Long timeStamp) {
        return new SimpleDateFormat(time_pattern).format(new Date(timeStamp));
    }

    public static String getFirstDay(String date) {
        String currentDate = DateUtil.getFormatNowDate("yyyy.MM.dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date result = new Date();
        try {
            if ("week".equals(date)) {
//                result = sdf1.parse(DateUtil.firstDayOfCurrentWeek(currentDate));
                Date dates = new Date();
                String time = sdf.format(dates);
                Date date1 = sdf.parse(time);
                int riqi = dayForWeek(time);
                if (riqi == 1) {
                    result = geLastWeekMonday(date1);
                } else {
                    result = getThisWeekMonday(date1);
                }
            } else if ("month".equals(date)) {
                result = sdf1
                        .parse(DateUtil.firstDayOfCurrentMonth(currentDate));

                Calendar c = Calendar.getInstance();
                int today = c.get(c.DAY_OF_MONTH);
                if (today == 1) {
                    //是
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.add(Calendar.MONTH, -1);
                    calendar1.set(Calendar.DAY_OF_MONTH, 1);
                    result = calendar1.getTime();
                } else {
                    result = sdf1
                            .parse(DateUtil.firstDayOfCurrentMonth(currentDate));
                }
            } else if ("yesterday".equals(date)) {
                Date d1 = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d1);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                result = calendar.getTime();
            }
        } catch (Exception e) {
            log.info("获取第一天（getFirstDay）失败：" + e.getStackTrace());
        }

        return sdf.format(result);
    }

    //根据传入的week判断是否是周的第一天，如果是周的第一天，取上周的周一
    public static Date geLastWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }

    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    public static int dayForWeek(String pTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(pTime));
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    /**
     * 获取前一个月第一天(yyyyMMdd)
     *
     * @return String:前一个月第一天
     */
    public static String getLast_month_firstDay() {
        SimpleDateFormat sdf = new SimpleDateFormat(date_pattern);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MONTH, -1);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        String firstDay = sdf.format(calendar1.getTime());
        return firstDay;
    }

    /**
     * 获取系统日期前N天后后N天的指定格式的日期
     *
     * @param num        相隔天数
     * @param dateFormat 日期格式
     */
    public static String getAddOrRedToNowDate(int num, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, num);
        date = calendar.getTime();
        String returnDate = sdf.format(date);

        return returnDate;
    }

    /**
     * 日期格式转化
     *
     * @param time 时间
     */
    public static String getDatePattern(Long time) {
        String date = "";
        if (time != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_PATTERN_MINUTE);
            date = dateFormat.format(time);
        }
        return date;
    }

    /**
     * 与当前时间差 秒
     *
     * @param time 时间 毫秒
     * @return 秒
     */
    public static Long diffNDate(Long time) {
        Date a = new Date();
        Long interval = (a.getTime() - time) / 1000;
        return interval;
    }

    /**
     * 判断结束日期不小于开始日期，并且间隔日期不能超过给定值。
     *
     * @param start    开始日期
     * @param end      结束日期
     * @param interval 间隔大小
     * @return boolean
     */
    public static boolean compareStartDifferEnd(Date start, Date end, int interval) {
        long startTime = start.getTime();
        long endTime = end.getTime();
        long oneDay = 60 * 60 * 24 * 1000;
        if (endTime >= startTime && (endTime - startTime) / oneDay < interval) {
            return true;
        }
        return false;
    }

    /**
     * 月份与当前月份比较
     *
     * @param yearMonthStr 年月，格式 yyyyMM
     * @return int 小于当前月：返回-1  等于：返回0  大于：返回1
     */
    public static int compareCurrentMonth(String yearMonthStr) {
        Date date = DateUtil.parseDate(yearMonthStr, date_st_pattern_yyyyMM);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        YearMonth yearMonth = YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        int difference = yearMonth.compareTo(YearMonth.now());
        if (difference > 0) {
            return 1;
        } else if (difference < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 获取月份最后一天
     *
     * @param yearMonthStr 年月，格式 yyyyMM
     * @return String 日期格式yyyyMMdd
     */
    public static String getLastDayOfMonth(String yearMonthStr) {
        Date date = DateUtil.parseDate(yearMonthStr, date_st_pattern_yyyyMM);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        SimpleDateFormat sdf = new SimpleDateFormat(date_s_pattern);
        return sdf.format(cal.getTime());
    }

    /**
     * 获取日期格式日期
     *
     * @param d 时间
     * @param f 格式
     * @return 结果
     */
    public static Date dateFormatConversion(String d, String f) {
        if (StringUtil.isEmpty(d) || StringUtil.isEmpty(f)) {
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(f);
        try {
            return formatter.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 计算{date}与当前日期的间隔天数
     *
     * @return 间隔天数
     */
    public static long until(Date dateStart, Date dateEnd) {
        return dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .until(dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), ChronoUnit.DAYS);
    }

    /**
     * n天前
     */
    public static Date plusDays(Date date, long days) {
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(days).atStartOfDay(
                ZoneId.systemDefault()).toInstant());
    }

    /**
     * n天后
     */
    public static Date minusDays(Date date, long days) {
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(days)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 返回正值是代表左侧日期大于右侧日期，反之亦然，日期格式必须一致
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static Integer timeContrast(String startTime, String endTime) {
        return startTime.compareTo(endTime);
    }

    /**
     * @Description TODO 年-月-日格式日期字符串比较大小
     */
    public static int dateStrCompare(String date1, String date2) {
        String[] date1s = date1.split("-");
        String[] date2s = date2.split("-");

        int year1 = Integer.valueOf(date1s[0]);
        int month1 = Integer.valueOf(date1s[1]);
        int day1 = Integer.valueOf(date1s[2]);

        int year2 = Integer.valueOf(date2s[0]);
        int month2 = Integer.valueOf(date2s[1]);
        int day2 = Integer.valueOf(date2s[2]);

        if (year1 < year2) {
            return 0;
        } else if (year1 > year2) {
            return 1;
        } else {
            if (month1 < month2) {
                return 0;
            } else if (month1 > month2) {
                return 1;
            } else {
                if (day1 < day2) {
                    return 0;
                } else if (day1 > day2) {
                    return 1;
                } else {
                    return 2;
                }
            }
        }
    }

    /**
     * long转String
     */
    public static String transferLongToStr(Long millSec) {
        String dateStr = null;
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat(time_pattern);
            String format = dateFormat.format(new Date(millSec));
            return format;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return dateStr;
    }

    /**
     * 根据日期格式比较时间大小
     *
     * @param date1
     * @param date2
     * @param format
     * @return 1 date1大于date2  2 date1=date2  3 date1小于date2
     */
    public static Integer dateStrCompareWithFormat(String date1, String date2, String format) {

        if (StringUtil.isEmpty(date1) || StringUtil.isEmpty(date2) || StringUtil.isEmpty(format)) {
            return null;
        }

        Integer result = null;
        try {
            BigDecimal date1Big = new BigDecimal("0");
            BigDecimal date2Big = new BigDecimal("0");
            if (StringUtil.isNotEmpty(format)) {
                date1Big = new BigDecimal(DateUtil.parseDate(date1, format).getTime());
                date2Big = new BigDecimal(DateUtil.parseDate(date2, format).getTime());
            } else {
                date1Big = new BigDecimal(date1);
                date2Big = new BigDecimal(date2);
            }
            result = date1Big.compareTo(date2Big);
            if (result == 1) {
                result = 1;
            } else if (result == 0) {
                result = 2;
            } else if (result == -1) {
                result = 3;
            }
        } catch (Exception e) {
            log.info("=======比较日期失败=======" + e.getMessage());
        }
        return result;
    }


}
