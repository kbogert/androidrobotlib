package edu.uga.robots;

public interface SoundSensor {

	public float getCurrentDb();
	
	public void setLoudNotify(float db, String name, SimpleEventCallback callback);
}
