package com.nh.dataspider.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;


public class DateUtil {
	
	public static final String toString(Date date, String pattern) {
        String result = null;
        if (date != null) {
            result = DateFormatUtils.format(date, pattern);
        }
        return result;		
    }

    public static String getCurrentYear() {
        return DateUtil.toString(new Date(), "yyyy");
    }

    public static String getCurrentMonthDay() {
        return DateUtil.toString(new Date(), "MMdd");
    }
    
    /**
     * date类型进行格式化输出（返回类型：String）
     *
     * @param date
     * @return
     */
    public static String dateFormat(Date date) {
        return DateUtil.toString(date, "yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * date类型进行格式化输出（返回类型：String）
     *
     * @param date
     * @return
     */
    public static String daFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }
    
    /**
     * 将"2015-08-31 21:08:06"型字符串转化为Date
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String str) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = formatter.parse(str);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return date;
        }
        return date;
    }
    
    /**
     * 将"2015-08-31"型字符串转化为Date
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date stringToDates(String str) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            date = formatter.parse(str);
        } catch (ParseException e) {
            return date;
        }
        return date;
    }
    
    public static Date getDateByRFC3339(String dateStr){
    	DateTime dateTime = new DateTime(dateStr);
    	long timeInMillis = dateTime.toCalendar(Locale.getDefault()).getTimeInMillis();
        Date date = new Date(timeInMillis);
    	return date;
    }
    
    /**
     * 获取两个日期之间的 工作日（排出周六、周日） 的日期
     * @param startDate 开始时间（时间格式：yyyy-MM-dd）
     * @param endDate 结束时间（时间格式：yyyy-MM-dd）
     * @return
     */
    public static List<String> getWorkDays(String startDate, String endDate) {
    	List<String> workDays = Lists.newArrayList();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	List<String> allDays = Lists.newArrayList();
    	int dateTime = 24 * 60 * 60 * 1000;
    	try {
	    	long start = format.parse(startDate).getTime();
	    	long end = format.parse(endDate).getTime();
	    	
	    	while(end>=start) {
	    		allDays.add(format.format(start));
	    		start += dateTime;
	    	}
	    	
	    	Calendar calendar = Calendar.getInstance();
	    	for(String l : allDays) {
	    		calendar.setTime(format.parse(l));
	    		int week = calendar.get(Calendar.DAY_OF_WEEK);
	    		if(week !=1 && week != 7) {
	    			workDays.add(l);
	    		}
	    	}
    	} catch (Exception e) {
            return Lists.newArrayList();
        }
    	return workDays;
    }
    
    /**
     * 获取两个日期之间的 非工作日（周六、周日） 的日期
     * @param startDate 开始时间（时间格式：yyyy-MM-dd）
     * @param endDate 结束时间（时间格式：yyyy-MM-dd）
     * @return
     */
    public static List<String> getWeekDays(String startDate, String endDate) {
    	List<String> weekDays = Lists.newArrayList();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	List<String> allDays = Lists.newArrayList();
    	int dateTime = 24 * 60 * 60 * 1000;
    	try {
	    	long start = format.parse(startDate).getTime();
	    	long end = format.parse(endDate).getTime();
	    	
	    	while(end>=start) {
	    		allDays.add(format.format(start));
	    		start += dateTime;
	    	}
	    	
	    	Calendar calendar = Calendar.getInstance();
	    	for(String l : allDays) {
	    		calendar.setTime(format.parse(l));
	    		int week = calendar.get(Calendar.DAY_OF_WEEK);
	    		if(week ==1 || week == 7) {
	    			weekDays.add(l);
	    		}
	    	}
    	} catch (Exception e) {
            return Lists.newArrayList();
        }
    	return weekDays;
    }
    
    /**
     * 给 月日 缺少0的，补充完整（比如给‘2021-1-1’转成‘2021-01-01’）
     * @param dateStr
     * @return
     */
    public static String fillDateWithZero(String dateStr) {
    	dateStr = dateStr.trim();
    	int start = dateStr.indexOf("-");
    	int end = dateStr.lastIndexOf("-");
    	
    	String month = dateStr.substring(start+1, end).trim();
    	if(month.length()==1) {
    		month = "0"+month;
    	}
    	String day = dateStr.substring(end+1, dateStr.length());
    	if(day.length()==1) {
    		day = "0"+day;
    	}
    	return dateStr.substring(0, start)+"-"+month+"-"+day;
    }
    
    /**
     * 给月日，时分秒缺少0的补充完整（比如给‘2021-1-1’转成‘2021-01-01 00:00:00’ ‘2021-1-1 1:1:1’转成‘2021-01-01 01:01:01’）
     * @param str
     * @return
     */
    public static String fillDateTimeWithZero(String str) {
    	String retStr = "";
    	//默认是有时分秒的
    	if(str.indexOf(":")>0 || str.split(" ").length>1) {
    		String[] split = str.split(" ");
    		retStr = fillDateWithZero(split[0])+"";
    		String[] time = split[1].split(":");
    		for(int i=0;i<time.length;i++) {
    			if(time[i].length()==1) {
    				time[i] = "0"+time[i];
    			}else if(Integer.parseInt(time[i])>60){
    				time[i] = "00";
    			}
    		}
    		if(time.length==0) {
    			retStr += " 00:00:00";
    		}else if(time.length==1) {
    			retStr += " "+time[0]+":00:00";
    		}else if(time.length==2) {
    			retStr += " "+time[0]+":"+time[1]+":00";
    		}else if(time.length>2) {
    			retStr += " "+time[0]+":"+time[1]+":"+time[2];
    		}
    	}else {
    		//默认只有年月日
    		retStr = fillDateWithZero(str)+" 00:00:00";
    	}
    	return retStr;
    }

    public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(str.length()==10){
            format = new SimpleDateFormat("yyyy-MM-dd");
        }
        try {
            //设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
        	format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess=false;
        }
        return convertSuccess;
    }
    
    /**
     * 时间加减年数
     *
     * @param startDate 要处理的时间，Null则为当前时间
     * @param years     加减的年数
     * @return Date
     */
    public static Date dateAddYears(Date startDate, int years) {
        if (startDate == null) {
            startDate = new Date();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) + years);
        return c.getTime();
    }
    
    /**
     * 时间加减月数
     *
     * @param startDate 要处理的时间，Null则为当前时间
     * @param months    加减的月数
     * @return Date
     */
    public static Date dateAddMonths(Date startDate, int months) {
        if (startDate == null) {
            startDate = new Date();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) + months);
        return c.getTime();
    }
    
    /**
     * 时间加减天数
     *
     * @param startDate 要处理的时间，Null则为当前时间
     * @param days      加减的天数
     * @return Date
     */
    public static Date dateAddDays(Date startDate, int days) {
        if (startDate == null) {
            startDate = new Date();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(Calendar.DATE, c.get(Calendar.DATE) + days);
        return c.getTime();
    }
    
    /**
     * 时间加减小时
     *
     * @param startDate 要处理的时间，Null则为当前时间
     * @param hours     加减的小时
     * @return Date
     */
    public static Date dateAddHours(Date startDate, int hours) {
        if (startDate == null) {
            startDate = new Date();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(Calendar.HOUR, c.get(Calendar.HOUR) + hours);
        return c.getTime();
    }

    /**
     * 时间加减分钟
     *
     * @param startDate 要处理的时间，Null则为当前时间
     * @param minutes   加减的分钟
     * @return Date
     */
    public static Date dateAddMinutes(Date startDate, int minutes) {
        if (startDate == null) {
            startDate = new Date();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + minutes);
        return c.getTime();
    }
    
    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
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
