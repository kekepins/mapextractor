package map.model;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

//import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class TileInfo {
	

	private GpsPoint upLeftPoint;
	private GpsPoint downRightPoint;
	private int zoomLevel;
	private int tileX;
	private int tileY;
	private BufferedImage image;

	private String domain;

	public TileInfo(String domain) {
		this.domain = domain;
	}

	public GpsPoint getUpLeftPoint() {
		return upLeftPoint;
	}

	public void setUpLeftPoint(GpsPoint upLeftPoint) {
		this.upLeftPoint = upLeftPoint;
	}

	public GpsPoint getDownRightPoint() {
		return downRightPoint;
	}

	public void setDownRightPoint(GpsPoint downRightPoint) {
		this.downRightPoint = downRightPoint;
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	public int getTileX() {
		return tileX;
	}

	public void setTileX(int tileX) {
		this.tileX = tileX;
	}

	public int getTileY() {
		return tileY;
	}

	public void setTileY(int tileY) {
		this.tileY = tileY;
	}

	public String toString() {
		return "Tile [" + tileX + "][" + tileY + "] zoom [" + zoomLevel
				+ "] HG [" + upLeftPoint + "] BD [" + downRightPoint + "]";
	}

	public String getId() {
		return domain + "-" + tileX + "-" + tileY + "-" + zoomLevel;
	}

	public String getDomain() {
		return domain;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public boolean hasImage() {
		return image != null;
	}

	public byte[] getData(String ext) {

		ByteOutputStream bos = null;
		try {
			bos = new ByteOutputStream();
			try {
				ImageIO.write(image, ext, bos);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			try {
				bos.close();
			} catch (Exception e) {
			}
		}

		return bos == null ? null : bos.getBytes();
	}

}
