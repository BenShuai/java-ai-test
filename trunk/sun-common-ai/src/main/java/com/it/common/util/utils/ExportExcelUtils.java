package com.it.common.util.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.util.CollectionUtils;

import javax.swing.plaf.synth.Region;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName:  excel导出工具类
 * @Description:  支持.xls和.xlsx文件
 * @Author: xiaoao
 * @Date: 2019/11/7
 * @Version: 1.0
 **/
public class ExportExcelUtils<T>{

    // 2007 版本以上 最大支持1048576行
    public  final static String  EXCEl_FILE_2007 = "2007";
    // 2003 版本 最大支持65536 行
    public  final static String  EXCEL_FILE_2003 = "2003";

    /**
     * <p>
     * 导出无头部标题行Excel <br>
     * 时间格式默认：yyyy-MM-dd hh:mm:ss <br>
     * </p>
     *
     * @param title 表格标题
     * @param dataset 数据集合
     * @param out 输出流
     * @param version 2003 或者 2007，不传时默认生成2003版本
     */
    public void exportExcel(String title, Collection<T> dataset, OutputStream out, String version) {
        if(StringUtils.isEmpty(version) || EXCEL_FILE_2003.equals(version.trim())){
            exportExcel2003(title, null, dataset, out, "yyyy-MM-dd HH:mm:ss");
        }else{
            exportExcel2007(title, null, dataset, out, "yyyy-MM-dd HH:mm:ss");
        }
    }

    /**
     * <p>
     * 导出带有头部标题行的Excel <br>
     * 时间格式默认：yyyy-MM-dd hh:mm:ss <br>
     * </p>
     *
     * @param title 表格标题
     * @param headers 头部标题集合
     * @param dataset 数据集合
     * @param out 输出流
     * @param version 2003 或者 2007，不传时默认生成2003版本
     */
    public void exportExcel(String title,String[] headers, Collection<T> dataset, OutputStream out,String version) {
        if(StringUtils.isBlank(version) || EXCEL_FILE_2003.equals(version.trim())){
            exportExcel2003(title, headers, dataset, out, "yyyy-MM-dd HH:mm:ss");
        }else {
            exportExcel2007(title, headers, dataset, out, "yyyy-MM-dd HH:mm:ss");
        }
    }

    /**
     * <p>
     * IDS品牌报表总表导出带有头部标题行的Excel <br>
     * 时间格式默认：yyyy-MM-dd hh:mm:ss <br>
     * </p>
     *
     * @param title 表格标题
     * @param headers 头部标题集合
     * @param dataset 数据集合
     * @param out 输出流
     */
    public void idsExportExcel(String title,String[] headers,Collection<T> columnDataset, Collection<T> dataset, OutputStream out,String projectName,String[] columnHj,String[] brandHj,String startTime,String overTime) {

            exportExcel20072(title, headers, columnDataset,dataset, out, "yyyy-MM-dd HH:mm:ss",projectName,columnHj,brandHj,startTime,overTime);
    }

    /**
     * <p>
     * IDS品牌报表总表Excel导出方法,利用反射机制遍历对象的所有字段，将数据写入Excel文件中 <br>
     * 此版本生成2007以上版本的文件 (文件后缀：xlsx)
     * </p>
     *
     * @param title
     *            表格标题名
     * @param headers
     *            表格头部标题集合
     * @param dataset
     *            需要显示的数据集合,集合中一定要放置符合JavaBean风格的类的对象。此方法支持的
     *            JavaBean属性的数据类型有基本数据类型及String,Date
     * @param out
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern
     *            如果有时间数据，设定输出格式。默认为"yyyy-MM-dd hh:mm:ss"
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void exportExcel20072(String title, String[] headers,Collection<T> columnDataset, Collection<T> dataset, OutputStream out, String pattern,String projectName,String[] columnHj,String[] brandHj,String startTime,String overTime) {
        // 声明一个工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 生成一个表格
        XSSFSheet sheet = workbook.createSheet(title);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(1,true);
        // 生成一个样式
        XSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(new XSSFColor(new Color(93,105,189)));
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setTopBorderColor(new XSSFColor(new Color(215,219,240)));
        style.setLeftBorderColor(new XSSFColor(new Color(215,219,240)));
        style.setRightBorderColor(new XSSFColor(new Color(215,219,240)));
        style.setBottomBorderColor(new XSSFColor(new Color(215,219,240)));
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        XSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setColor(new XSSFColor(Color.white));
        font.setFontHeightInPoints((short) 10);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        XSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(new XSSFColor(java.awt.Color.WHITE));
        style2.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style2.setTopBorderColor(new XSSFColor(new Color(215,219,240)));
        style2.setLeftBorderColor(new XSSFColor(new Color(215,219,240)));
        style2.setRightBorderColor(new XSSFColor(new Color(215,219,240)));
        style2.setBottomBorderColor(new XSSFColor(new Color(215,219,240)));
        style2.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        XSSFFont font2 = workbook.createFont();
        font2.setFontName("华文细黑");
        font2.setFontHeightInPoints((short) 10);
        font2.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);
        //合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,10));
        sheet.addMergedRegion(new CellRangeAddress(1,1,0,10));
        sheet.addMergedRegion(new CellRangeAddress(2,2,0,10));
        //样式3
        XSSFCellStyle style3 = workbook.createCellStyle();
        style3.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style3.setFillForegroundColor(new XSSFColor(new Color(62,69,140)));
        //字体3
        XSSFFont font3 = workbook.createFont();
        font3.setFontName("黑体");
        font3.setColor(new XSSFColor(Color.white));
        font3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font3.setFontHeightInPoints((short)20);
        style3.setFont(font3);
        //样式4
        XSSFCellStyle style4 = workbook.createCellStyle();
        style4.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style4.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style4.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style4.setFillForegroundColor(new XSSFColor(new Color(93,105,189)));
        //字体4
        XSSFFont font4 = workbook.createFont();
        font4.setFontName("黑体");
        font4.setColor(new XSSFColor(Color.white));
        font4.setFontHeightInPoints((short)16);
        style4.setFont(font4);
        //样式5
        XSSFCellStyle style5 = workbook.createCellStyle();
        style5.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style5.setFillForegroundColor(new XSSFColor(new Color(93,105,189)));
        //字体5
        XSSFFont font5 = workbook.createFont();
        font5.setFontName("黑体");
        font5.setColor(new XSSFColor(Color.white));
        font5.setFontHeightInPoints((short)10);
        style5.setFont(font5);
        //样式6
        XSSFCellStyle style6 = workbook.createCellStyle();
        style6.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style6.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style6.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style6.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style6.setTopBorderColor(new XSSFColor(new Color(215,219,240)));
        style6.setLeftBorderColor(new XSSFColor(new Color(215,219,240)));
        style6.setRightBorderColor(new XSSFColor(new Color(215,219,240)));
        style6.setBottomBorderColor(new XSSFColor(new Color(215,219,240)));
        style6.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style6.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style6.setFillForegroundColor(new XSSFColor(new Color(0,237,219)));
        //字体6
        XSSFFont font6 = workbook.createFont();
        font6.setFontName("华文细黑");
        font6.setFontHeightInPoints((short)10);
        style6.setFont(font6);
        //样式7
        XSSFCellStyle style7 = workbook.createCellStyle();
        style7.setFillForegroundColor(new XSSFColor(new Color(237,239,249)));
        style7.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style7.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style7.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style7.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style7.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style7.setTopBorderColor(new XSSFColor(new Color(215,219,240)));
        style7.setLeftBorderColor(new XSSFColor(new Color(215,219,240)));
        style7.setRightBorderColor(new XSSFColor(new Color(215,219,240)));
        style7.setBottomBorderColor(new XSSFColor(new Color(215,219,240)));
        style7.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style7.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        style7.setFont(font2);

        // 产生表格标题行
        XSSFRow row = sheet.createRow(3);
        XSSFCell cellHeader;
        for (int i = 0; i < headers.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style);
            cellHeader.setCellValue(new XSSFRichTextString(headers[i]));
        }
        //第一行
        row = sheet.createRow(0);
        cellHeader=row.createCell(0);
        cellHeader.setCellValue("IDS");
        cellHeader.setCellStyle(style3);

        //第二行
        row = sheet.createRow(1);
        cellHeader=row.createCell(0);
        cellHeader.setCellValue(projectName+"内容服务报表");
        cellHeader.setCellStyle(style4);
        //第三行
        row = sheet.createRow(2);
        cellHeader=row.createCell(0);
        cellHeader.setCellValue(startTime+"----"+overTime);
        cellHeader.setCellStyle(style5);

        // 遍历集合数据，产生数据行
        if(dataset == null || dataset.isEmpty()){
            System.out.println("没有数据");
            return;
        }
        Iterator<T> it = dataset.iterator();
        int index = 3;
        //序号
        Integer num=1;
        T t;
        Field[] fields;
        Field field;
        XSSFRichTextString richString;
        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
        Matcher matcher;
        String fieldName;
        String getMethodName;
        XSSFCell cell;
        Class tCls;
        Method getMethod;
        Object value;
        String textValue;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            t = (T) it.next();
            // 利用反射，根据JavaBean属性的先后顺序，动态调用getXxx()方法得到属性值
            fields = t.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                cell = row.createCell(i);
                if(index%2!=0){
                    cell.setCellStyle(style7);
                }else {
                    cell.setCellStyle(style2);
                }
                if(i==0){
                    cell.setCellValue(num.toString());
                    num++;
                }else {
                    field = fields[i];
                    fieldName = field.getName();
                    getMethodName = "get" + fieldName.substring(0, 1).toUpperCase()
                            + fieldName.substring(1);
                    try {
                        tCls = t.getClass();
                        getMethod = tCls.getMethod(getMethodName, new Class[] {});
                        value = getMethod.invoke(t, new Object[] {});
                        // 判断值的类型后进行强制类型转换
                        textValue = null;
                        if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof Float) {
                            textValue = String.valueOf((Float) value);
                            cell.setCellValue(textValue);
                        } else if (value instanceof Double) {
                            textValue = String.valueOf((Double) value);
                            cell.setCellValue(textValue);
                        } else if (value instanceof Long) {
                            cell.setCellValue((Long) value);
                        }
                        if (value instanceof Boolean) {
                            textValue = "是";
                            if (!(Boolean) value) {
                                textValue = "否";
                            }
                        } else if (value instanceof Date) {
                            textValue = sdf.format((Date) value);
                        } else {
                            // 其它数据类型都当作字符串简单处理
                            if (value != null) {
                                textValue = value.toString();
                            }
                        }
                        if (textValue != null) {
                            matcher = p.matcher(textValue);
                            if (matcher.matches()) {
                                // 是数字当作double处理
                                cell.setCellValue(Double.parseDouble(textValue));
                            } else {
                                richString = new XSSFRichTextString(textValue);
                                cell.setCellValue(richString);
                            }
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } finally {
                        // 清理资源
                    }
                }
            }
            if(index%2!=0){
                cell = row.createCell(7);
                cell.setCellStyle(style7);
                cell = row.createCell(8);
                cell.setCellStyle(style7);
                cell = row.createCell(9);
                cell.setCellStyle(style7);
                cell = row.createCell(10);
                cell.setCellStyle(style7);
            }else {
                cell = row.createCell(7);
                cell.setCellStyle(style2);
                cell = row.createCell(8);
                cell.setCellStyle(style2);
                cell = row.createCell(9);
                cell.setCellStyle(style2);
                cell = row.createCell(10);
                cell.setCellStyle(style2);
            }

        }

        //brand合计
        row = sheet.createRow(index+1);
        cellHeader=row.createCell(0);
        cellHeader.setCellValue(num.toString());
        cellHeader.setCellStyle(style6);
        for (int i = 1; i <= brandHj.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style6);
            cellHeader.setCellValue(new XSSFRichTextString(brandHj[i-1]));
        }

        //栏目
        row = sheet.createRow(index+2);
        sheet.addMergedRegion(new CellRangeAddress(index+2,index+2,0,10));
        cellHeader=row.createCell(0);
        cellHeader.setCellValue("栏目");
        cellHeader.setCellStyle(style4);

        // 遍历集合数据，产生数据行
        if(columnDataset == null || columnDataset.isEmpty()){
            System.out.println("没有数据");
            return;
        }
        it = columnDataset.iterator();
        index =index+2;
        //序号
        num=1;
        T columnT;
        Field[] cFields;
        Field cField;
        XSSFRichTextString cRichString;
        Pattern columnP = Pattern.compile("^//d+(//.//d+)?$");
        Matcher columnMatcher;
        String columnFieldName;
        String columnGetMethodName;
        XSSFCell columnCell;
        Class columnTCls;
        Method columnGetMethod;
        Object columnValue;
        String columnTextValue;
        SimpleDateFormat columnSdf = new SimpleDateFormat(pattern);
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            columnT = (T) it.next();
            // 利用反射，根据JavaBean属性的先后顺序，动态调用getXxx()方法得到属性值
            cFields = columnT.getClass().getDeclaredFields();
            for (int i = 0; i < cFields.length; i++) {
                columnCell = row.createCell(i);
                if(index%2!=0){
                    columnCell.setCellStyle(style7);
                }else {
                    columnCell.setCellStyle(style2);
                }
                if(i==0){
                    columnCell.setCellValue(num.toString());
                    num++;
                }else {
                    cField = cFields[i];
                    columnFieldName = cField.getName();
                    columnGetMethodName = "get" + columnFieldName.substring(0, 1).toUpperCase()
                            + columnFieldName.substring(1);
                    try {
                        columnTCls = columnT.getClass();
                        columnGetMethod = columnTCls.getMethod(columnGetMethodName, new Class[] {});
                        columnValue = columnGetMethod.invoke(columnT, new Object[] {});
                        // 判断值的类型后进行强制类型转换
                        columnTextValue = null;
                        if (columnValue instanceof Integer) {
                            columnCell.setCellValue((Integer) columnValue);
                        } else if (columnValue instanceof Float) {
                            columnTextValue = String.valueOf((Float) columnValue);
                            columnCell.setCellValue(columnTextValue);
                        } else if (columnValue instanceof Double) {
                            columnTextValue = String.valueOf((Double) columnValue);
                            columnCell.setCellValue(columnTextValue);
                        } else if (columnValue instanceof Long) {
                            columnCell.setCellValue((Long) columnValue);
                        }
                        if (columnValue instanceof Boolean) {
                            columnTextValue = "是";
                            if (!(Boolean) columnValue) {
                                columnTextValue = "否";
                            }
                        } else if (columnValue instanceof Date) {
                            columnTextValue = columnSdf.format((Date) columnValue);
                        } else {
                            // 其它数据类型都当作字符串简单处理
                            if (columnValue != null) {
                                columnTextValue = columnValue.toString();
                            }
                        }
                        if (columnTextValue != null) {
                            columnMatcher = columnP.matcher(columnTextValue);
                            if (columnMatcher.matches()) {
                                // 是数字当作double处理
                                columnCell.setCellValue(Double.parseDouble(columnTextValue));
                            } else {
                                cRichString = new XSSFRichTextString(columnTextValue);
                                columnCell.setCellValue(cRichString);
                            }
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } finally {
                        // 清理资源
                    }
                }
            }
            if(index%2!=0){
                cell = row.createCell(7);
                cell.setCellStyle(style7);
                cell = row.createCell(8);
                cell.setCellStyle(style7);
                cell = row.createCell(9);
                cell.setCellStyle(style7);
                cell = row.createCell(10);
                cell.setCellStyle(style7);
            }else {
                cell = row.createCell(7);
                cell.setCellStyle(style2);
                cell = row.createCell(8);
                cell.setCellStyle(style2);
                cell = row.createCell(9);
                cell.setCellStyle(style2);
                cell = row.createCell(10);
                cell.setCellStyle(style2);
            }
        }

        //brand合计
        row = sheet.createRow(index+1);
        cellHeader=row.createCell(0);
        cellHeader.setCellValue(num.toString());
        cellHeader.setCellStyle(style6);
        for (int i = 1; i <= columnHj.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style6);
            cellHeader.setCellValue(new XSSFRichTextString(columnHj[i-1]));
        }

        try {
            workbook.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * 通用Excel导出方法,利用反射机制遍历对象的所有字段，将数据写入Excel文件中 <br>
     * 此版本生成2007以上版本的文件 (文件后缀：xlsx)
     * </p>
     *
     * @param title
     *            表格标题名
     * @param headers
     *            表格头部标题集合
     * @param dataset
     *            需要显示的数据集合,集合中一定要放置符合JavaBean风格的类的对象。此方法支持的
     *            JavaBean属性的数据类型有基本数据类型及String,Date
     * @param out
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern
     *            如果有时间数据，设定输出格式。默认为"yyyy-MM-dd hh:mm:ss"
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void exportExcel2007(String title, String[] headers, Collection<T> dataset, OutputStream out, String pattern) {
        // 声明一个工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 生成一个表格
        XSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(20);
        // 生成一个样式
        XSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(new XSSFColor(java.awt.Color.gray));
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        XSSFFont font = workbook.createFont();
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("宋体");
        font.setColor(new XSSFColor(java.awt.Color.BLACK));
        font.setFontHeightInPoints((short) 11);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        XSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(new XSSFColor(java.awt.Color.WHITE));
        style2.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style2.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        XSSFFont font2 = workbook.createFont();
        font2.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 产生表格标题行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cellHeader;
        for (int i = 0; i < headers.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style);
            cellHeader.setCellValue(new XSSFRichTextString(headers[i]));
        }

        // 遍历集合数据，产生数据行
        if(dataset == null || dataset.isEmpty()){
            System.out.println("没有数据");
            return;
        }
        Iterator<T> it = dataset.iterator();
        int index = 0;
        T t;
        Field[] fields;
        Field field;
        XSSFRichTextString richString;
        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
        Matcher matcher;
        String fieldName;
        String getMethodName;
        XSSFCell cell;
        Class tCls;
        Method getMethod;
        Object value;
        String textValue;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            t = (T) it.next();
            // 利用反射，根据JavaBean属性的先后顺序，动态调用getXxx()方法得到属性值
            fields = t.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(style2);
                field = fields[i];
                fieldName = field.getName();
                getMethodName = "get" + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                try {
                    tCls = t.getClass();
                    getMethod = tCls.getMethod(getMethodName, new Class[] {});
                    value = getMethod.invoke(t, new Object[] {});
                    // 判断值的类型后进行强制类型转换
                    textValue = null;
                    if (value instanceof Integer) {
                        cell.setCellValue((Integer) value);
                    } else if (value instanceof Float) {
                        textValue = String.valueOf((Float) value);
                        cell.setCellValue(textValue);
                    } else if (value instanceof Double) {
                        textValue = String.valueOf((Double) value);
                        cell.setCellValue(textValue);
                    } else if (value instanceof Long) {
                        cell.setCellValue((Long) value);
                    }
                    if (value instanceof Boolean) {
                        textValue = "是";
                        if (!(Boolean) value) {
                            textValue = "否";
                        }
                    } else if (value instanceof Date) {
                        textValue = sdf.format((Date) value);
                    } else {
                        // 其它数据类型都当作字符串简单处理
                        if (value != null) {
                            textValue = value.toString();
                        }
                    }
                    if (textValue != null) {
                        matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            // 是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                        } else {
                            richString = new XSSFRichTextString(textValue);
                            cell.setCellValue(richString);
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } finally {
                    // 清理资源
                }
            }
        }
        try {
            workbook.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * <p>
     * 通用Excel导出方法,利用反射机制遍历对象的所有字段，将数据写入Excel文件中 <br>
     * 此方法生成2003版本的excel,文件名后缀：xls <br>
     * </p>
     *
     * @param title
     *            表格标题名
     * @param headers
     *            表格头部标题集合
     * @param dataset
     *            需要显示的数据集合,集合中一定要放置符合JavaBean风格的类的对象。此方法支持的
     *            JavaBean属性的数据类型有基本数据类型及String,Date
     * @param out
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern
     *            如果有时间数据，设定输出格式。默认为"yyyy-MM-dd hh:mm:ss"
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void exportExcel2003(String title, String[] headers, Collection<T> dataset, OutputStream out, String pattern) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(20);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("宋体");
        font.setColor(HSSFColor.WHITE.index);
        font.setFontHeightInPoints((short) 11);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.WHITE.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        HSSFCell cellHeader;
        for (int i = 0; i < headers.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style);
            cellHeader.setCellValue(new HSSFRichTextString(headers[i]));
        }

        // 遍历集合数据，产生数据行
        if(dataset == null || dataset.isEmpty()){
            System.out.println("没有数据");
            return;
        }
        Iterator<T> it = dataset.iterator();
        int index = 0;
        T t;
        Field[] fields;
        Field field;
        HSSFRichTextString richString;
        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
        Matcher matcher;
        String fieldName;
        String getMethodName;
        HSSFCell cell;
        Class tCls;
        Method getMethod;
        Object value;
        String textValue;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            t = (T) it.next();
            // 利用反射，根据JavaBean属性的先后顺序，动态调用getXxx()方法得到属性值
            fields = t.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(style2);
                field = fields[i];
                fieldName = field.getName();
                getMethodName = "get" + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                try {
                    tCls = t.getClass();
                    getMethod = tCls.getMethod(getMethodName, new Class[] {});
                    value = getMethod.invoke(t, new Object[] {});
                    // 判断值的类型后进行强制类型转换
                    textValue = null;
                    if (value instanceof Integer) {
                        cell.setCellValue((Integer) value);
                    } else if (value instanceof Float) {
                        textValue = String.valueOf((Float) value);
                        cell.setCellValue(textValue);
                    } else if (value instanceof Double) {
                        textValue = String.valueOf((Double) value);
                        cell.setCellValue(textValue);
                    } else if (value instanceof Long) {
                        cell.setCellValue((Long) value);
                    }
                    if (value instanceof Boolean) {
                        textValue = "是";
                        if (!(Boolean) value) {
                            textValue = "否";
                        }
                    } else if (value instanceof Date) {
                        textValue = sdf.format((Date) value);
                    } else {
                        // 其它数据类型都当作字符串简单处理
                        if (value != null) {
                            textValue = value.toString();
                        }
                    }
                    if (textValue != null) {
                        matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            // 是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                        } else {
                            richString = new HSSFRichTextString(textValue);
                            cell.setCellValue(richString);
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } finally {
                    // 清理资源
                }
            }
        }
        try {
            workbook.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * 导出IDS设备服务报表Excel导出方法,利用反射机制遍历对象的所有字段，将数据写入Excel文件中 <br>
     * 此版本生成2007以上版本的文件 (文件后缀：xlsx)
     * </p>
     *
     * @param title
     *            表格标题名
     * @param headers
     *            表格头部标题集合
     * @param dataset
     *            需要显示的数据集合,集合中一定要放置符合JavaBean风格的类的对象。此方法支持的
     *            JavaBean属性的数据类型有基本数据类型及String,Date
     * @param out
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern
     *            如果有时间数据，设定输出格式。默认为"yyyy-MM-dd hh:mm:ss"
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void deviceExportExcel2007(String title, String[] headers,Collection<T> dataset, OutputStream out, String pattern,String projectName,String startTime,String overTime,String[] brandHj) {
        // 声明一个工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 生成一个表格
        XSSFSheet sheet = workbook.createSheet(title);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(1,true);
        // 生成一个样式
        XSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(new XSSFColor(new Color(93,105,189)));
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setTopBorderColor(new XSSFColor(new Color(215,219,240)));
        style.setLeftBorderColor(new XSSFColor(new Color(215,219,240)));
        style.setRightBorderColor(new XSSFColor(new Color(215,219,240)));
        style.setBottomBorderColor(new XSSFColor(new Color(215,219,240)));
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        XSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setColor(new XSSFColor(Color.white));
        font.setFontHeightInPoints((short) 10);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        XSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(new XSSFColor(java.awt.Color.WHITE));
        style2.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style2.setTopBorderColor(new XSSFColor(new Color(215,219,240)));
        style2.setLeftBorderColor(new XSSFColor(new Color(215,219,240)));
        style2.setRightBorderColor(new XSSFColor(new Color(215,219,240)));
        style2.setBottomBorderColor(new XSSFColor(new Color(215,219,240)));
        style2.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        XSSFFont font2 = workbook.createFont();
        font2.setFontName("华文细黑");
        font2.setFontHeightInPoints((short) 10);
        font2.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);
        //合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,16));
        sheet.addMergedRegion(new CellRangeAddress(1,1,0,16));
        sheet.addMergedRegion(new CellRangeAddress(2,2,0,16));
        //样式3
        XSSFCellStyle style3 = workbook.createCellStyle();
        style3.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style3.setFillForegroundColor(new XSSFColor(new Color(62,69,140)));
        //字体3
        XSSFFont font3 = workbook.createFont();
        font3.setFontName("黑体");
        font3.setColor(new XSSFColor(Color.white));
        font3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font3.setFontHeightInPoints((short)20);
        style3.setFont(font3);
        //样式4
        XSSFCellStyle style4 = workbook.createCellStyle();
        style4.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style4.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style4.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style4.setFillForegroundColor(new XSSFColor(new Color(93,105,189)));
        //字体4
        XSSFFont font4 = workbook.createFont();
        font4.setFontName("黑体");
        font4.setColor(new XSSFColor(Color.white));
        font4.setFontHeightInPoints((short)16);
        style4.setFont(font4);
        //样式5
        XSSFCellStyle style5 = workbook.createCellStyle();
        style5.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style5.setFillForegroundColor(new XSSFColor(new Color(93,105,189)));
        //字体5
        XSSFFont font5 = workbook.createFont();
        font5.setFontName("黑体");
        font5.setColor(new XSSFColor(Color.white));
        font5.setFontHeightInPoints((short)10);
        style5.setFont(font5);
        //样式6
        XSSFCellStyle style6 = workbook.createCellStyle();
        style6.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style6.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style6.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style6.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style6.setTopBorderColor(new XSSFColor(new Color(215,219,240)));
        style6.setLeftBorderColor(new XSSFColor(new Color(215,219,240)));
        style6.setRightBorderColor(new XSSFColor(new Color(215,219,240)));
        style6.setBottomBorderColor(new XSSFColor(new Color(215,219,240)));
        style6.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style6.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style6.setFillForegroundColor(new XSSFColor(new Color(0,237,219)));
        //字体6
        XSSFFont font6 = workbook.createFont();
        font6.setFontName("华文细黑");
        font6.setFontHeightInPoints((short)10);
        style6.setFont(font6);
        //样式7
        XSSFCellStyle style7 = workbook.createCellStyle();
        style7.setFillForegroundColor(new XSSFColor(new Color(237,239,249)));
        style7.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style7.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style7.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style7.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style7.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style7.setTopBorderColor(new XSSFColor(new Color(215,219,240)));
        style7.setLeftBorderColor(new XSSFColor(new Color(215,219,240)));
        style7.setRightBorderColor(new XSSFColor(new Color(215,219,240)));
        style7.setBottomBorderColor(new XSSFColor(new Color(215,219,240)));
        style7.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style7.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        style7.setFont(font2);

        // 产生表格标题行
        XSSFRow row = sheet.createRow(3);
        XSSFCell cellHeader;
        for (int i = 0; i < headers.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style);
            cellHeader.setCellValue(new XSSFRichTextString(headers[i]));
        }
        //第一行
        row = sheet.createRow(0);
        cellHeader=row.createCell(0);
        cellHeader.setCellValue("IDS");
        cellHeader.setCellStyle(style3);

        //第二行
        row = sheet.createRow(1);
        cellHeader=row.createCell(0);
        cellHeader.setCellValue(projectName+"设备服务报表");
        cellHeader.setCellStyle(style4);
        //第三行
        row = sheet.createRow(2);
        cellHeader=row.createCell(0);
        cellHeader.setCellValue(startTime+"----"+overTime);
        cellHeader.setCellStyle(style5);

        // 遍历集合数据，产生数据行
        if(dataset == null || dataset.isEmpty()){
            System.out.println("没有数据");
            return;
        }
        Iterator<T> it = dataset.iterator();
        int index = 3;
        //序号
        Integer num=1;
        T t;
        Field[] fields;
        Field field;
        XSSFRichTextString richString;
        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
        Matcher matcher;
        String fieldName;
        String getMethodName;
        XSSFCell cell;
        Class tCls;
        Method getMethod;
        Object value;
        String textValue;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            t = (T) it.next();
            // 利用反射，根据JavaBean属性的先后顺序，动态调用getXxx()方法得到属性值
            fields = t.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                cell = row.createCell(i);
                if(index%2!=0){
                    cell.setCellStyle(style7);
                }else {
                    cell.setCellStyle(style2);
                }
                if(i==0){
                    cell.setCellValue(num.toString());
                    num++;
                }else {
                    field = fields[i];
                    fieldName = field.getName();
                    getMethodName = "get" + fieldName.substring(0, 1).toUpperCase()
                            + fieldName.substring(1);
                    try {
                        tCls = t.getClass();
                        getMethod = tCls.getMethod(getMethodName, new Class[] {});
                        value = getMethod.invoke(t, new Object[] {});
                        // 判断值的类型后进行强制类型转换
                        textValue = null;
                        if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof Float) {
                            textValue = String.valueOf((Float) value);
                            cell.setCellValue(textValue);
                        } else if (value instanceof Double) {
                            textValue = String.valueOf((Double) value);
                            cell.setCellValue(textValue);
                        } else if (value instanceof Long) {
                            cell.setCellValue((Long) value);
                        }
                        if (value instanceof Boolean) {
                            textValue = "是";
                            if (!(Boolean) value) {
                                textValue = "否";
                            }
                        } else if (value instanceof Date) {
                            textValue = sdf.format((Date) value);
                        } else {
                            // 其它数据类型都当作字符串简单处理
                            if (value != null) {
                                textValue = value.toString();
                            }
                        }
                        if (textValue != null) {
                            matcher = p.matcher(textValue);
                            if (matcher.matches()) {
                                // 是数字当作double处理
                                cell.setCellValue(Double.parseDouble(textValue));
                            } else {
                                richString = new XSSFRichTextString(textValue);
                                cell.setCellValue(richString);
                            }
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } finally {
                        // 清理资源
                    }
                }
            }
        }

        //合计
        row = sheet.createRow(index+1);
        cellHeader=row.createCell(0);
        cellHeader.setCellValue(num.toString());
        cellHeader.setCellStyle(style6);
        for (int i = 1; i <= brandHj.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style6);
            cellHeader.setCellValue(new XSSFRichTextString(brandHj[i-1]));
        }
        try {
            workbook.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
