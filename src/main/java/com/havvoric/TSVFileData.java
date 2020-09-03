package com.havvoric;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
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
            String firstRow = br.readLine();
            if (StringUtils.isBlank(firstRow)) {
                System.err.printf("Failed to read headers from: '%s'. Aborting load.%n", filename);
                return null;
            }
            List<String> columnNames = TSVFileData.extractRow(firstRow);
            tsvFileData.setColumnNames(columnNames);

            // rows
            Object[] tableLines = br.lines().toArray();
            List<List<String>> data = new ArrayList<>();
            // data rows
            for (Object tableLine : tableLines) {
                data.add(extractRow(tableLine));
            }

            fr.close();
            tsvFileData.setData(data);
            return tsvFileData;
        }
    }

    static List<String> extractRow(Object raw) {
        String line = raw.toString().stripTrailing();
        String [] dataLine = line.split("\t");
        ArrayList<String> strings = new ArrayList<>();
        Collections.addAll(strings, dataLine);
        return strings;
    }

    public void toFile(String filename) {
        File outputFile = new File(filename);
        try (FileWriter fw = new FileWriter(outputFile);
             BufferedWriter bw = new BufferedWriter(fw)) {
            StringBuilder sb = new StringBuilder();
            for(int col = 0; col < getColumnCount(); col++) {
                if(col!=0) {
                    sb.append('\t');
                }
                sb.append(getColumnName(col));
            }
            sb.append('\r').append('\n');

            bw.write(sb.toString());

            for(int row = 0; row < getRowCount(); row++) {
                sb = new StringBuilder();
                for(int col = 0; col < getColumnCount(); col++) {
                    if(col != 0) {
                        sb.append('\t');
                    }
                    sb.append(getValueAt(row, col));
                }
                sb.append('\r').append('\n');
                bw.write(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
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
