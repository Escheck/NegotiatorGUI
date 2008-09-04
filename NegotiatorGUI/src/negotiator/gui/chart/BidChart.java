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
	private DefaultXYDataset bidData = new DefaultXYDataset();
	
	//empty constructor; but: don't you always know the possible bids and the pareto before the 1st bid? 
	public BidChart(){
		//new JFreeChart("Bids", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		chart = createOverlaidChart();
		XYPlot plot = chart.getXYPlot();	    
	    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRange(0,1);
		NumberAxis domainAxis = (NumberAxis)plot.getDomainAxis(); 
		domainAxis.setRange(0,1);
	}
	public BidChart(double [][] possibleBids,double[][] pareto, double [][] bidSeriesA, double [][] bidSeriesB){
		this.pareto = pareto;
		this.possibleBids = possibleBids;
		this.bidSeriesA = bidSeriesA;
		this.bidSeriesB = bidSeriesB;
		chart = createOverlaidChart();
		//set the axis maximum to 1 since utilities cannot be higher than 1
		XYPlot plot = chart.getXYPlot();	    
	    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRange(0,1);
		NumberAxis domainAxis = (NumberAxis)plot.getDomainAxis(); 
		domainAxis.setRange(0,1);
	}
	//returning the chart 
	public JFreeChart getChart(){
		return chart;
	}
	//set-Methods
	public void setPareto(double [][] pareto){
		this.pareto = pareto;
		bidData.addSeries("pareto optimal bids",pareto);
	}
	public void setPossibleBids(double [][] possibleBids){
		this.possibleBids = possibleBids;
		possibleBidData.addSeries("all possible bids",possibleBids);
	}
	public void setBidSeriesA(double [][] bidSeriesA){
		this.bidSeriesA = bidSeriesA;
		bidData.addSeries("Agent A's bids",bidSeriesA);
	}
	public void setBidSeriesB(double [][] bidSeriesB){
		this.bidSeriesB = bidSeriesB;
		bidData.addSeries("Agent B's bids",bidSeriesB);
	}
			
	/**
     * Creates an overlaid chart.
     *
     * @return The chart.
     */
    private JFreeChart createOverlaidChart() {
    	NumberAxis domainAxis = new NumberAxis("Agent B");
        ValueAxis rangeAxis = new NumberAxis("Agent A");
        final XYDotRenderer renderer = new XYDotRenderer();
        renderer.setDotHeight(1);
        renderer.setDotWidth(1);
		final XYItemRenderer renderer1 = new XYLineAndShapeRenderer();
    	
		//create default plot, quick hack so that the graph panel is not empty
    	if(possibleBids==null||pareto==null||bidSeriesA==null||bidSeriesB==null){
    		System.out.println("no plot available at initialization");
    		double emptyDataset1 [][] = new double[2][0];
    		double emptyDataset2 [][] = new double[2][0];
    		possibleBidData.addSeries("",emptyDataset1);
    		bidData.addSeries("",emptyDataset2);
    		renderer1.setSeriesPaint(0, Color.WHITE);
            plot = new XYPlot(possibleBidData, domainAxis, rangeAxis, renderer);
            plot = new XYPlot(bidData, domainAxis, rangeAxis, renderer1);
    	}
        // create plots ...
    	if (possibleBids!=null){
    		possibleBidData.addSeries("all possible bids",possibleBids);
            // to get dots instead of a line we need a XYDotRenderer:
            //final XYDotRenderer renderer1 = new XYDotRenderer();
            //renderer1.setDotHeight(1);
            //renderer1.setDotWidth(1);
            plot = new XYPlot(possibleBidData, domainAxis, rangeAxis, renderer);
    	}
    	if (pareto!=null){
    		bidData.addSeries("pareto optimal bids",pareto);
	        final XYItemRenderer renderer2 = new XYLineAndShapeRenderer();
	        renderer2.setSeriesPaint(0, Color.RED);
	        plot.setDataset(1, bidData);
	        plot.setRenderer(1, renderer2);
    	}
    	if (bidSeriesA!=null){
    		bidData.addSeries("Agent A's bids",bidSeriesA);
		    final XYItemRenderer renderer3 = new XYLineAndShapeRenderer();
		    plot.setDataset(2, bidData);
		    plot.setRenderer(2, renderer3);
    	}
    	if (bidSeriesB!=null){
    		bidData.addSeries("Agent B's bids",bidSeriesB);
	        final XYItemRenderer renderer4 = new XYLineAndShapeRenderer();
	        renderer4.setSeriesPaint(0, Color.ORANGE);
	        plot.setDataset(3, bidData);
	        plot.setRenderer(3, renderer4);
    	}
    	
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        // return a new chart containing the overlaid plot...
        return new JFreeChart("Bids", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }
}
