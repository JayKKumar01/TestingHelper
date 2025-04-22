package jsoncreationhelper.utils;

import jsoncreationhelper.constants.AppPaths;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelReportHelper {
    private final XSSFWorkbook workbook = new XSSFWorkbook();
    private final Map<String, XSSFSheet> sheets = new LinkedHashMap<>();
    private final CellStyle headerStyle;
    private final CellStyle grayStyle;
    private final CellStyle greenStyle;
    private final CellStyle redStyle;

    public ExcelReportHelper() {
        // Create header style: green background + bold font
        headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        headerStyle.setFont(boldFont);

        // Style for "Yet To Start" (Grey)
        grayStyle = workbook.createCellStyle();
        grayStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        grayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Style for "Pass" (Green)
        greenStyle = workbook.createCellStyle();
        greenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Style for "Fail" (Red)
        redStyle = workbook.createCellStyle();
        redStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    public void addEntry(String sheetName, String xpath, String tagName, String value) {
        XSSFSheet sheet = sheets.computeIfAbsent(sheetName, name -> {
            // Create new sheet and initialize header row
            XSSFSheet newSheet = workbook.createSheet(name);
            Row header = newSheet.createRow(0);

            String[] headers = { "XPath", "TagName", "Value", "Presence", "Status" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Add dropdowns for Presence and Status columns
            createDropdown(newSheet, 3);  // Column 3: Presence
            createDropdown(newSheet, 4);  // Column 4: Status

            return newSheet;
        });

        // Add data entry to the sheet
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

    private void createDropdown(XSSFSheet sheet, int columnIndex) {
        // Define the range for the dropdown options
        XSSFRichTextString listString = new XSSFRichTextString("Yet To Start,Pass,Fail");

        // Create the data validation for the dropdown list
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(
                new String[] {"Yet To Start", "Pass", "Fail"}
        );
        CellRangeAddressList addressList = new CellRangeAddressList(1, 65535, columnIndex, columnIndex);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);

        // Apply the validation to the entire column
        sheet.addValidationData(validation);
    }

    public void save(String formId) {
        // Base file name with formId
        String baseFileName = "Report_" + formId + ".xlsx";
        File file = new File(AppPaths.JSON_OUTPUT_BASE + File.separator + formId + File.separator + baseFileName);

        // Check if the file already exists, and if so, increment the index
        int fileIndex = 1;
        while (file.exists()) {
            String newFileName = "Report_" + formId + "(" + fileIndex + ").xlsx";
            file = new File(AppPaths.JSON_OUTPUT_BASE + File.separator + formId + File.separator + newFileName);
            fileIndex++;
        }

        // Create the necessary directories and save the file
        file.getParentFile().mkdirs();
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
            System.out.println("📊 Excel report saved at: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
