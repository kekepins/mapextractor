package map.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import map.conf.Configuration;
import map.model.GpsPoint;
import map.model.GpxPoint;
import map.model.TileInfo;
import map.tilemanager.TilesProvider;

/**
 * Map view
 * 
 */
@SuppressWarnings("serial")
public class JMapViewer extends JPanel {

	public static final int TILE_SIZE = 256;
	
	public static BufferedImage LOADING_IMAGE;
    public static BufferedImage ERROR_IMAGE;

    static {
        try {
            LOADING_IMAGE = ImageIO.read(JMapViewer.class.getResourceAsStream("images/hourglass.png"));
            ERROR_IMAGE = ImageIO.read(JMapViewer.class.getResourceAsStream("images/error.png"));
        } catch (Exception e1) {
            LOADING_IMAGE = null;
            ERROR_IMAGE = null;
        }
    }
    
    private List<TilesProvider> tilesProviders;
    private TilesProvider currentTilesProvider;
    
 	/**
	 * Vectors for clock-wise tile painting
	 */
	protected static final Point[] move = { new Point(1, 0), new Point(0, 1),
			new Point(-1, 0), new Point(0, -1) };


	private List<GpxPoint> gpsTrace;
	private List<GpxPoint> editionTrace;
	private int selectedEditionPoint = -1;

	private boolean isTileGridVisible;
	private boolean isGpsTraceVisible = false;
	private boolean isEditionMode = false;

	//private TilesManager tilesManager;
	private String tileType;

	// Zoom levels
	private int maxZoom = 15;
	private int minZoom = 1;
	private int zoom;

	private boolean downloadTiles;

	// private ScaleBar scaleBar;
	private ScaleBar2 scaleBar;
	
	private GpsPoint nearestPoint;


	/**
	 * x- and y-position of the center of this map-panel on the world map
	 * denoted in screen pixel regarding the current zoom level.
	 */
	protected Point center;

	// zoom
	protected JSlider zoomSlider;
	protected JButton zoomInButton;
	protected JButton zoomOutButton;

	// Events
	private DataChangedListener dataChangedListener;
	private EditionPointAddedListener editionPointListener;

	// Display colors
	private Color gpsTraceColor;
	private Color editTraceColor;

	// JobDispatcher jobDispatcher;

	/**
	 * Creates a standard {@link JMapViewer} instance that can be controlled via
	 * mouse: hold right mouse button for moving, double click left mouse button
	 * or use mouse wheel for zooming. Loaded tiles are stored the
	 * {@link MemoryTileCache} and the tile loader uses 4 parallel threads for
	 * retrieving the tiles.
	 */
	public JMapViewer(DataChangedListener dataChangedListener,
			EditionPointAddedListener editionPointListener,
			List<GpxPoint> edtionTrace) {
		new DefaultMapController(this).addListeners();
		this.dataChangedListener = dataChangedListener;
		this.editionPointListener = editionPointListener;
		this.editionTrace = edtionTrace;
		isTileGridVisible = true;
		downloadTiles = false;
		setLayout(null);
		initializeZoomSlider();
		scaleBar = new ScaleBar2(Color.black);
		scaleBar.setLocation(10, 30);
		gpsTraceColor = Color.red;
		editTraceColor = Color.green;
		tilesProviders = Configuration.getConfiguration().getTilesProviders();
		currentTilesProvider = tilesProviders.get(0);
		add(scaleBar);
	}

	protected void initializeZoomSlider() {
		zoomSlider = new JSlider(minZoom, this.getMaxZoom());
		zoomSlider.setOrientation(JSlider.VERTICAL);
		// zoomSlider.setBounds(10, 10, 30, 150);
		zoomSlider.setBounds(10, 110, 30, 150);
		zoomSlider.setOpaque(false);
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setZoom(zoomSlider.getValue());
			}
		});
		add(zoomSlider);
		int size = 18;
		try {
			ImageIcon icon = new ImageIcon(getClass().getResource(
					"images/plus.png"));
			zoomInButton = new JButton(icon);
		} catch (Exception e) {
			zoomInButton = new JButton("+");
			zoomInButton.setFont(new Font("sansserif", Font.BOLD, 9));
			zoomInButton.setMargin(new Insets(0, 0, 0, 0));
		}
		// zoomInButton.setBounds(4, 155, size, size);
		zoomInButton.setBounds(4, 255, size, size);
		zoomInButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				zoomIn();
			}
		});
		add(zoomInButton);
		try {
			ImageIcon icon = new ImageIcon(getClass().getResource(
					"images/minus.png"));
			zoomOutButton = new JButton(icon);
		} catch (Exception e) {
			zoomOutButton = new JButton("-");
			zoomOutButton.setFont(new Font("sansserif", Font.BOLD, 9));
			zoomOutButton.setMargin(new Insets(0, 0, 0, 0));
		}
		// zoomOutButton.setBounds(8 + size, 155, size, size);
		zoomOutButton.setBounds(8 + size, 255, size, size);
		zoomOutButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				zoomOut();
			}
		});
		add(zoomOutButton);
	}

	public void setDisplayPositionByLatLon(double lat, double lon, int zoom) {
		setDisplayPositionByLatLon(new Point(getWidth() / 2, getHeight() / 2),
				lat, lon, zoom);
	}

	/**
	 * Changes the map pane so that the specified coordinate at the given zoom
	 * level is displayed on the map at the screen coordinate
	 * <code>mapPoint</code>.
	 * 
	 * @param mapPoint
	 *            point on the map denoted in pixels where the coordinate should
	 *            be set
	 */
	public void setDisplayPositionByLatLon(Point mapPoint, double lat,
			double lon, int zoom) {
		int x = GpsUtility.LonToX(lon, zoom);
		int y = GpsUtility.LatToY(lat, zoom);
		setDisplayPosition(mapPoint, x, y, zoom);
	}

	public void setDisplayPosition(int x, int y, int zoom) {
		setDisplayPosition(new Point(getWidth() / 2, getHeight() / 2), x, y,
				zoom);
	}

	public void setDisplayPosition(Point mapPoint, int x, int y, int zoom) {
		if (zoom > this.getMaxZoom() || zoom < minZoom)
			return;

		// Get the plain tile number
		Point p = new Point();
		p.x = x - mapPoint.x + getWidth() / 2;
		p.y = y - mapPoint.y + getHeight() / 2;
		center = p;
		setIgnoreRepaint(true);
		try {
			int oldZoom = this.zoom;
			this.zoom = zoom;
			if (oldZoom != zoom) {
				zoomChanged(oldZoom);
			}
			if (zoomSlider.getValue() != zoom) {
				zoomSlider.setValue(zoom);
			}
		} finally {
			setIgnoreRepaint(false);
			repaint();
		}
	}



	public Point2D.Double getPosition() {
		double lon = GpsUtility.XToLon(center.x, zoom);
		double lat = GpsUtility.YToLat(center.y, zoom);
		return new Point2D.Double(lat, lon);
	}

	public Point2D.Double getPosition(Point mapPoint) {
		int x = center.x + mapPoint.x - getWidth() / 2;
		int y = center.y + mapPoint.y - getHeight() / 2;
		double lon = GpsUtility.XToLon(x, zoom);
		double lat = GpsUtility.YToLat(y, zoom);
		return new Point2D.Double(lat, lon);
	}

	/**
	 * Calculates the position on the map of a given coordinate
	 * 
	 * @param lat
	 * @param lon
	 * @return point on the map or <code>null</code> if the point is not visible
	 */
	public Point getMapPosition(double lat, double lon) {
		int x = GpsUtility.LonToX(lon, zoom);
		int y = GpsUtility.LatToY(lat, zoom);
		System.out.println( "zoom " + zoom + "lat " + lat + " long " + lon);
		System.out.println( "getMapPosition " + x + ":" + y);
		x -= center.x - getWidth() / 2;
		y -= center.y - getHeight() / 2;
		System.out.println( "After getMapPosition " + x + ":" + y);
		if (x < 0 || y < 0 || x > getWidth() || y > getHeight()) {
			System.out.println( "return null");
			return null;
		}
		System.out.println( "return point");
		return new Point(x, y);
	}

	@Override
	protected void paintComponent(Graphics graphic) {

		super.paintComponent(graphic);
		System.out.println("Paint component");

		drawTiles(graphic);

		// outer border of the map

		int mapSize = TILE_SIZE << zoom;
		graphic.drawRect((getWidth() / 2) - center.x, (getHeight() / 2) - center.y, mapSize, mapSize);

		if (isGpsTraceVisible && gpsTrace != null) {
			drawGps(gpsTrace, graphic, gpsTraceColor, false, -1);
		}

		if (isEditionMode && editionTrace.size() > 0) {
			drawGps(editionTrace, graphic, editTraceColor, true,selectedEditionPoint);
		}
		
		if ( nearestPoint != null ) {
			Color graphPointColor = new Color(150, 50, 50, 180);
			graphic.setColor(graphPointColor);
			Point p = getMapPosition(nearestPoint.getLatitude(), nearestPoint.getLongitude());
			if (p != null) {
				int x = p.x - 10 / 2;
				int y = p.y - 10 / 2;
				graphic.fillOval(x, y, 10, 10);
			}
		}

	}
	
	private void drawTiles(Graphics graphic) {
		int iMove = 0;

		// Dans quel tile se trouve le centre ?
		int currentTileX = center.x / TILE_SIZE;
		int currentTileY = center.y / TILE_SIZE;

		// Offset du centre en pixel
		int offsetXPixel = (center.x % TILE_SIZE);
		int offsetYPixel = (center.y % TILE_SIZE);

		System.out.println("Center " + center.x + ":" + center.y);
		System.out.println("currentTileX " + currentTileX + ";currentTileY "
				+ currentTileY + ";offx " + offsetXPixel + ";offy "
				+ offsetYPixel);

		int currentPosXPixel = (getWidth() / 2) - offsetXPixel;
		int currentPosYPixel = (getHeight() / 2) - offsetYPixel;

		//System.out.println("currentPosXPixel " + currentPosXPixel + ";currentPosYPixel " + currentPosYPixel);

		// start top
		if (offsetYPixel < (TILE_SIZE - offsetYPixel)) {

			// start left
			if (offsetXPixel < (TILE_SIZE - offsetXPixel)) {
				iMove = 2;
			} else {
				iMove = 3;
			}
		} else {
			if (offsetXPixel < (TILE_SIZE - offsetXPixel)) {
				iMove = 1;
			} else {
				iMove = 0;
			}
		} // calculate the visibility borders

		System.out.println("imove " + iMove);

		// Pixels Limits from -TILE_SIZE to width or Height
		int xMinPixel = -TILE_SIZE;
		int yMinPixel = -TILE_SIZE;
		int xMaxPixel = getWidth();
		int yMaxPixel = getHeight();

		/*System.out.println("xMinPixel " + xMinPixel + ";xMaxPixel " + xMaxPixel
				+ ";yMinPixel " + yMinPixel + ";yMaxPixel " + yMaxPixel);*/
		

		// paint the tiles in a spiral, starting from center of the map
		boolean painted = true;
		int x = 0;

		while (painted) {

			painted = false;
			for (int i = 0; i < 4; i++) {
				if (i % 2 == 0) {
					x++;
				}

				for (int j = 0; j < x; j++) {
					if (xMinPixel <= currentPosXPixel && currentPosXPixel <= xMaxPixel &&
						yMinPixel <= currentPosYPixel && currentPosYPixel <= yMaxPixel) {

	
						// tile is visible
						TileInfo tile = getTile(currentTileX, currentTileY,	zoom, false);

						if (tile != null) {

							painted = true;
							BufferedImage image = tile.getImage();
							graphic.drawImage(image, currentPosXPixel,
									currentPosYPixel, null);
						}

						if (isTileGridVisible) {

							graphic.drawRect(currentPosXPixel,
									currentPosYPixel, TILE_SIZE, TILE_SIZE);

							String tileInfo = "" + currentTileX + "*"
									+ currentTileY;
							graphic.drawChars(tileInfo.toCharArray(), 0,
									tileInfo.length(), currentPosXPixel + 5,
									currentPosYPixel + 12);
						}

					}

					Point p = move[iMove];
					currentPosXPixel += p.x * TILE_SIZE;
					currentPosYPixel += p.y * TILE_SIZE;
					currentTileX += p.x;
					currentTileY += p.y;

				}

				iMove = (iMove + 1) % move.length;
			}
		}
	}

	private void drawGps(List<GpxPoint> gpxPoints, Graphics graphic,
			Color color, boolean displayPoint, int selectedPoint) {

		Graphics2D g2D = (Graphics2D) graphic;
		g2D.setStroke(new BasicStroke(2f));
		graphic.setColor(color);

		List<Integer> xPix = new ArrayList<Integer>();
		List<Integer> yPix = new ArrayList<Integer>();
		for (GpxPoint point : gpxPoints) {
			Point p = getMapPosition(point.getLatitude(), point.getLongitude());
			if (p != null) {
				xPix.add(p.x);
				yPix.add(p.y);
			} else {
				// draw
				graphic.drawPolyline(toIntArray(xPix), toIntArray(yPix),
						xPix.size());
				xPix.clear();
				yPix.clear();
			}
		}

		if (xPix.size() > 1) {
			graphic.drawPolyline(toIntArray(xPix), toIntArray(yPix),
					xPix.size());
		}

		if (displayPoint) {
			Color graphPointColor = new Color(150, 50, 50, 180);
			g2D.setColor(graphPointColor);
			for (GpxPoint point : gpxPoints) {
				Point p = getMapPosition(point.getLatitude(),
						point.getLongitude());
				if (p != null) {
					int x = p.x - 10 / 2;
					int y = p.y - 10 / 2;
					;
					g2D.fillOval(x, y, 10, 10);
				}
			}

		}

		if (selectedPoint != -1 && gpxPoints.size() > selectedPoint) {
			g2D.setColor(Color.WHITE);
			GpxPoint gpxPoint = gpxPoints.get(selectedPoint);
			Point p = getMapPosition(gpxPoint.getLatitude(),
					gpxPoint.getLongitude());
			if (p != null) {
				int x = p.x - 12 / 2;
				int y = p.y - 12 / 2;
				;
				g2D.drawOval(x, y, 12, 12);

			}
		}

	}

	private int[] toIntArray(List<Integer> list) {
		int[] ret = new int[list.size()];
		int i = 0;
		for (Integer e : list) {
			ret[i++] = e.intValue();
		}
		return ret;
	}

	/**
	 * Moves the visible map pane.
	 * 
	 * @param x
	 *            horizontal movement in pixel.
	 * @param y
	 *            vertical movement in pixel
	 */
	public void moveMap(int x, int y) {
		center.x += x;
		center.y += y;
		repaint();
	}

	/**
	 * @return the current zoom level
	 */
	public int getZoom() {
		return zoom;
	}

	/**
	 * Increases the current zoom level by one
	 */
	public void zoomIn() {
		setZoom(zoom + 1);
	}

	/**
	 * Increases the current zoom level by one
	 */
	public void zoomIn(Point mapPoint) {
		setZoom(zoom + 1, mapPoint);
	}

	/**
	 * Decreases the current zoom level by one
	 */
	public void zoomOut() {
		setZoom(zoom - 1);
	}

	/**
	 * Decreases the current zoom level by one
	 */
	public void zoomOut(Point mapPoint) {
		setZoom(zoom - 1, mapPoint);
	}

	public void setZoom(int zoom, Point mapPoint) {
		if (zoom > this.getMaxZoom() || zoom < this.getMinZoom()
				|| zoom == this.zoom)
			return;
		Point2D.Double zoomPos = getPosition(mapPoint);
		// jobDispatcher.cancelOutstandingJobs(); // Clearing outstanding load
		// requests
		setDisplayPositionByLatLon(mapPoint, zoomPos.x, zoomPos.y, zoom);
	}

	public void setZoom(int zoom) {
		setZoom(zoom, new Point(getWidth() / 2, getHeight() / 2));
		scaleBar.updateScale(zoom);
	}

	protected TileInfo getTile(int tilex, int tiley, int zoom, boolean update) {
		int max = (1 << zoom);
		if (tilex < 0 || tilex >= max || tiley < 0 || tiley >= max) {
			return null;
		}
		TileInfo tileInfo = this.currentTilesProvider.getTile(tilex, tiley, zoom, isDownloadTiles(), update);

		return tileInfo;
	}

	protected void zoomChanged(int oldZoom) {
		zoomSlider.setToolTipText("Zoom level " + zoom);
		zoomInButton.setToolTipText("Zoom to level " + (zoom + 1));
		zoomOutButton.setToolTipText("Zoom to level " + (zoom - 1));
		zoomOutButton.setEnabled(zoom > this.getMinZoom());
		zoomInButton.setEnabled(zoom < this.getMaxZoom());
	}

	public boolean isTileGridVisible() {
		return isTileGridVisible;
	}

	public void setTileGridVisible(boolean isTileGridVisible) {
		this.isTileGridVisible = isTileGridVisible;
		repaint();
	}

	public void setZoomContolsVisible(boolean visible) {
		zoomSlider.setVisible(visible);
		zoomInButton.setVisible(visible);
		zoomOutButton.setVisible(visible);
	}

	public boolean getZoomContolsVisible() {
		return zoomSlider.isVisible();
	}


	public void setTileType(String tileType) {
		this.tileType = tileType;
		
		for ( TilesProvider tilesProvider : this.tilesProviders) {
			if ( tilesProvider.getType().equals(tileType)) {
				currentTilesProvider = tilesProvider;
			}
		}
		repaint();
	}
	
	public TilesProvider getCurrentTilesProvider() {
		return this.currentTilesProvider;
	}

	public String getTileType() {
		return this.tileType;
	}

	public int getMaxZoom() {
		return maxZoom;
	}

	public void setMaxZoom(int maxZoom) {
		this.maxZoom = maxZoom;
	}

	public int getMinZoom() {
		return minZoom;
	}

	public void setMinZoom(int minZoom) {
		this.minZoom = minZoom;
	}

	public List<GpxPoint> getGpsTrace() {
		return gpsTrace;
	}

	public void setGpsTrace(List<GpxPoint> gpsTrace) {
		this.gpsTrace = gpsTrace;
		setDisplayPositionByLatLon(gpsTrace.get(0).getLatitude(),
				gpsTrace.get(0).getLongitude(), this.zoom);
		repaint();
	}

	public boolean isGpsTraceVisible() {
		return isGpsTraceVisible;
	}

	public void setGpsTraceVisible(boolean isGpsTraceVisible) {
		this.isGpsTraceVisible = isGpsTraceVisible;
		repaint();
	}

	public void onMousePressed(Point point) {
		Point2D.Double point2D = getPosition(point);
		dataChangedListener.onDataChanged(point2D.x, point2D.y, zoom);
	}

	public boolean isDownloadTiles() {
		return downloadTiles;
	}

	public void setDownloadTiles(boolean downloadTiles) {
		this.downloadTiles = downloadTiles;

	}

	public void setEditionMode(boolean isEditionMode) {
		this.isEditionMode = isEditionMode;
	}

	public boolean isEditionMode() {
		return isEditionMode;
	}

	public void onDoubleClick(Point point) {
		if (isEditionMode) {
			// Add point to edition trace
			Point2D.Double point2D = getPosition(point);
			GpxPoint gpxPoint = new GpxPoint(point2D.x, point2D.y, null, null);
			editionTrace.add(gpxPoint);

			System.out.println("Add point : " + point2D.x + "/" + point2D.y);
			repaint();
			editionPointListener.onPointAdded(gpxPoint);
		}

	}

	public void setSelectedEditionPoint(int selectedEditionPoint) {
		this.selectedEditionPoint = selectedEditionPoint;
		repaint();
	}

	public int getSelectedEditionPoint() {
		return selectedEditionPoint;
	}

	public void setGpsTraceColor(Color gpsTraceColor) {
		this.gpsTraceColor = gpsTraceColor;
		if (gpsTrace != null && gpsTrace.size() > 0) {
			repaint();
		}
	}

	public void setEditTraceColor(Color editTraceColor) {
		this.editTraceColor = editTraceColor;
		if (editionTrace != null && editionTrace.size() > 0) {
			repaint();
		}

	}
	
	public void setNearestPoint(GpsPoint nearestPoint) {
		this.nearestPoint = nearestPoint;
		repaint();
	}
	
	public void reloadVisibleTiles() {
		
		// Get visible tiles ...
		int iMove = 0;

		// Dans quel tile se trouve le centre ?
		int currentTileX = center.x / TILE_SIZE;
		int currentTileY = center.y / TILE_SIZE;

		// Offset du centre en pixel
		int offsetXPixel = (center.x % TILE_SIZE);
		int offsetYPixel = (center.y % TILE_SIZE);
		int currentPosXPixel = (getWidth() / 2) - offsetXPixel;
		int currentPosYPixel = (getHeight() / 2) - offsetYPixel;


		// start top
		if (offsetYPixel < (TILE_SIZE - offsetYPixel)) {

			// start left
			if (offsetXPixel < (TILE_SIZE - offsetXPixel)) {
				iMove = 2;
			} else {
				iMove = 3;
			}
		} else {
			if (offsetXPixel < (TILE_SIZE - offsetXPixel)) {
				iMove = 1;
			} else {
				iMove = 0;
			}
		} // calculate the visibility borders


		// Pixels Limits from -TILE_SIZE to width or Height
		int xMinPixel = -TILE_SIZE;
		int yMinPixel = -TILE_SIZE;
		int xMaxPixel = getWidth();
		int yMaxPixel = getHeight();


		// paint the tiles in a spiral, starting from center of the map
		boolean painted = true;
		int x = 0;

		while (painted) {

			painted = false;
			for (int i = 0; i < 4; i++) {
				if (i % 2 == 0) {
					x++;
				}

				for (int j = 0; j < x; j++) {
					if (xMinPixel <= currentPosXPixel && currentPosXPixel <= xMaxPixel &&
						yMinPixel <= currentPosYPixel && currentPosYPixel <= yMaxPixel) {

						System.out.println("Tiles is visible " + currentTileX  + " " + currentTileY  + " " + zoom);
						
						// Tile is visible
						TileInfo tile = getTile(currentTileX, currentTileY, zoom, true);


					}

					Point p = move[iMove];
					currentPosXPixel += p.x * TILE_SIZE;
					currentPosYPixel += p.y * TILE_SIZE;
					currentTileX += p.x;
					currentTileY += p.y;

				}

				iMove = (iMove + 1) % move.length;
			}
		}
	}

}
