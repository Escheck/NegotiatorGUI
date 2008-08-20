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
	XYPlot plot;
	//empty constructor 
	public BidChart(){
	}
	public BidChart(double [][] possibleBids,double[][] pareto, double [][] bidSeriesA, double [][] bidSeriesB){
		this.pareto = pareto;
		this.possibleBids = possibleBids;
		this.bidSeriesA = bidSeriesA;
		this.bidSeriesB = bidSeriesB;
		chart = createOverlaidChart();
	}
	//returning the chart 
	public JFreeChart getChart(){
		return chart;
	}
	//set-Methods
	public void setPareto(double [][] pareto){
		this.pareto = pareto;
	}
	public void setPossibleBids(double [][] possibleBids){
		this.possibleBids = possibleBids;
	}
	public void setBidSeriesA(double [][] bidSeriesA){
		this.bidSeriesA = bidSeriesA;
	}
	public void setBidSeriesB(double [][] bidSeriesB){
		this.bidSeriesB = bidSeriesB;
	}
		
	/**
     * Creates an overlaid chart.
     *
     * @return The chart.
     */
    private JFreeChart createOverlaidChart() {

        // create plot ...
    	if (possibleBids!=null){
    		DefaultXYDataset data1 = new DefaultXYDataset();
    		data1.addSeries("all possible bids",possibleBids);
            // to get dots instead of a line we need a XYDotRenderer:
            final XYDotRenderer renderer1 = new XYDotRenderer();
            renderer1.setDotHeight(1);
            renderer1.setDotWidth(1);
            
            NumberAxis domainAxis = new NumberAxis("Agent B");
            ValueAxis rangeAxis = new NumberAxis("Agent A");
            plot = new XYPlot(data1, domainAxis, rangeAxis, renderer1);
            
    	}
    	
        // add a second dataset and renderer...
        DefaultXYDataset data2 = new DefaultXYDataset();
        data2.addSeries("pareto optimal bids",pareto);
        final XYItemRenderer renderer2 = new XYLineAndShapeRenderer();
        renderer2.setSeriesPaint(0, Color.RED);
        plot.setDataset(1, data2);
        plot.setRenderer(1, renderer2);
        
        // add a third dataset and renderer...
        DefaultXYDataset data3 = new DefaultXYDataset();
        data2.addSeries("Agent A's bids",bidSeriesA);
        final XYItemRenderer renderer3 = new XYLineAndShapeRenderer();
        plot.setDataset(2, data3);
        plot.setRenderer(2, renderer3);
        
        // add a third dataset and renderer...
        DefaultXYDataset data4 = new DefaultXYDataset();
        data2.addSeries("Agent B's bids",bidSeriesB);
        final XYItemRenderer renderer4 = new XYLineAndShapeRenderer();
        renderer4.setSeriesPaint(0, Color.ORANGE);
        plot.setDataset(3, data4);
        plot.setRenderer(3, renderer4);
        
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        // return a new chart containing the overlaid plot...
        return new JFreeChart("Overlaid Plot Example", JFreeChart.DEFAULT_TITLE_FONT, plot, true);

    }

    /**
     * Creates a sample datasets just for testing.
     *
     * @return The dataset.
     */
    private DefaultXYDataset createDataset1() {
    	DefaultXYDataset dataset = new DefaultXYDataset();
        // create dataset 1...
    	final int NUMBEROFPOSSIBLEBIDS = 10000;
    	double redSeries[][] = new double[2][NUMBEROFPOSSIBLEBIDS];
		
		//some sample data
		for (int i=0;i<NUMBEROFPOSSIBLEBIDS;i++){
			redSeries[0][i] = (float)Math.random();
			redSeries[1][i] = (float)Math.random();
		}
		// set the labels for the graph:
	    String redSeriesLabel = "possible bids";
	    dataset.addSeries(redSeriesLabel, redSeries);
        return dataset;

    }
    private DefaultXYDataset createDataset2(String label) {
    	DefaultXYDataset dataset = new DefaultXYDataset();
        // create dataset 1...
    	double redSeries[][] = new double[2][5];
		
		//some sample data
		for (int i=0;i<5;i++){
			redSeries[0][i] = (float)Math.random();
			redSeries[1][i] = (float)Math.random();
		}
		// set the labels for the graph:
	    String redSeriesLabel = label;
	    dataset.addSeries(redSeriesLabel, redSeries);
        return dataset;

    }

}
