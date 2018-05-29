package map.model;

import java.util.Date;

public class GpxPoint extends GpsPoint {
	private Date date;
	private double weight;
	private int id;
	
	public GpxPoint() {
	}
	
	public GpxPoint(double latitude, double longitude, Double altitude, Date date) {
		super.setLatitude( latitude );
		super.setLongitude(longitude);
		super.setAltitude( altitude );
		this.date = date;
	}
	

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getKey() {
		return getLatitude() + "-" +  getLongitude();
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GpxPoint other = (GpxPoint) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
