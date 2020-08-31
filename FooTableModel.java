import javax.swing.table.AbstractTableModel;
import java.util.*;
import java.nio.file.Path;

public class FooTableModel extends AbstractTableModel {
	String curFile;
	Map<String, FileData> fileData = new HashMap<>();

	private FileData getFileData(String filename) {
		if(!fileData.containsKey(curFile)) {
			FileData tFileData = null;
			try {
				tFileData = FileData.fromFile(curFile);
			} catch(Exception e) {
				System.err.printf("Failed to load '%s': %s%n", curFile, e.getMessage());
			}
			fileData.put(curFile, tFileData);
		}
		return fileData.get(filename);
	}		

	@Override
	public String getColumnName(int column) {
		return getFileData(curFile).getColumnName(column);
	}

	@Override
	public int getRowCount() {
		return getFileData(curFile).getRowCount();
	}

	@Override
	public int getColumnCount() {
		return getFileData(curFile).getColumnCount();
	}

	@Override
	public Object getValueAt(int row, int column) {
		return getFileData(curFile).getValueAt(row, column);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		getFileData(curFile).setValueAt(value, row, col);
		fireTableCellUpdated(row, col);
	}

	public void setColumnNames(List<String> columnNames) {
		getFileData(curFile).setColumnNames(columnNames);
		fireTableStructureChanged();
	}

	public void setData(List<List<String>> cellData) {
		getFileData(curFile).setData(cellData);
		fireTableDataChanged();
	}

	public void addRow(List<String> rowData) {
		getFileData(curFile).addRow(rowData);
		/*
		   Map<Integer, String> row = new HashMap<>();
		   int col = 0;
		   for(String v : rowData) {
		   row.put(col++, v);
		   }
		   data.put(maxRow++, row);
		   */
		fireTableDataChanged();
	}

	public void setFile(Path path, String filename) {
		curFile = path.resolve(filename).toString();
		fireTableStructureChanged();
		fireTableDataChanged();
		/*
		   setColumnNames(Collections.singletonList(filename));
		   setData(Collections.singletonList(Collections.singletonList("data")));
		   */
	}
}
