package com.nut.locationservice.common.util;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author MengJinyue
 * @create 2021/2/4
 * @Describtion 日期工具类
 */
public class DateUtils {
	
	/**
	 * 格式(yyyy-MM-dd HH:mm:ss)
	 */
	public static final String DF_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 格式(yyyy-MM-dd HH:mm:ss)
	 */
	public static final String DF_YMD_HMSSSS = "yyyy-MM-dd HH:mm:ss.SSS";
	
	/**
	 * 格式(yyyyMMddHHmmss)
	 */
	public static final String DF_YMDHMS = "yyyyMMddHHmmss";
	
	/**
	 * 格式(yyyy-MM-dd)
	 */
	public static final String DF_Y_M_D = "yyyy-MM-dd";
	
	/**
	 * 格式(yyyy/MM/dd)
	 */
	public static final String DF_YMDLine = "yyyy/MM/dd";
	
	/**
	 * 格式(yyyyMMdd)
	 */
	public static final String DF_YMD = "yyyyMMdd";
	
	/**
	 * 格式(yyyy/MM/dd HH:mm)
	 */
	public static final String DF_YMD_HM = "yyyy/MM/dd HH:mm";
	
	/**
	 * 格式(yyyy-MM-dd HH:mm)
	 */
	public static final String DF_YMD_HMLine = "yyyy-MM-dd HH:mm";
	
	
	/**
	 * 格式(yyyy/MM/dd HH:mm:ss)
	 */
	public static final String DF_YMD_HM_S = "yyyy/MM/dd HH:mm:ss";
	
	/**
	 * 格式(yyyy.MM.dd HH:mm)
	 */
	public static final String DF_YMD_HMPoint = "yyyy.MM.dd HH:mm";
	
	/**
	 * 格式(MM-dd HH:mm:ss)
	 */
	public static final String DF_MD_HMS = "MM-dd HH:mm:ss";
	
	/**
	 * 格式(MM.dd)
	 */
	public static final String DF_MD = "MM.dd";
	
	/**
	 * 格式(HH:mm)
	 */
	public static final String DF_HM = "HH:mm";

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
	 * 根据当前日期往前/往后推移{day}天的日期;
	 * @param day 天数;正整数 - 向前推移{day}天的日期; 负整数 - 向后推移{day}天的日期
	 * 例：当天日期为：2021-08-03 -> getFormatDate(0)
	 * 	     明天日期为：2021-08-04 -> getFormatDate(1)
	 *	     昨天日期为：2021-08-02 -> getFormatDate(-1)
	 * @return 返回日期格式：yyyy-MM-dd
	 */
	public static Date getFormatDate(int day) {
		Calendar date = Calendar.getInstance();
		date.setTime(new Date());
		date.set(Calendar.DATE, date.get(Calendar.DATE) + day);
		return date.getTime();
	}
	
	/**
	 * 在指定日期上加减天数
	 */
	public static Date getFormatDate(Date date, int day) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, day);
			return cal.getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取两个日期之间的所有日期集合
	 */
	public static List<String> getDays(String startDate, String endDate) {
		try {
			if (startDate.compareTo(endDate) > 0) {
				return null;
			}
			List<String> days = new ArrayList<>();
			SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
			Date endDay = dft.parse(endDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dft.parse(startDate));
			days.add(dft.format(cal.getTime()));
			
			while (cal.getTime().compareTo(endDay) < 0) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
				days.add(dft.format(cal.getTime()));
			}
			
			return days;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 判断是否超过24小时
	 * @param date1
	 * @param date2
	 * @return
	 * @throws Exception
	 */
	public static boolean judgmentDate(String date1, String date2) throws Exception { 
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DF_YMD_HMS);
		Date start = sdf.parse(date1); 
		Date end = sdf.parse(date2); 
		long cha = end.getTime() - start.getTime(); 

		if(cha<0){
			return false; 
		}

		double result = cha * 1.0 / (1000 * 60 * 60);

		if(result<=24){ 
			return true; 
		}else{ 
			return false; 
		} 
	}
	
	/**
	* 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
	*
	* @param nowTime 当前时间
	* @param startTime 开始时间
	* @param endTime 结束时间
	* @return
	 * @throws ParseException 
	*/
	public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime){
		
		if (nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime()) {
			return true;
		}
	
		Calendar date = Calendar.getInstance();
		date.setTime(nowTime);
	
		Calendar begin = Calendar.getInstance();
		begin.setTime(startTime);
	
		Calendar end = Calendar.getInstance();
		end.setTime(endTime);
	
		if (date.after(begin) && date.before(end)) {
			return true;
		} else {
			return false;
		}
	}
}