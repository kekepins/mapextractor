package map.altitude;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import map.model.GpxPoint;

public class AltitudeService {
	//public static String ALTI_SERVICE_URL = "http://www.earthtools.org/height/45.232221/5.817448";
	
	public static String ALTI_SERVICE_URL = "http://www.earthtools.org/height/";
	
	public static Double getAltitude(double longitude, double latitude) throws Exception{
		//Http request to altiservice
		
		String url =  ALTI_SERVICE_URL + latitude + "/" + longitude;
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        connection.setRequestMethod("GET");

        // add request header
        connection.setRequestProperty("Accept", "image/png,image/*;q=0.8,*/*;q=0.5");
        connection.setRequestProperty("Accept-Language", "fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3");
       
        int responseCode = connection.getResponseCode();
        InputStreamReader in = new InputStreamReader((InputStream) connection.getContent());
        BufferedReader buff = new BufferedReader(in);
        String line;
        do {
          line = buff.readLine();
          if ( line != null ) {
	          int idx =  line.indexOf("<meters>"); 
	          if( idx != -1) {
	        	  String strAltitude = line.substring(idx + 8, line.indexOf("</"));
	        	  
	        	  System.out.println("Alt: " + strAltitude);
	        	  return Double.parseDouble(strAltitude);
	          }
	          System.out.println(line);
          }
        } 
        while (line != null);
        
        return null;

	}
	
	public static void main(String[] args ) throws Exception{
		
        System.setProperty("http.proxyHost", "websurfing1-tin1.esi.adp.com");
        System.setProperty("http.proxyPort", "8080");

		double latitude = 45.232221;
		double longitude = 5.817448;
	
		Double altitude = getAltitude(longitude, latitude);
		
		System.out.println("alti " + altitude);
	}

	public static void computeAltitude(List<GpxPoint> listPoints) {
		for ( GpxPoint gpxPoint : listPoints ) {
			if ( gpxPoint.getAltitude() == null ) {
				try {
					Double alti = getAltitude(gpxPoint.getLongitude(), gpxPoint.getLatitude());
					if (alti != null ) {
						gpxPoint.setAltitude(alti);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
