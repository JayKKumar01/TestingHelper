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
    private final CellStyle headerStyle;

    public ExcelReportHelper() {
        // Create header style: green background + bold font
        headerStyle = workbook.createCellStyle();

        // Green background
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Bold font
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        headerStyle.setFont(boldFont);
    }

    public void addEntry(String sheetName, String xpath, String tagName, String value) {
        XSSFSheet sheet = sheets.computeIfAbsent(sheetName, name -> {
            XSSFSheet newSheet = workbook.createSheet(name);
            Row header = newSheet.createRow(0);

            String[] headers = { "XPath", "TagName", "Value", "Presence", "Status" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            return newSheet;
        });

        int rowIndex = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(xpath);
        row.createCell(1).setCellValue(tagName);
        row.createCell(2).setCellValue(value);
        // Auto-size columns after each new entry
        for (int i = 0; i <= 4; i++) {
            sheet.autoSizeColumn(i);
        }
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
