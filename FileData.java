import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileData {	
		private int maxRow;
		private List<String> columnNames = new ArrayList<>();
		private Map<Integer, Map<Integer, String>> data = new HashMap<>();

		public String getColumnName(int column) {
			if(column < 0 || column >= columnNames.size()) {
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
			if(!data.containsKey(row)) {
				return "";
			}
			if(!data.get(row).containsKey(column)) {
				return "";
			}
			return data.get(row).get(column);
		}

		public void setValueAt(Object value, int row, int column) {
			if(!data.containsKey(row)) {
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
			for(List<String> rowData : cellData) {
				Map<Integer, String> row = new HashMap<>();
				int col = 0;
				for(String v : rowData) {
					row.put(col++, v);
				}
				data.put(maxRow++, row);
			}
		}

		public void addRow(List<String> rowData) {
			Map<Integer, String> row = new HashMap<>();
			int col = 0;
			for(String v : rowData) {
				row.put(col++, v);
			}
			data.put(maxRow++, row);
		}

	public static FileData fromFile(String filename) throws IOException {
		System.err.println("Reading file from: '" + filename + "'");
		File inputFile = new File(filename);
		try (FileReader fr = new FileReader(inputFile);
				BufferedReader br = new BufferedReader(fr)) 
		{
			FileData fileData = new FileData();
			String firstRow = br.readLine().trim();
			if (firstRow != null) {
				// headers:
				String[] cn = firstRow.split("\t");

				List<String> columnNames = new ArrayList<String>();
				for (int i = 0; i < cn.length; i++) {
					columnNames.add(cn[i]);
				}
				fileData.setColumnNames(columnNames);
			}
			// rows
			Object[] tableLines = br.lines().toArray();
			List<List<String>> data = new ArrayList<>();
			// data rows
			for (int i = 0; i < tableLines.length; i++) {
				String line = tableLines[i].toString().trim();
				String[] dataRow = line.split("\t");
				ArrayList<String> strings = new ArrayList<>();
				for (int j =0; j < dataRow.length; j++) {
					strings.add(dataRow[j]);
				}
				data.add(strings);
			}

			fr.close();
			fileData.setData(data);
			return fileData;
		}
	}

};
