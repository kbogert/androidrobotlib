package edu.uga.robots;

public interface PositionSensor {

	public double [] getLastPosition();
	
	public void setProximityAlert(double lat, double lon, float radius, String name, SimpleEventCallback callback);
}
