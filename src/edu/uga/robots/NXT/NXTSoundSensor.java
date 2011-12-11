package edu.uga.robots.NXT;

import javax.inject.Inject;

import edu.uga.robots.SimpleEventCallback;
import edu.uga.robots.SoundSensor;
import edu.uga.robots.NXT.NXTBluetooth.NXTBluetoothListener;

public class NXTSoundSensor implements SoundSensor, NXTBluetoothListener {
	private NXTBluetooth bt;
	private float currentDb = -1;
	private int port = 1;
	
	private float notifyDb;
	private String notifyName;
	private SimpleEventCallback notifyCallback;
	
	@Inject 
	public NXTSoundSensor(NXTBluetooth b) {
		bt = b;
		bt.registerListener(NXTMessages.GETINPUTVALUES, this);
		bt.sendBTMessage(0, NXTMessages.getSetInputStateMessage(port, NXTMessages.SOUND_DB, NXTMessages.PERIODCOUNTERMODE));
		/*
		 * Right now this is in periodcounter mode, which hopefully gives higher values for louder sounds.  May have to switch it to raw mode if this is not the case
		 */
		bt.sendBTMessage(50, NXTMessages.getResetInputScaledValueMessage(port));

		bt.sendBTMessage(200, NXTMessages.getGetInputValuesMessage(port));  // Begin Reading

	}
	
	@Override
	public float getCurrentDb() {
		synchronized(this) {
			return currentDb;
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
					currentDb = data[12] + (((int)data[13]) << 8);
				}

				bt.sendBTMessage(100, NXTMessages.getGetInputValuesMessage(port));  
			}
			
			if (notifyCallback != null) {
				if (currentDb >  notifyDb) {
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
	public void setLoudNotify(float db, String name, SimpleEventCallback callback) {
		notifyDb = db;
		notifyName = name;
		notifyCallback = callback;
	}

}
