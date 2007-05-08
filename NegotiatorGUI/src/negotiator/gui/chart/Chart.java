package negotiator.gui.chart;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.util.Properties;
import javax.swing.JFrame;


import jcckit.GraphicsPlotCanvas;
import jcckit.data.DataCurve;
import jcckit.data.DataPlot;
import jcckit.data.DataPoint;
import jcckit.util.ConfigParameters;
import jcckit.util.PropertiesBasedConfigData;

/**
 * @author Dmytro Tykhonov
 *
 */
public class Chart {
	JFrame fFrame;
	GraphicsPlotCanvas fPlotCanvas;
	DataPlot fDataPlot;
	public Chart() {
		GraphicsPlotCanvas fPlotCanvas = createPlotCanvas();

		fDataPlot = new DataPlot();
		fPlotCanvas.connect(fDataPlot);
		fFrame = new JFrame();
		fFrame.setSize(300,300);

		
		fFrame.setLayout(new BorderLayout());
		fFrame.add(fPlotCanvas.getGraphicsCanvas(), BorderLayout.CENTER);
//		fFrame.add(createControlPanel(), BorderLayout.SOUTH);
	}
	
	
	private GraphicsPlotCanvas createPlotCanvas() {
		Properties props = new Properties();
		ConfigParameters config
		= new ConfigParameters(new PropertiesBasedConfigData(props));
		props.put("plot/legendVisible", "false");
		props.put("plot/coordinateSystem/xAxis/minimum", "0");
		props.put("plot/coordinateSystem/xAxis/maximum", "1");
		props.put("plot/coordinateSystem/xAxis/axisLabel", "Agent A");
		props.put("plot/coordinateSystem/xAxis/ticLabelFormat", "%.1f%");		
		props.put("plot/coordinateSystem/yAxis/axisLabel", "Agent B");
		props.put("plot/coordinateSystem/yAxis/minimum", "0");
		props.put("plot/coordinateSystem/yAxis/maximum", "1");
		props.put("plot/coordinateSystem/yAxis/ticLabelFormat", "%.1f%");
		props.put("plot/curveFactory/definitions", "curve");
		props.put("plot/curveFactory/curve/withLine", "false");
		props.put("plot/curveFactory/curve/symbolFactory/className", 
		"jcckit.plot.BarFactory");
		props.put("plot/curveFactory/curve/symbolFactory/attributes/className", 
		"jcckit.graphic.ShapeAttributes");
		props.put("plot/curveFactory/curve/symbolFactory/attributes/fillColor", 
		"0xfe8000");
		props.put("plot/curveFactory/curve/symbolFactory/attributes/lineColor", 
		"0");
		props.put("plot/curveFactory/curve/symbolFactory/size", "0.08");
		props.put("plot/initialHintForNextCurve/className", 
		"jcckit.plot.PositionHint");
		props.put("plot/initialHintForNextCurve/position", "0 0.1");

		return new GraphicsPlotCanvas(config);
	}
	public void addCurve(String pCurveName, double[][] pValues) {
		try {
			DataCurve curve = new DataCurve(pCurveName);
			for(int i=0; i<pValues.length;i++)
				curve.addElement(new DataPoint(pValues[i][0],pValues[i][1]));
			fDataPlot.addElement(curve);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void show() {
		fFrame.setVisible(true);
	}
	public void hide() {
		fFrame.setVisible(false);
	}
}
