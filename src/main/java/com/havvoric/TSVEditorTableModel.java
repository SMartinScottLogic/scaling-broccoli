package com.havvoric;

import javax.swing.table.AbstractTableModel;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TSVEditorTableModel extends AbstractTableModel {
	String curFile;
	Map<String, TSVFileData> tsvFileData = new HashMap<>();

	private TSVFileData getFileData(String filename) {
		if(!tsvFileData.containsKey(curFile)) {
			TSVFileData tFileData = null;
			try {
				tFileData = TSVFileData.fromFile(curFile);
			} catch(Exception e) {
				System.err.printf("Failed to load '%s': %s%n", curFile, e.getMessage());
			}
			tsvFileData.put(curFile, tFileData);
		}
		return tsvFileData.get(filename);
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

	public void addRow(List<String> rowData) {
		getFileData(curFile).addRow(rowData);
		fireTableDataChanged();
	}

	public void saveAll() {
		tsvFileData.entrySet().stream().forEach(e -> save(e.getKey(), e.getKey(), e.getValue()));
	}

	private void save(String filename, String targetFilename, TSVFileData tsvFileData) {
		System.err.printf("Save '%s' as '%s'%n", filename, targetFilename);
		tsvFileData.toFile(filename);
	}

	public void save() {
		save(curFile, curFile, tsvFileData.get(curFile));
	}
	
	public void save(String filename) {
		save(curFile, filename, tsvFileData.get(curFile));
	}

	public void setFile(Path path, String filename) {
		curFile = path.resolve(filename).toString();
		fireTableStructureChanged();
		fireTableDataChanged();
	}
}
