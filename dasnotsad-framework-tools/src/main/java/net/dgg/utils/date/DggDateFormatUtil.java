package net.dgg.utils.date;

import net.dgg.utils.constant.DggDateFormatType;
import net.dgg.utils.object.DggObjectUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/24 0024
 * @Description: 日期格式化工具类(formatter 可以使用DggDateConstant)
 */
public class DggDateFormatUtil {

    protected static ZoneId zone = ZoneId.systemDefault();

    /**
     * @param type
     * @return
     * @description 获取格式化后的日期字符串
     */
    public static String formatCurrentDate(DggDateFormatType type) {
        if (DggObjectUtil.isEmpty(type)) {
            type = DggDateFormatType.DATE_FORMATTER;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(type.getValue());
        LocalDateTime localDateTime = LocalDateTime.now();
        return dateTimeFormatter.format(localDateTime);
    }

    /**
     * @param date 字符串日期
     * @param type 字符串日期的格式
     * @return
     * @description 将指定格式的日期字符串转为java.util.Date
     */
    public static Date stringConvertDate(String date, DggDateFormatType type) {
        if (DggObjectUtil.isEmpty(type)) {
            type = DggDateFormatType.DATE_FORMATTER;
        }
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(type.getValue()));
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * @param dateTime 字符串日期
     * @param type     字符串日期的格式
     * @return
     * @description: 将指定格式的日期字符串转为java.util.Date
     */
    public static Date stringConvertDateTime(String dateTime, DggDateFormatType type) {
        if (DggObjectUtil.isEmpty(type)) {
            type = DggDateFormatType.DATETIME_FORMATTER;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(type.getValue()));
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * @param date
     * @param type
     * @return
     * @description: 格式化日期
     */
    public static String dateFormat(Date date, DggDateFormatType type) {
        if (DggObjectUtil.isEmpty(type)) {
            type = DggDateFormatType.DATETIME_FORMATTER;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(type.getValue());
        LocalDateTime localDateTime = DggDateUtil.getLocalDateTime(date);
        return dateTimeFormatter.format(localDateTime);
    }

    /**
     * @param date
     * @param originalFormatter 原始格式类型
     * @param newlyFormatter    格式化类型
     * @return
     * @description: 将原始字符串日期(仅限年月日)格式成为新的字符串日期
     */
    public static String replaceFormatDate(String date, String originalFormatter, String newlyFormatter) {
        DateTimeFormatter originalDateTimeFormatter = DateTimeFormatter.ofPattern(originalFormatter);
        DateTimeFormatter newlyDateTimeFormatter = DateTimeFormatter.ofPattern(newlyFormatter);
        LocalDate localDateTime = LocalDate.parse(date, originalDateTimeFormatter);
        return localDateTime.format(newlyDateTimeFormatter);
    }

    /**
     * @param date
     * @param originalFormatter 原始格式类型
     * @param newlyFormatter    格式化类型
     * @return
     * @description: 将原始字符串日期(包含时间)格式成为新的字符串日期
     */
    public static String replaceFormatDateTime(String date, String originalFormatter, String newlyFormatter) {
        DateTimeFormatter originalDateTimeFormatter = DateTimeFormatter.ofPattern(originalFormatter);
        DateTimeFormatter newlyDateTimeFormatter = DateTimeFormatter.ofPattern(newlyFormatter);
        LocalDateTime localDateTime = LocalDateTime.parse(date, originalDateTimeFormatter);
        return localDateTime.format(newlyDateTimeFormatter);
    }

}
