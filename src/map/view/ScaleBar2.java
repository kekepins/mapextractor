package map.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class ScaleBar2 extends JComponent {
	
    private static final int PADDING_RIGHT = 100;

	private int zoom;
	private Color color;
/*
	private static double[] zoomScaleDistances = {10000., 5000., 2000., 2000., 1000., 500., 200., 100.,
		50., 20., 10., 5., 2., 2., 1.,
		0.5, 0.2, 0.1};*/
	
	private static String[] zoomScaleTexts = {"10000km", "5000km", "2000km", "2000km", "1000km", 
		"500km", "200km", "100km",
		"50km", "20km", "10km", "5km", "2km", "2km", "1km",
		"500m", "200m", "100m"};  
	
	private static final int[] scaleZoomPixels = {64, 64, 51, 102, 102, 102, 81, 81,
		81, 65, 65, 65, 52, 105, 105,
		105, 83, 83, 83, 67, 67, 67};

	/*
	private static final double PIX_METERS[] = new double[] { 156543.0339280410,
		78271.5169640205, 39135.7584820102, 19567.8792410051,
		9783.9396205026, 4891.9698102513, 2445.9849051256, 1222.9924525628,
		611.4962262814, 305.7481131407, 152.8740565704, 76.4370282852,
		38.2185141426, 19.1092570713, 9.5546285356, 4.7773142678, 
		2.3886571339, 1.1943285670, 0.5971642835 };	*/

	/**
	 * Constructor
	 */
	public ScaleBar2(Color color)	{
		setSize(100 + PADDING_RIGHT, 30);
		this.color = color;
		setOpaque(false);
	}

	public void paint(Graphics graphic)	{
	    String text =zoomScaleTexts[this.zoom];
        Rectangle2D bound = graphic.getFontMetrics().getStringBounds(text, graphic);
        graphic.setColor(this.color);
        graphic.drawLine(0, 5, scaleZoomPixels[this.zoom], 5);
        graphic.drawLine(0, 0, 0, 10);
        graphic.drawLine(scaleZoomPixels[this.zoom], 0, scaleZoomPixels[this.zoom], 10);
        graphic.drawLine(scaleZoomPixels[this.zoom]/2, 2, scaleZoomPixels[this.zoom]/2, 8);
         
        graphic.drawString(text, (int)(scaleZoomPixels[this.zoom]-bound.getWidth()/2), 23);
        graphic.drawString("0", 0, 23);

	}

	/**
	 * Update the scale level
	 * @param inZoom new zoom level
	 */
	public void updateScale(int inZoom)	{
		zoom = inZoom;
	}
	
	/*public static void main(String[] args) {
		System.out.println("Start");
		int zoom = 0;
		for ( double scaleDistance : zoomScaleDistances) {
			System.out.println("zoom " + zoom);
			System.out.println("zoomScaleDistances " + zoomScaleDistances[zoom]);
			System.out.println("zoomScaleTexts " + zoomScaleTexts[zoom]);
			System.out.println("pixels " + zoomScaleTexts[zoom]);
			double pixels = zoomScaleDistances[zoom]*1000 / PIX_METERS[zoom];
			System.out.println("pixels distance " + pixels);
			zoom++;
		}
			
	}
	*/
}
