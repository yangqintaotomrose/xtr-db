package com.xtr.framework.utils;

import com.xtr.framework.hutool.IData;
import com.xtr.framework.hutool.IDataset;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;

public class ExcelUtils {
    public static IDataset getDataset(String path) throws Exception {
        File file = new File(path);
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();//带公式计算
        XSSFSheet sheet = wb.getSheetAt(0);
        int totalRows = sheet.getPhysicalNumberOfRows(); // 获取总行数
        System.out.println(totalRows);
        IDataset list = new IDataset();
        int totalColumns = 0;
        for (int rowIndex = 0; rowIndex <= totalRows; rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);
            IData rowData = new IData();
            if (row != null) {
                int numCells = row.getPhysicalNumberOfCells();
                if (numCells > totalColumns) {
                    totalColumns = numCells;
                }
                for (int i = 0; i <numCells; i++) {
                    XSSFCell cell =  row.getCell(i);
                    CellType cellType = cell.getCellType();
                    switch (cellType) { // 假设cellType是预先定义的CellType枚举值
                        case STRING:
                            rowData.set("c"+i,cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            rowData.set("c"+i,cell.getNumericCellValue());
                            break;
                        case BOOLEAN:
                            rowData.set("c"+i,true);
                            break;
                        case FORMULA:
                            Cell newCell= evaluator.evaluateInCell(cell);
                            CellType newCellType = newCell.getCellType();
                            switch (newCellType) { // 假设cellType是预先定义的CellType枚举值
                                case STRING:
                                    rowData.set("c"+i,newCell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    rowData.set("c"+i,newCell.getStringCellValue());
                                    break;
                                case BOOLEAN:
                                    rowData.set("c"+i,true);
                                    break;
                                case BLANK:
                                    rowData.set("c"+i,"");
                                    break;
                                default:
                                    throw new IllegalArgumentException("Unsupported cell type111: " + newCell);
                            }
                            //rowData.set("a"+i,cell.getCellFormula().toString());
                            break;
                        case BLANK:
                            rowData.set("c"+i,"");
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported cell type: " + cellType);
                    }

                }
                list.add(rowData);
            }
        }
        return list;
    }
}
