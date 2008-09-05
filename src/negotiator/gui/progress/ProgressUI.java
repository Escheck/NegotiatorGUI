package negotiator.gui.progress;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TextArea;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.events.LogMessageEvent;
import negotiator.events.NegotiationSessionEvent;
import negotiator.exceptions.Warning;
import negotiator.gui.chart.BidChart;
import negotiator.gui.chart.ScatterPlot;
import negotiator.gui.negosession.NegoSessionUI;
import negotiator.tournament.NegotiationSession2;
import negotiator.utility.UtilitySpace;
import negotiator.NegotiationEventListener;
import negotiator.Bid;

public class ProgressUI extends JPanel implements NegotiationEventListener {
	private JPanel log;
	private JTextArea textOutput;
	private TextArea logText;
	private JScrollPane jScrollPane1;
	private JScrollPane table;
	private JPanel chart;
	private ProgressInfo progressinfo; // the table model	
	private JTable biddingTable;
	protected int round = 0;
	private BidChart bidChart;
	protected NegotiationSession2 session;
	
	public ProgressUI ()
	{
		bidChart = new BidChart();
		progressinfo = new ProgressInfo();
		biddingTable = new  JTable(progressinfo);
		biddingTable.setGridColor(Color.lightGray);
		ProgressUI1("initialized...",bidChart,biddingTable);
	}
	
	public  ProgressUI (String logging,BidChart bidChart, JTable bidTable){
		ProgressUI1 (logging, bidChart,  bidTable);
	}

	public void ProgressUI1 (String logging,BidChart bidChart, JTable bidTable){
		Container pane = this;
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		
		//the chart panel 
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
        JFreeChart plot = bidChart.getChart();
        chart = new ChartPanel(plot);
        chart.setMinimumSize(new Dimension(350, 350)); 
        chart.setBorder(loweredetched);
        c.insets = new Insets(10, 0, 0, 10);
        c.ipadx = 10;
		c.ipady = 10;
        pane.add(chart,c);
        
		//the table panel 
        table = new JScrollPane(bidTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    //bidTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	    table.setMinimumSize(new Dimension (200,200));
	    
		//table.setMinimumSize(new Dimension (200,200));
		//table =new JPanel(new BorderLayout());
        //table.add(bidTable.getTableHeader(), "North");
        //table.add(bidTable,"Center");
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 10;
		c.ipady = 10;
		c.insets = new Insets(0, 0, 10, 10);
		table.setBorder(loweredetched);
		pane.add(table, c);
		
		/**************create logging panel ************/    
		// set the constraints
		c.gridx = 0;
		c.gridy = 0;
		//c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 2;
		
		logText = new TextArea();
		logText.setText("");
		log = new JPanel(new BorderLayout());
		log.setMinimumSize(new Dimension(300,500));
		log.add(logText,"Center");
		pane.add(log,c);

		/*
        jScrollPane1 = new javax.swing.JScrollPane();
        textOutput = new javax.swing.JTextArea();
        textOutput.setColumns(100);
        textOutput.setLineWrap(true);
        textOutput.setRows(50);
        jScrollPane1.setViewportView(textOutput);
		//pane.add(jScrollPane1,c);*/

		//setTitle("Progress");
		setSize(700,600);
		setVisible(true);
		
	}
	private double[][] getPareto(){
		double [][] pareto=null;
		ArrayList <BidPoint>paretoBids = null;
		BidSpace bs = session.getBidSpace();
		if(bs==null)System.out.println("bidspace == null");
		else{
			try {
				paretoBids = bs.getParetoFrontier();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(paretoBids!=null){
				pareto = new double [2][paretoBids.size()];
				for(int i=0; i<paretoBids.size();i++) 
				  {
					pareto[0][i]= paretoBids.get(i).utilityA;
					pareto[1][i]= paretoBids.get(i).utilityB;
				  }
			}
		}
		return pareto;
	}
	
	private double[][] getAllBidsInBidSpace(){
		//save the possible bids in double [][] and display in graph 
		double [][] possibleBids=null;
		BidSpace bs = session.getBidSpace();
		if(bs==null)System.out.println("bidspace == null");
		else{
			ArrayList<BidPoint> allBids = bs.bidPoints;// always gives a nullpointer
			if(allBids!=null){
				possibleBids = new double [2][allBids.size()];
				int i=0;
				for(BidPoint p: bs.bidPoints) 
				  {possibleBids[0][i]= p.utilityA; possibleBids[1][i]= p.utilityB; i++;}
				//bidChart.setPossibleBids(possibleBids);
			}else{
				System.out.println("possibleBids is null");
			}
		}
		return possibleBids;
	}
	
	public void addLoggingText(String t){
		//textOutput.append(t);
		logText.append(t+"\n");
	}
	
	/** run this for a demo of ProgressnUI */
	public static void main(String[] args) 
	{
		//create sample data:
		double [][] possibleBids = new double [2][1000];
		for(int i=0;i<1000;i++){
			possibleBids [0][i]= Math.random();
			possibleBids [1][i]= Math.random();
		}
		double[][] pareto = new double [2][4];
		double [][] bidSeriesA = new double [2][4];
		double [][] bidSeriesB = new double [2][4];
		
		for(int i=0;i<4;i++){
			double paretox = Math.random();
			if (paretox<0.5)paretox+=0.5;
			double paretoy = Math.random();
			if (paretoy<0.5)paretoy+=0.5;
			if (i==0)
				pareto [1][0]=1;
			if (i==3)
				pareto [0][3]=1;
													
			pareto [0][i]= paretox;
			pareto [1][i]= paretoy;
			bidSeriesA [0][i]= Math.random();
			bidSeriesA [1][i]= Math.random();
			bidSeriesB [0][i]= Math.random();
			bidSeriesB [1][i]= Math.random();
		}
		
		BidChart myChart = new BidChart();
		JTable myTable = new JTable(5,5);
		try {
			new ProgressUI("Logging started...",myChart,myTable); 
		} catch (Exception e) { new Warning("ProgressUI failed to launch: ",e); }
		
		//when the dataset is changes the chart is automatically updated
		myChart.setPossibleBids(possibleBids);
		//myChart.setPareto(pareto);
		myChart.setBidSeriesA(bidSeriesA);
		myChart.setBidSeriesB(bidSeriesB);
	}
	
	public void setNegotiationSession(NegotiationSession2 nego){
		session = nego;
		double [][] pb = getAllBidsInBidSpace();
		if(pb!=null)
			bidChart.setPossibleBids(pb);
		double [][] paretoB = getPareto();
		if(paretoB!=null)
			bidChart.setPareto(paretoB);
	}
	
	public void handleActionEvent(negotiator.events.ActionEvent evt) {
		System.out.println("Caught event "+evt+ "in ProgressUI");
		round+=1;
		if(round>biddingTable.getModel().getRowCount()){
			progressinfo.addRow();
		}
		//round = evt.getRound();
		biddingTable.getModel().setValueAt(round,round-1,0);
		biddingTable.getModel().setValueAt(evt.getAgentAsString(),round-1,1);
		biddingTable.getModel().setValueAt(evt.getNormalizedUtilityA(),round-1,2);
		biddingTable.getModel().setValueAt(evt.getNormalizedUtilityB(),round-1,3);
		//opponent model?
		
		//adding graph data:
		double [][] curveA = session.getNegotiationPathA();
		double [][] curveB = session.getNegotiationPathB();
		if(curveA!=null)
			bidChart.setBidSeriesA(curveA);
		if(curveB!=null)
			bidChart.setBidSeriesB(curveB);	
		
	}
	public void resetGUI(){
		//clear TextArea:
		logText.setText("");
		//clear graph
		bidChart.removeAllPlots();
		//clear table
		progressinfo.reset();
	}
	
	public void handleLogMessageEvent(LogMessageEvent evt) {
		addLoggingText(evt.getMessage());		
	}

	public void handeNegotiationSessionEvent(NegotiationSessionEvent evt) {
		// TODO Auto-generated method stub
		
	}	
}

/********************************************************************/

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
		System.out.println("temp length "+temp.length);
		for(int j=0;j<temp.length-1;j++){
			for(int i=0;i<colNames.length;i++){
				temp [j][i] = data [j][i];
				System.out.println("temp data "+temp [j][i]);
			}
		}
		data = temp;
		fireTableDataChanged();
	}
	
	public void reset()
	{
		data = new Object [6][colNames.length];
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
/********************************************************************/
/*
class MyBidCellRenderer implements TableCellRenderer {
	ProgressInfo progressinfo;
	
	
    public MyBidCellRenderer(ProgressInfo n) {	
    	progressinfo=n;  
    }
    
    // the default converts everything to string...
    public Object getTableCellRendererComponent(JTable table, Object value,
             boolean isSelected, boolean hasFocus, int row, int column) {
		return progressinfo.getValueAt(row,column);   
	}
}*/
