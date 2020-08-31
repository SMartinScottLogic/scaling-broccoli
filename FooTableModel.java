import javax.swing.table.AbstractTableModel;
import java.util.*;

public class FooTableModel extends AbstractTableModel {
	List<String> columnNames = Collections.emptyList();
	Map<Integer, Map<Integer, String>> data = new HashMap<>();
	Integer maxRow = 0;
	
	@Override
	public String getColumnName(int column) {
		return columnNames.get(column);
	}
	
	@Override
	public int getRowCount() {
		return maxRow;
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.size();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		if(!data.containsKey(row)) {
			return "";
		}
		if(!data.get(row).containsKey(column)) {
			return "";
		}
		String value = data.get(row).get(column);
		return value;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		if(!data.containsKey(row)) {
			data.put(row, new HashMap<Integer, String>());
		}
		data.get(row).put(col, value.toString());
        fireTableCellUpdated(row, col);
    }
    
    public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
		fireTableStructureChanged();
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
		fireTableDataChanged();
	}
	
	public void addRow(List<String> rowData) {
		Map<Integer, String> row = new HashMap<>();
		int col = 0;
		for(String v : rowData) {
			row.put(col++, v);
		}
		data.put(maxRow++, row);
		fireTableDataChanged();
	}
}
