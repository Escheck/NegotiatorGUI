package negotiator.gui.progress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

/**
 * Simple table model for showing results. Only adding of rows (containing one
 * result) is supported. Simplified verson of {@link NegoTableModel}.
 * 
 * @author W.Pasman 15jul15
 *
 */
public class SimpleTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 444361373323002284L;
	private List<String> colNames = new ArrayList<String>();
	private List<List<Object>> rows = new ArrayList<List<Object>>();

	/**
	 * @param headers
	 *            a list of column names.
	 */
	public SimpleTableModel(List<String> headers) {
		super();
		this.colNames = headers;
	}

	/**
	 * Adds a row with new values. The values from the <key,value> pairs in the
	 * map are inserted in the correct columns given the headers. You must call
	 * setHeaders.
	 * 
	 * @param map
	 */
	public void addRow(Map<String, String> map) {
		List<Object> row = new ArrayList<Object>();
		for (String key : colNames) {
			if (map.containsKey(key)) {
				row.add(map.get(key));
			} else {
				row.add("");
			}
		}
		rows.add(row);

		fireTableDataChanged();
	}

	public int getColumnCount() {
		return colNames.size();
	}

	public int getRowCount() {
		return rows.size();
	}

	public String getColumnName(int col) {
		return colNames.get(col);
	}

	public Object getValueAt(int row, int col) {
		return rows.get(row).get(col);
	}

}
