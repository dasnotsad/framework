package utils.date;

import net.dgg.utils.constant.DggDateFormatType;
import net.dgg.utils.date.DggDateFormatUtil;
import org.junit.Test;

import java.util.Date;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/24 0024
 * @Description:
 */
public class DggDateFormatUtilTest {


    @Test
    public void formatCurrentDateTest() {

        String result0 = DggDateFormatUtil.formatCurrentDate(DggDateFormatType.MONTH_FORMATTER);
        System.out.println(result0);

        String result1 = DggDateFormatUtil.formatCurrentDate(DggDateFormatType.DATE_FORMATTER);
        System.out.println(result1);

        String result2 = DggDateFormatUtil.formatCurrentDate(DggDateFormatType.DATETIME_FORMATTER);
        System.out.println(result2);

        String result3 = DggDateFormatUtil.formatCurrentDate(DggDateFormatType.CONTINUITY_MONTH_FORMATTER);
        System.out.println(result3);

        String result4 = DggDateFormatUtil.formatCurrentDate(DggDateFormatType.CONTINUITY_DATE_FORMATTER);
        System.out.println(result4);

        String result5 = DggDateFormatUtil.formatCurrentDate(DggDateFormatType.CONTINUITY_TIMESTAMP_FORMATTER);
        System.out.println(result5);

        String result6 = DggDateFormatUtil.formatCurrentDate(DggDateFormatType.CONTINUITY_MILLISECOND_FORMATTER);
        System.out.println(result6);

        String result7 = DggDateFormatUtil.formatCurrentDate(DggDateFormatType.BACKSLASH_MONTH_FORMATTER);
        System.out.println(result7);

        String result8 = DggDateFormatUtil.formatCurrentDate(DggDateFormatType.BACKSLASH_DATE_FORMATTER);
        System.out.println(result8);

        String result9 = DggDateFormatUtil.formatCurrentDate(DggDateFormatType.BACKSLASH_DATETIME_FORMATTER);
        System.out.println(result9);
    }

    @Test
    public void stringConvertDateTest() {
        String strDate = "2019/05/22";
        Date date = DggDateFormatUtil.stringConvertDate(strDate, DggDateFormatType.BACKSLASH_DATE_FORMATTER);
        System.out.println(date);

        String strDate1 = "20190522";
        Date date1 = DggDateFormatUtil.stringConvertDate(strDate1, DggDateFormatType.CONTINUITY_DATE_FORMATTER);
        System.out.println(date1);
    }

    @Test
    public void stringConvertDateTimeTest() {
        String strDate = "2019/05/22 17:22:33";
        Date date = DggDateFormatUtil.stringConvertDateTime(strDate, DggDateFormatType.BACKSLASH_DATETIME_FORMATTER);
        System.out.println(date);

        String strDate1 = "20190522172233";
        Date date1 = DggDateFormatUtil.stringConvertDateTime(strDate1, DggDateFormatType.CONTINUITY_TIMESTAMP_FORMATTER);
        System.out.println(date1);
    }


}
