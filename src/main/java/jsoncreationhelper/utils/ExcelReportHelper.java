package jsoncreationhelper.utils;

import jsoncreationhelper.constants.AppPaths;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelReportHelper {
    private final XSSFWorkbook workbook = new XSSFWorkbook();
    private final Map<String, XSSFSheet> sheets = new LinkedHashMap<>();

    public void addEntry(String sheetName, String xpath, String tagName, String value) {
        XSSFSheet sheet = sheets.computeIfAbsent(sheetName, name -> {
            XSSFSheet newSheet = workbook.createSheet(name);
            Row header = newSheet.createRow(0);
            header.createCell(0).setCellValue("XPath");
            header.createCell(1).setCellValue("TagName");
            header.createCell(2).setCellValue("Value");
            header.createCell(3).setCellValue("Exists");
            header.createCell(4).setCellValue("Status");
            return newSheet;
        });

        int rowIndex = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(xpath);
        row.createCell(1).setCellValue(tagName);
        row.createCell(2).setCellValue(value);
    }

    public void save(String formId) {
        try {
            File file = new File(AppPaths.JSON_OUTPUT_BASE + File.separator + formId + File.separator + "Report.xlsx");
            file.getParentFile().mkdirs();
            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
                System.out.println("📊 Excel report saved at: " + file.getAbsolutePath());
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
