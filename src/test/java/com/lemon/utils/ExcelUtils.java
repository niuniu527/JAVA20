package com.lemon.utils;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.lemon.pojo.CaseInfo;
import com.lemon.pojo.WriteBackData;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {
    //批量回写存储到list集合中
    public static List<WriteBackData> wbdList = new ArrayList<>();

    /**
     * 封装后
     * easypoi
     * 1.excel文件流
     * 2.easypoi导入参数(创建ImportParas对象)
     * 3.导入
     * 4.关流
     * 5.映射,去Caseinfo加上@Excel注解实现
     */
    public static void main(String[] args) throws Exception {


    }

    /**
     * 读取excel数据并封装到指定对象中
     *
     * @param sheetIndex 开始sheet索引
     * @param sheetNum   sheet个数
     * @param clazz      excel映射类字节对象
     * @return
     * @throws Exception
     */
    public static List read(int sheetIndex, int sheetNum, Class clazz) throws Exception {
        //1.读excel文件流
        FileInputStream fis = new FileInputStream(Constants.EXCEL_PATH);
        //2.easypoi导入参数
        ImportParams params = new ImportParams();
        params.setStartSheetIndex(sheetIndex);
        params.setSheetNum(sheetNum);
        //3.导入importExcel（excel文件流，映射关系字节码对象，导入参数）
        //CaseInfo.class获取CaseInfo类里的所有内容
        List caseInfoList = ExcelImportUtil.importExcel(fis, clazz, params);
        //4.关流
        fis.close();
        return caseInfoList;
    }

    /**
     * 批量回写（遍历wbdList集合）
     */
    public static void batchWrite() throws IOException {
        FileInputStream fis = new FileInputStream(Constants.EXCEL_PATH);
        Workbook sheets = WorkbookFactory.create(fis);
        //循环wbdList集合
        for (WriteBackData wbd : wbdList) {
            //取出属性
            int sheetIndex = wbd.getSheetIndex();
            int rowNum = wbd.getRowNum();
            int cellNum = wbd.getCellNum();
            String content = wbd.getContent();
            //获取对应的Sheet对象
            Sheet sheet = sheets.getSheetAt(sheetIndex);
            //获取row
            Row row = sheet.getRow(rowNum);
            //获取cell
            Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            //回写内容
            cell.setCellValue(content);
        }
        //回写到excel文件中
        FileOutputStream fos = new FileOutputStream(Constants.EXCEL_PATH);
        sheets.write(fos);
        fis.close();
        fos.close();
    }
}
