/*
 * Author: Max W. Y. Lam (Aug 1 2015)
 * Version: Milestone 1
 * 
 * */

package agents.anac.y2016.maxoops;

import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ShapeUtilities;

import Jama.Matrix;

public class PLOTComponent {

	public static void plotUnivariateTrend(PREDComponent exp) {
		int pts = exp.n;
		XYSeries val = new XYSeries("Smoothed Value");
		XYSeries train = new XYSeries("Time Series Data");
		train.add(1, exp.x.get(0, 0) * exp.scaler);
		for (int i = 1; i < pts; i++) {
			train.add(i + 1, exp.x.get(i, 0) * exp.scaler);
			val.add(i + 1, exp.y.get(i, 0) * exp.scaler);
		}
		Matrix pred = exp.predict(5);
		for (int i = 0; i < 5; i++) {
			val.add(i + pts + 1, pred.get(i, 0));
		}
		XYSeriesCollection data = new XYSeriesCollection(val);
		XYSeriesCollection trainData = new XYSeriesCollection(train);
		XYDifferenceRenderer fill = new XYDifferenceRenderer();
		fill.setPositivePaint(ChartColor.LIGHT_CYAN);
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Time-Series Exponential Smoothing", "X", "Y", trainData,
				PlotOrientation.VERTICAL, true, true, true);
		XYPlot addLines = chart.getXYPlot();
		addLines.setBackgroundPaint(ChartColor.WHITE);
		addLines.setDataset(1, data);
		XYLineAndShapeRenderer ren = new XYLineAndShapeRenderer();
		ren.setSeriesLinesVisible(0, true);
		ren.setSeriesShapesVisible(0, true);
		ren.setSeriesShape(0, ShapeUtilities.createDiagonalCross(3, 1));
		addLines.setRenderer(0, ren);
		XYLineAndShapeRenderer ren1 = new XYLineAndShapeRenderer();
		ren1.setSeriesLinesVisible(0, true);
		ren1.setSeriesShapesVisible(0, true);
		ren1.setSeriesShape(0, ShapeUtilities.createDiamond(3));
		addLines.setRenderer(1, ren1);
		addLines.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		final NumberAxis rangeAxis = (NumberAxis) addLines.getDomainAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setRange(0, pts + 6);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		JFrame plot = new JFrame("Time Series Plot");
		plot.setContentPane(chartPanel);
		plot.pack();
		RefineryUtilities.centerFrameOnScreen(plot);
		plot.setVisible(true);
	}

	public static void plotDataPoints(Matrix data, String title) {
		int pts = data.getRowDimension();
		XYSeries dataXYSeries = new XYSeries("Data");
		for (int i = 1; i < pts; i++) {
			dataXYSeries.add(i + 1, data.get(i, 0));
		}
		XYSeriesCollection dataXYSeriesCollection = new XYSeriesCollection(
				dataXYSeries);
		JFreeChart chart = ChartFactory.createXYLineChart(title, "t", "f(t)",
				dataXYSeriesCollection, PlotOrientation.VERTICAL, true, true,
				true);
		XYPlot addLines = chart.getXYPlot();
		addLines.setBackgroundPaint(ChartColor.WHITE);
		XYLineAndShapeRenderer ren = new XYLineAndShapeRenderer();
		ren.setSeriesLinesVisible(0, true);
		ren.setSeriesShapesVisible(0, true);
		addLines.setRenderer(0, ren);
		addLines.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		final NumberAxis rangeAxis = (NumberAxis) addLines.getDomainAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setRange(0, pts + 1);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		JFrame plot = new JFrame("Points Plot");
		plot.setContentPane(chartPanel);
		plot.pack();
		RefineryUtilities.centerFrameOnScreen(plot);
		plot.setVisible(true);
	}

	public static void plotDataPoints(ArrayList<Double> data, String title) {
		int pts = data.size();
		XYSeries dataXYSeries = new XYSeries("Data");
		for (int i = 1; i < pts; i++) {
			dataXYSeries.add(i + 1, data.get(i));
		}
		XYSeriesCollection dataXYSeriesCollection = new XYSeriesCollection(
				dataXYSeries);
		JFreeChart chart = ChartFactory.createXYLineChart(title, "t", "f(t)",
				dataXYSeriesCollection, PlotOrientation.VERTICAL, true, true,
				true);
		XYPlot addLines = chart.getXYPlot();
		addLines.setBackgroundPaint(ChartColor.WHITE);
		XYLineAndShapeRenderer ren = new XYLineAndShapeRenderer();
		ren.setSeriesLinesVisible(0, true);
		ren.setSeriesShapesVisible(0, false);
		addLines.setRenderer(0, ren);
		addLines.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		final NumberAxis rangeAxis = (NumberAxis) addLines.getDomainAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setRange(0, pts + 1);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		JFrame plot = new JFrame("Points Plot");
		plot.setContentPane(chartPanel);
		plot.pack();
		RefineryUtilities.centerFrameOnScreen(plot);
		plot.setVisible(true);
	}

}
