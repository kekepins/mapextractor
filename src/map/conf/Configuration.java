package map.conf;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import map.tilemanager.DbTilesManager;
import map.tilemanager.FileTilesManager;
import map.tilemanager.GenericTilesManager;
import map.tilemanager.TilesManager;
import map.tilemanager.TilesProvider;

public class Configuration {
	
	public static Configuration configuration;
	
	private static final String CONFIG_FILE = "config.properties";
	
	private File tileDir;
	private File gpsTraceDir;
	private String httpProxy;
	private String httpProxyPort;
	private String dbName;
	
	private List<TilesManager> tilesManagers = new ArrayList<TilesManager>();
	private List<String> tileTypes = new ArrayList<String>();
	private List<TilesProvider> tilesProviders = new ArrayList<TilesProvider>();
	
	public File getTileDir() {
		return tileDir;
	}
	
	public void load() throws Exception {
		String filename = CONFIG_FILE;
		InputStream input = Configuration.class.getClassLoader().getResourceAsStream(filename);
		if(input==null){
            throw new Exception("Impossible de trouver config.properties");
		}
		Properties prop = new Properties();
		prop.load(input);
		
		String tileDirStr = prop.getProperty("tile.dir");
		tileDir = new File(tileDirStr);
		
		String gpsTraceDirProp = prop.getProperty("gps.trace.dir");
		if (gpsTraceDirProp != null ) {
			gpsTraceDir = new File(gpsTraceDirProp);
		}
		
		httpProxy = prop.getProperty("http.proxyHost");
		httpProxyPort  = prop.getProperty("http.proxyPort");
		
		PersistenceMode mode = getMode( prop );
		
		String tileProviders = prop.getProperty("tile.providers.ids");
				
		if (tileProviders != null) {
			StringTokenizer tokenizer = new StringTokenizer(tileProviders,",");
			while (tokenizer.hasMoreElements()) {
				String id = tokenizer.nextToken();
				TilesManager tilesManager = readTileManager(id, prop);
				tilesManagers.add(tilesManager);
				tileTypes.add(id);
				TilesManager localTilesManager = null;
				if (mode == PersistenceMode.file ) {
					localTilesManager = new FileTilesManager(id, tileDir, prop.getProperty(id + ".extension"));
				}
				else {
					dbName = prop.getProperty("persistence.db.name");
					localTilesManager = new DbTilesManager( dbName, id, prop.getProperty(id + ".extension"));
				}
				
				tilesProviders.add( new TilesProvider(id, localTilesManager, tilesManager));
				
				File mngTileDir = new File(tileDir, id);
				if ( !mngTileDir.exists() ) {
					mngTileDir.mkdirs();
				}
			}
		}
	
	}
	 
	public static Configuration getConfiguration() {
		if  (configuration == null ) {
			configuration = new Configuration();
			try {
				configuration.load();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return configuration;
	}

	public String getHttpProxy() {
		return httpProxy;
	}

	public String getHttpProxyPort() {
		return httpProxyPort;
	}
	
	private TilesManager readTileManager(String id, Properties properties) {
		
		GenericTilesManager tilesManager = new GenericTilesManager();
		tilesManager.setId(id);
		tilesManager.setExtension(properties.getProperty(id + ".extension"));
		tilesManager.setReferer(properties.getProperty(id + ".referer"));
		tilesManager.setKey(properties.getProperty(id + ".key"));
		tilesManager.setUrlTemplate(properties.getProperty(id + ".url"));
		
		return tilesManager;
	}
	
	public List<String> getTileTypes() {
		return this.tileTypes;
	}

	public List<TilesManager> getTilesManagers() {
		return tilesManagers;
	}
	
	public List<TilesProvider> getTilesProviders() {
		return this.tilesProviders;
	}

	public File getGpsTraceDir() {
		return gpsTraceDir;
	}

	public void setGpsTraceDir(File gpsTraceDir) {
		this.gpsTraceDir = gpsTraceDir;
	}
	
	private PersistenceMode getMode(Properties prop) {
		
		String mode = (String) prop.get("persistence.mode");
		
		if ( PersistenceMode.database.name().equals( mode ) ) {
			return PersistenceMode.database;
		}
		
		return PersistenceMode.file;
		
	}
	
	public String getDbName() {
		return this.dbName;
	}


}
