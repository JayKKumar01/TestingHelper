package com.github.jaykkumar01.jsoncreationhelper.utils;

import com.github.jaykkumar01.jsoncreationhelper.models.InputData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InputDataProvider {
    private static final DataFormatter formatter = new DataFormatter();
    private static final String TAG_FILE_PATH = "path_to_your_excel_file.xlsx";

    public static List<InputData> load(String formID) {
        List<InputData> list = new ArrayList<>();
        Iterator<Row> itr = getRowIterator(formID);

        if (itr == null) {
            return null;
        }

        // Skip header row
        if (itr.hasNext()) {
            itr.next();
        }

        while (itr.hasNext()) {
            Row row = itr.next();

            // Extracting values from columns
            Cell xpathCell = row.getCell(0);
            Cell tagNameCell = row.getCell(1);
            Cell varNameCell = row.getCell(2);
            Cell sampleDataCell = row.getCell(3);

            // Format the values and check if any required field is empty
            String xpath = formatVal(xpathCell);
            String tagName = formatVal(tagNameCell);
            String variableName = formatVal(varNameCell);

            // Skip rows where any required field (XPath, TagName, VariableName) is missing or empty
            if (xpath == null || tagName == null || variableName == null || xpath.isEmpty() || tagName.isEmpty() || variableName.isEmpty()) {
                continue;  // Skip this row
            }

            // Create InputData object and populate it
            InputData inputData = new InputData();
            inputData.setXPath(xpath);
            inputData.setTagName(tagName);
            inputData.setVariableName(variableName);
            inputData.setSampleData(formatVal(sampleDataCell));

            // Add to the list
            list.add(inputData);
        }

        return list;
    }

    private static String formatVal(Cell val) {
        // Check if the cell is null or if the formatted value is empty
        if (val == null || formatter.formatCellValue(val).trim().isEmpty()) {
            return null;
        }
        return formatter.formatCellValue(val).trim();
    }

    private static Iterator<Row> getRowIterator(String formID) {
        try (FileInputStream fis = new FileInputStream(TAG_FILE_PATH)) {
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            return wb.getSheet(formID).rowIterator();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
