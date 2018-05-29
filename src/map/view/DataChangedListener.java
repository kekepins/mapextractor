package map.view;

public interface DataChangedListener {
	
	public void onDataChanged(double longitude, double latitude, int zoomLevel);
	
	public void onEditionChanged();

}
