package negotiator.gui.chart;

import java.awt.Font;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.date.SerialDate;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demonstration application showing a time series chart overlaid with a vertical XY bar chart.
 */
public class OverlaidXYPlotDemo extends ApplicationFrame {

    /**
     * Constructs a new demonstration application.
     *
     * @param title  the frame title.
     */
    public OverlaidXYPlotDemo(final String title) {

        super(title);
        final JFreeChart chart = createOverlaidChart();
        final ChartPanel panel = new ChartPanel(chart, true, true, true, true, true);
        panel.setPreferredSize(new java.awt.Dimension(800, 500));
        setContentPane(panel);

    }

    /**
     * Creates an overlaid chart.
     *
     * @return The chart.
     */
    private JFreeChart createOverlaidChart() {

        // create plot ...
        final DefaultXYDataset data1 = createDataset1();
        // to get dots instead of a line we need a XYDotRenderer:
        final XYDotRenderer renderer1 = new XYDotRenderer();
        renderer1.setDotHeight(1);
        renderer1.setDotWidth(1);
        
        final NumberAxis domainAxis = new NumberAxis("Agent B");
        final ValueAxis rangeAxis = new NumberAxis("Agent A");
        final XYPlot plot = new XYPlot(data1, domainAxis, rangeAxis, renderer1);
        
        // add a second dataset and renderer...
        final XYDataset data2 = createDataset2();
        final XYItemRenderer renderer2 = new XYLineAndShapeRenderer();
        
        plot.setDataset(1, data2);
        plot.setRenderer(1, renderer2);
        
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        // return a new chart containing the overlaid plot...
        return new JFreeChart("Overlaid Plot Example", JFreeChart.DEFAULT_TITLE_FONT, plot, true);

    }

    /**
     * Creates a sample dataset.
     *
     * @return The dataset.
     */
    private DefaultXYDataset createDataset1() {
    	DefaultXYDataset dataset = new DefaultXYDataset();
        // create dataset 1...
    	final int NUMBEROFPOSSIBLEBIDS = 100000;
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
    private DefaultXYDataset createDataset2() {
    	DefaultXYDataset dataset = new DefaultXYDataset();
        // create dataset 1...
    	double redSeries[][] = new double[2][5];
		
		//some sample data
		for (int i=0;i<5;i++){
			redSeries[0][i] = (float)Math.random();
			redSeries[1][i] = (float)Math.random();
		}
		// set the labels for the graph:
	    String redSeriesLabel = "pareto";
	    dataset.addSeries(redSeriesLabel, redSeries);
        return dataset;

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final OverlaidXYPlotDemo demo = new OverlaidXYPlotDemo("Overlaid XYPlot Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
