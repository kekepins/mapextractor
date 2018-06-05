package map.tilemanager;

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
	


}
