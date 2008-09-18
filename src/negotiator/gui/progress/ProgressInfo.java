package negotiator.gui.progress;

import javax.swing.table.AbstractTableModel;

class ProgressInfo extends AbstractTableModel{
	//public Bid ourOldBid;
	//public Bid oppOldBid;
	//public UtilitySpace utilitySpace;
	private String[] colNames={"Round","Side","utilA","utilB","Opp. model"};
	private Object[][] data;
	
	public ProgressInfo() 
	{
		super();
		data = new Object [6][colNames.length];
	}
	
	public void addRow(){
		int currentLength = data.length;
		Object [][] temp = new Object [currentLength+1][colNames.length];
		//System.out.println("temp length "+temp.length);
		for(int j=0;j<temp.length-1;j++){
			for(int i=0;i<colNames.length;i++){
				temp [j][i] = data [j][i];
				//System.out.println("temp data "+temp [j][i]);
			}
		}
		data = temp;
		fireTableDataChanged();
	}
	
	public void reset()
	{
		System.out.println("reset the JTable now.");
		data = new Object [6][colNames.length];
		fireTableDataChanged();
	}

	public int getColumnCount() {
        return colNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return colNames[col];
    }
    
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }
    
	public void setValueAt(Object value, int row, int col)  {
		data[row][col] = value;
		//Notify all listeners that the value of the cell at (row, column) has been updated
		fireTableCellUpdated(row, col);
	}

}
