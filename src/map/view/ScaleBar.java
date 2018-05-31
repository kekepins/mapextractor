package map.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class ScaleBar extends JComponent {
	
    private static final int PADDING_RIGHT = 100;

	private int zoom;
	private Color color;
	
	private static String[] zoomScaleTexts = {"10000km", "5000km", "2000km", "2000km", "1000km", 
		"500km", "200km", "100km",
		"50km", "20km", "10km", "5km", "2km", "2km", "1km",
		"500m", "200m", "100m"};  
	
	private static final int[] scaleZoomPixels = {64, 64, 51, 102, 102, 102, 81, 81,
		81, 65, 65, 65, 52, 105, 105,
		105, 83, 83, 83, 67, 67, 67};

	/**
	 * Constructor
	 */
	public ScaleBar(Color color)	{
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
	
}
