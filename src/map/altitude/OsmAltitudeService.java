package map.altitude;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import map.model.GpxPoint;
import map.model.OsmPoint;
import map.model.OsmResults;

// https://github.com/Jorl17/open-elevation/blob/master/docs/api.md
public class OsmAltitudeService implements AltitudeServiceInterface {

	private static final String ALTI_SERVICE_URL = "https://api.open-elevation.com/api/v1/lookup?locations=";

	@Override
	public List<GpxPoint> computeAltitude(List<GpxPoint> listPoints) throws IOException {
		
		String url =  getUrl(listPoints);
		
		System.out.println(url);
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setInstanceFollowRedirects(true);

        // optional default is GET
        connection.setRequestMethod("GET");
        HttpURLConnection.setFollowRedirects(true);
        //connection.setFollowRedirects(true);
        
        // add request header
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
        connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        connection.setRequestProperty("Accept-Language", "fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3");
        
        
        int responseCode = connection.getResponseCode();
        InputStreamReader in = new InputStreamReader((InputStream) connection.getContent());
        BufferedReader buff = new BufferedReader(in);
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = buff.readLine()) != null) {
        	sb.append(output);
        }
        
        String json = sb.toString();
        
        System.out.println(json);
        
        ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        
		OsmResults results = objectMapper.readValue(json, OsmResults.class);
		
		if ( results != null ) {
			
			List<OsmPoint> points = results.getResults();
			
			if ( points != null && points.size() == listPoints.size() ) {
				for ( int idx = 0; idx < points.size(); idx++ ) {
					listPoints.get(idx).setAltitude(points.get(idx).getElevation());
				}
			}
		}

        
        return listPoints;
	}

	@Override
	public Double getAltitude(double longitude, double latitude) throws IOException {
		List<GpxPoint> listPoints = new ArrayList<>();
		listPoints.add(new GpxPoint(latitude, longitude, null, null));
		listPoints = computeAltitude(listPoints);
		
		return listPoints.get(0).getAltitude();
	}
	
	private String getUrl(List<GpxPoint> gpsPoints) {
		StringBuffer url = new StringBuffer( ALTI_SERVICE_URL); 
		
		for (GpxPoint gpsPoint : gpsPoints ) {
			url.append(gpsPoint.getLatitude() + "," + gpsPoint.getLongitude() + "|");
		}

		url.deleteCharAt(url.length()-1);
		return url.toString();
	}
	
	
	
	public static void main(String[] args ) throws IOException {
		
		List<GpxPoint> listPoints = new ArrayList<>();
		
		listPoints.add(new GpxPoint( 5.817448, 45.232221, null, null));
		listPoints.add(new GpxPoint( 5.85, 45.232221, null, null));
		
		OsmAltitudeService osmAltitudeService = new OsmAltitudeService();
		osmAltitudeService.computeAltitude(listPoints);
	}

}
