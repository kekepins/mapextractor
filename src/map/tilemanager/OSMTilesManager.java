package map.tilemanager;


public class OSMTilesManager extends WebTilesManager {

	@Override
	protected String constructUrl(int tileX, int tileY, int zoom) {
		return "http://tile.openstreetmap.org/" + zoom + "/"  + tileX + "/" + tileY + ".png";
	}

	@Override
	protected String getReferer() {
		return "http://dev.openlayers.org/releases/OpenLayers-2.13.1/examples/wmts.html";
	}

	@Override
	protected String getTileType() {
		return "OSM";
	}

	@Override
	protected String getTileImgExtension() {
		return "png";
	}

}
