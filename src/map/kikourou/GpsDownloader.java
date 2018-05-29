package map.kikourou;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

public class GpsDownloader {
	public void getGpsTrace() {
		
		String cookieVal = null;
		String url = "http://www.kikourou.net/entrainement/ficheseance.php?id=258813";
		System.out.println(url);
		try {
	        URL obj = new URL(url);
	        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
	
	        // optional default is GET
	        connection.setRequestMethod("GET");
	
	        // add request header
		    connection.setRequestProperty("Accept-Language", "fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3");
	       
	        int responseCode = connection.getResponseCode();
	        System.out.println("\nSending 'GET' request to URL : " + url);
	        System.out.println("Response Code : " + responseCode);
	        System.out.println("Response Message : " + connection.getResponseMessage());
	        connection.connect();
	        
	        String headerName=null;
	        for (int i=1; (headerName = connection.getHeaderFieldKey(i))!=null; i++) {
	         	if (headerName.equals("Set-Cookie")) {                  
	         		String cookie = connection.getHeaderField(i); 
	         		System.out.println("Cookie " + cookie);
	         		
	         		cookie = cookie.substring(0, cookie.indexOf(";"));
	                String cookieName = cookie.substring(0, cookie.indexOf("="));
	                String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
	                
	                System.out.println("Cookie name-->" + cookieName);
	                System.out.println("Cookie value-->" + cookieValue);
	                if ( cookieName.equals("PHPSESSID")) {
	                	cookieVal = cookie;
	                }
	         	}
	        }
	        
	        /*InputStream is = connection.getInputStream();		
	        //OutputStream os = new FileOutputStream(tileInfo.getTileFile());
	        ByteArrayOutputStream bais = new ByteArrayOutputStream();
	    			
	        byte[] buffer = new byte[1024];		
	        int byteReaded = is.read(buffer);
	        while(byteReaded != -1)  {
	        	bais.write(buffer,0,byteReaded);
	            byteReaded = is.read(buffer);
	        }
	        bais.flush();*/

        //tileInfo.write(bais.toByteArray());
    			
       //os.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		// Request 2
		URL urlDownload;
		try {
			urlDownload = new URL("http://www.kikourou.net/entrainement/gps_export.php?type=gpx");
			HttpURLConnection httpConn = (HttpURLConnection) urlDownload.openConnection();
			//String myCookie = "userId=igbrown";
			httpConn.setRequestProperty("Cookie", cookieVal);
	        int responseCode = httpConn.getResponseCode();
			
	        InputStream inputStream = httpConn.getInputStream();
            
             
            // opens an output stream to save into file
	        String saveFilePath = "d:\\temp\\exportgps.gpx";
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[256];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public static void main(String[] args) {
		System.out.println("Start");
		GpsDownloader gpsDownloader = new GpsDownloader();
		gpsDownloader.getGpsTrace();
	}

}
