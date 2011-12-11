package edu.uga.robots;

public interface LightSensor {

	public int getLightLevel();
	
	public void setBrightnessChangeEventNotify(int amt, String name, SimpleEventCallback callback);
}
