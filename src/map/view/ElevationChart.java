package map.view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import map.gps.GpsUtility;
import map.model.GpxPoint;

@SuppressWarnings("serial")
public class ElevationChart extends JFrame {
	
	public ElevationChart(List<GpxPoint> editionTrace) {
		super("XY Line Chart Example with JFreechart");
		JPanel chartPanel = createChartPanel(editionTrace);
		add(chartPanel, BorderLayout.CENTER);

		setSize(640, 480);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	private JPanel createChartPanel(List<GpxPoint> editionTrace) {
		String chartTitle = "Profile";
		String xAxisLabel = "Distance";
		String yAxisLabel = "Altitude";

		XYDataset dataset = createDataset(editionTrace);

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset);

		return new ChartPanel(chart);
	}

	private XYDataset createDataset(List<GpxPoint> trace) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("Deniv");
		double distance = 0.; 
		GpxPoint lastPoint = null;
		if ( trace != null ) {
			for (GpxPoint point : trace ) {
				if ( point != null && point.getAltitude() != null ) {
					if ( lastPoint == null ) {
						lastPoint = point;
					}
					else {
						distance += GpsUtility.calculateDistance(lastPoint, point);
						lastPoint = point ;
					}
					
					series.add( distance, point.getAltitude() );
				}
			}
		}
		
		dataset.addSeries(series);
		return dataset;
	}

}
