package net.dgg.utils.work;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import net.dgg.utils.core.DggBeanUtil;
import net.dgg.utils.constant.DggDateFormatType;
import net.dgg.utils.date.DggDateUtil;
import net.dgg.utils.exception.DggUtilException;
import net.dgg.utils.number.DggAmountUtil;
import net.dgg.utils.regex.DggRegexUtil;
import net.dgg.utils.request.DggRequestUtil;
import net.dgg.utils.string.DggStringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.STRING;

/**
 * @Description: Excel工具类
 * @Author Created by yan.x on 2019-08-28 .
 **/
public class DggExcelUtil {

    private static final String TYPE_XLS = ".xls";
    private static final String TYPE_XLSX = ".xlsx";
    private static final String fileNameDateFormat = DggDateFormatType.CONTINUITY_TIMESTAMP_FORMATTER.getValue();

    private DggExcelUtil() {
    }

    //读取excel
    public static Workbook readExcel(String filePath) {
        Workbook wb = null;
        if (filePath == null) {
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if (TYPE_XLS.equals(extString)) {
                return wb = new HSSFWorkbook(is);
            } else if (TYPE_XLSX.equals(extString)) {
                return wb = new XSSFWorkbook(is);
            } else {
                return wb = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    /**
     * Excel 导入
     *
     * @param file        : 文件
     * @param columnNames : 为Excel 实体类属性集合一一对应,比如我的实体类有 id,name,age,address
     * @return
     */
    public static List<Map<String, Object>> importExcel(MultipartFile file, String... columnNames) {
        List<Map<String, Object>> lines = new LinkedList<>();
        try {
            //根据路径获取这个操作excel的实例
            Workbook wb = WorkbookFactory.create(file.getInputStream());
            //根据页面index 获取sheet页
            Sheet sheet = wb.getSheetAt(0);
            Row row = null;
            //循环sheet页中数据从第二行开始，第一行是标题
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                row = sheet.getRow(i);
                Map<String, Object> clbo = new LinkedHashMap<>();
                for (int j = 0; j < columnNames.length; j++) {
                    Object value = "";
                    if (row.getCell(j) != null) {
                        CellType cellType = row.getCell(j).getCellType();
                        if (CellType.NUMERIC.equals(cellType)) {
                            if ("General".equals(row.getCell(j).getCellStyle().getDataFormatString())) {
                                value = String.valueOf(row.getCell(j).getNumericCellValue());
                            } else if ("m/d/yy".equals(row.getCell(j).getCellStyle().getDataFormatString())) {
                                value = DggDateUtil.formatDate(row.getCell(j).getDateCellValue());
                            } else {
                                value = String.valueOf(row.getCell(j).getNumericCellValue());
                            }
                        } else {
                            row.getCell(j).setCellType(CellType.STRING);//设置成String
                            value = row.getCell(j).getStringCellValue();
                        }
                    }
                    clbo.put(columnNames[j], value);
                }
                lines.add(clbo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DggUtilException("读取失败，请稍后再试！");
        }
        return lines;
    }

    /**
     * 导入数据
     *
     * @param file  : 文件
     * @param clazz : DTO类型
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) {
        if (file == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        try {
            return ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 导入数据
     *
     * @param file       : 文件
     * @param titleRows  : 表格标题行数,默认0
     * @param headerRows : 表头行数,默认1
     * @param clazz      : DTO类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> clazz) {
        if (file == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        try {
            return ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 导入数据
     *
     * @param filePath   : 文件路径
     * @param titleRows  : 表格标题行数,默认0
     * @param headerRows : 表头行数,默认1
     * @param clazz      : DTO类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> clazz) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }

        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);

        try {
            return ExcelImportUtil.importExcel(new File(filePath), clazz, params);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 导出xlsx类型Excel
     *
     * @param response
     * @param fileName : 标题
     * @param dataList : 数据源
     * @param clazz    : DTO类型
     * @param <T>
     */
    public static <T> void exportExcelXlsx(HttpServletResponse response, String fileName, List<? extends Object> dataList, Class<T> clazz) {
        exportExcel(response, fileName, TYPE_XLSX, dataList, clazz);
    }

    /**
     * 导出xls类型Excel
     *
     * @param response
     * @param fileName : 标题
     * @param dataList : 数据源
     * @param clazz    : DTO类型
     * @param <T>
     */
    public static <T> void exportExcelXls(HttpServletResponse response, String fileName, List<? extends Object> dataList, Class<T> clazz) {
        exportExcel(response, fileName, TYPE_XLS, dataList, clazz);
    }

    /**
     * 导出Excel
     *
     * @param response
     * @param fileName : 标题
     * @param type     : Excel类型
     * @param dataList : 数据源
     * @param clazz    : DTO类型
     * @param <T>
     */
    public static <T> void exportExcel(HttpServletResponse response, String fileName, String type, List<? extends Object> dataList, Class<T> clazz) {
        try {
            // 数据
            List<String> _eNames = DggBeanUtil.getPropertyName(clazz);
            if (null == _eNames || _eNames.isEmpty()) {
                return;
            }
            List<T> eliLists = new ArrayList<>();
            if (null != dataList) {
                for (Object data : dataList) {
                    if (null == data) {
                        continue;
                    }
                    T _t = clazz.newInstance();
                    for (String eName : _eNames) {
                        try {
                            DggBeanUtil.setProperty(_t, eName, DggBeanUtil.getProperty(data, eName));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    eliLists.add(_t);
                }
            }
            exportExcel(response, fileName, eliLists, new ExportParams(), clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件导出
     *
     * @param response
     * @param fileName
     * @param dataList
     */
    private static void exportExcel(HttpServletResponse response, String fileName, List<Map<String, Object>> dataList) {
        Workbook workbook = ExcelExportUtil.exportExcel(dataList, ExcelType.HSSF);
        if (workbook != null) {
            exportExcel(response, fileName, workbook);
        }
    }

    /**
     * 文件导出
     *
     * @param response
     * @param fileName
     * @param dataList
     * @param exportParams
     * @param clazz
     * @param <T>
     */
    private static <T> void exportExcel(HttpServletResponse response, String fileName, List<T> dataList, ExportParams exportParams, Class<T> clazz) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, clazz, dataList);
        if (workbook != null) {
            exportExcel(response, fileName, workbook);
        }
    }

    /**
     * 文件导出
     *
     * @param response
     * @param fileName : 标题
     * @param workbook
     */
    private static void exportExcel(HttpServletResponse response, String fileName, Workbook workbook) {
        try {
            //编码
            response.setCharacterEncoding("UTF-8");
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 导出
     *
     * @param list      需要导出的数据list
     * @param sheetName 文件名
     * @param title     表头数组
     * @param keyArray  list里面对象的key值
     * @param widths    每个单元格的宽度
     * @param styleArr  每个单元格位置  -1 靠左  0 居中  1 靠右
     */
    public static void exportExcel(List list, String sheetName, String[] title, String[] keyArray, int[] widths, int[] styleArr) {
        //第一步创建workbook
        HSSFWorkbook wb = new HSSFWorkbook();
        //第二步创建sheet
        HSSFSheet sheet = wb.createSheet(sheetName);
        //设置宽度
        DggPoiUtil.setColumnWidth(sheet, widths);
        sheet.setDefaultRowHeight((short) (360));
        HSSFRow row_0 = sheet.createRow(0);
        for (int i = 0; i < title.length; i++) {
            Cell cell_0_i = row_0.createCell(i);
            cell_0_i.setCellType(STRING);
            cell_0_i.setCellStyle(DggPoiUtil.getCellStyle(wb, 0, true, HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex()));
            cell_0_i.setCellValue(title[i]);
        }
        CellStyle[] styleArray = new CellStyle[styleArr.length];
        for (int k = 0; k < styleArr.length; k++) {
            styleArray[k] = DggPoiUtil.getCellStyle(wb, styleArr[k], true);

        }

        for (int i = 0; i < list.size(); i++) {
            Row row_i = sheet.createRow(i + 1);
            for (int j = 0; j < keyArray.length; j++) {
                Cell cell_i_j = row_i.createCell(j);
                cell_i_j.setCellType(STRING);
                cell_i_j.setCellStyle(styleArray[j]);
                Object value = DggBeanUtil.getFieldValue(list.get(i), keyArray[j]);
                if (value == null) {
                    cell_i_j.setCellValue("");
                } else if (value instanceof BigDecimal) {
                    BigDecimal amt = (BigDecimal) value;
                    cell_i_j.setCellValue(DggAmountUtil.toString(amt, ""));
                } else if (value instanceof Date) {
                    Date amt = (Date) value;
                    cell_i_j.setCellValue(DggDateUtil.formatDate(amt));
                } else {
                    cell_i_j.setCellValue(value.toString());
                }
            }
        }
        String fileName = sheetName + DggDateUtil.formatDate(fileNameDateFormat) + TYPE_XLS;
        try {
            HttpServletResponse response = DggRequestUtil.getResponse();
            OutputStream out = null;
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            out = response.getOutputStream();
            wb.write(out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 导出Excel
     *
     * @param filename            保存到客户端的文件名
     * @param title               标题行 例：String[]{"名称","地址"}
     * @param key                 从查询结果List取得的MAP的KEY顺序，需要和title顺序匹配，例：String[]{"name","address"}
     * @param values              结果集
     * @param httpServletResponse
     */
    public static void exportExcel(String filename, String[] title, String[] key, @SuppressWarnings("rawtypes") List<Map> values, HttpServletResponse httpServletResponse) {
        OutputStream servletOutputStream = null;
        try {
            servletOutputStream = new BufferedOutputStream(httpServletResponse.getOutputStream());
            HSSFWorkbook workbook = null;//创建工作簿
            httpServletResponse.setContentType("application/octet-stream; charset=UTF-8");
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\""
                    + new String(filename.getBytes("GBK"), "ISO8859-1") + "\"");
            workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();//创建页
            HSSFRow row = null;
            HSSFCell cell = null;
            row = sheet.createRow((short) 0);
            for (int i = 0; title != null && i < title.length; i++) {
                cell = row.createCell(i);
                cell.setCellType(STRING);
                cell.setCellValue(new HSSFRichTextString(title[i]));
            }
            @SuppressWarnings("rawtypes")
            Map map = null;
            for (int i = 0; values != null && i < values.size(); i++) {
                row = sheet.createRow(i + 1);
                map = values.get(i);
                for (int i2 = 0; i2 < key.length; i2++) {
                    cell = row.createCell(i2);
                    cell.setCellType(STRING);
                    if (map.get(key[i2]) == null) {
                        cell.setCellValue(new HSSFRichTextString(""));
                    } else {
                        cell.setCellValue(new HSSFRichTextString(map.get(key[i2]).toString()));
                    }
                }
            }
            workbook.write(servletOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != servletOutputStream) {
                    servletOutputStream.flush();
                    servletOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 导出Map数据
     *
     * @param list      需要导出的数据list
     * @param sheetName 文件名
     * @param title     表头数组
     * @param keyArray  list里面对象的key值
     * @param widths    每个单元格的宽度
     * @param styleArr  每个单元格位置  -1 靠左  0 居中  1 靠右
     */
    public static void exportExcelMap(List<Map<String, Object>> list, String sheetName, String[] title, String[] keyArray, int[] widths, int[] styleArr) {
        //第一步创建workbook
        HSSFWorkbook wb = new HSSFWorkbook();
        //第二步创建sheet
        HSSFSheet sheet = wb.createSheet(sheetName);
        //设置宽度
        DggPoiUtil.setColumnWidth(sheet, widths);
        sheet.setDefaultRowHeight((short) (360));
        HSSFRow row_0 = sheet.createRow(0);
        for (int i = 0; i < title.length; i++) {
            Cell cell_0_i = row_0.createCell(i);
            cell_0_i.setCellType(STRING);
            cell_0_i.setCellStyle(DggPoiUtil.getCellStyle(wb, 0, true, HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex()));
            cell_0_i.setCellValue(title[i]);
        }
        CellStyle[] styleArray = new CellStyle[styleArr.length];
        for (int k = 0; k < styleArr.length; k++) {
            styleArray[k] = DggPoiUtil.getCellStyle(wb, styleArr[k], true);

        }
        for (int i = 0; i < list.size(); i++) {
            Row row_i = sheet.createRow(i + 1);
            for (int j = 0; j < keyArray.length; j++) {
                Cell cell_i_j = row_i.createCell(j);
                cell_i_j.setCellType(STRING);
                cell_i_j.setCellStyle(styleArray[j]);
                Map<String, Object> dataMap = list.get(i);
                Object value = dataMap.get("" + keyArray[j] + "");
                if (value == null) {
                    cell_i_j.setCellValue("");
                } else if (value instanceof BigDecimal && ((BigDecimal) value).intValue() != 0) {
                    BigDecimal amt = (BigDecimal) value;
                    //银行家进位法
                    BigDecimal as = amt.setScale(2, BigDecimal.ROUND_HALF_UP);
                    cell_i_j.setCellValue(DggAmountUtil.toString(as, ""));
                } else if (value instanceof Date) {
                    Date amt = (Date) value;
                    cell_i_j.setCellValue(DggDateUtil.formatDate(amt));
                } else {
                    cell_i_j.setCellValue(value.toString());
                }
            }
        }
        String fileName = sheetName + DggDateUtil.formatDate(fileNameDateFormat) + TYPE_XLS;
        try {
            HttpServletResponse response = DggRequestUtil.getResponse();
            OutputStream out = null;
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            out = response.getOutputStream();
            wb.write(out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出有json的那种
     *
     * @param list      需要导出的数据list
     * @param sheetName 文件名
     * @param title     表头数组
     * @param keyArray  list里面对象的key值
     * @param widths    每个单元格的宽度
     * @param styleArr  每个单元格位置  -1 靠左  0 居中  1 靠右
     */
    public static void exportExcelJson(List list, String sheetName, String[] title, String[] keyArray, int[] widths, int[] styleArr) {
        //第一步创建workbook
        HSSFWorkbook wb = new HSSFWorkbook();
        //第二步创建sheet
        HSSFSheet sheet = wb.createSheet(sheetName);
        //设置宽度
        DggPoiUtil.setColumnWidth(sheet, widths);
        sheet.setDefaultRowHeight((short) (360));
        HSSFRow row_0 = sheet.createRow(0);
        for (int i = 0; i < title.length; i++) {
            Cell cell_0_i = row_0.createCell(i);
            cell_0_i.setCellType(STRING);
            cell_0_i.setCellStyle(DggPoiUtil.getCellStyle(wb, 0, true, HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex()));
            cell_0_i.setCellValue(title[i]);
        }
        CellStyle[] styleArray = new CellStyle[styleArr.length];
        for (int k = 0; k < styleArr.length; k++) {
            styleArray[k] = DggPoiUtil.getCellStyle(wb, styleArr[k], true);
        }

        for (int i = 0; i < list.size(); i++) {
            Row row_i = sheet.createRow(i + 1);
            for (int j = 0; j < keyArray.length; j++) {
                Cell cell_i_j = row_i.createCell(j);
                cell_i_j.setCellType(STRING);
                cell_i_j.setCellStyle(styleArray[j]);
                Object value = DggBeanUtil.getFieldJson(list.get(i), keyArray[j]);
                if (value == null) {
                    cell_i_j.setCellValue("");
                } else if (value instanceof BigDecimal) {
                    BigDecimal amt = (BigDecimal) value;
                    //银行家进位法
                    BigDecimal as = amt.setScale(2, BigDecimal.ROUND_HALF_UP);
                    cell_i_j.setCellValue(DggAmountUtil.toString(as, ""));
                } else if (value instanceof Date) {
                    Date amt = (Date) value;
                    cell_i_j.setCellValue(DggDateUtil.formatDate(amt));
                } else {
                    cell_i_j.setCellValue(value.toString());
                }
            }
        }
        String fileName = sheetName + DggDateUtil.formatDate(fileNameDateFormat) + TYPE_XLS;
        try {
            HttpServletResponse response = DggRequestUtil.getResponse();
            OutputStream out = null;
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            out = response.getOutputStream();
            wb.write(out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param cell
     * @return String    返回类型
     * @Title: changeToString
     * @Description: 将Cell对象转换成String类型（导入时调用）
     */
    private static String cellValue(Cell cell) {
        String strCell = "";
        if (cell != null) {
            switch (cell.getCellType()) {
                case FORMULA:
                    cell.setCellType(STRING);
                    strCell = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        Date d = cell.getDateCellValue();
                        strCell = DggDateUtil.formatDate(d, "yyyy-MM-dd HH:mm");
                        //只有日期的转化
                        strCell = strCell.contains("00:00") ? strCell.substring(0, 10) : strCell;
                    } else {
                        strCell = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                case STRING:
                    strCell = cell.getStringCellValue();
                    break;
                default:
            }
        }
        return strCell.trim();
    }

    /**
     * @param row
     * @param cellIndex
     * @return String    返回类型
     * @Title: cellToString
     * @Description: 根据索引返回行中某个单元格的值
     */
    private static String cellValue(Row row, int cellIndex) {
        return cellValue((Cell) row.getCell(cellIndex));
    }

    /**
     * @param row
     * @param cellIndex
     * @return String    返回类型
     * @Title: cellValueStr
     * @Description: 读取单元格的文本，如果是数字将替换“.0”
     */
    private static String cellValueStr(Row row, int cellIndex) {
        String value = cellValue((Cell) row.getCell(cellIndex));
        if (!DggStringUtil.isEmpty(value)) {
            value = value.replace(".0", "");
        }
        return value;
    }

    /**
     * @param row
     * @param cellIndex
     * @param title
     * @return String    返回类型
     * @Title: cellTitleCheck
     * @Description: 检查Excel文件的某个列的列名是否与传入的title一致
     */
    private static String cellTitleCheck(Row row, int cellIndex, String title) {
        String result = "";
        String s = cellValue(row, cellIndex);
        if (!DggStringUtil.isEmpty(s) || !s.equals(title)) {
            result = "第" + (cellIndex + 1) + "列必须为[" + title + "]列<br/>";
        }
        return result;
    }

    /**
     * @param row        行对象
     * @param cellIndex  列索引
     * @param objErr     错误提示接受数组
     * @param errorIndex 错误提示保存位置
     * @param title      字段名
     * @return boolean    返回类型
     * @Title: cellValueBlank
     * @Description: Excel单元格非空检查
     */
    private static boolean cellValueNotBlank(Row row, int cellIndex, Object objErr[], int errorIndex, String title) {
        boolean result = true;
        String s = cellValue(row, cellIndex);
        objErr[cellIndex] = s.replace(".0", "");
        if (!DggStringUtil.isEmpty(s)) {
        } else {
            result = false;
            objErr[errorIndex] = objErr[errorIndex] + title + "不能为空\r\n";
        }
        return result;
    }

    /**
     * @param row        行对象
     * @param cellIndex  列索引
     * @param objErr     错误提示接受数组
     * @param errorIndex 错误提示保存位置
     * @param title      字段名
     * @return boolean    返回类型
     * @Title: cellValueBlank
     * @Description: Excel单元格非空检查(字符不包含小数)
     */
    private static boolean cellTextValueNotBlank(Row row, int cellIndex, Object objErr[], int errorIndex, String title) {
        boolean result = true;
        ;
        String s = cellValue(row, cellIndex);
        objErr[cellIndex] = s.replace(".0", "");
        if (!DggStringUtil.isEmpty(s)) {
        } else {
            result = false;
            objErr[errorIndex] = objErr[errorIndex] + title + "不能为空\r\n";
        }
        return result;
    }

    /**
     * @param cellValue
     * @param dbValue
     * @param objErr
     * @param errorIndex
     * @param title
     * @return boolean    返回类型
     * @Title: cellValueEqualsDbValue
     * @Description: 比较两个字符串的值，并记录错误信息
     */
    private static boolean cellValueEqualsDbValue(String cellValue, String dbValue, Object objErr[], int errorIndex, String title) {
        boolean result = true;
        if (!cellValue.equals(dbValue)) {
            result = false;
            objErr[errorIndex] = objErr[errorIndex] + title + "与数据库中数据不一致！\r\n";
        }
        return result;
    }

    /**
     * @param row        行对象
     * @param cellIndex  列索引
     * @param objErr     错误提示接受数组
     * @param errorIndex 错误提示保存位置
     * @param title      字段名
     * @return boolean    返回类型
     * @Title: cellValueNotBlankNumber
     * @Description: Excel单元格非空、电子邮件地址检查
     */
    private static boolean cellValueNotBlankEmail(Row row, int cellIndex, Object objErr[], int errorIndex, String title) {
        boolean result = true;
        String s = cellValue(row, cellIndex);
        objErr[cellIndex] = s;
        if (!DggStringUtil.isEmpty(s)) {
            if (DggRegexUtil.isEmail(s)) {
            } else {
                result = false;
                objErr[errorIndex] = objErr[errorIndex] + "必须为邮箱地址的" + title + "\r\n";
            }
        } else {
            result = false;
            objErr[errorIndex] = objErr[errorIndex] + title + "不能为空\r\n";
        }
        return result;
    }

    /**
     * @param row        行对象
     * @param cellIndex  列索引
     * @param objErr     错误提示接受数组
     * @param errorIndex 错误提示保存位置
     * @param title      字段名
     * @return boolean    返回类型
     * @Title: cellValueNotBlankMobile
     * @Description: Excel单元格非空、手机号码地址检查
     */
    private static boolean cellValueNotBlankMobile(Row row, int cellIndex, Object objErr[], int errorIndex, String title) {
        boolean result = true;
        String s = cellValue(row, cellIndex);
        BigDecimal db = new BigDecimal(s);//避免电话号码读出来为科学计数法
        s = db.toPlainString();
        objErr[cellIndex] = s + "";
        if (!DggStringUtil.isEmpty(s)) {
            if (DggRegexUtil.isMobileSimple(s)) {
            } else {
                result = false;
                objErr[errorIndex] = objErr[errorIndex] + "必须为手机号码的" + title + "\r\n";
            }
        } else {
            result = false;
            objErr[errorIndex] = objErr[errorIndex] + title + "不能为空\r\n";
        }
        return result;
    }

    private static Object getCellFormatValue(Cell cell) {
        Object cellValue = null;
        if (cell != null) {
            //判断cell类型
            switch (cell.getCellType()) {
                case NUMERIC: {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                }
                case FORMULA: {
                    //判断cell是否为日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        //转换为日期格式YYYY-mm-dd
                        cellValue = cell.getDateCellValue();
                    } else {
                        //数字
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case STRING: {
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }

    public static void main(String[] args) {
        Workbook wb = null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String, String>> list = null;
        String cellData = null;
        String filePath = "D:\\Documents\\Downloads\\gssalary.xlsx";
        String columns[] = {"id", "age", "product", "goods", "item"};
        wb = readExcel(filePath);
        if (wb != null) {
            //用来存放表中数据
            list = new ArrayList<Map<String, String>>();
            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rownum = sheet.getPhysicalNumberOfRows();
            //获取第一行
            row = sheet.getRow(0);
            //获取最大列数
            int colnum = row.getPhysicalNumberOfCells();
            for (int i = 1; i < rownum; i++) {
                Map<String, String> map = new LinkedHashMap<String, String>();
                row = sheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < colnum; j++) {
                        cellData = (String) getCellFormatValue(row.getCell(j));
                        map.put(columns[j], cellData);
                    }
                } else {
                    break;
                }
                list.add(map);
            }
        }
        //遍历解析出来的list
        for (Map<String, String> map : list) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.print(entry.getKey() + ":" + entry.getValue() + ",");
            }
            System.out.println();
        }

    }
}