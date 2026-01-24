package com.xtr.framework.common.utils;


import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 时间工具类
 *
 * @author hougt
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils
{
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDD = "yyyyMMdd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static String getYear()
    {
        return dateTimeNow(YYYY);
    }

    /**
     * 获取昨天日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getYesterday()
    {
        return dateTimeYesterday(YYYYMMDD);
    }
    public static String getTheDayBeforeYesterday()
    {
    	return dateTimeTheDayBeforeYesterday(YYYYMMDD);
    }

    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTimeYesterday(final String format)
    {
    	Calendar cal=Calendar.getInstance();
    	cal.add(Calendar.DATE,-1);
    	Date d=cal.getTime();

        return parseDateToStr(format, d);
    }
    public static final String dateTimeTheDayBeforeYesterday(final String format)
    {
    	Calendar cal=Calendar.getInstance();
    	cal.add(Calendar.DATE,-2);
    	Date d=cal.getTime();

    	return parseDateToStr(format, d);
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算相差天数
     */
    public static int differentDaysByMillisecond(Date date1, Date date2)
    {
        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate)
    {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor)
    {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor)
    {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    //根据年份和当年的第几周，获取这一周的日期
    public static String getDayOfWeek(int year, int weekindex) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        c.setWeekDate(year, weekindex, 1);

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 2;
        c.add(Calendar.DATE, -dayOfWeek); // 得到本周的第一天
        String begin = sdf.format(c.getTime());
        c.add(Calendar.DATE, 6); // 得到本周的最后一天
        String end = sdf.format(c.getTime());
        String range = begin + "-" + end;
        return range;
    }

    public static void main(String[] args) {
    	 //获取一个Calendar对象
        Calendar calendar = Calendar.getInstance();
        //设置星期一为一周开始的第一天
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        //设置在一年中第一个星期所需最少天数
        calendar.setMinimalDaysInFirstWeek(4);
        //获得当前的年
        int weekYear = calendar.get(Calendar.YEAR);
        //获得当前日期属于今年的第几周
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        //格式化日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        try {
            parse = simpleDateFormat.parse("2019-12-31");
            System.out.println("2019-12-31转换后的日期为：" + parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(parse);
        int weekOfYear1 = calendar.get(Calendar.WEEK_OF_YEAR);
        System.out.println("2019-12-31所在周属于第" + weekOfYear1 + "周");
        Calendar c = new GregorianCalendar();
        //设定日期为2019-12-31 23:59:59
        c.set(2019, Calendar.DECEMBER, 31, 23, 59, 59);

        //获得当前日期属于今年的第几周
        Integer weekOfYearLastWeek1 = c.get(Calendar.WEEK_OF_YEAR);
        System.out.println("当前日期属于第" + weekOfYearLastWeek1 + "周");
        //获得指定年的第几周的开始日期（dayOfWeek是从周日开始排序的）
        calendar.setWeekDate(2019, 52, 2);
        //获得Calendar的时间
        Date starttime = calendar.getTime();
        //获得指定年的第几周的结束日期
        calendar.setWeekDate(2019, 52, 1);
        Date endtime = calendar.getTime();
        //将时间戳格式化为指定格式
        String dateStart = simpleDateFormat.format(starttime);
        String dateEnd = simpleDateFormat.format(endtime);
        System.out.println("2019年第52周的开始日期为：" + dateStart);
        System.out.println("2019年第52周的结束日期为：" + dateEnd);
	}
}
