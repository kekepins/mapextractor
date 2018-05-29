package map.altitude;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import map.model.GpxPoint;

public interface AltitudeServiceInterface {
	public List<GpxPoint> computeAltitude(List<GpxPoint> listPoints) throws IOException;
	public Double getAltitude(double longitude, double latitude) throws IOException;
}
