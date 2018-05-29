package map.tilemanager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import map.model.GpsPoint;
import map.model.TileInfo;

public class FileTilesManager extends TilesManager{
	
	private String type;
	private File tileDir;
	private String ext;
	
	public FileTilesManager(String type, File baseTileDir, String ext) {
		this.type = type;
		this.ext = ext;
		this.tileDir = new File(baseTileDir, type);
	}

	@Override
	protected String getTileImgExtension() {
		// TODO Auto-generated method stub
		return ext;
	}

	@Override
	public TileInfo getTileInfo(int xTile, int yTile, int zoomLevel) {
		TileInfo tileInfo = new TileInfo(type);
		tileInfo.setTileX(xTile);
		tileInfo.setTileY(yTile);
		tileInfo.setZoomLevel(zoomLevel);
		List<GpsPoint> bounds = tileToBounds(xTile, yTile, zoomLevel);
		tileInfo.setUpLeftPoint(bounds.get(0));
		tileInfo.setDownRightPoint(bounds.get(1));
		if ( exist(xTile, yTile, zoomLevel) ) {
			tileInfo.setImage(getImage(xTile, yTile, zoomLevel));
		}
		
		return tileInfo;

	}

	@Override
	protected String getTileType() {
		return this.type;
	}
	
	private boolean exist(int xTile, int yTile, int zoomLevel) {
		return getTileFile(xTile, yTile, zoomLevel).exists();
	}

	
	public BufferedImage getImage(int xTile, int yTile, int zoomLevel) {
		if ( exist(xTile, yTile, zoomLevel) ) {
			try {
				return ImageIO.read(this.getTileFile(xTile, yTile, zoomLevel));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		else {
			return null;
		}

	}
	
	private File getTileFile(int xTile, int yTile, int zoomLevel) {
		String imgFile = this.type + "-" + xTile + "-" + yTile + "-" + zoomLevel + "." + ext;
		return new File(tileDir, imgFile);
	}

	@Override
	public void save(TileInfo tileInfo) {
		System.out.println("Save file " + tileInfo);
		try {
			OutputStream os = new FileOutputStream(this.getTileFile(tileInfo.getTileX(), tileInfo.getTileY(), tileInfo.getZoomLevel()));
			os.write(tileInfo.getData(ext));
			os.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	@Override
	public void delete(TileInfo tileInfo) {
		System.out.println("Delete file " + tileInfo);
		this.getTileFile(tileInfo.getTileX(), tileInfo.getTileY(), tileInfo.getZoomLevel()).delete();
	}


}
