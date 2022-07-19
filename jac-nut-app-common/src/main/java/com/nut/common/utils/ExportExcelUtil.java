package com.nut.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;

/**
 * @Description: POI生成Excel工具类
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.utils
 * @Author: yzl
 * @CreateTime: 2021-06-17 09:36
 * @Version: 1.0
 */
@Slf4j
public class ExportExcelUtil {

    protected String uploadUrl = null;

    protected SXSSFWorkbook wb = null;

    protected Sheet sheet = null;

    public ExportExcelUtil(String url)
    {
        this.wb = new SXSSFWorkbook();
        this.sheet = wb.createSheet();
        this.uploadUrl = url;
    }

    /**
     * 创建通用EXCEL头部
     *
     * @param headString 头部显示的字符
     * @param colSum 该报表的列数
     */
    public void createNormalHead(String headString, int colSum)
    {

        Row row = sheet.createRow(0);

        // 设置第一行
        Cell cell = row.createCell(0);
        row.setHeight((short) 400);

        // 定义单元格为字符串类型
        cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        cell.setCellValue(new XSSFRichTextString(headString));

        // 指定合并区域
        sheet.addMergedRegion(new CellRangeAddress(0, (short)0, 0, (short)colSum));
        CellStyle cellStyle = wb.createCellStyle();

        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐
        cellStyle.setWrapText(true);// 指定单元格自动换行

        // 设置单元格字体
        Font font = wb.createFont();
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("宋体");
        font.setFontHeight((short)300);
        cellStyle.setFont(font);

        cell.setCellStyle(cellStyle);
    }

    /**
     * 创建内容单元格
     *
     //     * @param wb XSSFWorkbook
     * @param row XSSFRow
     * @param col short型的列索引
    //     * @param align 对齐方式
     * @param val 列值
     */
    public void cteateCell(Row row, int col,String val,CellStyle cellstyle)
    {
        Cell cell = row.createCell(col);
        cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        cell.setCellValue(new XSSFRichTextString(val));
        cell.setCellStyle(cellstyle);
    }

    public File getExcelFile(File file)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            wb.write(fos);
        }
        catch (FileNotFoundException e)
        {
            log.error("excel file generate fail !", e);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 通用生成Excel方法，已定义列头，样式
     *
     * @param columnArr 列标题数组
     * @param width 宽度,3000
     * @param dataArr 表格数据数组
     * @param headTitle 第一行标题
     * @param fileName 文件名称
     * @return upload地址
     */
    public String commonGenerateExcel(String[] columnArr, int width, String[][] dataArr, String headTitle,
                                      String fileName)
    {
        String httpResult = null;

        int rowaccess=100;//内存中缓存记录行数

        try
        {
            // Excel列数
            int column = columnArr.length;

            /*keep 100 rowsin memory,exceeding rows will be flushed to disk*/
            wb = new SXSSFWorkbook(rowaccess);
            sheet = wb.createSheet();

            // 给工作表列定义列宽(实际应用自己更改列数)
            for (int i = 0; i < column; i++)
            {
                sheet.autoSizeColumn(i);
            }
            // 创建单元格样式
            CellStyle cellStyle = wb.createCellStyle();
            // 指定单元格居中对齐
            cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            // 指定单元格垂直居中对齐
            cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
            // 指定当单元格内容显示不下时自动换行
            cellStyle.setWrapText(true);
            // 设置单元格字体
            Font font = wb.createFont();
            font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
            font.setFontName("微软雅黑");
            font.setFontHeight((short) 200);
            cellStyle.setFont(font);

            int startRowNo = 0;
            if (StringUtil.isNotEmpty(headTitle)) {
                // 创建报表头部
                this.createNormalHead(headTitle, column);
                startRowNo = 1;
            }
            log.info("******************************生成Excel（createExcel）-S**************************************");
            Row cloumnRow = sheet.createRow(startRowNo);
            CellStyle cellstyle = wb.createCellStyle();
            cellstyle.setAlignment(XSSFCellStyle.ALIGN_CENTER_SELECTION);
            for (int i = 0; i < column; i++)
            {
                this.cteateCell(cloumnRow, i, columnArr[i],cellstyle);
            }
            cloumnRow = null;
            // 循环创建中间的单元格的各项的值
            for (int i = 0; i < dataArr.length; i++)
            {
                Row row = sheet.createRow(i + 1 + startRowNo);
                for (int j = 0; j < dataArr[i].length; j++)
                {
                    this.cteateCell(row, j, dataArr[i][j],cellstyle);
                }
                row = null;

                //每当行数达到设置的值就刷新数据到硬盘,以清理内存
                if(i % rowaccess == 0){
                    ((SXSSFSheet)sheet).flushRows();
                }
            }
            log.debug(fileName + " generate excel success!");
            // httpResult = HttpUtil.uploadFile(uploadUrl, this.getByteByFile(fileName), fileName,
            // "application/vnd.ms-excel", UUID.randomUUID().toString(), "create");
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<String, Object>();
            param.add("name", fileName);
            param.add("file", new FileSystemResource(getExcelFile(new File(fileName))));
            log.info("******************************生成Excel（createExcel）-E**************************************");
            RestTemplate restTemplate = new RestTemplate();
            httpResult = restTemplate.postForObject(uploadUrl, param, String.class);
            //httpResult = httpResult + "?name="+fileName;
            System.out.println("httpResult======="+httpResult);
        }
        catch (Exception e)
        {
            log.error(" generate excel fail!", e);
            return null;
        }

        return httpResult;
    }

    /**
     * 通用生成Excel方法，已定义列头，样式
     *
     * @param parm  在 excel 上面 显示额外的 显示区域
     * @param columnArr 列标题数组
     * @param width 宽度,3000
     * @param dataArr 表格数据数组
     * @param headTitle 第一行标题
     * @param fileName 文件名称
     * @return upload地址
     */
    public String commonGenerateExcelForTitleArea(Queue<String> parm, String[] columnArr, int width, String[][] dataArr, String headTitle,
                                                  String fileName)
    {
        String httpResult = null;

        int rowaccess=100;//内存中缓存记录行数

        try
        {
            // Excel列数
            int column = columnArr.length;


            /*keep 100 rowsin memory,exceeding rows will be flushed to disk*/
            wb = new SXSSFWorkbook(rowaccess);
            sheet = wb.createSheet();

            // 给工作表列定义列宽(实际应用自己更改列数)
            for (int i = 0; i < column; i++)
            {
                //sheet.setColumnWidth(i, width);
                sheet.autoSizeColumn(i);
            }
            // 创建单元格样式
            CellStyle cellStyle = wb.createCellStyle();
            // 指定单元格居中对齐
            cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            // 指定单元格垂直居中对齐
            cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
            // 指定当单元格内容显示不下时自动换行
            cellStyle.setWrapText(true);
            // 设置单元格字体
            Font font = wb.createFont();
            font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
            font.setFontName("微软雅黑");
            font.setFontHeight((short) 200);
            cellStyle.setFont(font);




            // 创建报表头部
            this.createNormalHead(headTitle, column);
            log.info("******************************生成Excel（createExcel）-S**************************************");

            CellStyle cellstyle = wb.createCellStyle();
            cellstyle.setAlignment(XSSFCellStyle.ALIGN_CENTER_SELECTION);


            int extendRow = parm.size()/2;
            for (int i = 0; i < extendRow; i++) {
                Row row = sheet.createRow(i +1);
                this.cteateCell(row, 0, parm.poll(),cellstyle);
                this.cteateCell(row, 1, parm.poll(),cellstyle);
            }
            Row cloumnRow = sheet.createRow(1 + extendRow);
            for (int i = 0; i < column; i++)
            {
                this.cteateCell(cloumnRow, i, columnArr[i],cellstyle);
            }


            cloumnRow = null;
            // 循环创建中间的单元格的各项的值
            for (int i = 0; i < dataArr.length; i++)
            {
                Row row = sheet.createRow(i + 2 + extendRow);
                for (int j = 0; j < dataArr[i].length; j++)
                {
                    this.cteateCell(row, j, dataArr[i][j],cellstyle);
                }
                row = null;

                //每当行数达到设置的值就刷新数据到硬盘,以清理内存
                if(i % rowaccess == 0){
                    ((SXSSFSheet)sheet).flushRows();
                }
            }
            log.debug(fileName + " generate excel success!");
            // httpResult = HttpUtil.uploadFile(uploadUrl, this.getByteByFile(fileName), fileName,
            // "application/vnd.ms-excel", UUID.randomUUID().toString(), "create");
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<String, Object>();
            param.add("name", fileName);
            param.add("file", new FileSystemResource(getExcelFile(new File(fileName))));
            log.info("******************************生成Excel（createExcel）-E**************************************");
            RestTemplate restTemplate = new RestTemplate();
            httpResult = restTemplate.postForObject(uploadUrl, param, String.class);
            //httpResult = httpResult + "?name="+fileName;
            System.out.println("httpResult======="+httpResult);
        }
        catch (Exception e)
        {
            log.error(" generate excel fail!", e);
            return null;
        }

        return httpResult;
    }

    public static void main(String[] arg)
    {
        ExportExcelUtil exportExcel = new ExportExcelUtil("");
        String[] titleArr = new String[]{"手机", "车牌", "品牌", "车型", "购买时间", "车辆VIN码"};
        String[][] dataArr = new String[5][];
        for (int i = 0; i < 5; i++) {
            String[] tmp = new String[titleArr.length];

            tmp[0] = "11111";
            tmp[1] = "22222";
            tmp[2] = "33333";
            tmp[3] = "44444";
            tmp[4] = "55555";
            tmp[5] = "66666";
            dataArr[i] = tmp;
        }
        String resultUrl = exportExcel.commonGenerateExcel(titleArr, 100, dataArr, "用户基本信息", "用户基本信息.xls");
        System.out.println(resultUrl);
    }
}
