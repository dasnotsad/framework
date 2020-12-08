package utils.date;

import net.dgg.utils.constant.DggDateFormatType;
import net.dgg.utils.constant.DggDateType;
import net.dgg.utils.date.DggDateFormatUtil;
import net.dgg.utils.date.DggDateUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;

import static org.hamcrest.core.Is.is;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/24 0024
 * @Description:
 */
public class DggDateUtilTest {

    /*@Test
    public void nowDateTest() {
        Date date = DggDateUtil.nowDate();
        System.out.println(date);
    }

    @Test
    public void getCurrentDateTest() {
        String result = DggDateUtil.getCurrentDate();
        //Assert.assertEquals("日期不相等!", "2019-06-24", result);
    }

    @Test
    public void getCurrentDateTimeTest() {
        System.out.println(DggDateUtil.getCurrentDateTime());
    }

    @Test
    public void getFixedDateTest() {
        String strDate = "2019-05-22";
        Date date = DggDateFormatUtil.stringConvertDate(strDate, DggDateFormatType.DATE_FORMATTER);
        String result = DggDateUtil.getFixedDate(date);
        Assert.assertEquals("日期不相等!", strDate, result);
    }

    @Test
    public void dateConvertToLocalDateTimeTest() {
        LocalDateTime localDateTime = DggDateUtil.dateConvertToLocalDateTime(DggDateUtil.nowDate());
        Assert.assertThat("年份不相等", 2019, is(localDateTime.getYear()));
    }

    @Test
    public void localDateTimeConvertToDateTest() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = DggDateUtil.localDateTimeConvertToDate(localDateTime);
        //Assert.assertTrue("不在当前日期之前!", date.before(DggDateUtil.nowDate()));
    }

    @Test
    public void dateMinusTest() {
        Date date = DggDateUtil.nowDate();
        System.out.println("当前日期为: " + DggDateFormatUtil.dateFormat(date, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println("============================");
        Date yearDate = DggDateUtil.dateMinus(date, 1L, DggDateType.YEARS);
        Date monthDate = DggDateUtil.dateMinus(date, 1L, DggDateType.MONTHS);
        Date dayDate = DggDateUtil.dateMinus(date, 1L, DggDateType.DAYS);
        Date hourDate = DggDateUtil.dateMinus(date, 1L, DggDateType.HOURS);
        Date minuteDate = DggDateUtil.dateMinus(date, 1L, DggDateType.MINUTES);
        Date secondDate = DggDateUtil.dateMinus(date, 1L, DggDateType.SECONDS);
        System.out.println(DggDateFormatUtil.dateFormat(yearDate, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println(DggDateFormatUtil.dateFormat(monthDate, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println(DggDateFormatUtil.dateFormat(dayDate, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println(DggDateFormatUtil.dateFormat(hourDate, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println(DggDateFormatUtil.dateFormat(minuteDate, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println(DggDateFormatUtil.dateFormat(secondDate, DggDateFormatType.DATETIME_FORMATTER));
    }

    @Test
    public void datePlusTest() {
        Date date = DggDateUtil.nowDate();
        System.out.println("当前日期为: " + DggDateFormatUtil.dateFormat(date, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println("============================");
        Date yearDate = DggDateUtil.datePlus(date, 1L, DggDateType.YEARS);
        Date monthDate = DggDateUtil.datePlus(date, 1L, DggDateType.MONTHS);
        Date dayDate = DggDateUtil.datePlus(date, 1L, DggDateType.DAYS);
        Date hourDate = DggDateUtil.datePlus(date, 1L, DggDateType.HOURS);
        Date minuteDate = DggDateUtil.datePlus(date, 1L, DggDateType.MINUTES);
        Date secondDate = DggDateUtil.datePlus(date, 1L, DggDateType.SECONDS);
        System.out.println(DggDateFormatUtil.dateFormat(yearDate, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println(DggDateFormatUtil.dateFormat(monthDate, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println(DggDateFormatUtil.dateFormat(dayDate, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println(DggDateFormatUtil.dateFormat(hourDate, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println(DggDateFormatUtil.dateFormat(minuteDate, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println(DggDateFormatUtil.dateFormat(secondDate, DggDateFormatType.DATETIME_FORMATTER));
    }

    @Test
    public void dateDifferTest() {
        String strDate = "2019-05-22 17:22:33";
        Date smallDate = DggDateFormatUtil.stringConvertDateTime(strDate, DggDateFormatType.DATETIME_FORMATTER);
        System.out.println("较小日期为: " + DggDateFormatUtil.dateFormat(smallDate, DggDateFormatType.DATETIME_FORMATTER));
        Date bigDate = DggDateUtil.nowDate();
        System.out.println("较大日期为: " + DggDateFormatUtil.dateFormat(bigDate, DggDateFormatType.DATETIME_FORMATTER));
        System.out.println("============================");
        long yearNumber = DggDateUtil.dateDiffer(smallDate, bigDate, DggDateType.YEARS);
        long monthNumber = DggDateUtil.dateDiffer(smallDate, bigDate, DggDateType.MONTHS);
        long dayNumber = DggDateUtil.dateDiffer(smallDate, bigDate, DggDateType.DAYS);
        long hourNumber = DggDateUtil.dateDiffer(smallDate, bigDate, DggDateType.HOURS);
        long minuteNumber = DggDateUtil.dateDiffer(smallDate, bigDate, DggDateType.MINUTES);
        long secondNumber = DggDateUtil.dateDiffer(smallDate, bigDate, DggDateType.SECONDS);
        System.out.println(yearNumber);
        System.out.println(monthNumber);
        System.out.println(dayNumber);
        System.out.println(hourNumber);
        System.out.println(minuteNumber);
        System.out.println(secondNumber);
    }

    @Test
    public void dateOfWeekTest() {
        String strDate = "2019-05-22 17:22:33";
        Date date = DggDateFormatUtil.stringConvertDateTime(strDate, DggDateFormatType.DATETIME_FORMATTER);
        String result = DggDateUtil.dateOfWeek(date, String.class);
        System.out.println(result);
    }


    @Test
    public void replaceFormatDateTest() {
        String strDate = "2019-05-22";
        String result = DggDateFormatUtil.replaceFormatDate(strDate, DggDateFormatType.DATE_FORMATTER.getValue(), DggDateFormatType.CONTINUITY_DATE_FORMATTER.getValue());
        System.out.println(result);
    }

    @Test
    public void replaceFormatDateTimeTest() {
        String strDate = "2019-05-22 16:22:33";
        String result = DggDateFormatUtil.replaceFormatDateTime(strDate, DggDateFormatType.DATETIME_FORMATTER.getValue(), DggDateFormatType.CONTINUITY_TIMESTAMP_FORMATTER.getValue());
        System.out.println(result);
    }*/

}
