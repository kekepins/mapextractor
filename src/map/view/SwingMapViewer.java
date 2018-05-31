package map.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import map.conf.Configuration;
import map.export.ExportUtils;
import map.gps.GpsReducer;
import map.gps.GpsUtility;
import map.gps.GpxDecoder;
import map.model.GpsPoint;
import map.model.GpxPoint;
import map.sql.SQLLiteUtils;
import map.tilemanager.TilesManager;


// http://www.earthtools.org/height/<latitude>/<longitude>
// http://www.earthtools.org/height/45.232221/5.817448
public class SwingMapViewer extends JFrame implements DataChangedListener, ActionListener{

	private static final long serialVersionUID = 1L;
	
	private static final Color[] colors={Color.orange,Color.red,Color.blue,Color.green, Color.yellow};
	
	// Actions
	private static final String ACTION_SHOW_GPS = "SHOW_GPS";
	private static final String ACTION_GRID_VISIBLE = "SHOW_GRID";
	private static final String ACTION_EDITION_MODE = "EDITION_MODE";
	private static final String ACTION_LOAD_TILES = "LOAD_TILES";
	private static final String ACTION_EXPORT_TRACE_AS_IMG = "EXPORT_TRACE_AS_IMG";
	//private static final String ACTION_EXPORT_TRACE_AS_PDF = "EXPORT_TRACE_AS_PDF";
	private static final String ACTION_EXPORT_ANDROID = "EXPORT_EXPORT_ANDROID";
	private static final String ACTION_LOAD_GPS = "LOAD_GPS";
	private static final String ACTION_GPS_COLOR_CHANGED = "GPS_COLOR_CHANGED";
	private static final String ACTION_EDIT_TRACE_COLOR_CHANGED = "EDIT_TRACE_COLOR_CHANGED";
	private static final String ACTION_VISIBLE_TILES = "ACTION_VISIBLE_TILES";
	//private static final String ACTION_EXPORT_ANDROID_ALL = "EXPORT_ANDROID_ALL";
	
	
	private static final String ACTION_TRACE_DENIV = "TRACE_DENIV";
	
	
	// Gps trace points 
	private List<GpxPoint> gpsPoints; 
	private List<GpxPoint> editionTrace;
	
	// Info view
	private JLabel infoLabel;
	private JLabel filenameLabel;
	private JLabel gpsPointsCountLabel;
	private JLabel gpsDistanceLabel;
	private JLabel editionPointsCountLabel;
	private JLabel edtionDistanceLabel;
	
	// Toolbar 
	private JToggleButton btnDrawGps;
	
	private JColorComboBox chooseColorGpscomboBox;
	private JColorComboBox chooseColorEditcomboBox;
	
	// Map
	private JMapViewer map;
	
	private EditionDialog editionDialog;

	public SwingMapViewer() {
		super("Map Extractor");
		setSize(1200, 800);
		
		editionTrace = new ArrayList<GpxPoint>();
		editionDialog = new EditionDialog(this, editionTrace, this);
		map = new JMapViewer(this, editionDialog,  editionTrace);
		map.setBorder(BorderFactory.createLineBorder(Color.black));
		editionDialog.setMapViewer(map);
		
		int zoomLevel = 10;
		double latitude = 45.232221;
		double longitude = 5.817448;
		
		map.setDisplayPositionByLatLon(latitude, longitude, zoomLevel);
		
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel menuPanel =  new JPanel();
		
		JPanel buttomPanel = new JPanel();
		add(menuPanel, BorderLayout.NORTH);
		add(buttomPanel, BorderLayout.SOUTH);
		add(map, BorderLayout.CENTER);
		
		constructInfoView();
		constructTools(menuPanel);
		
		infoLabel = new JLabel("");
		buttomPanel.add(infoLabel);
	}
	
	private void constructInfoView() {
		// Add info data
		JPanel infoPanel = new JPanel();
		infoPanel.setPreferredSize(new Dimension(150, 500));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		
		
		// Trace gps info
		JPanel traceGpsInfoPanel = new JPanel();
		traceGpsInfoPanel.setLayout(new BoxLayout(traceGpsInfoPanel, BoxLayout.Y_AXIS));
		traceGpsInfoPanel.setPreferredSize(new Dimension(130, 200));
		traceGpsInfoPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(3, 3, 3, 3))
			);
		
		JLabel trackDetailsLabel = new JLabel("Trace GPS");
		trackDetailsLabel.setMinimumSize(new Dimension(120, 20));
		Font biggerFont = trackDetailsLabel.getFont();
		biggerFont = biggerFont.deriveFont(Font.BOLD, biggerFont.getSize2D() + 2.0f);
		trackDetailsLabel.setFont(biggerFont);
		traceGpsInfoPanel.add(trackDetailsLabel);
		traceGpsInfoPanel.add(Box.createVerticalStrut(10)); // Fixed width invisible separator.
		infoPanel.add(traceGpsInfoPanel);
		filenameLabel = new JLabel("Pas de fichier chargé");
		filenameLabel.setMinimumSize(new Dimension(120, 20));
		traceGpsInfoPanel.add(filenameLabel);
		
		gpsPointsCountLabel = new JLabel("");
		gpsPointsCountLabel.setMinimumSize(new Dimension(120, 20));
		traceGpsInfoPanel.add(gpsPointsCountLabel);
		
		gpsDistanceLabel  = new JLabel("");
		traceGpsInfoPanel.add(gpsDistanceLabel);
		
		chooseColorGpscomboBox = new JColorComboBox(colors);
		chooseColorGpscomboBox.setMaximumSize(new Dimension(120,20));
		chooseColorGpscomboBox.setMinimumSize(new Dimension(120,20));
		chooseColorGpscomboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		chooseColorGpscomboBox.setSelectedColor(Color.red);
		chooseColorGpscomboBox.setActionCommand(ACTION_GPS_COLOR_CHANGED);
		chooseColorGpscomboBox.addActionListener( this );
	    traceGpsInfoPanel.add(chooseColorGpscomboBox);

		
		// Edition infos 
		JPanel editionGpsInfoPanel = new JPanel();
		editionGpsInfoPanel.setLayout(new BoxLayout(editionGpsInfoPanel, BoxLayout.Y_AXIS));

		editionGpsInfoPanel.setPreferredSize(new Dimension(130, 200));
		editionGpsInfoPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(3, 3, 3, 3))
			);

		JLabel editionDetailsLabel = new JLabel("Edition");
		editionDetailsLabel.setMinimumSize(new Dimension(120, 20));
		editionDetailsLabel.setFont(biggerFont);
		editionGpsInfoPanel.add(editionDetailsLabel);
		editionGpsInfoPanel.add(Box.createVerticalStrut(10)); // Fixed width invisible separator.
		editionPointsCountLabel = new JLabel("Pas en mode édition");
		editionPointsCountLabel.setMinimumSize(new Dimension(120, 20));
		editionPointsCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		editionGpsInfoPanel.add(editionPointsCountLabel);
		edtionDistanceLabel  = new JLabel("");
		editionGpsInfoPanel.add(edtionDistanceLabel);

		chooseColorEditcomboBox = new JColorComboBox(colors);
		chooseColorEditcomboBox.setMaximumSize(new Dimension(120,20));
		chooseColorEditcomboBox.setMinimumSize(new Dimension(120,20));
		chooseColorEditcomboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		chooseColorEditcomboBox.setSelectedColor(Color.green);
		chooseColorEditcomboBox.setActionCommand(ACTION_EDIT_TRACE_COLOR_CHANGED);
		chooseColorEditcomboBox.addActionListener( this );
		editionGpsInfoPanel.add(chooseColorEditcomboBox);
		infoPanel.add(editionGpsInfoPanel);
		
		add(infoPanel, BorderLayout.EAST);
	}
	
	private void constructTools(JPanel menuPanel) {
		menuPanel.setLayout(new BorderLayout());
		JLabel appIMg = new JLabel( new ImageIcon(getClass().getResource("images/map.png")), JLabel.LEFT);
		appIMg.setPreferredSize(new Dimension(90,100));
		menuPanel.add(appIMg, BorderLayout.WEST);
		
		JPanel actionPanel = new JPanel(new BorderLayout());
		menuPanel.add(actionPanel);
			
		JLabel titleLabel = new JLabel("GPS toolbox", JLabel.LEFT );
		titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 22));
		actionPanel.add(titleLabel, BorderLayout.CENTER );
		
		JToolBar toolBar = new JToolBar();
		
		/*JComboBox<TilesManager> tilesManagerSelector = new JComboBox<TilesManager>(tilesManagers.toArray(new TilesManager[tilesManagers.size()]));
		tilesManagerSelector.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				map.setTilesManager((TilesManager) e.getItem());
			}
		});
		*/
		List<String> tileTypes = Configuration.getConfiguration().getTileTypes();
				
		JComboBox<String> tilesManagerSelector = new JComboBox<String>(tileTypes.toArray(new String[tileTypes.size()]));
		tilesManagerSelector.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				map.setTileType((String) e.getItem());
			}
		});
		toolBar.add(tilesManagerSelector);
		toolBar.addSeparator();
		
		JButton btnLoadGps = new JButton( new ImageIcon(getClass().getResource("images/open2.png")));
		btnLoadGps.setToolTipText("Charger fichier Gps");
		btnLoadGps.setActionCommand(ACTION_LOAD_GPS);
		btnLoadGps.addActionListener( this );
		toolBar.add(btnLoadGps);
		
		btnDrawGps = new JToggleButton( new ImageIcon(getClass().getResource("images/draw-gps.png")));
		btnDrawGps.setToolTipText("Montrer Gps");
		btnDrawGps.setActionCommand(ACTION_SHOW_GPS);
		btnDrawGps.addActionListener( this );
		toolBar.add(btnDrawGps);
		
		JToggleButton btnEditionGps = new JToggleButton( new ImageIcon(getClass().getResource("images/edit.png")));
		btnEditionGps.setToolTipText("Mode edition");
		btnEditionGps.setActionCommand(ACTION_EDITION_MODE);
		btnEditionGps.addActionListener( this );
		toolBar.add(btnEditionGps);
	
		toolBar.addSeparator();
		
		JToggleButton btnDrawRectangle = new JToggleButton( new ImageIcon(getClass().getResource("images/rectangle.png")), true);
		btnDrawRectangle.setToolTipText("Rectangle visible");
		btnDrawRectangle.setActionCommand(ACTION_GRID_VISIBLE);
		btnDrawRectangle.addActionListener( this );
		toolBar.add(btnDrawRectangle);
		
		JToggleButton btnLoadTiles = new JToggleButton( new ImageIcon(getClass().getResource("images/internet.png")));
		btnLoadTiles.setToolTipText("Charger les tuiles manquantes sur internet");
		btnLoadTiles.setActionCommand(ACTION_LOAD_TILES);
		btnLoadTiles.addActionListener( this );
		toolBar.add(btnLoadTiles);
		
		toolBar.addSeparator();
		
		JButton btnExportImage = new JButton( new ImageIcon(getClass().getResource("images/export_img.png")));
		btnExportImage.setToolTipText("Exporter la trace sous forme d'image");
		btnExportImage.setActionCommand(ACTION_EXPORT_TRACE_AS_IMG);
		btnExportImage.addActionListener( this );
		toolBar.add(btnExportImage);
		
		JButton btnRefreshImage = new JButton( new ImageIcon(getClass().getResource("images/view_refresh.png")));
		btnRefreshImage.setToolTipText("Refresh visible tiles");
		btnRefreshImage.setActionCommand(ACTION_VISIBLE_TILES);
		btnRefreshImage.addActionListener( this );
		toolBar.add(btnRefreshImage);

		
		/*
		JButton btnExportPdf = new JButton( new ImageIcon(getClass().getResource("images/export_pdf.png")));
		btnExportPdf.setToolTipText("Exporter la trace sous forme de pdf");
		btnExportPdf.setActionCommand(ACTION_EXPORT_TRACE_AS_PDF);
		btnExportPdf.addActionListener( this );
		toolBar.add(btnExportPdf);*/

		JButton btnExportAndroid = new JButton( new ImageIcon(getClass().getResource("images/android.png")));
		btnExportAndroid.setToolTipText("Exporter les données vers Android");
		btnExportAndroid.setActionCommand(ACTION_EXPORT_ANDROID);
		btnExportAndroid.addActionListener( this );
		toolBar.add(btnExportAndroid);
		/*
		JButton btnExportAllAndroid = new JButton( new ImageIcon(getClass().getResource("images/android.png")));
		btnExportAllAndroid.setToolTipText("Exporter toutes les données");
		btnExportAllAndroid.setActionCommand(ACTION_EXPORT_ANDROID_ALL);
		btnExportAllAndroid.addActionListener( this );
		toolBar.add(btnExportAllAndroid);*/
		
		JButton btnDeniv = new JButton( new ImageIcon(getClass().getResource("images/deniv.png")));
		btnDeniv.setToolTipText("Trace deniv");
		btnDeniv.setActionCommand(ACTION_TRACE_DENIV);
		btnDeniv.addActionListener( this );
		toolBar.add(btnDeniv);

		
		actionPanel.add(toolBar, BorderLayout.SOUTH);
	}



	@Override
	public void onDataChanged(double latitude, double longitude, int zoomLevel) {
		String dbName = Configuration.getConfiguration().getDbName();
		
		if ( dbName != null ) {
			Connection connection;
			try {
				//connection = SQLLiteUtils.createDatabase("db/tiles1415183807951.sqlite");
				connection = SQLLiteUtils.createDatabase(dbName);
				
				double delta = 0.02; 
				GpsPoint gpsPoint = SQLLiteUtils.selectNearestGpsPoint(connection, latitude, longitude, delta);
				if (gpsPoint != null ) {
					System.out.println("Nearest found " + gpsPoint);
					map.setNearestPoint(gpsPoint);
				}
				
				connection.close();
	
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
	
			infoLabel.setText(String.format("%.5f:%.5f: Zoom %d",  longitude ,latitude, zoomLevel));
		}
	}
	
	protected void loadGpsData(File gpsFile ) {
		if ( gpsFile != null && gpsFile.exists() ) {
			List<GpxPoint> fileGpsPoints = GpxDecoder.parse(gpsFile);
			if ( fileGpsPoints != null && fileGpsPoints.size() > 0) {
				
				// shring
				gpsPoints = GpsReducer.shrink(fileGpsPoints);
				
				map.setGpsTrace(gpsPoints);
				gpsPointsCountLabel.setText(gpsPoints.size()  + " points");
				map.setGpsTraceVisible(true);
				btnDrawGps.setSelected(true);
				
				double distance = GpsUtility.calculateDistance( gpsPoints );
				gpsDistanceLabel.setText(String.format("Distance : %.2f km", distance));
			}
			filenameLabel.setText("Fichier : " +  gpsFile.getName());
		}
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Action:" + e);
		String command = e.getActionCommand();
		if (ACTION_GRID_VISIBLE.equals(command)) {
			map.setTileGridVisible(!map.isTileGridVisible());
		} 
		else if (ACTION_SHOW_GPS.equals(command)) {
			map.setGpsTraceVisible(!map.isGpsTraceVisible());
		} 
		else if (ACTION_LOAD_TILES.equals(command)) {
			map.setDownloadTiles(!map.isDownloadTiles());
			repaint();
		} 
		else if (ACTION_EDITION_MODE.equals(command)) {
			if (editionDialog.isVisible()) {
				editionDialog.setVisible(false);
				map.setEditionMode(false);
			} else {
				editionDialog.setVisible(true);
				map.setEditionMode(true);
			}
		} 
		else if (ACTION_LOAD_GPS.equals(command)) {
			JFileChooser fileChooser = new JFileChooser();
			
			File traceDir = Configuration.getConfiguration().getGpsTraceDir();
			if ( traceDir != null ) {
				fileChooser.setCurrentDirectory(traceDir);
			}
			FileNameExtensionFilter filter = new FileNameExtensionFilter("GPX FILES", "gpx");
			fileChooser.setFileFilter(filter);
			
			int rVal = fileChooser.showOpenDialog(SwingMapViewer.this);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				File newGpsFile = fileChooser.getSelectedFile();
				SwingMapViewer.this.loadGpsData(newGpsFile);
			}

		}
		else if ( ACTION_EXPORT_TRACE_AS_IMG.equals(command)) {
			if ( gpsPoints != null && gpsPoints.size() > 0 ) {
				ExportUtils.generateImageForTrace(gpsPoints, map.getCurrentTilesProvider(), map.getZoom(), map.isDownloadTiles() );
			}
		}
		else if ( ACTION_GPS_COLOR_CHANGED.equals(command)) {
			map.setGpsTraceColor(chooseColorGpscomboBox.getSelectedColor());
		}
		else if ( ACTION_EDIT_TRACE_COLOR_CHANGED.equals(command)) {
			map.setEditTraceColor(chooseColorEditcomboBox.getSelectedColor());
		}
		else if ( ACTION_EXPORT_ANDROID.equals(command)) {
			try {
				if ( gpsPoints != null && gpsPoints.size() > 0) {
					SQLLiteUtils.generateDbTilesForTrace("tiles"+System.currentTimeMillis()+".db", gpsPoints, Configuration.getConfiguration().getTilesProviders(), -1, false);
				}
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} 
		}
		else if ( ACTION_TRACE_DENIV.equals(command)) {
			if ( gpsPoints != null && gpsPoints.size() > 0 ) {
				ElevationChart chart = new ElevationChart(gpsPoints);
				chart.pack();
				chart.setVisible(true);
			}
		}
		else if ( ACTION_VISIBLE_TILES.equals(command)) {
			map.reloadVisibleTiles();
			repaint();
		}
		
		/*else if ( ACTION_EXPORT_ANDROID_ALL.equals(command)) {
			try {
				//SQLLiteUtils.generateDbTilesWhithAllFiles("tiles"+System.currentTimeMillis()+".db", Configuration.getConfiguration().getTilesProviders(), gpsPoints );
				SQLLiteUtils.generateDbTilesWhithAllFiles(Configuration.getConfiguration().getDbName(), Configuration.getConfiguration().getTilesProviders(), gpsPoints );
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}*/

	}
	

		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Configuration config = Configuration.getConfiguration();
		
		if ( config == null ) {
			return;
		}
		
		String proxy = config.getHttpProxy();
		String proxyPort = config.getHttpProxyPort();
		
		if ( proxy != null && proxyPort != null) {
			System.setProperty("http.proxyHost", proxy);
		    System.setProperty("http.proxyPort", proxyPort);
			System.setProperty("https.proxyHost", proxy);
		    System.setProperty("https.proxyPort", proxyPort);

		}
		
		List<TilesManager> tilesManagers = config.getTilesManagers();
		
		if ( tilesManagers == null || tilesManagers.size() == 0) {
			return;
		}
		
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
	
		SwingMapViewer swingMap = new SwingMapViewer();
		swingMap.setVisible(true);
	}

	@Override
	public void onEditionChanged() {
		if ( editionTrace != null && editionTrace.size() > 0) {
			editionPointsCountLabel.setText(editionTrace.size()  + " points");
			
			double distance = GpsUtility.calculateDistance( editionTrace );
			edtionDistanceLabel.setText(String.format("Distance : %.2f km", distance));
		}

	}

}
