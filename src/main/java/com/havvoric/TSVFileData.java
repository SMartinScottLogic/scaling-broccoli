package com.havvoric;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TSVFileData {
    private int maxRow;
    private List<String> columnNames = new ArrayList<>();
    private Map<Integer, Map<Integer, String>> data = new HashMap<>();

    public static TSVFileData fromFile(String filename) throws IOException {
        System.err.printf("Reading file from: '%s'%n", filename);
        File inputFile = new File(filename);
        try (FileReader fr = new FileReader(inputFile);
             BufferedReader br = new BufferedReader(fr)) {
            TSVFileData tsvFileData = new TSVFileData();
            String firstRow = br.readLine().trim();
            if (StringUtils.isBlank(firstRow)) {
                System.err.printf("Failed to read headers from: '%s'. Aborting load.%n", filename);
                return null;
            }

            // headers:
            String[] cn = firstRow.split("\t");

			List<String> columnNames = new ArrayList<>();
			Collections.addAll(columnNames, cn);
            tsvFileData.setColumnNames(columnNames);

            // rows
            Object[] tableLines = br.lines().toArray();
            List<List<String>> data = new ArrayList<>();
            // data rows
            for (Object tableLine : tableLines) {
                String line = tableLine.toString().trim();
                String[] dataRow = line.split("\t");
                ArrayList<String> strings = new ArrayList<>();
                Collections.addAll(strings, dataRow);
                data.add(strings);
            }

            fr.close();
            tsvFileData.setData(data);
            return tsvFileData;
        }
    }

    public String getColumnName(int column) {
        if (column < 0 || column >= columnNames.size()) {
            return "";
        }
        return columnNames.get(column);
    }

    public int getColumnCount() {
        return columnNames.size();
    }

    public int getRowCount() {
        return maxRow;
    }

    public String getValueAt(int row, int column) {
        if (!data.containsKey(row)) {
            return "";
        }
        if (!data.get(row).containsKey(column)) {
            return "";
        }
        return data.get(row).get(column);
    }

    public void setValueAt(Object value, int row, int column) {
        if (!data.containsKey(row)) {
            data.put(row, new HashMap<>());
        }
        data.get(row).put(column, value.toString());
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public void setData(List<List<String>> cellData) {
        data = new HashMap<>();
        maxRow = 0;
        for (List<String> rowData : cellData) {
            Map<Integer, String> row = new HashMap<>();
            int col = 0;
            for (String v : rowData) {
                row.put(col++, v);
            }
            data.put(maxRow++, row);
        }
    }

    public void addRow(List<String> rowData) {
        Map<Integer, String> row = new HashMap<>();
        int col = 0;
        for (String v : rowData) {
            row.put(col++, v);
        }
        data.put(maxRow++, row);
    }

}
