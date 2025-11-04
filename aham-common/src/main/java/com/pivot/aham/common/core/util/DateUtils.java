package com.pivot.aham.common.core.util;

import com.pivot.aham.common.core.Constants;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Desc:date工具类
 * @author md
 * @version 2016-05-17
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils{

    /**
     * pattern
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_FORMAT2 = "yyyyMMdd";

    public static final String DATE_FORMAT3 = "yyyy/MM/dd";

    public static final String DATE_FORMAT4 = "yyyy/MM/dd HH:mm:ss";
    
    public static final String DATE_FORMAT7 = "yyyy-MM-dd'T'HH:mm:ss";
    
    public static final String DATE_FORMAT5 = "ddMM"; //Added By Wooi Tatt
    
    public static final String DATE_FORMAT6 = "yyMMdd"; //Added By Wooi Tatt

    public static final String DATE_FORMAT_ZH = "yyyy年MM月dd日";

    public static final String DATE_FORMAT_MONTH_DAY_ZH = "M月d日";

    public static final String DATE_FORMAT_TO_MINUTES = "yyyy-MM-dd HH:mm";

    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIME_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String DATE_TIME_FORMAT_UTC_HMS = "HH:mm:ss";

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.S";

    public static final String DATE_TIME_FORMAT1 = "yyyyMMdd HH:mm:ss";

    public static final String DATE_TIME_FORMAT2 = "yyyyMMddHH:mm:ss";

    public static final String DATE_TIME_KEY_FORMAT2 = "yyyyMMddHHmmss";

    public static Date now() {
        return new Date();
    }

    public static Date nowUTC() {
        Calendar cal = Calendar.getInstance();
        //获得时区和 GMT-0 的时间差,偏移量
        int offset = cal.get(Calendar.ZONE_OFFSET);
        //获得夏令时  时差
        int dstoff = cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND, - (offset + dstoff));
        return cal.getTime();
    }

    public static String nowUTCStr() {
        return DateFormatUtils.format(nowUTC(), DATE_TIME_FORMAT_UTC);
    }

    public static String getUTCStr(Date date) {
        return DateFormatUtils.format(date, DATE_TIME_FORMAT_UTC);
    }

    public static String getUTCTimeStr(Date date) {
        return DateFormatUtils.format(date, DATE_TIME_FORMAT_UTC_HMS);
    }

    /**
     * 上N个月第一天
     * @param now
     * @param subMonth
     * @return
     */
    public static Date subDateBegin(Date now, int subMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int month = cal.get(Calendar.MONTH);
        cal.set(Calendar.MONTH, month-subMonth);
        cal.set(Calendar.DAY_OF_MONTH,1);
        dayStart(cal.getTime());
        return cal.getTime();
    }

    /**
     * 上N个月最后一天
     * @param now
     * @param subMonth
     * @return
     */
    public static Date subDateEnd(Date now, int subMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int month = cal.get(Calendar.MONTH);
        cal.set(Calendar.MONTH, month-subMonth);
        boolean flag = true;
        int day = 31;
        while(flag){
            cal.set(Calendar.DAY_OF_MONTH,day);
            int month_change = cal.get(Calendar.MONTH);
            if(month == month_change){
                day--;
            }else{
                flag = false;
            }
        }
        dayEnd(cal.getTime());
        return cal.getTime();
    }


    /**
     * 加offsetday
     *
     * @param now
     * @param offset
     * @return
     */
    public static Date addDateByDay(Date now, int offset) {
        return addDays(now,offset);
    }

    /**
     * 减offset天
     *
     * @param now
     * @param offset
     * @return
     */
    public static Date subDateByDay(Date now, int offset) {
        return addDateByDay(now,-offset);
    }

    /**
     * 某天的开始
     *
     * @param day
     * @return
     */
    public static Date dayStart(Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTime();
    }

    /**
     * 某天的结束
     * @param day
     * @return
     */
    public static Date dayEnd(Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        cal.set(Calendar.MILLISECOND,999);
        return cal.getTime();
    }

    /**
     * 某月的开始
     *
     * @param day
     * @return
     */
    public static Date monthStart(Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTime();
    }

    /**
     * 某月的结束
     * @param day
     * @return
     */
    public static Date monthEnd(Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        int lastDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, lastDate);
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        cal.set(Calendar.MILLISECOND,999);
        return cal.getTime();
    }

    /**
     * 间隔秒
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getBetween(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        long n = end.getTimeInMillis() - start.getTimeInMillis();
        return (int)(n / 1000l);
    }

    /**
     * get current date,fomart:yyyy-MM-dd HH:mm:ss
     *
     * @return String
     * @throws Exception
     */
    public static String getNowPlusTime() {
        String nowDate = "";
        try {
            java.sql.Date date = null;
            date = new java.sql.Date(DateUtils.now().getTime());
            nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            return nowDate;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 间隔天数
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getDayBetween(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        long n = end.getTimeInMillis() - start.getTimeInMillis();
        return (int)(n / (60 * 60 * 24 * 1000l));
    }

    /**
     * 间隔月
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getMonthBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int year1 = start.get(Calendar.YEAR);
        int year2 = end.get(Calendar.YEAR);
        int month1 = start.get(Calendar.MONTH);
        int month2 = end.get(Calendar.MONTH);
        int n = (year2 - year1) * 12;
        n = n + month2 - month1;
        return n;
    }

    /**
     * 间隔月，多一天就多算一个月
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getMonthBetweenWithDay(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int year1 = start.get(Calendar.YEAR);
        int year2 = end.get(Calendar.YEAR);
        int month1 = start.get(Calendar.MONTH);
        int month2 = end.get(Calendar.MONTH);
        int n = (year2 - year1) * 12;
        n = n + month2 - month1;
        int day1 = start.get(Calendar.DAY_OF_MONTH);
        int day2 = end.get(Calendar.DAY_OF_MONTH);
        if (day1 <= day2) {
            n++;
        }
        return n;
    }



    /**
     * 获取下一个工作日
     * @return
     */
    public static Date getNextWorkDate() {

        Calendar calendar = Calendar.getInstance();
        // 取明天
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        for (int i = 0; i < 7; i++) {
            if (!isWeekEnd(calendar)) {
                return calendar.getTime();
            } else {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        return null;
    }

    /**
     * 获取当日周几
     * @param date
     * @return
     */
    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 判断是否周末
     * @param calendar
     * @return
     */
    public static boolean isWeekEnd(Calendar calendar) {

        final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return Calendar.SATURDAY == dayOfWeek || Calendar.SUNDAY == dayOfWeek;

    }

    /**
     * 判断是否周末
     * @param date
     * @return
     */
    public static boolean isWeekEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return Calendar.SATURDAY == dayOfWeek || Calendar.SUNDAY == dayOfWeek;

    }

    /**
     * 日期加N秒
     * @param today
     * @param second
     * @return
     */
    public static Date addSecond(Date today, int second) {
        if (null == today) {
            return null;
        }

        return new Date(today.getTime() + second * 1000);
    }





    private final static int VAR_DAY = 1000 * 60 * 60 * 24;
    /**
     * 可以接受的字符串类型
     */
    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
            "yyyyMMdd"  , "yyyyMMddHH:mm:ss"
    };

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, Object... pattern) {
        if(date == null){
            return null;
        }
        String formatDate;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date,Locale locale, Object... pattern) {
        if(date == null){
            return null;
        }
        String formatDate;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString(),locale);
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd",locale);
        }
        return formatDate;
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }



    /**
     * 得到指定日期年份字符串 格式（yyyy）
     */
    public static String getYear(Date date) {
        return formatDate(date, "yyyy");
    }

    /**
     * 得到指定日期月份字符串 格式（MM）
     */
    public static String getMonth(Date date) {
        return formatDate(date, "MM");
    }

    /**
     * 得到指定日期天字符串 格式（dd）
     */
    public static String getDay(Date date) {
        return formatDate(date, "dd");
    }


    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 日期型字符串转化为日期 格式
     * {  "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
     "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
     "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
     "yyyyMMdd"  , "yyyyMMddHH:mm:ss" }
     */
    public static Date parseDate(Object str) {
        if (str == null){
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 是否闰年
     *
     * @param year 年
     * @return 是否闰年
     */
    public static boolean isLeapYear(int year){
        return new GregorianCalendar().isLeapYear(year);
    }

    /**
     * 获取过去的天数
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = System.currentTimeMillis()-date.getTime();
        return t/(24*60*60*1000);
    }

    /**
     * 获取过去的小时
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = System.currentTimeMillis()-date.getTime();
        return t/(60*60*1000);
    }

    /**
     * 获取过去的分钟
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = System.currentTimeMillis()-date.getTime();
        return t/(60*1000);
    }

    /**
     * 转换为时间（天,时:分:秒.毫秒）
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(long timeMillis){
        long day = timeMillis/(24*60*60*1000);
        long hour = (timeMillis/(60*60*1000)-day*24);
        long min = ((timeMillis/(60*1000))-day*24*60-hour*60);
        long s = (timeMillis/1000-day*24*60*60-hour*60*60-min*60);
        long sss = (timeMillis-day*24*60*60*1000-hour*60*60*1000-min*60*1000-s*1000);
        return (day>0?day+",":"")+hour+":"+min+":"+s+"."+sss;
    }


    /**
     * 生日转为年龄，计算法定年龄
     * @param birthDay 生日
     * @return 年龄
     */
    public static int ageOfNow(Date birthDay) {
        return age(birthDay,new Date());
    }

    /**
     * 计算相对于dateToCompare的年龄，长用于计算指定生日在某年的年龄
     * @param birthDay 生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     * @throws Exception
     */
    public static int age(Date birthDay, Date dateToCompare) {
        if(birthDay == null){
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateToCompare);
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("Birthday is after date "+ formatDate(dateToCompare));
        }

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthDay);
        int age = year - cal.get(Calendar.YEAR);

        int monthBirth = cal.get(Calendar.MONTH);
        if (month == monthBirth) {
            int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            if (dayOfMonth < dayOfMonthBirth) {
                //如果生日在当月，但是未达到生日当天的日期，年龄减一
                age--;
            }
        } else if (month < monthBirth){
            //如果当前月份未达到生日的月份，年龄计算减一
            age--;
        }

        return age;
    }


    /**
     * 获取一天中的小时
     * @param date
     * @return
     */
    public static int getHourOfDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 按年月日获取日期
     * 注: in calendar.set(xxx): month is base from 0, so must use :natural_month - 1
     *
     * @param year
     * @param natural_month
     * @param day
     * @return
     */
    public static Date getDate(int year, int natural_month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, natural_month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 设置时间
     * @param hrs
     * @param min
     * @param sec
     * @return
     */
    public static Date getDate(Date date, int hrs, int min, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,hrs);
        calendar.set(Calendar.MINUTE,min);
        calendar.set(Calendar.SECOND,sec);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * get specified date,fomart:yyyy-MM-dd HH:mm:ss
     *
     * @return String
     * @throws Exception
     */
    public static String getPlusTime(Date date) {
        if (date == null) return null;
        try {
            String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            return nowDate;
        } catch (Exception e) {
            // do nothing for this;
            return "";
        }
    }

    /**
     * 获取某年的某月
     */
    public static String getDateYearMonth(Date date) {
        String nowDate = "";

        try {
            if (date != null) {
                nowDate = (new SimpleDateFormat("yyyy-MM")).format(date);
            }

            return nowDate;
        } catch (Exception var3) {
            return "";
        }
    }

    /**
     * 获取友好型与当前时间的差
     *
     * @param millis 毫秒时间戳
     * @return 友好型与当前时间的差
     * <ul>
     * <li>如果小于1秒钟内，显示刚刚</li>
     * <li>如果在1分钟内，显示XXX秒前</li>
     * <li>如果在1小时内，显示XXX分钟前</li>
     * <li>如果在1小时外的今天内，显示今天15:32</li>
     * <li>如果是昨天的，显示昨天15:32</li>
     * <li>如果是当年的，显示10-15</li>
     * <li>其余显示，2016-10-15</li>
     * <li>时间不合法的情况全部日期和时间信息，如2018-05-13 14:21:20</li>
     * </ul>
     */
    public static String getFriendly(long millis) {
        long now = System.currentTimeMillis();
        long span = now - millis;
        if (span < 0) {
            return String.format("%tF %tT", millis, millis);
        }
        if (span < 1000) {
            return "刚刚";
        } else if (span < Constants.TIMES.MINUTE) {
            return String.format("%d秒前", span / Constants.TIMES.SECOND);
        } else if (span < Constants.TIMES.HOUR) {
            return String.format("%d分钟前", span / Constants.TIMES.MINUTE);
        }
        // 获取当天00:00
        long wee = now / Constants.TIMES.DAY * Constants.TIMES.DAY;
        if (millis >= wee) {
            return String.format("今天%tR", millis);
        } else if (millis >= wee - Constants.TIMES.DAY) {
            return String.format("昨天%tR", millis);
        } else {
            wee = now / Constants.TIMES.YEAR * Constants.TIMES.YEAR;
            if (millis >= wee) {
                return String.format("%tm-%te", millis, millis);
            }
            return String.format("%tF", millis);
        }
    }
    public static Date getStartDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}

