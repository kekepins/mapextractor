package map.sql;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import map.conf.Configuration;
import map.gps.GpsUtility;
import map.gps.GpxDecoder;
import map.model.GpsPoint;
import map.model.GpxPoint;
import map.model.TileInfo;
import map.tilemanager.TilesManager;
import map.tilemanager.TilesProvider;

public class SQLLiteUtils {

	private static final int MAX_ZOOM = 15;

	public static Connection createDatabase(String dbName)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {

		// Load the JDBC driver class dynamically.
		Driver d = (Driver) Class.forName("org.sqlite.JDBC").newInstance();
		DriverManager.registerDriver(d);
		String url = "jdbc:sqlite:" + dbName;
		return DriverManager.getConnection(url);

	}
	


	public static void createTables(Connection connection)  {

		Statement stmt = null;
		try {
			stmt = connection.createStatement();
		
			String sql = "CREATE TABLE IF NOT EXISTS TILES "
			+ "(X INT    NOT NULL,"
			+ " Y INT    NOT NULL, "
			+ " Z INT    NOT NULL, "
			+ " TYPE     CHAR(3) NOT NULL, "
			+ " IMAGE    BLOB)";
	
			stmt.executeUpdate(sql);
	
			sql = "CREATE TABLE IF NOT EXISTS GPS_POINTS "
			+ "(ID INT NOT NULL,"
			+ " LATITUDE REAL   NOT NULL,"
			+ " LONGITUDE REAL  NOT NULL, "
			+ " ALTITUDE  REAL  NULL, "
			+ " DATE STRING NULL)";
	
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS GPS_TRACE_INFOS "
				+ "(ID INT NOT NULL,"
				+ " DESCRIPTION STRING NOT NULL,"
				+ " DISTANCE REAL  NOT NULL)";

		
			stmt.executeUpdate(sql);			
		
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 

	}

	public static void insertTiles(Connection connection, List<TileInfo> tiles, String ext)
			throws SQLException {

		PreparedStatement preparedStatement = connection
				.prepareStatement("insert into TILES values (?, ?, ?, ?, ?);");

		int tileInserted = 0;
		for (TileInfo tileInfo : tiles) {
			//if (tileInfo.exist()) {
			if ( tileInfo != null && tileInfo.hasImage()) { 
				byte[] imageData = tileInfo.getData(ext); //getByteArrayFromFile(tileInfo.getTileFile()); 
				preparedStatement.setInt(1, tileInfo.getTileX());
				preparedStatement.setInt(2, tileInfo.getTileY());
				preparedStatement.setInt(3, tileInfo.getZoomLevel());
				preparedStatement.setString(4, tileInfo.getDomain());
				preparedStatement.setBytes(5, imageData);
				preparedStatement.executeUpdate();
				tileInserted++;
			}

			//}

		}
		
		preparedStatement.close();

		System.out.println("Tile insert:" + tileInserted);

	}
	
	public static void insertTile(Connection connection, TileInfo tileInfo, byte[] imageData){

		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection
					.prepareStatement("insert into TILES values (?, ?, ?, ?, ?);");
			
			preparedStatement.setInt(1, tileInfo.getTileX());
			preparedStatement.setInt(2, tileInfo.getTileY());
			preparedStatement.setInt(3, tileInfo.getZoomLevel());
			preparedStatement.setString(4, tileInfo.getDomain());
			preparedStatement.setBytes(5, imageData);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if ( preparedStatement != null ) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void deleteTile(Connection connection, TileInfo tileInfo) {
		System.out.println("Delete tile " + tileInfo);
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement("delete from TILES where X=? AND Y=? AND Z=? AND TYPE=?");
			preparedStatement.setInt(1, tileInfo.getTileX());
			preparedStatement.setInt(2, tileInfo.getTileY());
			preparedStatement.setInt(3, tileInfo.getZoomLevel());
			preparedStatement.setString(4, tileInfo.getDomain());
			preparedStatement.executeUpdate();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if ( preparedStatement != null ) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		

		
	}

	
	public static void insertGpsInfos(Connection connection, int id, String description ) throws SQLException {

		PreparedStatement preparedStatement = connection
				.prepareStatement("insert into GPS_TRACE_INFOS values (?, ?, ?)");
		preparedStatement.setInt(1, id);
		preparedStatement.setString(2, description);
		preparedStatement.setDouble(3, 125.3);
		preparedStatement.executeUpdate();

		preparedStatement.close();

	}
	

	public static void insertGpsPoints(Connection connection, int id,
			List<GpxPoint> points) throws SQLException {

		PreparedStatement preparedStatement = connection
				.prepareStatement("insert into GPS_POINTS values (?, ?, ?, ?, ?);");

		//int id = 1;

		for (GpxPoint point : points) {

			preparedStatement.setInt(1, id);
			preparedStatement.setDouble(2, point.getLatitude());
			preparedStatement.setDouble(3, point.getLongitude());
			if (point.getAltitude() != null) {
				preparedStatement.setDouble(4, point.getAltitude());

			}
			else {
				preparedStatement.setNull(4, Types.DECIMAL);
			}

			if (point.getDate() != null) {

				preparedStatement
						.setString(5, getDateTime());

			}

			else {
				preparedStatement.setNull(5, Types.VARCHAR);
			}
			preparedStatement.executeUpdate();
		}
		
		preparedStatement.close();
	}

	public static void selectTileAndWriteToDisk(Connection connection,
			TileInfo tileInfo,
			File dir) {

		String query = "select IMAGE from TILES where X=? and Y=? and Z=? and TYPE=?";
		PreparedStatement preparedStatement = null;
		try {

			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, tileInfo.getTileX());
			preparedStatement.setInt(2, tileInfo.getTileY());
			preparedStatement.setInt(3, tileInfo.getZoomLevel());
			preparedStatement.setString(4, tileInfo.getDomain());
			ResultSet rslt = preparedStatement.executeQuery();

			if (rslt.next()) {
				byte[] imgArr = rslt.getBytes("IMG");
				InputStream in = new ByteArrayInputStream(imgArr);
				BufferedImage bImageFromConvert = ImageIO.read(in);
				ImageIO.write(bImageFromConvert, "PNG", new File(dir, "img"
						+ System.currentTimeMillis() + ".png"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static boolean exist(Connection connection,TileInfo tileInfo) {
		String query = "select IMAGE from TILES where X=? and Y=? and Z=? and TYPE=?";
		PreparedStatement preparedStatement = null;
		try {

			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, tileInfo.getTileX());
			preparedStatement.setInt(2, tileInfo.getTileY());
			preparedStatement.setInt(3, tileInfo.getZoomLevel());
			preparedStatement.setString(4, tileInfo.getDomain());
			ResultSet rslt = preparedStatement.executeQuery();

			if (rslt.next()) {
				return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {

			try {
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	
	public static BufferedImage selectBufferedImage(Connection connection,int tileX, int tileY, int zoomLevel, String tileType ) {

		String query = "select IMAGE from TILES where X=? and Y=? and Z=? and TYPE=?";
		PreparedStatement preparedStatement = null;
		try {

			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, tileX);
			preparedStatement.setInt(2, tileY);
			preparedStatement.setInt(3, zoomLevel);
			preparedStatement.setString(4, tileType);
			ResultSet rslt = preparedStatement.executeQuery();

			if (rslt.next()) {
				byte[] imgArr = rslt.getBytes("IMAGE");
				InputStream in = new ByteArrayInputStream(imgArr);
				return ImageIO.read(in);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {

			try {
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static byte[] selectImageBytes(Connection connection,TileInfo tileInfo) {

		String query = "select IMAGE from TILES where X=? and Y=? and Z=? and TYPE=?";
		PreparedStatement preparedStatement = null;
		try {

			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, tileInfo.getTileX());
			preparedStatement.setInt(2, tileInfo.getTileY());
			preparedStatement.setInt(3, tileInfo.getZoomLevel());
			preparedStatement.setString(4, tileInfo.getDomain());
			ResultSet rslt = preparedStatement.executeQuery();

			if (rslt.next()) {
				return rslt.getBytes("IMAGE");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {

			try {
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void generateDbTilesForTrace(String dbName,
			List<GpxPoint> points, List<TilesProvider> tilesProviders, int zoomLevel,
			boolean downloadTiles)
		throws InstantiationException, IllegalAccessException,
				ClassNotFoundException, SQLException {
		
		List<TileInfo> tiles = new ArrayList<TileInfo>();
		
		Connection connection = createDatabase(dbName);
		createTables(connection);
		System.out.println("createTables");
		
		for ( TilesProvider tilesProvider : tilesProviders ) {
		
			for (int zoom = 1; zoom <= MAX_ZOOM; zoom++) {
				Collection<TileInfo> tilesZoom = tilesProvider.getAllTilesToDisplay(points, zoom, downloadTiles);
				if ( tilesZoom != null && tilesZoom.size() > 0 ) {
					tiles.addAll(tilesZoom);
					System.out.println("insertTiles " +tilesProvider.getType()  + " " +  zoom);		
					insertTiles(connection, tiles, tilesProvider.getExt());
				}
			}
		}
		int id = 1;
		insertGpsInfos( connection, id,  "desc" );
		insertGpsPoints(connection, id, points);
		System.out.println("insertGpsPoints=>end creationdb");
		connection.close();
	
	}
	
	public static void generateDbTilesWhithAllFiles(String dbName, List<TilesProvider> tilesProviders, List<GpxPoint> points)
		throws InstantiationException, IllegalAccessException,
				ClassNotFoundException, SQLException {
		Connection connection = createDatabase(dbName);
		createTables(connection);
		System.out.println("createTables");
		
		/*for (TilesProvider tilesProvider : tilesProviders ) {
			File tileDir = Configuration.getConfiguration().getTileDir();
			File workTileDir = new File( tileDir, tilesProvider.getType());
			
			File[] tileFiles = workTileDir.listFiles();
			
			for ( File tileFile : tileFiles) {
				System.out.println("File " + tileFile.getName());
				String fileName = tileFile.getName();
				int firstMinus = fileName.indexOf("-", 4);
				String x = fileName.substring(4, firstMinus);
				int secondMinus = fileName.indexOf("-", firstMinus + 1);
				String y = fileName.substring(firstMinus + 1, secondMinus);
				int thirdMinus = fileName.indexOf(".", secondMinus + 1);
				String z = fileName.substring(secondMinus + 1, thirdMinus);
				System.out.println(x + ":" + y + ":" + z);
				
				TileInfo tileInfo = tilesProvider.getTile(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z), false);
				insertTile(connection, tileInfo, tileInfo.getData(tilesProvider.getExt()));
			}
		}*/
		int id = 1;
		if ( points != null && points.size() > 0) {
			insertGpsInfos( connection, id,  "desc" );
			insertGpsPoints(connection, id, points);
		}
		System.out.println("insertGpsPoints=>end creationdb");
		connection.close();


	}
	
	public static GpsPoint selectNearestGpsPoint(Connection connection, double latitude, double longitude, double delta) {
		// 0.02 == 2.22km
		//double delta = 0.1; 
		String sql = "Select LATITUDE, LONGITUDE, ALTITUDE, DATE FROM GPS_POINTS " +
					  "where LATITUDE > ? and LATITUDE < ? and LONGITUDE > ? and LONGITUDE < ?";
		PreparedStatement preparedStatement = null;
		GpsPoint nearestPoint = null;
		try {

			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setDouble(1, latitude - delta );
			preparedStatement.setDouble(2, latitude + delta );
			preparedStatement.setDouble(3, longitude - delta);
			preparedStatement.setDouble(4, longitude + delta);
			ResultSet rslt = preparedStatement.executeQuery();
			double minDistance = Double.MAX_VALUE;
			
			while (rslt.next()) {
				double lat = rslt.getDouble(1);
				double longi = rslt.getDouble(2);
				double alti = rslt.getDouble(3);
				double distance = 
					GpsUtility.calculateDistance(latitude, longitude,
							lat, longi);
				
				if ( nearestPoint == null || distance <  minDistance ) {
					nearestPoint = new GpsPoint(longi, lat, alti);
					minDistance = distance;		
				}
				
				//System.out.println("" + lat + "/" + longi +":" + distance);
				
			}
			
			return nearestPoint;
		}
		catch(Exception e ) {
			e.printStackTrace();
			return null;
		}
		finally  {
			try {
				if ( preparedStatement != null ) {
					preparedStatement.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}
	
    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

	

	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {

		Configuration conf = Configuration.getConfiguration();
		List<TilesManager> tilesManagers = conf.getTilesManagers();
		List<GpxPoint> points = GpxDecoder.parse(new File("C:\\temp\\gpx\\porte.gpx"));
	}




}
