package edu.uga.robots;

public interface MovementSensor {

	public float [] getOrientation();
	
	public float [] getCurrentAccel();
	
	public float [] getCurrentVelocity();
	
	public float [] getCurrentPosition();
	
	public void reset();
	
	public void setSensorSpeed(int speed);
	
	public void setBumpNotify(float threshold, String name, SimpleEventCallback callback);
	public float[] getLastBump();
	
}
