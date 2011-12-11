package edu.uga.robots;

import java.util.Random;

import javax.inject.Inject;

import edu.uga.robots.NXT.NXTBluetooth;

public class DumbPhotographAndSendPlanner implements Planner {

	private MainThread myThread = null;
	private CameraControl cameraControl;
	private DifferentialDriveMovement drive;
	private BluetoothTransmitter comm;
	private DistanceSensor sensor;
	private NXTBluetooth nxtComm;
	
	@Inject
	public void setCameraControl(CameraControl c) {
		cameraControl = c;
	}
	
	@Inject
	public void setComm(BluetoothTransmitter comm) {
		this.comm = comm;
	}
	
	@Inject
	public void setSensor(DistanceSensor sensor) {
		this.sensor = sensor;
	}

	@Inject
	public void setDifferentialDriveMovement(DifferentialDriveMovement d) {
		drive = d;
	}
	
	@Inject
	public void setNXTBluetooth(NXTBluetooth n) {
		nxtComm = n;
	}
	
	@Override
	public void pause() {
		myThread.flagToPause();

	}

	@Override
	public void resume() {
		synchronized(myThread) {
			myThread.notify();
		}
	}

	@Override
	public Thread start() {
		myThread = new MainThread();
		myThread.start();
		return myThread;
	}
	
	public enum RobotState {Stopped, Forward, Backward, Turning, TakingPhoto, TransmittingPhoto};

	protected class MainThread extends Thread {

		private boolean shouldPause = false;
		private boolean shouldStop = false;
		private RobotState state;
		private byte [] picture;

		public void flagToPause() {
			synchronized(this) {
				shouldPause = true;
			}
			
		}

		public void flagToStop() {
			synchronized(this) {
				shouldStop = true;
			}
			
		}
		
		@Override
		public void run() {
		
			state = RobotState.Stopped;

			nxtComm.connectTo("Insert NXT MAC");
			comm.connectTo("00:02:72:15:5D:34");
			
			while (true) {
				synchronized (this) {
					if (shouldPause) {
						
						try {
							this.wait();
						} catch (InterruptedException e) {
							return;
						}
						
					}
				}
				
				
				synchronized (this) {
					if (shouldStop) {
						return;
					}
				}
				
				
				// move forward until we are a set distance from an object
				// take a picture
				// try to send it to the computer
				// turn randomly
				
				
				switch (state) {
				
				case Stopped:
					if (sensor.getDistanceReading() <= 30 && sensor.getDistanceReading() >= 0) {
						state = RobotState.Turning;
					} else {
						state = RobotState.Forward;
						drive.moveForward(0);
					}
					break;
					
				case Forward:
					if (sensor.getDistanceReading() <= 35 && sensor.getDistanceReading() >= 0) {
						drive.stop();
						state = RobotState.TakingPhoto;
					}
					break;
				case Backward:
					float diff = 35 - sensor.getDistanceReading();
					int degrees = (int)(diff / (Math.PI * 2 * 2.54	) * 360); 
					drive.moveBackward(degrees);
					state = RobotState.TakingPhoto;
					break;
				case Turning:
					Random r = new Random();
					degrees = (r.nextInt(320) - 160);
					if (degrees >= 0)
						degrees += 20;
					else
						degrees -= 20;
					drive.turn(degrees);
					state = RobotState.Stopped;
					break;
				case TakingPhoto:
					if (sensor.getDistanceReading() <= 25 && sensor.getDistanceReading() >= 0) {
						state = RobotState.Backward;
					} else {
						picture = cameraControl.takeJpegPictureSync();
						state = RobotState.TransmittingPhoto;
					}
					break;
				case TransmittingPhoto:
					comm.transmit(picture);
					state = RobotState.Turning;
					break;
				}
								
			}
			
			
		}
		
		
		
		
	}
}
