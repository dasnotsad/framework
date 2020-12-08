package net.dgg.utils.work;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * POI工具类
 *
 * @author Administrator
 */
public class DggPoiUtil {

    /**
     * 设置行宽
     *
     * @param sheet
     * @param widths
     */
    public static void setColumnWidth(HSSFSheet sheet, int[] widths) {
        if (widths == null) {
            return;
        }
        for (int i = 0; i < widths.length; i++) {
            sheet.setColumnWidth(i, widths[i]);
        }
    }

    /**
     * 设置样式（无背景色）
     *
     * @param wb
     * @param align
     * @param hasBorder
     * @return
     */
    public static CellStyle getCellStyle(HSSFWorkbook wb, int align, boolean hasBorder) {
        return getCellStyle(wb, align, hasBorder, (short) -1);
    }

    /**
     * 设置样式（无背景色）
     *
     * @param wb
     * @param align
     * @param hasBorder
     * @return
     */
    public static CellStyle getCellStyle(HSSFWorkbook wb, int align, boolean hasBorder, boolean isBigDecimal) {
        return getCellStyle(wb, align, hasBorder, (short) -1, isBigDecimal);
    }

    /**
     * 设置样式（无背景色）
     *
     * @param wb
     * @param align
     * @param hasBorder
     * @return
     */
    public static CellStyle getCellStyle(HSSFWorkbook wb, int align, boolean hasBorder, short color) {
        return getCellStyle(wb, align, hasBorder, color, false);
    }

    /**
     * 设置样式，包含背景色
     *
     * @param wb
     * @param align
     * @param hasBorder
     * @return
     */
    public static CellStyle getCellStyle(HSSFWorkbook wb, int align, boolean hasBorder, short color, boolean isBigDecimal) {
        CellStyle style = wb.createCellStyle();
        if (align < 0) {
            style.setAlignment(HorizontalAlignment.LEFT);
        } else if (align == 0) {
            style.setAlignment(HorizontalAlignment.CENTER);
        } else {
            style.setAlignment(HorizontalAlignment.RIGHT);
        }
        if (hasBorder) {
            style.setBorderBottom(BorderStyle.THIN); //下边框
            style.setBorderLeft(BorderStyle.THIN);//左边框
            style.setBorderTop(BorderStyle.THIN);//上边框
            style.setBorderRight(BorderStyle.THIN);//右边框
        }
        if (color >= 0) {
            style.setFillForegroundColor(color);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (isBigDecimal) {
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat("###,##0.00"));
        }
        return style;
    }

}
