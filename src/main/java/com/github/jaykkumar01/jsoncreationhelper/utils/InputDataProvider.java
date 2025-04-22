package com.github.jaykkumar01.jsoncreationhelper.utils;

import com.github.jaykkumar01.jsoncreationhelper.models.InputData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for handling Excel sheet operations and extracting data for PDF comparison.
 */
public class InputDataProvider {
    private static final DataFormatter formatter = new DataFormatter();

    private static final String TAG_FILE_PATH = "";

    /**
     * Retrieve the data from the Excel sheet and create a list of DataModel objects.
     *
     * @return List of DataModel objects containing PDF paths and other configuration.
     */
    public static List<InputData> load(String formID) {
        List<InputData> list = new ArrayList<>();
        Iterator<Row> itr = getRowIterator(formID);

        // Check if the iterator is null
        if (itr == null) {
            return null;
        }

        // Skip the header row
        if (itr.hasNext()) {
            itr.next();
        }

        while (itr.hasNext()) {
            Row row = itr.next();

            // Extract cell values for path1, path2, folder, range1, and range2
            Cell cell1 = row.getCell(0);
            Cell cell2 = row.getCell(1);
            Cell cell3 = row.getCell(2);
            Cell cell4 = row.getCell(3);

            // Continue to the next iteration if any of the essential cells is null
//            if (path1 == null || path2 == null) {
//                continue;
//            }
//
//            String strPath1 = formatVal(path1);
//            String strPath2 = formatVal(path2);
//
//            // Continue to the next iteration if any of the essential values is null
//            if (strPath1 == null || strPath2 == null) {
//                continue;
//            }

            // Create a DataModel object and add it to the list
            InputData dataModel = new InputData();

            list.add(dataModel);
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
        FileInputStream fis;
        XSSFWorkbook wb = null;
        try {
            fis = new FileInputStream(TAG_FILE_PATH);
            wb = new XSSFWorkbook(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (wb == null) {
            return null;
        }
        return wb.getSheetAt(0).rowIterator();
    }
}
