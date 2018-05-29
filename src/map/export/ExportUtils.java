package map.export;

import gpx2.GpxDecoder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import map.conf.Configuration;
import map.model.GpsPoint;
import map.model.GpxPoint;
import map.model.TileInfo;
import map.tilemanager.TilesManager;
import map.tilemanager.TilesProvider;

public class ExportUtils {
	
	public static void generateImageForTrace(List<GpxPoint> points, TilesProvider tilesProvider, int zoomLevel, boolean downloadTiles) {
		// First get necessary tiles
		Map<String, TileInfo> tiles = tilesProvider.getTiles(points, zoomLevel, downloadTiles);
		
		int minX = -1;
		int minY = -1;
		int maxX = -1;
		int maxY = -1;		

		// Get min and max
		for ( TileInfo tile : tiles.values() ) {
			zoomLevel = tile.getZoomLevel();
			int tileX = tile.getTileX();
			int tileY = tile.getTileY();
			
			if( minX==-1 || minX > tileX) {
				minX = tileX;
			}
			
			if( minY==-1 || minY >tileY) {
				minY = tileY;
			}
			
			if ( maxX==-1 || maxX < tileX ) {
				maxX = tileX;
			}
			if ( maxY==-1 || maxY < tileY ) {
				maxY = tileY;
			}
		}
		
		System.out.println( minX + ":" + minY + "/" + maxX +  ":" + maxY);
		
		// Load all necessary images in min/max limit
		Map<String, BufferedImage> bufferedImages = new HashMap<String,BufferedImage>();
		for ( int x = minX; x <= maxX; x++ ) {
			for ( int y = minY; y <= maxY; y++ ) {
				// Get tile
				String key = x + "-" + y + "-" + zoomLevel;
				TileInfo tile = tiles.get(key);
				
				if ( tile == null ) {
					System.out.println("Tile NOT in list load and add it : " + key);
					// load tile
					tile = tilesProvider.getTile(x, y, zoomLevel, downloadTiles, false);
					tiles.put( key, tile);
				}
				// FIXME put back
				/*if ( downloadTiles && !tile.exist() ) {
					System.out.println("Download not existing tile : " + key);
					tilesManager.downloadTileImage(tile);
				}*/
				
				String keyBuff = ""  + (x  - minX) + "-" + (y - minY);
				System.out.println("Adding buffimg " + keyBuff +" for " + key);
				bufferedImages.put(keyBuff, tile.getImage());
			}
		}
		
		// Now all tiles are loaded, generate final image
		// FIXME crad
		int type = bufferedImages.values().iterator().next().getType();  
        int chunkWidth = bufferedImages.values().iterator().next().getWidth();  
        int chunkHeight =  bufferedImages.values().iterator().next().getHeight();
        
        //Initializing the final image
        int rowCount = (maxX - minX) + 1;
        int colCount =(maxY - minY) + 1;
        int finalImgwidth = chunkWidth * rowCount;
        int finalImgHeigth = chunkHeight*colCount;
        
        System.out.println("Final img:" + finalImgwidth + "*" + finalImgHeigth);
        
        BufferedImage finalImg = new BufferedImage(finalImgwidth, finalImgHeigth, BufferedImage.TYPE_INT_ARGB);
        
        for (int row = 0; row <= rowCount; row++) {  
            for (int col = 0; col < colCount; col++) {
            	
            	String keyBuff = "" + row + "-"  + col;
            	System.out.println("Managing :"  + keyBuff);
            	
                finalImg.createGraphics().drawImage(
                		bufferedImages.get(keyBuff), chunkWidth * row, chunkHeight * col, null);  
            }  
        }
        
        String upKey = minX + "-" + minY + "-" + zoomLevel;
        TileInfo tileUp = tiles.get(upKey);
        String downKey = maxX + "-" + maxY + "-" + zoomLevel;
        TileInfo tileDown = tiles.get(downKey);
        drawGpsPoints(points, finalImg.createGraphics(), tileUp.getUpLeftPoint(), tileDown.getDownRightPoint(), finalImgwidth, finalImgHeigth);
       
        try {
			ImageIO.write(finalImg, "png", new File("export"+ System.currentTimeMillis() + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
	}
	

	
	private static void drawGpsPoints(List<GpxPoint> points, Graphics2D graphic2D, GpsPoint upcorner, GpsPoint buttomCorner, int finalImgWidth, int finalImgHeigth) {
		// Set gps points style 
		graphic2D.setStroke(new BasicStroke(2f));
		graphic2D.setColor(Color.RED);
		double longitudeSize = buttomCorner.getLongitude() - upcorner.getLongitude();
		double latitudeSize = upcorner.getLatitude() - buttomCorner.getLatitude();
		int[] xPixels = new int[points.size()];
		int[] yPixels = new int[points.size()];

		int idx = 0;
		// Construct polyline
		for (GpxPoint point : points ) {
			double propX = (point.getLongitude() - upcorner.getLongitude()) / longitudeSize; 
			int pixX = (int) (finalImgWidth * propX);
			double propY = (upcorner.getLatitude() - point.getLatitude() ) / latitudeSize;
			int pixY = (int) (finalImgHeigth * propY);
			xPixels[idx] = pixX;
			yPixels[idx] = pixY;
			idx++;
		}
		
		graphic2D.drawPolyline(xPixels, yPixels, xPixels.length);
		
	}
	
	public static void main(String[] args ) {
		System.out.println("Start");
		Configuration config = Configuration.getConfiguration();
		
		if ( config == null ) {
			return;
		}
		
		String proxy = config.getHttpProxy();
		String proxyPort = config.getHttpProxyPort();
		
		if ( proxy != null && proxyPort != null) {
			System.setProperty("http.proxyHost", proxy);
		    System.setProperty("http.proxyPort", proxyPort);
		}
		
		List<TilesManager> tilesManagers = config.getTilesManagers();

		
		List<GpxPoint> points =  GpxDecoder.parse(new File("C:\\temp\\gpx\\porte.gpx"));
		//generateImageForTrace(points, tilesManagers.get(1), 12, true);
	}
}

