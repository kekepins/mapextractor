package map.tilemanager;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import map.gps.GpsUtility;
import map.model.GpxPoint;
import map.model.TileInfo;

public class TilesProvider {
	
	private final static int CACHE_SIZE = 50;
	
	private TilesManager localManager;
	private TilesManager extManager;
	private String type;
	
	private LinkedHashMap<String, TileInfo> cacheTiles;
	
	@SuppressWarnings("serial")
	public TilesProvider(String type, TilesManager localManager, TilesManager extManager) {
		// true = use access order instead of insertion order.
        this.cacheTiles = new LinkedHashMap<String, TileInfo>(CACHE_SIZE, 0.75f, true) {                                
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, TileInfo> eldest) {
                // When to remove the eldest entry.
                return size() > CACHE_SIZE; // Size exceeded the max allowed.
            }
        };
        this.setType(type);
        this.localManager = localManager;
        this.extManager = extManager;
	}
	
	public String getExt() {
		return localManager.getTileImgExtension();
	}
	
	public TileInfo getTile(int xTile, int yTile, int zoomLevel, boolean download, boolean update) {
		// Look in cache
		TileInfo tileInfo = cacheTiles.get(getKey(xTile, yTile));
		
		if ( tileInfo != null && update ) {
			// remove from cache and 
			cacheTiles.remove(getKey(xTile, yTile));
			
			// remove from persistence
			localManager.delete(tileInfo);
			
			tileInfo = null;
		}
		
			
		if ( tileInfo == null ) {
			tileInfo = localManager.getTileInfo(xTile, yTile, zoomLevel);
			
			if ( !tileInfo.hasImage() && download ) {
				tileInfo = extManager.getTileInfo(xTile, yTile, zoomLevel);
				
				if ( tileInfo.hasImage()) {
					System.out.println("Tile load from internet " + xTile + ":" + yTile + " save in local (db)");
					localManager.save(tileInfo);
				}
			}
			else {
				System.out.println("Tile " + xTile + ":" + yTile + " not in local (db)");
			}
			
			// Add in cache			
			if (tileInfo.hasImage()) {
				System.out.println("Add Tile " + xTile + ":" + yTile + " in cache");
				cacheTiles.put(getKey(xTile, yTile), tileInfo);
			}
		}
		else {
			System.out.println("Tile " + xTile + ":" + yTile + " in cache");
		}
		
		return tileInfo;
	}
	
	/**
	 * Get all tiles from a point list
	 */
	public Map<String, TileInfo> getTiles(List<GpxPoint> points, int zoomLevel, boolean download) {
		Map<String, TileInfo> tiles = new HashMap<String, TileInfo>();
		
		for ( GpxPoint point : points ) {
			TileInfo tileInfo = getTile(
					GpsUtility.getXTile(point.getLatitude(), point.getLongitude(), zoomLevel),
					GpsUtility.getYTile(point.getLatitude(), point.getLongitude(), zoomLevel),
					zoomLevel,
					download,
					false
					);
			
			if ( tileInfo != null && !tiles.containsKey(tileInfo.getId())) {
				tiles.put(tileInfo.getId(), tileInfo);
			}
		}
		
		return tiles;
		
	}
	
	public TileInfo getTileLocally(int xTile, int yTile, int zoomLevel) {
		TileInfo tileInfo = cacheTiles.get(getKey(xTile, yTile));
		
		if ( tileInfo == null ) {
			tileInfo = localManager.getTileInfo(xTile, yTile, zoomLevel);
		}
		
		if ( !tileInfo.hasImage()) {
			return null;
		}
		
		return tileInfo;
	}
	
	public Collection<TileInfo> getAllTilesToDisplay(List<GpxPoint> points, int zoomLevel, boolean downloadTiles) {
		// First get necessary tiles
		Map<String, TileInfo> tiles = getTiles(points, zoomLevel, downloadTiles);
		
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
		
		// Load all necessary tiles in min/max limit
		for ( int x = minX; x <= maxX; x++ ) {
			for ( int y = minY; y <= maxY; y++ ) {
				// Get tile
				String key = x + "-" + y + "-" + zoomLevel;
				TileInfo tile = tiles.get(key);
				
				if ( tile == null ) {
					// load tile
					tile = getTile(x, y, zoomLevel, downloadTiles, false);
					tiles.put( key, tile);
				}
	
			}
		}
		
		return tiles.values();
	}
	
	
	private String getKey(int xTile, int yTile) {
		return xTile + "-" + yTile;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
