package map.model;

public class GpsPoint {
	private double longitude;
	private double latitude;
	private Double altitude;
		
	public GpsPoint() {
	}
	
	public GpsPoint(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public GpsPoint(double longi, double lat, double alti) {
		this.longitude = longi;
		this.latitude = lat;
		this.altitude = alti;
	}

	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public Double getAltitude() {
		return altitude;
	}
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}
	
	public String toString() {
		return latitude + ":" + longitude; 
		
	}
}
