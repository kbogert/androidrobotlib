package edu.uga.robots.test;

import java.util.Random;

import javax.inject.Inject;

import edu.uga.robots.BluetoothTransmitter;
import edu.uga.robots.CameraControl;
import edu.uga.robots.DifferentialDriveMovement;
import edu.uga.robots.Planner;

public class SendPhotoTest implements Planner {

	private MainThread myThread = null;
	private CameraControl cameraControl;
	private BluetoothTransmitter comm;
	
	@Inject
	public void setCameraControl(CameraControl c) {
		cameraControl = c;
	}
	
	@Inject
	public void setComm(BluetoothTransmitter comm) {
		this.comm = comm;
	}
	
	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public Thread start() {
		myThread = new MainThread();
		myThread.start();
		return myThread;
	}

	protected class MainThread extends Thread {

		private boolean shouldPause = false;
		private boolean shouldStop = false;
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
		
			comm.connectTo("00:02:72:15:5D:34");
			picture = cameraControl.takeJpegPictureSync();
			comm.transmit(picture);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {

			}
			picture = cameraControl.takeJpegPictureSync();
			comm.transmit(picture);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {

			}
			picture = cameraControl.takeJpegPictureSync();
			comm.transmit(picture);
						
			
		}
		
		
		
		
	}
}
