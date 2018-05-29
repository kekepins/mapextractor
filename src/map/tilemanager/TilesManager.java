package map.tilemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.model.GpsPoint;
import map.model.GpxPoint;
import map.model.TileInfo;

public abstract class TilesManager {
	
	protected abstract String getTileType();
	
	protected abstract String getTileImgExtension();
	
	public abstract void save(TileInfo tileInfo);
	
	public abstract void delete(TileInfo tileInfo);
	
	public abstract TileInfo getTileInfo(int xTile, int yTile, int zoomLevel);

	
	public int getYTile(final double lat, final double lon,	final int zoom ) {
		int yTile = (int) Math
				.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1
						/ Math.cos(Math.toRadians(lat)))
						/ Math.PI)
						/ 2 * (1 << zoom));
		
		if (yTile < 0)
			yTile = 0;
		if (yTile >= (1 << zoom))
			yTile = ((1 << zoom) - 1);
		
		return yTile;
	}
	
	public int getXTile(final double lat, final double lon,	final int zoom ) {
		int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
		
		if (xtile < 0)
			xtile = 0;
		if (xtile >= (1 << zoom))
			xtile = ((1 << zoom) - 1);

		return xtile;
	}
	
	public List<GpsPoint> tileToBounds(int xTile, int yTile, int zoom) {
		// Left up
		List<GpsPoint> points = new ArrayList<GpsPoint>();
		points.add( new GpsPoint(tile2lon(xTile, zoom), tile2lat(yTile, zoom)));
		points.add( new GpsPoint(tile2lon(xTile + 1, zoom), tile2lat(yTile + 1, zoom)));
		
		return points;
	}
	
	private double tile2lon(int xTile, int zoom) {
		return xTile / Math.pow(2.0, zoom) * 360.0 - 180;
	}

	private double tile2lat(int yTile, int zoom) {
		double n = Math.PI - (2.0 * Math.PI * yTile) / Math.pow(2.0, zoom);
		return Math.toDegrees(Math.atan(Math.sinh(n)));
	}

	
	public TileInfo getTileInfo(double latitude, double longitude, int zoomLevel) {
	
		return getTileInfo(getXTile(latitude, longitude, zoomLevel), getYTile(latitude, longitude, zoomLevel), zoomLevel );
	}

		
	
	/**
	 * Get all tiles from a point list
	 */
	public Map<String, TileInfo> getTileInfos(List<GpxPoint> points, int zoomLevel) {
		Map<String, TileInfo> tiles = new HashMap<String, TileInfo>();
		
		for ( GpxPoint point : points ) {
			TileInfo tileInfo = getTileInfo(point.getLatitude(), point.getLongitude(), zoomLevel);
			if ( !tiles.containsKey(tileInfo.getId())) {
				tiles.put(tileInfo.getId(), tileInfo);
			}
		}
		
		return tiles;
		
	}
	
	
	public Collection<TileInfo> getAllTilesToDisplay(List<GpxPoint> points, TilesManager tilesManager, int zoomLevel) {
		// First get necessary tiles
		Map<String, TileInfo> tiles = tilesManager.getTileInfos(points, zoomLevel);
		
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
					tile = tilesManager.getTileInfo(x, y, zoomLevel);
					tiles.put( key, tile);
				}
	
			}
		}
		
		return tiles.values();
	}
	
	
	public String toString() {
		return getTileType();
	}


}
