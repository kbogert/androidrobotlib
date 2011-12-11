package edu.uga.robots.NXT;

import javax.inject.Inject;

import edu.uga.robots.LightSensor;
import edu.uga.robots.SimpleEventCallback;
import edu.uga.robots.NXT.NXTBluetooth.NXTBluetoothListener;

public class NXTLightSensor implements LightSensor, NXTBluetoothListener {
	
	private NXTBluetooth bt;
	private int lightlevel = -1;
	private int port = 3;
	
	private int notifyLevel;
	private String notifyName;
	private SimpleEventCallback notifyCallback;
	
	@Inject 
	public NXTLightSensor(NXTBluetooth b) {
		bt = b;
		bt.registerListener(NXTMessages.GETINPUTVALUES, this);
		bt.sendBTMessage(0, NXTMessages.getSetInputStateMessage(port, NXTMessages.LIGHT_INACTIVE, 0));
		bt.sendBTMessage(50, NXTMessages.getResetInputScaledValueMessage(port));

		bt.sendBTMessage(200, NXTMessages.getGetInputValuesMessage(port));  // Begin Reading

	}
	
	@Override
	public int getLightLevel() {
		synchronized(this) {
			return lightlevel;
		}

	}

	@Override
	public void btCallback(int message, byte[] data) {
		switch (message) {
		case NXTMessages.GETINPUTVALUES:
			
			if (data[2] != 0) {
				bt.sendBTMessage(20, NXTMessages.getGetInputValuesMessage(port)); // repeat reading 
			} else {
				synchronized(this) {
					lightlevel = data[8] + (((int)data[9]) << 8);
				}

				bt.sendBTMessage(100, NXTMessages.getGetInputValuesMessage(port));  
			}
			
			if (notifyCallback != null) {
				if (lightlevel > notifyLevel) {
					Thread notifyThread = new Thread() {
						
						@Override
						public void run() {
							notifyCallback.eventNotification(notifyName);
						}
						
					};
					notifyThread.setDaemon(true);
					notifyThread.start();
				}
			}
			 
			break;
		}
	}
	

	@Override
	public void setBrightnessChangeEventNotify(int amt, String name, SimpleEventCallback callback) {
		notifyLevel = amt;
		notifyName = name;
		notifyCallback = callback;
	}

}
