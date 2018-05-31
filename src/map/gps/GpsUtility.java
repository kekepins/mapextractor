package map.gps;

import java.util.List;

import map.model.GpsPoint;
import map.model.GpxPoint;


public class GpsUtility {

    private static int TILE_SIZE = 256;
    public static final double MAX_LAT = 85.05112877980659;
    public static final double MIN_LAT = -85.05112877980659;

    public static double radius(int aZoomlevel) {
        return (TILE_SIZE * (1 << aZoomlevel)) / (2.0 * Math.PI);
    }

    /**
     * Returns the absolut number of pixels in y or x, defined as: 2^Zoomlevel *
     * TILE_WIDTH where TILE_WIDTH is the width of a tile in pixels
     * 
     * @param aZoomlevel
     */
    public static int getMaxPixels(int aZoomlevel) {
        return TILE_SIZE * (1 << aZoomlevel);
    }

    public static int falseEasting(int aZoomlevel) {
        return getMaxPixels(aZoomlevel) / 2;
    }

    public static int falseNorthing(int aZoomlevel) {
        return (-1 * getMaxPixels(aZoomlevel) / 2);
    }

    /**
     * Transform longitude to pixelspace
     * 
     * @param aLongitude
     *            [-180..180]
     * @return [0..2^Zoomlevel*TILE_SIZE[
     */
    public static int LonToX(double aLongitude, int aZoomlevel) {
        double longitude = Math.toRadians(aLongitude);
        int x = (int) ((radius(aZoomlevel) * longitude) + falseEasting(aZoomlevel));
        x = Math.min(x, getMaxPixels(aZoomlevel) - 1);
        return x;
    }

    /**
     * Transforms latitude to pixelspace
     * 
     * @param aLat
     *            [-90...90]
     * @return [0..2^Zoomlevel*TILE_SIZE[
     */
    public static int LatToY(double aLat, int aZoomlevel) {
        if (aLat < MIN_LAT)
            aLat = MIN_LAT;
        else if (aLat > MAX_LAT)
            aLat = MAX_LAT;
        double latitude = Math.toRadians(aLat);
        int y = (int) (-1
                * (radius(aZoomlevel) / 2.0 * Math.log((1.0 + Math.sin(latitude)) / (1.0 - Math.sin(latitude)))) - falseNorthing(aZoomlevel));
        y = Math.min(y, getMaxPixels(aZoomlevel) - 1);
        return y;
    }

    /**
     * Transforms pixel coordinate X to longitude
     * 
     * @param aX
     *            [0..2^Zoomlevel*TILE_WIDTH[
     * @return ]-180..180[
     */
    public static double XToLon(int aX, int aZoomlevel) {
        aX -= falseEasting(aZoomlevel);
        double longRadians = aX / radius(aZoomlevel);
        double longDegrees = Math.toDegrees(longRadians);
        return longDegrees;
    }

    /**
     * Transforms pixel coordinate Y to latitude
     * 
     * @param aY
     *            [0..2^Zoomlevel*TILE_WIDTH[
     * @return [MIN_LAT..MAX_LAT] is about [-85..85]
     */
    public static double YToLat(int aY, int aZoomlevel) {
        aY += falseNorthing(aZoomlevel);
        double latitude = (Math.PI / 2) - (2 * Math.atan(Math.exp(-1.0 * aY / radius(aZoomlevel))));
        return -1 * Math.toDegrees(latitude);
    }
    
	/**
	 * Calculate distance between two points
	 */
	public static double calculateDistance(double latitude1, double longitude1,
			double latitude2, double longitude2) {
		double earthRadius = 6371; // kilometers
		double dLat = Math.toRadians(latitude2 - latitude1);
		double dLng = Math.toRadians(longitude2 - longitude1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(latitude1))
				* Math.cos(Math.toRadians(latitude2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		float dist = (float) (earthRadius * c);

		return dist;
	}
	
	public static double calculateDistance(GpsPoint point1,
			GpsPoint point2) {
		return calculateDistance(point1.getLatitude(), point1.getLongitude(), point2.getLatitude(), point2.getLongitude());
	}
	
	public static double calculateDistance( List<GpxPoint> points) {
		double distance = 0.;
		
		if ( points == null || points.size() <= 1 ) {
			return 0.;
		}
		
		for	(int idx = 1; idx < points.size(); idx++ ) {
			distance += calculateDistance(points.get(idx -1 ), points.get(idx));
		}
		
		return distance;
	}
	
	
	public static double calcRatioX(double longitude) {
		return ((longitude + 180.0) / 360.0);
	}
	
	public static double calcRatioY(double latitude) {
	    double sinLatitude = Math.sin(latitude * Math.PI / 180.0);
	    return (0.5 - Math.log((1 + sinLatitude) / (1.0 - sinLatitude)) / (4.0 * Math.PI));
	}
	
	public static double getYFromLatitude(double latitude)
	{
		return (1 - Math.log(Math.tan(latitude * Math.PI / 180) + 1 / Math.cos(latitude * Math.PI / 180)) / Math.PI) / 2;
	}
	
	public static int getYTile(final double lat, final double lon,	final int zoom ) {
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
	
	public static int getXTile(final double lat, final double lon,	final int zoom ) {
		int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
		
		if (xtile < 0)
			xtile = 0;
		if (xtile >= (1 << zoom))
			xtile = ((1 << zoom) - 1);

		return xtile;
	}
	




}
