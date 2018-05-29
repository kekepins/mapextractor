package map.tilemanager;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import map.model.GpsPoint;
import map.model.TileInfo;

public abstract class WebTilesManager extends TilesManager {
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0";
	
	protected abstract String constructUrl( int tileX, int tileY, int zoom); 
	
	protected abstract String getReferer();
	
	public void save(TileInfo tileInfo) {
		
	}
	
	public TileInfo getTileInfo(int xTile, int yTile, int zoomLevel) {
		
		BufferedImage image=  downloadTileImage( xTile,  yTile,  zoomLevel);
		if (image != null) {
			TileInfo tileInfo = new TileInfo(getTileType());
			tileInfo.setTileX(xTile);
			tileInfo.setTileY(yTile);
			tileInfo.setZoomLevel(zoomLevel);
			List<GpsPoint> bounds = tileToBounds(xTile, yTile, zoomLevel);
			tileInfo.setUpLeftPoint(bounds.get(0));
			tileInfo.setDownRightPoint(bounds.get(1));
			tileInfo.setImage(image);
			
			return tileInfo;
		}
		
		return null;

	}
	

	/**
	 * Download an image tile and write to disk
	 */
	private BufferedImage downloadTileImage(int xTile, int yTile, int zoomLevel) {
		
		String url = constructUrl(xTile, yTile, zoomLevel );
		System.out.println(url);
		try {
	        URL obj = new URL(url);
	        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
	
	        // optional default is GET
	        connection.setRequestMethod("GET");
	
	        // add request header
	        connection.setRequestProperty("User-Agent", USER_AGENT);
	        connection.setRequestProperty("Accept", "image/png,image/*;q=0.8,*/*;q=0.5");
	        connection.setRequestProperty("Accept-Language", "fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3");
	        connection.setRequestProperty("Referer", getReferer() );
	       
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
			return ImageIO.read(in);

	        //tileInfo.write(bais.toByteArray());
	    			
	       //os.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void delete(TileInfo tileInfo) {
		
	}

	

}
