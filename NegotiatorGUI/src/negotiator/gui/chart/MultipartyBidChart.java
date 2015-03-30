package negotiator.gui.chart;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.SwingUtilities;


import negotiator.Party;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

public class MultipartyBidChart {
	
	private int maxRound=100; // default is 100
	private ArrayList<String> partyNames;
	
	private JFreeChart chart;
	private Color[] possibleLineColors= {Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN,Color.YELLOW,Color.MAGENTA, Color.PINK, Color.YELLOW, Color.DARK_GRAY,
			Color.BLACK, Color.RED, Color.LIGHT_GRAY, Color.lightGray};
	
	private NumberAxis roundAxis;
	private ValueAxis  utilityAxis;
	private XYPlot plot;
	private ArrayList<XYItemRenderer> partyUtilityLines;
	 
	private ArrayList<DefaultXYDataset> partyDataList;
    private DefaultXYDataset agreementDataList=new DefaultXYDataset();	
	private DefaultXYDataset possibleBidData=new DefaultXYDataset();
	private DefaultXYDataset nashData=new DefaultXYDataset();
	
	final XYDotRenderer agreementRender=new  XYDotRenderer();
	final XYItemRenderer nashRender=new XYLineAndShapeRenderer();;
	final XYDotRenderer dotRenderer=new XYDotRenderer();
	
	
	public MultipartyBidChart(ArrayList<String> partyNames, int maxRound) {
		this.maxRound=maxRound;		
		this.partyNames=partyNames;
		chart=createOverlaidChart(partyNames);
		plot.getDomainAxis().setRange(0, maxRound+1);
		plot.getRangeAxis().setRange(0.0, 1.1);
		
	}


	public MultipartyBidChart(ArrayList<String> partyNames) {
		this.partyNames=partyNames;
		chart=createOverlaidChart(partyNames);
		plot.getDomainAxis().setRange(0, maxRound+1);
		plot.getRangeAxis().setRange(0.0, 1.1);
	}
	
		
	public JFreeChart getChart() {
		return chart;
	}
	
	public void setMaxRound(int round) {
		maxRound=round;
		plot.getDomainAxis().setRange(0, maxRound);
	}
	
	public void setBidSeries(final ArrayList<double [][]> bidSeries){
		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	for (int i=0; i<bidSeries.size()-1; i++) {
		    		partyDataList.get(i).addSeries(partyNames.get(i), bidSeries.get(i));	    		
		    	}				
		    	partyDataList.get(bidSeries.size()-1).addSeries("Product", bidSeries.get(bidSeries.size()-1));
		    }
		});		
		

	}
	
	public void setNashSeries(final double[][] nashSeries) {
		nashData.addSeries("Nash", nashSeries);
	}
	
	public void setAgreementPoints(double[][] agreementSeries) {
		agreementDataList.addSeries("Agreements",agreementSeries);
	}
	
	
	private JFreeChart createOverlaidChart(ArrayList<String> partyNames) {
		
		roundAxis= new NumberAxis("Round");
		utilityAxis= new NumberAxis("Utility");
		partyUtilityLines=new ArrayList<XYItemRenderer>(partyNames.size());
		partyDataList= new ArrayList<DefaultXYDataset>(partyNames.size());
				
		// If partyNames includes more than 13 items, may need to modify the following part
		for (int i=0; i<=partyNames.size(); i++) {
			XYItemRenderer currentRenderer=new XYLineAndShapeRenderer();
			currentRenderer.setSeriesPaint(0,possibleLineColors[i]);
			partyUtilityLines.add(currentRenderer);			
		}
		
		agreementRender.setDotHeight(10);
		agreementRender.setDotWidth(10);
		agreementRender.setSeriesPaint(0, Color.RED);
		
		nashRender.setSeriesPaint(0, Color.BLACK);
		
		dotRenderer.setDotHeight(2);
		dotRenderer.setDotWidth(2);
		// createFrom plot
		plot=new XYPlot(possibleBidData,roundAxis,utilityAxis,dotRenderer);
		
		
		plot.setDataset(2,nashData);
		plot.setRenderer(2, nashRender);
				
		int index=0;
		for (;index<=partyNames.size(); index++) {
			partyDataList.add(index,new DefaultXYDataset());
			plot.setDataset(index+3, partyDataList.get(index));
			plot.setRenderer(index+3, partyUtilityLines.get(index));	
		}
		
		index=index+4+partyNames.size();
		
		plot.setDataset(index, agreementDataList);
		plot.setRenderer(index,agreementRender);
		
		// index should be increased if you want to add something else...
		
		
		JFreeChart chart=new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		chart.setBackgroundPaint(new Color(255,255,255));
		return chart;
		
	}

}
