package edu.uga.robots.NXT;

import javax.inject.Inject;

import android.util.Log;

import edu.uga.robots.DifferentialDriveMovement;
import edu.uga.robots.Util;
import edu.uga.robots.NXT.NXTBluetooth.NXTBluetoothListener;

public class NXTDifferentialDrive implements DifferentialDriveMovement, NXTBluetoothListener {
	private static final String TAG = "NXTDifferentialDrive";
	
	private int MOTOR_LEFT = 2;
	private int MOTOR_RIGHT = 1;
	private int [] tachos = {0,0,0};
	private NXTBluetooth bt;
	
	@Inject
	public NXTDifferentialDrive(NXTBluetooth b) {
		bt = b;
		bt.registerListener(NXTMessages.GETOUTPUTSTATE, this);
	}
	
	@Override
	public void moveBackward(int degrees) {
		// if degrees != 0:
		// register a listener with the NXT for getOutputstate (or setOutputstate, if the return package waits until the wheel stops turning, which I doubt)
		// send the setoutputstate
		// block on this object
		// in the NXTBluetooth callback:
		//	if the message was a setoutputstate, send the getOutputstate, delay 1 sec
		//  if the message was the getoutputstate, check the status.  If not moving, wakeup all threads blocking 
		if (degrees < 0)
			degrees *= -1;
		
		Log.e("NXTDifferentialDrive", "Motor Backward: " + degrees);

		bt.sendBTMessage(0, NXTMessages.getMotorMessage(MOTOR_LEFT, 40 * -1, degrees));
		bt.sendBTMessage(0, NXTMessages.getMotorMessage(MOTOR_RIGHT, 40 * -1, degrees));
		
		bt.sendBTMessage(250, NXTMessages.getGetOutputStateMessage(MOTOR_LEFT));
		bt.sendBTMessage(250, NXTMessages.getGetOutputStateMessage(MOTOR_RIGHT));
		
		if (degrees != 0) {
			
			synchronized(this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public void moveForward(int degrees) {
		if (degrees < 0)
			degrees *= -1;
		
		int ratio;
		if (tachos[1] == 0 || tachos[2] == 0) {
			ratio = 0;
		} else {
			ratio = (int)((float)tachos[1] / (float)tachos[2]);
		}
		Log.e("NXTDifferentialDrive", "Motor Ratio Forward: " + ratio);
		
		bt.sendBTMessage(0, NXTMessages.getMotorMessage(MOTOR_LEFT, 40, degrees));
		bt.sendBTMessage(0, NXTMessages.getMotorMessage(MOTOR_RIGHT, 40, degrees));

		bt.sendBTMessage(250, NXTMessages.getGetOutputStateMessage(MOTOR_LEFT));
		bt.sendBTMessage(250, NXTMessages.getGetOutputStateMessage(MOTOR_RIGHT));

		if (degrees != 0) {
			
			synchronized(this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

	}

	@Override
	public void turn(int degrees) {
		Log.e("NXTDifferentialDrive", "Motor Turn Degrees: " + degrees);
		int mult = 1;
		if (degrees < 0) {
			mult = -1;
			degrees *= mult;
		}
		if (degrees == 0) {
			return;
		}

		bt.sendBTMessage(0, NXTMessages.getMotorMessage(MOTOR_LEFT, -50, degrees));
		bt.sendBTMessage(0, NXTMessages.getMotorMessage(MOTOR_RIGHT, 50, degrees));

		bt.sendBTMessage(250, NXTMessages.getGetOutputStateMessage(MOTOR_LEFT));
		bt.sendBTMessage(250, NXTMessages.getGetOutputStateMessage(MOTOR_RIGHT));
		
		synchronized(this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	@Override
	public void stop() {
		bt.sendBTMessage(0, NXTMessages.getMotorIdleMessage(MOTOR_LEFT));
		bt.sendBTMessage(0, NXTMessages.getMotorIdleMessage(MOTOR_RIGHT));
		
	}

	@Override
	public void btCallback(int message, byte[] data) {

		switch (message) {
		case NXTMessages.GETOUTPUTSTATE:
			synchronized(this) {
				tachos[data[3]] = Util.uBytetoInt(data[13]) + (Util.uBytetoInt(data[14]) << 8) + (Util.uBytetoInt(data[15]) << 16) + (Util.uBytetoInt(data[16]) << 24);
			}
			Log.e(TAG, "Motor: " + data[3] + " RunState: " + data[8] + " tach: " + tachos[data[3]]);
			if (data[8] == 0) { // TODO Should this take which motor we're looking at into account?  I think not for differential drive
				synchronized(this) {
					this.notifyAll();
				}
			} else {
				bt.sendBTMessage(500, NXTMessages.getGetOutputStateMessage(data[3]));
			}
			break;
		}
	}

}
