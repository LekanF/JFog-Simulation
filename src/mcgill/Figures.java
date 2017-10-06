package mcgill;
//package org.jfree.chart.demo;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jfree.chart.demo.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class Figures extends ApplicationFrame{

	public Figures(final String title) throws FileNotFoundException, IOException {
		super(title);
		// TODO Auto-generated constructor stub
		final CategoryDataset dataset = createDataSet();
		final JFreeChart chart = createChart(dataset);
		
		//add the chart to a panel...
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 370));
		setContentPane(chartPanel);
	}

	private JFreeChart createChart(final CategoryDataset dataset) {
		// TODO Auto-generated method stub
		final JFreeChart chart = ChartFactory.createBarChart(
				"Overload", 	// Chart title 
				"Fogs",				// Domain axis label
				"Tasks",			// range axis label
				dataset,				// data
				PlotOrientation.HORIZONTAL,// Orientation
				false,					// include legend
				true,
				false);
		//Background Color for chart
		chart.setBackgroundPaint(Color.lightGray);
		
		
		//get a reference to the plot for further customisation
		final CategoryPlot plot  = chart.getCategoryPlot();
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
		
		// change the auto tick unit selection to integer units only..
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRange(0.0, 800.0);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
				
		return chart;
	}

	private CategoryDataset createDataSet() throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		final Integer[][] data = Schedule.assignDevicesToFogs();//
//		final double[][] data = new double [][]{
//				{1.0, 43.0, 35.0, 58.0, 54.0, 77.0, 71.0, 89.0, 85.0, 23.0},
//				{54.0, 75.0, 63.0, 83.0, 43.0, 46.0, 27.0, 13.0, 14.0, 15.0}
////				{41.0, 33.0, 22.0, 34.0, 62.0, 32.0, 42.0, 34.0}
//		};

		return DatasetUtilities.createCategoryDataset("Tasks ", "Fog ", data);
		

		
	}

}
