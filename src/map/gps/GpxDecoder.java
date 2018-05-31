package map.gps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import map.model.GpxPoint;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GpxDecoder {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
	private static final SimpleDateFormat msdateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'"); // allow millis too
	
	public static List<GpxPoint> parse(File gpxFile) {
		List<GpxPoint> points = new ArrayList<GpxPoint>();
		Document dom;
		try {
			dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(gpxFile);
			NodeList nodeList = dom.getElementsByTagName("trkpt");
			for (int trackPointIdx = 0; trackPointIdx < nodeList.getLength(); trackPointIdx++) {
		        Node trackPointNode =  nodeList.item(trackPointIdx);
		        String latitude = getNodeValue(trackPointNode, "lat");
		        String longitude = getNodeValue(trackPointNode, "lon");
		        String elevation = null;
		        String time = null;
		        
		        NodeList childTrackPointNodes = trackPointNode.getChildNodes();
		        for (int childIdx = 0; childIdx < childTrackPointNodes.getLength(); childIdx++) {
		        	Node childNode =  childTrackPointNodes.item(childIdx);
		        	String name = childNode.getNodeName();
		        	if ( name != null && ("ele".equals(name) ) ){
		        		elevation = childNode.getLastChild().getNodeValue();
		        		
		        	}
		        	
		        	if ( name != null && ("time".equals(name) ) ){
		        		time = childNode.getLastChild().getNodeValue();
		        	}
			        	//String value = childNode.getNodeValue();
		        	//System.out.println(name + ":" + value);
		        }
		        if ( latitude != null && longitude != null ) {
		        	GpxPoint gpsPoint = new GpxPoint();
		        	gpsPoint.setLongitude(Double.parseDouble(longitude));
		        	gpsPoint.setLatitude(Double.parseDouble(latitude));
		        	
		        	if ( elevation != null ) {
		        		gpsPoint.setAltitude(Double.parseDouble(elevation));
		        	}
		        	
		        	if ( time != null ) {
		        		Date date = getDateFromString(time);
		        		gpsPoint.setDate(date);
		        		
		        	}
		        	
		        	points.add(gpsPoint);
		        }
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return points;

	}
	
	public static void exportToFile(List<GpxPoint> gpsPoints, File destination ) throws IOException {
		FileOutputStream fos = new FileOutputStream(destination);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
		
		// Headers
		writer.write("<?xml version=\"1.0\" encoding=\"windows-1252\"?>");
		writer.newLine();
		
		writer.write("<gpx ");
		writer.write("xmlns=\"http://www.topografix.com/GPX/1/1\" "); writer.newLine();
		writer.write("creator=\"GpsMap keke production - saucisson powa\"" ); writer.newLine();
		writer.write("version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" ); writer.newLine();
		writer.write("xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">"); writer.newLine();
		
		writer.write("<trk>");writer.newLine();
		writer.write("  <name>test export</name>");writer.newLine();
		writer.write("  <trkseg>");writer.newLine();
		
		for ( GpxPoint point : gpsPoints) {
			writer.write("      <trkpt lat=\"" + point.getLatitude() + "\" lon=\"" + point.getLongitude() +  "\">");
			writer.newLine();
			if ( point.getDate() != null  ) {
				writer.write( String.format("          <time>%s</time>", msdateFormat.format(point.getDate()) ) ); 
				writer.newLine();
			}
			
			if ( point.getAltitude() != null && point.getAltitude() > 0.) {
				writer.write("		  <ele>" + point.getAltitude() + "</ele>");writer.newLine();
			}
			writer.write("      </trkpt>");writer.newLine();
		}
		writer.write("  </trkseg>");writer.newLine();
		writer.write("</trk>");writer.newLine();
		writer.write("</gpx>");
	 
		writer.close();
	}
	
	private static Date getDateFromString(String strDate) {
		
		 try {
			 if (strDate.length() == 20) {
				 return dateFormat.parse(strDate);
			 }
			 else if (strDate.length() == 24) {
				 return msdateFormat.parse(strDate);
			 }
		 }
		 catch(Exception e ) {
			 System.out.println("Date cant be decode " + e);
			 
		 }
		 return null;
	}
	
	
	private static String getNodeValue(Node trackPointNode, String key) {
        Node valNode = trackPointNode.getAttributes().getNamedItem(key);
        if (valNode != null ) {
        	return valNode.getNodeValue();
        }
        return null;
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Start");
		//List<GpxPoint> points = GpxDecoder.parse(new File("D:\\gps\\get301tracks\\120925_cabane_bellefond.gpx"));
		List<GpxPoint> points = GpxDecoder.parse(new File("C:\\temp\\gpx\\porte.gpx"));
		GpxDecoder.exportToFile(points, new File("c:\\temp\\export.gpx"));
		
		System.out.println("end");

	}
	

}

