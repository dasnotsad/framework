package net.dgg.utils.date;

import net.dgg.utils.constant.DggDateFormatType;
import net.dgg.utils.constant.DggDateType;
import net.dgg.utils.exception.DggUtilException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/24 0024
 * @Description: 日期工具类
 */
public class DggDateUtil {

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    private static ZoneId zone = DggDateFormatUtil.zone;

    /**
     * @return java.util.Date
     * @description: 获取当前日期
     */
    public static Date nowDate() {
        return new Date();
    }

    /**
     * @return java.lang.String
     * @description: 返回当前日期 格式为: yyyy-MM-dd
     */
    public static String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DggDateFormatType.DATE_FORMATTER.getValue());
        return LocalDateTime.now().format(formatter);
    }

    /**
     * @return java.lang.String
     * @description: 返回当前日期 格式为: yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DggDateFormatType.DATETIME_FORMATTER.getValue());
        return LocalDateTime.now().format(formatter);
    }

    /**
     * @param date
     * @return java.lang.String
     * @description: 返回指定日期 格式为: yyyy-MM-dd
     */
    public static String getFixedDate(Date date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DggDateFormatType.DATE_FORMATTER.getValue());
        LocalDateTime localDateTime = dateConvertToLocalDateTime(date);
        return dateTimeFormatter.format(localDateTime);
    }

    /**
     * @param date
     * @return java.time.LocalDateTime
     * @description: java.util.Date 转换为 LocalDateTime
     */
    public static LocalDateTime dateConvertToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * @param localDateTime
     * @return java.util.Date
     * @description: LocalDateTime 转换为 java.util.Date
     */
    public static Date localDateTimeConvertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(OffsetDateTime.now().getOffset()));
    }

    /**
     * @param date
     * @return java.time.LocalDateTime
     * @description: 根据date 获取LocalDateTime
     */
    public static LocalDateTime getLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * @param date
     * @param count
     * @param dateType
     * @return java.util.Date
     * @description: 将日期减少相应(年 、 月 、 日 、 时 、 分 、 秒)的count数
     */
    public static Date dateMinus(Date date, long count, DggDateType dateType) {
        LocalDateTime localDateTime = getLocalDateTime(date);
        switch (dateType) {
            case YEARS:
                return Date.from(localDateTime.minusYears(count).atZone(zone).toInstant());
            case MONTHS:
                return Date.from(localDateTime.minusMonths(count).atZone(zone).toInstant());
            case DAYS:
                return Date.from(localDateTime.minusDays(count).atZone(zone).toInstant());
            case HOURS:
                return Date.from(localDateTime.minusHours(count).atZone(zone).toInstant());
            case MINUTES:
                return Date.from(localDateTime.minusMinutes(count).atZone(zone).toInstant());
            case SECONDS:
                return Date.from(localDateTime.minusSeconds(count).atZone(zone).toInstant());
            default:
                return date;
        }
    }

    /**
     * @param date
     * @param count
     * @param dateType
     * @return java.util.Date
     * @description: 将日期增加相应(年 、 月 、 日 、 时 、 分 、 秒)的count数
     */
    public static Date datePlus(Date date, long count, DggDateType dateType) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = getLocalDateTime(date);
        switch (dateType) {
            case YEARS:
                return Date.from(localDateTime.plusYears(count).atZone(zone).toInstant());
            case MONTHS:
                return Date.from(localDateTime.plusMonths(count).atZone(zone).toInstant());
            case DAYS:
                return Date.from(localDateTime.plusDays(count).atZone(zone).toInstant());
            case HOURS:
                return Date.from(localDateTime.plusHours(count).atZone(zone).toInstant());
            case MINUTES:
                return Date.from(localDateTime.plusMinutes(count).atZone(zone).toInstant());
            case SECONDS:
                return Date.from(localDateTime.plusSeconds(count).atZone(zone).toInstant());
            default:
                return date;
        }
    }

    /**
     * @param smallDate
     * @param bigDate
     * @param dateType
     * @return
     * @description: 计算两个日期相差数(日 、 时 、 分 、 秒等也可以使用Duration.between ())
     */
    public static long dateDiffer(Date smallDate, Date bigDate, DggDateType dateType) {
        LocalDateTime smallLocalDateTime = getLocalDateTime(smallDate);
        LocalDateTime bigLocalDateTime = getLocalDateTime(bigDate);
        switch (dateType) {
            case YEARS:
                return smallLocalDateTime.until(bigLocalDateTime, ChronoUnit.YEARS);
            case MONTHS:
                return smallLocalDateTime.until(bigLocalDateTime, ChronoUnit.MONTHS);
            case DAYS:
                return smallLocalDateTime.until(bigLocalDateTime, ChronoUnit.DAYS);
            case HOURS:
                return smallLocalDateTime.until(bigLocalDateTime, ChronoUnit.HOURS);
            case MINUTES:
                return smallLocalDateTime.until(bigLocalDateTime, ChronoUnit.MINUTES);
            case SECONDS:
                return smallLocalDateTime.until(bigLocalDateTime, ChronoUnit.SECONDS);
            default:
                return 0L;
        }
    }

    /**
     * @param date
     * @param clazz
     * @param <T>
     * @return T
     * @description: 根据传入的日期返回星期(传入的标识为String则返回中文表示形式, 传入为Integer则返回数值表示形式)
     */
    public static <T> T dateOfWeek(Date date, Class clazz) {
        LocalDateTime localDateTime = getLocalDateTime(date);
        if (String.class == clazz) {
            switch (localDateTime.getDayOfWeek()) {
                case MONDAY:
                    return (T) "星期一";
                case TUESDAY:
                    return (T) "星期二";
                case WEDNESDAY:
                    return (T) "星期三";
                case THURSDAY:
                    return (T) "星期四";
                case FRIDAY:
                    return (T) "星期五";
                case SATURDAY:
                    return (T) "星期六";
                case SUNDAY:
                    return (T) "星期日";
                default:
                    return (T) "星期八";
            }

        } else if (clazz == Integer.class) {
            return (T) Integer.valueOf(localDateTime.getDayOfWeek().getValue());
        }
        throw new DggUtilException("clazz只能为String和Integer类型!");
    }

    /*--------------------------------------[ Add by yan.x 2020/8/4 begin ]------------------------------------*/

    /**
     * 得到当前日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(final String... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern[0]));
        } else {
            formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DggDateFormatType.DATETIME_FORMATTER.getValue()));
        }
        return formatDate;
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(final LocalDateTime date, final String... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = date.format(DateTimeFormatter.ofPattern(pattern[0]));
        } else {
            formatDate = date.format(DateTimeFormatter.ofPattern(DggDateFormatType.DATETIME_FORMATTER.getValue()));
        }
        return formatDate;
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(final Long date, final Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, DggDateFormatType.DATETIME_FORMATTER.getValue());
        }
        return formatDate;
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(final Date date, final Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, DggDateFormatType.DATETIME_FORMATTER.getValue());
        }
        return formatDate;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Long getTimestamp() {
        return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 日期型字符串转化为日期 格式
     * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
     * "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     */
    public static Date parseDate(final Object str) {
        if (str == null) {
            return null;
        }
        try {
            return DateUtils.parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    public static LocalDateTime parseDate() {
        return LocalDateTime.now(ZoneOffset.of("+8"));
    }


    public static LocalDateTime parseDate(final String date, final String... pattern) {
        if (pattern != null && pattern.length > 0) {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern[0]));
        } else {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DggDateFormatType.DATETIME_FORMATTER.getValue()));
        }
    }

    public static Long parseTimestamp(final String date, final String... pattern) {
        LocalDateTime localDateTime = parseDate(date, pattern);
        return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(final Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }


    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前时间字符串 格式（yyyyMMddHHmmss）
     */
    public static String getSeqTime() {
        return formatDate(new Date(), "yyyyMMddHHmmss");
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
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 获取指定时间
     *
     * @param date
     * @param moth    : 月
     * @param day     : 日
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static String getScheduleTime(Date date, int moth, int day, final String pattern) {
        SimpleDateFormat fmt = new SimpleDateFormat(StringUtils.isNotBlank(pattern) ? pattern : "yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, moth);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return fmt.format(calendar.getTime());
    }


    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDays(final Date date) {
        long t = getTimestamp() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     *
     * @param date
     * @return
     */
    public static long pastHour(final Date date) {
        long t = getTimestamp() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     * @return
     */
    public static long pastMinutes(final Date date) {
        long t = getTimestamp() - date.getTime();
        return t / (60 * 1000);
    }


    /**
     * 转换为时间（天,时:分:秒.毫秒）
     *
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(final long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    /**
     * 计算距离现在多久，精确
     *
     * @param date
     * @return
     */
    public static String getTimeBeforeAccurate(Date date) {
        Date now = new Date();
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        String r = "";
        if (day > 0) {
            r += day + "天";
        }
        if (hour > 0) {
            r += hour + "小时";
        }
        if (min > 0) {
            r += min + "分";
        }
        if (s > 0) {
            r += s + "秒";
        }
        r += "前";
        return r;
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param before
     * @param after
     * @return
     */
    public static double getDistanceOfTwoDate(final Date before, final Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
    }

    /**
     * 获取指定时间前后时间:年,月,日,天,小时,分钟,
     *
     * @param date          : 需要格式化的时间
     * @param amount        : i为正数 向后推迟i，负数时向前提前i
     * @param calendarField : {@link Calendar#HOUR} 年,月,日,天,小时,分钟,
     * @return
     */
    public static Date anyTimeByCurrentDay(final Date date, final int amount, final int calendarField) {
        Date dat = null;
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        cd.add(calendarField, amount);
        dat = cd.getTime();
        return new Timestamp(dat.getTime());
    }

    /**
     * 获取当前周的第一天：
     *
     * @return
     */
    public static Date getFirstDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_WEEK, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cal.getTime();
    }

    /**
     * 获取当前周最后一天
     *
     * @return
     */
    public static Date getLastDayOfWeek() {
        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_WEEK, 1);
            cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 6);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cal.getTime();
    }

    /**
     * 获取当日开始时间
     *
     * @return
     */
    public static Timestamp getDayBegin() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 001);
        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * 获取当日结束时间
     *
     * @return
     */
    public static Timestamp getDayEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.MILLISECOND, 001);
        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * 计算某一月份的最大天数
     *
     * @return
     */
    public static int getMaximumDay(final int amount) {
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.MONTH, amount - 1);//注意,Calendar对象默认一月为0
        return time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
    }
    /*--------------------------------------[ Add by yan.x 2020/8/4  end  ]------------------------------------*/
}
