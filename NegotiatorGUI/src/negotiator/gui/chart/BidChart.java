package negotiator.gui.chart;

import java.awt.Color;

import javax.swing.SwingUtilities;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;


public class BidChart {
	
	private double [][] possibleBids;
	private double [][]pareto;
	private double [][] bidSeriesA_;
	private double [][] bidSeriesB_;
	private String agentAName = "Agent A";
	private String agentBName = "Agent B";
	private JFreeChart chart;
	private XYPlot plot;
	private DefaultXYDataset possibleBidData = new DefaultXYDataset();
	private DefaultXYDataset paretoData = new DefaultXYDataset();
	private DefaultXYDataset bidderAData = new DefaultXYDataset();
	private DefaultXYDataset bidderBData = new DefaultXYDataset();
	private DefaultXYDataset nashData = new DefaultXYDataset();
	private DefaultXYDataset kalaiData = new DefaultXYDataset();
	private DefaultXYDataset agreementData = new DefaultXYDataset();
	private DefaultXYDataset lastBidAData = new DefaultXYDataset();
	private DefaultXYDataset lastBidBData = new DefaultXYDataset();
	final XYDotRenderer dotRenderer = new XYDotRenderer();
	final XYDotRenderer nashRenderer = new XYDotRenderer();
	final XYDotRenderer kalaiRenderer = new XYDotRenderer();
	final XYDotRenderer agreementRenderer = new XYDotRenderer();
	//final XYItemRenderer agreementRenderer = new XYLineAndShapeRenderer(false, true);
	final XYDotRenderer lastBidARenderer = new XYDotRenderer();
	final XYDotRenderer lastBidBRenderer = new XYDotRenderer();
	final XYItemRenderer paretoRenderer = new XYLineAndShapeRenderer(true,false);
	final XYItemRenderer lineARenderer = new XYLineAndShapeRenderer();
	final XYItemRenderer lineBRenderer = new XYLineAndShapeRenderer();
	private NumberAxis domainAxis ;
    private ValueAxis rangeAxis;

	//empty constructor; but: don't you always know the possible bids and the pareto before the 1st bid? 
	public BidChart(){

		BidChart1();
		
	}
	public BidChart(String agentAname, String agentBname, double [][] possibleBids,double[][] pareto){		
		this.agentAName = agentAname;
		this.agentBName = agentBname;
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
		paretoData.addSeries("Pareto efficient frontier",pareto);
	}
	
	public void setPossibleBids(double [][] possibleBids){
		this.possibleBids = possibleBids;
		possibleBidData.addSeries("all possible bids",possibleBids);
	}
	
	public void setLastBidAData(double [][] lastBid)
	{
		lastBidAData.addSeries("Last bid by A", lastBid);
	}
	
	public void setLastBidBData(double [][] lastBid)
	{
		lastBidBData.addSeries("Last bid by B", lastBid);
	}
	
	public void setBidSeriesA(double [][] bidSeriesA){
		this.bidSeriesA_ = bidSeriesA;
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
				bidderAData.addSeries("Agent A's bids",bidSeriesA_);
		    }
		});		
   
	}
        
	public void setBidSeriesB(double [][] bidSeriesB){
		this.bidSeriesB_ = bidSeriesB;
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
				bidderBData.addSeries("Agent B's bids",bidSeriesB_);
		    }
		});		
		

	}
	
	public void setNash(double[][]nash){
		nashData.addSeries("Nash Point",nash);
	}
	public void setKalai(double[][]kalai){
		nashData.addSeries("Kalai Point",kalai);
	}
	public void setAgreementPoint(double[][]agreement){
		agreementData.addSeries("Agreement",agreement);
	}
	
	public void removeAllPlots(){
		if(bidderAData.getSeriesCount()!=0)
			bidderAData.removeSeries("Bids of "+ agentAName);
		if(bidderBData.getSeriesCount()!=0)
			bidderBData.removeSeries("Bids of " + agentBName);
		if(agreementData.getSeriesCount()!=0)
			agreementData.removeSeries("Agreement");
		
	}
			
	/**
     * Creates an overlaid chart.
     *
     * @return The chart.
     */
    private JFreeChart createOverlaidChart() {
    	domainAxis = new NumberAxis(agentAName);
        rangeAxis = new NumberAxis(agentBName);
        dotRenderer.setDotHeight(2);
        dotRenderer.setDotWidth(2);
        nashRenderer.setDotHeight(5);
        nashRenderer.setDotWidth(5);
        nashRenderer.setSeriesPaint(0,Color.black);
        kalaiRenderer.setDotHeight(5);
        kalaiRenderer.setDotWidth(5);
        kalaiRenderer.setSeriesPaint(0,Color.pink);
        paretoRenderer.setSeriesPaint(0, Color.RED);
        lineARenderer.setSeriesPaint(0, Color.GREEN);
        lineBRenderer.setSeriesPaint(0, Color.BLUE);
        agreementRenderer.setDotHeight(10);
        agreementRenderer.setDotWidth(10);
        //agreementRenderer.setSeriesShape(0, new Ellipse2D.Float(10.0f, 10.0f, 10.0f, 10.0f));
        agreementRenderer.setSeriesPaint(0, Color.RED);
        lastBidARenderer.setSeriesPaint(0, Color.YELLOW);
        lastBidARenderer.setDotHeight(3);
        lastBidARenderer.setDotWidth(3);
        lastBidBRenderer.setSeriesPaint(0, Color.ORANGE);
        lastBidBRenderer.setDotHeight(3);
        lastBidBRenderer.setDotWidth(3);
       
		//create default plot, quick hack so that the graph panel is not empty
    	if(possibleBids!=null){
    		possibleBidData.addSeries("all possible bids",possibleBids);
//    		lastBidData.addSeries("Last bid", )
    	}
    	if (pareto!=null){
        	setPareto(pareto);   
        }
        // create plot ...
    	plot = new XYPlot(possibleBidData, domainAxis, rangeAxis, dotRenderer);
    	plot.setDataset(2, paretoData);
        plot.setRenderer(2, paretoRenderer);
        
    /*    DefaultXYDataset bidderADataSet = new DefaultXYDataset();
        bidderADataSet.addSeries("Bidder A", bidderAData);
        DefaultXYDataset bidderAData = new DefaultXYDataset();
        bidderAData.addSeries("Bidder B", bidderBData.toArray());*/
        		
    	plot.setDataset(3, bidderAData);
	    plot.setRenderer(3, lineARenderer);
	    plot.setDataset(4, bidderBData);
	    plot.setRenderer(4, lineBRenderer);
	   
	    plot.setDataset(5, nashData);
	    plot.setRenderer(5, nashRenderer);
	    plot.setDataset(6, kalaiData);
	    plot.setRenderer(6, kalaiRenderer);
	    plot.setDataset(7, agreementData);
	    plot.setRenderer(7, agreementRenderer);
	    plot.setDataset(8, lastBidAData);
	    plot.setRenderer(8, lastBidARenderer);
	    plot.setDataset(9, lastBidBData);
	    plot.setRenderer(9, lastBidBRenderer);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        // return a new chart containing the overlaid plot...
        JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chart.setBackgroundPaint(new Color(255,255,255));
        return chart;
    }
    public void setAgentAName (String value) {
    	agentAName = value;
    	domainAxis.setLabel(agentAName);
    }
    public void setAgentBName (String value) {
    	agentBName = value;
    	rangeAxis.setLabel(agentBName);
    }
}
