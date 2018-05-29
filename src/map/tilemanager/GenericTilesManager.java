package map.tilemanager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import map.model.TileInfo;

// http://mt1.googleapis.com/vt?lyrs=m@275307151&src=apiv3&hl=fr&x=63&y=41&z=7&scale=2&style=47,37%7Csmartmaps
public class GenericTilesManager extends WebTilesManager {
	
	private String urlTemplate;
	private String id;
	private String referer;
	private String key; 
	private String extension;
	
	
	public String getUrlTemplate() {
		return urlTemplate;
	}
	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReferer() {
		return referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@Override
	protected String constructUrl(int tileX, int tileY, int zoom) {
		return replaceVariable(tileX, tileY, zoom);
	}
	@Override
	protected String getTileType() {
		return id;
	}
	@Override
	protected String getTileImgExtension() {
		return getExtension();
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	 
	private String replaceVariable(int tileX, int tileY, int zoom) {
		String url = urlTemplate;
		if (key != null) {
			url = urlTemplate.replace("${key}", key);
		}
		
		url = url.replace("${row}", Integer.toString(tileX));
		url = url.replace("${col}", Integer.toString(tileY));
		url = url.replace("${zoom}", Integer.toString(zoom));
		
		System.out.println(url);
		
		return url;
	}
	
	public static void main(String[] args) {
		System.out.println("start");
		
		//String url = constructUrl(xTile, yTile, zoomLevel );
		String url = "http://wxs.ign.fr/nkznj7fyeuftuqjgsyk5mgu5/wmts?LAYER=GEOGRAPHICALGRIDSYSTEMS.MAPS.SCAN-EXPRESS.STANDARD&EXCEPTIONS=text/xml&FORMAT=image/jpeg&SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetTile&STYLE=normal&TILEMATRIXSET=PM&&TILEMATRIX=13&TILECOL=4220&TILEROW=2935";
		System.out.println(url);
		try {
	        URL obj = new URL(url);
	        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
	
	        // optional default is GET
	        connection.setRequestMethod("GET");
	
	        // add request header
	        connection.setRequestProperty("User-Agent", "https://tracedetrail.fr/fr/trace/trace/5704");
	        connection.setRequestProperty("Accept", "image/png,image/*;q=0.8,*/*;q=0.5");
	        connection.setRequestProperty("Accept-Language", "fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3");
	        connection.setRequestProperty("Referer", "https://tracedetrail.fr/fr/trace/trace/5704" );
	       
	        int responseCode = connection.getResponseCode();
	        System.out.println("\nSending 'GET' request to URL : " + url);
	        System.out.println("Response Code : " + responseCode);
	        System.out.println("Response Message : " + connection.getResponseMessage());
	        
	        InputStream is = connection.getInputStream();		
	        //OutputStream os = new FileOutputStream(tileInfo.getTileFile());
	        ByteArrayOutputStream bais = new ByteArrayOutputStream();
	    			
	        byte[] buffer = new byte[1024];		
	        int byteReaded = is.read(buffer);
	        while(byteReaded != -1)  {
	        	bais.write(buffer,0,byteReaded);
	            byteReaded = is.read(buffer);
	        }
	        bais.flush();
	        
			InputStream in = new ByteArrayInputStream(bais.toByteArray());

			ImageIO.read(in);

	        //tileInfo.write(bais.toByteArray());
	    			
	       //os.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
