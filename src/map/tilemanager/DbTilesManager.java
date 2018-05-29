package map.tilemanager;

import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import map.model.GpsPoint;
import map.model.TileInfo;
import map.sql.SQLLiteUtils;

public class DbTilesManager extends TilesManager {
	
	private String type;
	private String ext;
	protected Connection tilesDBconnection;
	
	public DbTilesManager(String dbName, String type, String ext) 
		throws Exception {
		this.type = type;
		this.ext = ext;
		
		tilesDBconnection = connect(dbName);
		SQLLiteUtils.createTables(tilesDBconnection);
		
	}


	@Override
	protected String getTileImgExtension() {
		return this.ext;
	}

	@Override
	protected String getTileType() {
		return this.type;
	}
	
	private Connection connect(String dbName)
		throws InstantiationException, IllegalAccessException,	ClassNotFoundException, SQLException {

		// Load the JDBC driver class dynamically.
		Driver d = (Driver) Class.forName("org.sqlite.JDBC").newInstance();
		DriverManager.registerDriver(d);
		String url = "jdbc:sqlite:" + dbName;
		return DriverManager.getConnection(url);
	}

	@Override
	public TileInfo getTileInfo(int xTile, int yTile, int zoomLevel) {

		BufferedImage image = SQLLiteUtils.selectBufferedImage(
				this.tilesDBconnection, xTile, yTile, zoomLevel, this.type);
		TileInfo tileInfo = new TileInfo(type);
		tileInfo.setTileX(xTile);
		tileInfo.setTileY(yTile);
		tileInfo.setZoomLevel(zoomLevel);
		List<GpsPoint> bounds = tileToBounds(xTile, yTile, zoomLevel);
		tileInfo.setUpLeftPoint(bounds.get(0));
		tileInfo.setDownRightPoint(bounds.get(1));
		tileInfo.setImage(image);

		return tileInfo;

	}
	
	@Override
	public void save(TileInfo tileInfo) {
		SQLLiteUtils.insertTile(this.tilesDBconnection, tileInfo, tileInfo.getData(ext));
		
	}
	
	public static void main(String[] args) {
		try {
			DbTilesManager localTilesManager = new DbTilesManager("C:\\dev\\test\\map\\tiles1413548429251.db", "OSM", "png");
			TileInfo tileInfo = localTilesManager.getTileInfo(1057, 734, 11);
			System.out.println("Done");
			
			
			TileInfo tileInfo2 = new TileInfo("OSM");
			tileInfo2.setTileX(8);
			tileInfo2.setTileY(5);
			tileInfo2.setZoomLevel(4);
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return this.tilesDBconnection;
	}


	@Override
	public void delete(TileInfo tileInfo) {
		SQLLiteUtils.deleteTile(this.tilesDBconnection, tileInfo);
		
	}


}
