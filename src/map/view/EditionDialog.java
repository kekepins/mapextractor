package map.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import map.altitude.AltitudeServiceInterface;
import map.altitude.OsmAltitudeService;
import map.gps.GpxDecoder;
import map.model.GpxPoint;

@SuppressWarnings("serial")
public class EditionDialog extends JDialog implements EditionPointAddedListener, ActionListener {
	
	private static final String ACTION_MINUS_POINT 		= "MINUS_POINT";
	private static final String ACTION_SAVE_TRACE 		= "SAVE_TRACE";
	private static final String ACTION_CALC_DENIV 		= "CALC_DENIV";
	private static final String ACTION_ELEVATION_CHART  = "ELEVATION_CHART";
	
	
	private JMapViewer mapViewer;
	private JList<GpxPoint> listPoints;
	private List<GpxPoint> editionTrace;
	private AltitudeServiceInterface altitudeService = new OsmAltitudeService();
	
	private DataChangedListener dataChangedListener;
	
	public EditionDialog(Frame owner, List<GpxPoint> editionTrace, DataChangedListener dataChangedListener) {
		super(owner, "Edition");
		this.editionTrace = editionTrace;
		setBounds(300, 300, 300, 200);
		this.setResizable(false);
		this.dataChangedListener  = dataChangedListener;
		initComponent();
	}
	
	private void initComponent() {
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add( topPanel );

		// Remove point button
		ImageIcon iconMinus = new ImageIcon(getClass().getResource("images/minus.png"));
		final JButton removeBtn = new JButton(iconMinus);
		removeBtn.setActionCommand(ACTION_MINUS_POINT);
		removeBtn.addActionListener(this);
		
		// Create a new listbox control
		JScrollPane scrollPane = new JScrollPane();
		DefaultListModel<GpxPoint> listModel = new DefaultListModel<GpxPoint>();
		listPoints = new JList<GpxPoint>( listModel );
		listPoints.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//listPoints.setCellRenderer(new ListPointsCellRender());
		listPoints.setCellRenderer(new MyCellRenderer());
		listPoints.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent listEvent) {
				if(!listEvent.getValueIsAdjusting()){
					removeBtn.setEnabled(true);
					int idx = listPoints.getSelectedIndex();
					mapViewer.setSelectedEditionPoint(idx);
				}
			}
		});
		
		ImageIcon iconCalDeniv = new ImageIcon(getClass().getResource("images/mountain.png"));
		JButton iconCalcDenivBtn = new JButton(iconCalDeniv);
		iconCalcDenivBtn.setActionCommand(ACTION_CALC_DENIV);
		iconCalcDenivBtn.addActionListener(this);
		
		// Save trace
		ImageIcon iconSave = new ImageIcon(getClass().getResource("images/save.png"));
		JButton saveTraceBtn = new JButton(iconSave);
		saveTraceBtn.setActionCommand(ACTION_SAVE_TRACE);
		saveTraceBtn.addActionListener(this);
		
		// Save trace
		ImageIcon iconElevationChart = new ImageIcon(getClass().getResource("images/deniv.png"));
		JButton elevationChartBtn = new JButton(iconElevationChart);
		elevationChartBtn.setActionCommand(ACTION_ELEVATION_CHART);
		elevationChartBtn.addActionListener(this);
		
		// Layout ...
		scrollPane.setPreferredSize(new Dimension(250, 400));
		scrollPane.setViewportView(listPoints);
		topPanel.add( scrollPane, BorderLayout.WEST );
		JPanel buttonPanel = new JPanel();
		removeBtn.setEnabled(false);
        buttonPanel.add(removeBtn);
        buttonPanel.add(iconCalcDenivBtn, BorderLayout.SOUTH);
        buttonPanel.add(saveTraceBtn, BorderLayout.SOUTH);
        buttonPanel.add(elevationChartBtn, BorderLayout.SOUTH);
        
        topPanel.add(buttonPanel);
 	}

	@Override
	public void onPointAdded(GpxPoint gpxPoint) {
		((DefaultListModel<GpxPoint>) listPoints.getModel()).addElement(gpxPoint);
		dataChangedListener.onEditionChanged();
	}
	
	public void setMapViewer(JMapViewer mapViewer) {
		this.mapViewer = mapViewer;
	}

	public JMapViewer getMapViewer() {
		return mapViewer;
	}

	class ListPointsCellRender extends JLabel implements
			ListCellRenderer<GpxPoint> {

		@Override
		public Component getListCellRendererComponent(
				JList<? extends GpxPoint> list, GpxPoint value, int index,
				boolean isSelected, boolean cellHasFocus) {
			
			setOpaque(true);

	        if (isSelected) {
	            setForeground(list.getSelectionForeground());
	            setBackground(list.getSelectionBackground());
	        } else {
	            setForeground(list.getForeground());
	            setBackground(list.getBackground());
	        }
	        
	        if ( value.getAltitude() != null ) {
				setText(index
						+ " --> "
						+ String.format("%.5f/%.5f/%f", value
								.getLatitude(), value.getLongitude(), value.getAltitude() ));
	        }
	        else {
				setText(index
						+ " --> "
						+ String.format("%.5f/%.5f", value
								.getLatitude(), value.getLongitude() ));
	        }

			return this;
		}
	}
	
	class MyCellRenderer extends JPanel implements ListCellRenderer<GpxPoint> {
		JLabel left, middle, right;

		MyCellRenderer() {
			setLayout(new GridLayout(1, 3));
			left = new JLabel();
			middle = new JLabel();
			right = new JLabel();
			left.setOpaque(true);
			middle.setOpaque(true);
			right.setOpaque(true);
			add(left);
			add(middle);
			add(right);
		}

		public Component getListCellRendererComponent(
				JList<? extends GpxPoint> list, GpxPoint value, int index,
				boolean isSelected, boolean cellHasFocus) {
			String leftData = String.format("%.5f", value.getLatitude());
			String middleData = String.format("%.5f", value.getLongitude()); 
			String rightData = "";
			if ( value.getAltitude() != null) {
				rightData += value.getAltitude();
			}
			
			
			left.setText(leftData);
			middle.setText(middleData);
			right.setText(rightData);
			
			if (isSelected) {
				/*left.setBackground(list.getSelectionBackground());
				left.setForeground(list.getSelectionForeground());
				middle.setBackground(list.getSelectionBackground());
				middle.setForeground(list.getSelectionForeground());
				right.setBackground(list.getSelectionBackground());
				right.setForeground(list.getSelectionForeground());
				*/
				//super.setBackground(Color.RED);
				left.setBackground(Color.RED);
				left.setForeground(list.getSelectionForeground());
				middle.setBackground(Color.RED);
				middle.setForeground(list.getSelectionForeground());
				right.setBackground(Color.RED);
				right.setForeground(list.getSelectionForeground());
				
			} else {
				left.setBackground(list.getBackground());
				left.setForeground(list.getForeground());
				middle.setBackground(list.getBackground());
				middle.setForeground(list.getForeground());
				right.setBackground(list.getBackground());
				right.setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			return this;
		}
	}

	public void actionPerformed(ActionEvent e) {

		System.out.println("Action:" + e);
		String command = e.getActionCommand();
		if (ACTION_MINUS_POINT.equals(command)) {
			int idx = listPoints.getSelectedIndex();
			((DefaultListModel<GpxPoint>) listPoints.getModel())
					.removeElementAt(idx);
			editionTrace.remove(idx);
			mapViewer.repaint();
			dataChangedListener.onEditionChanged();
		} else if (ACTION_SAVE_TRACE.equals(command)) {
			try {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					GpxDecoder.exportToFile(editionTrace, file);
				  //GpxDecoder.exportToFile(editionTrace, new File("export"
				  //			+ System.currentTimeMillis() + ".gpx"));
				  // save to file
				}
				/*GpxDecoder.exportToFile(editionTrace, new File("export"
						+ System.currentTimeMillis() + ".gpx"));*/
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (ACTION_CALC_DENIV.equals(command)) {
			//AltitudeService.computeAltitude(editionTrace);
			//AltitudeService2.computeAltitude(editionTrace);
			
			try {
				editionTrace = altitudeService.computeAltitude(editionTrace);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			this.repaint();
		}
		else if ( ACTION_ELEVATION_CHART.equals(command)) {
			// First compute altitude
			//AltitudeService2.computeAltitude(editionTrace);
			
			try {
				editionTrace = altitudeService.computeAltitude(editionTrace);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			ElevationChart chart = new ElevationChart(editionTrace);
			chart.pack();
			chart.setVisible(true);
		}

	}
	
}
