package negotiator.gui.chart;

import java.awt.Color;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.axis.*;
import org.jfree.data.xy.*;


public class BidChart {
	
	private double [][] possibleBids;
	private double [][]pareto;
	private double [][] bidSeriesA;
	private double [][] bidSeriesB;
	private JFreeChart chart;
	private XYPlot plot;
	private DefaultXYDataset possibleBidData = new DefaultXYDataset();
	private DefaultXYDataset paretoData = new DefaultXYDataset();
	private DefaultXYDataset bidderAData = new DefaultXYDataset();
	private DefaultXYDataset bidderBData = new DefaultXYDataset();
	final XYDotRenderer dotRenderer = new XYDotRenderer();
	final XYItemRenderer paretoRenderer = new XYLineAndShapeRenderer(true,false);
	final XYItemRenderer lineARenderer = new XYLineAndShapeRenderer();
	final XYItemRenderer lineBRenderer = new XYLineAndShapeRenderer();
	
	//empty constructor; but: don't you always know the possible bids and the pareto before the 1st bid? 
	public BidChart(){
		BidChart1();
	}
	public BidChart(double [][] possibleBids,double[][] pareto){
		this.pareto = pareto;
		this.possibleBids = possibleBids;
		BidChart1();
	}
	public void BidChart1(){
		chart = createOverlaidChart();  
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRange(0,1.1);
		NumberAxis domainAxis = (NumberAxis)plot.getDomainAxis(); 
		domainAxis.setRange(0,1.1);
	}
	//returning the chart 
	public JFreeChart getChart(){
		return chart;
	}
	
	//set-Methods
	public void setPareto(double [][] pareto){
		this.pareto = pareto;
		paretoData.addSeries("pareto optimal bids",pareto);
	}
	
	public void setPossibleBids(double [][] possibleBids){
		this.possibleBids = possibleBids;
		possibleBidData.addSeries("all possible bids",possibleBids);
	}
	
	public void setBidSeriesA(double [][] bidSeriesA){
		this.bidSeriesA = bidSeriesA;
		bidderAData.addSeries("Agent A's bids",bidSeriesA);   
	}
        
	public void setBidSeriesB(double [][] bidSeriesB){
		this.bidSeriesB = bidSeriesB;
		bidderBData.addSeries("Agent B's bids",bidSeriesB);
	}
			
	/**
     * Creates an overlaid chart.
     *
     * @return The chart.
     */
    private JFreeChart createOverlaidChart() {
    	NumberAxis domainAxis = new NumberAxis("Agent B");
        ValueAxis rangeAxis = new NumberAxis("Agent A");
        dotRenderer.setDotHeight(2);
        dotRenderer.setDotWidth(2);
        paretoRenderer.setSeriesPaint(0, Color.RED);
        lineARenderer.setSeriesPaint(0, Color.GREEN);
        lineBRenderer.setSeriesPaint(0, Color.BLUE);
        
		//create default plot, quick hack so that the graph panel is not empty
    	if(possibleBids!=null){
    		possibleBidData.addSeries("all possible bids",possibleBids);
    	}
    	if (pareto!=null){
        	setPareto(pareto);   
        }
        // create plot ...
    	plot = new XYPlot(possibleBidData, domainAxis, rangeAxis, dotRenderer);
    	plot.setDataset(2, paretoData);
        plot.setRenderer(2, paretoRenderer);
    	plot.setDataset(3, bidderAData);
	    plot.setRenderer(3, lineARenderer);
	    plot.setDataset(4, bidderBData);
	    plot.setRenderer(4, lineBRenderer);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        // return a new chart containing the overlaid plot...
        return new JFreeChart("Bids", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }
}
