package edu.uga.robots.android;

import javax.inject.Inject;

import edu.uga.robots.DistanceSensor;
import edu.uga.robots.NXT.NXTBluetooth;
import edu.uga.robots.NXT.NXTMessages;
import edu.uga.robots.NXT.NXTBluetooth.NXTBluetoothListener;

public class UltrasonicSensor implements DistanceSensor, NXTBluetoothListener {
	private NXTBluetooth bt;
	private float distance = -1;
	private int port = 0;
	
	@Inject 
	public UltrasonicSensor(NXTBluetooth b) {
		bt = b;
		bt.registerListener(NXTMessages.LSREAD, this);
		bt.sendBTMessage(0, NXTMessages.getSetInputStateMessage(port, NXTMessages.LOWSPEED_9V, 0));

		byte[] txData = new byte[3];
		txData[0] = 2;
		txData[1] = 65;  // Set the sensor to continuous measurement
		txData[2] = 2;
        bt.sendBTMessage(100, NXTMessages.getLSWriteMessage(port, txData, 1));
		
        txData = new byte[2];
		txData[0] = 2;
		txData[1] = 66; // Set the sensor to read mode ?
        bt.sendBTMessage(180, NXTMessages.getLSWriteMessage(port, txData, 1));

		bt.sendBTMessage(200, NXTMessages.getLSReadMessage(port));  // Begin Reading

	}
	
	@Override
	public float getDistanceReading() {
		synchronized(this) {
			return distance;
		}

	}

	@Override
	public void btCallback(int message, byte[] data) {
		switch (message) {
		case NXTMessages.LSREAD:
			if (data[2] != 0) {
				bt.sendBTMessage(20, NXTMessages.getLSReadMessage(port)); // repeat reading 
			} else {
				synchronized(this) {
					distance = data[4];
				}
				byte[] txData = new byte[2];
				txData[0] = 2;
				txData[1] = 66; // Set the sensor to read mode ?
	        	bt.sendBTMessage(100, NXTMessages.getLSWriteMessage(port, txData, 1));
	        	bt.sendBTMessage(120, NXTMessages.getLSReadMessage(port));
			}
			
			 
			break;
		}
	}

}
