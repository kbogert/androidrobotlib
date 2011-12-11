package edu.uga.robots;

public interface DifferentialDriveMovement {

	public void moveForward(int degrees);
	public void moveBackward(int degrees);
	public void turn(int degrees);
	public void stop();
}
