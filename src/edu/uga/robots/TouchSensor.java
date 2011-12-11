package edu.uga.robots;

public interface TouchSensor {

	public boolean isTouched();
	
	public void setChangeNotify(String name, SimpleEventCallback callback);
}
