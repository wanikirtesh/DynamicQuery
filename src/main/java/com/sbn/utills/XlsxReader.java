package com.sbn.utills;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**********************************
 * Created by Kirtesh Wani on 04-03-2015.
 * for ExcelBasedFramework
 ***********************************/
public class XlsxReader {
    private XSSFWorkbook workbook;
    private final String excelfilepath;
    private static final Logger LOGGER = LogManager.getLogger(XlsxReader.class);

    public XlsxReader(String excelFilePath) throws Exception {
        LOGGER.debug("Initializing the Excel file " + excelFilePath);
       try {
           workbook = new XSSFWorkbook(new FileInputStream(new File(excelFilePath)));
       }catch (Exception e){
           LOGGER.error(e);
           throw e;
       }
        excelfilepath = excelFilePath;
    }


    public void close(){
        workbook = null;
    }
    public String getCellStringData(String SheetName,int RowNum,int ColNum){
        try{
            XSSFSheet sheet = workbook.getSheet(SheetName);
            Cell cell = sheet.getRow(RowNum).getCell(ColNum);
            switch(sheet.getRow(RowNum).getCell(ColNum).getCellType()){
                case STRING:
                    return cell.getRichStringCellValue().getString();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        return Double.toString(cell.getNumericCellValue());
                    }
                case BOOLEAN:
                    return Boolean.toString(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                case BLANK:
                    return "";
                default:
                    return cell.getStringCellValue();
            }
        }
        catch(Exception e){
            return "";
        }
    }
    public int getRowCount(String SheetName){
        XSSFSheet sheet;
        try{
            sheet = workbook.getSheet(SheetName);
            return sheet.getPhysicalNumberOfRows();
        }finally{
            sheet=null;
        }
    }
    public int getColumnCount(String SheetName,int RowNum){
        XSSFSheet sheet;
        try{
            sheet = workbook.getSheet(SheetName);
            return sheet.getRow(RowNum).getPhysicalNumberOfCells();
        }finally{
            sheet=null;
        }
    }
    public boolean createSheet(String SheetName){
        try{
            FileOutputStream output = new FileOutputStream(excelfilepath);
            workbook.createSheet(SheetName);
            workbook.write(output);
            output.close();
            ExcelReaderInIt();
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    public boolean deleteSheet(String SheetName){
        try{
            FileOutputStream output = new FileOutputStream(excelfilepath);
            workbook.removeSheetAt(workbook.getSheetIndex(SheetName));
            workbook.write(output);
            output.close();
            ExcelReaderInIt();
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    private void ExcelReaderInIt() throws FileNotFoundException, IOException {
        workbook = new XSSFWorkbook(new FileInputStream(new File(excelfilepath)));
    }
}