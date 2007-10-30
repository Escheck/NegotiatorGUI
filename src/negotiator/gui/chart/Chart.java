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
		props.put("plot/curveFactory/definitions" , "def1 def2 def3 def4 def5 def6 def7 def8 def9 def10 def11 def12 def13 def14 def15");
		props.put("plot/curveFactory/def1/initialHintForNextPoint/className",
		"jcckit.plot.ShapeAttributesHint");
		props.put("plot/curveFactory/def1/initialHintForNextPoint/initialAttributes/fillColor", 
		"0x50a");
//		props.put("plot/curveFactory/curve/initialHintForNextPoint/fillColorHSBIncrement", 
//		"0.0 0.0 0.018");
		props.put("plot/curveFactory/def1/withLine", "false");
		props.put("plot/curveFactory/def1/symbolFactory/className", 
		"jcckit.plot.CircleSymbolFactory");
		props.put("plot/curveFactory/def1/symbolFactory/size", "0.007");
		
		//Pareto
		props.put("plot/curveFactory/def2/", "defaultDefinition/");
		props.put("plot/curveFactory/def2/symbolFactory/className" , "jcckit.plot.CircleSymbolFactory");
		props.put("plot/curveFactory/def2/symbolFactory/size", "0.001");		
		props.put("plot/curveFactory/def2/withLine", "true");
		props.put("plot/curveFactory/def2/symbolFactory/attributes/fillColor", "0x8000");
		props.put("plot/curveFactory/def2/symbolFactory/attributes/lineColor" , "");
		
		//Nash
		
		props.put("plot/curveFactory/def3/initialHintForNextPoint/className",
		"jcckit.plot.ShapeAttributesHint");
		props.put("plot/curveFactory/def3/initialHintForNextPoint/initialAttributes/fillColor", 
		"0x00FF00");
//		props.put("plot/curveFactory/curve/initialHintForNextPoint/fillColorHSBIncrement", 
//		"0.0 0.0 0.018");
		props.put("plot/curveFactory/def3/withLine", "false");
		props.put("plot/curveFactory/def3/symbolFactory/className", 
		"jcckit.plot.SquareSymbolFactory");
		
		props.put("plot/curveFactory/def3/symbolFactory/size", "0.007");
		//Kalai
		props.put("plot/curveFactory/def4/initialHintForNextPoint/className",
		"jcckit.plot.ShapeAttributesHint");
		props.put("plot/curveFactory/def4/initialHintForNextPoint/initialAttributes/fillColor", 
		"0xFF0000");
//		props.put("plot/curveFactory/curve/initialHintForNextPoint/fillColorHSBIncrement", 
//		"0.0 0.0 0.018");
		props.put("plot/curveFactory/def4/withLine", "false");
		props.put("plot/curveFactory/def4/symbolFactory/className", 
		"jcckit.plot.SquareSymbolFactory");
		
		props.put("plot/curveFactory/def4/symbolFactory/size", "0.007");
		//Negotiation paths
		props.put("plot/curveFactory/def5/", "defaultDefinition/");
		props.put("plot/curveFactory/def6/", "defaultDefinition/");		  
		props.put("plot/curveFactory/def7/", "defaultDefinition/");
		props.put("plot/curveFactory/def8/", "defaultDefinition/");
		props.put("plot/curveFactory/def9/", "defaultDefinition/");
		props.put("plot/curveFactory/def10/", "defaultDefinition/");
		props.put("plot/curveFactory/def11/", "defaultDefinition/");
		props.put("plot/curveFactory/def12/", "defaultDefinition/");
		props.put("plot/curveFactory/def13/", "defaultDefinition/");
		props.put("plot/curveFactory/def14/", "defaultDefinition/");
		props.put("plot/curveFactory/def15/", "defaultDefinition/");
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
