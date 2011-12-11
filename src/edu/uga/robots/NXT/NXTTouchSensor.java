package edu.uga.robots.NXT;

import javax.inject.Inject;

import edu.uga.robots.SimpleEventCallback;
import edu.uga.robots.TouchSensor;
import edu.uga.robots.NXT.NXTBluetooth.NXTBluetoothListener;

public class NXTTouchSensor implements TouchSensor, NXTBluetoothListener {
	
	private NXTBluetooth bt;
	private boolean istouched = false;
	private int port = 2;
	
	private String notifyName;
	private SimpleEventCallback notifyCallback;
	
	@Inject 
	public NXTTouchSensor(NXTBluetooth b) {
		bt = b;
		bt.registerListener(NXTMessages.GETINPUTVALUES, this);
		bt.sendBTMessage(0, NXTMessages.getSetInputStateMessage(port, NXTMessages.SWITCH, 0));

		bt.sendBTMessage(200, NXTMessages.getGetInputValuesMessage(port));  // Begin Reading

	}
	
	@Override
	public boolean isTouched() {
		synchronized(this) {
			return istouched;
		}

	}

	@Override
	public void btCallback(int message, byte[] data) {
		switch (message) {
		case NXTMessages.GETINPUTVALUES:
			boolean changed = istouched;
			if (data[2] != 0) {
				bt.sendBTMessage(20, NXTMessages.getGetInputValuesMessage(port)); // repeat reading 
			} else {
				synchronized(this) {
					istouched = data[8] != 0;
				}

				bt.sendBTMessage(100, NXTMessages.getGetInputValuesMessage(port));  
			}
			
			if (notifyCallback != null) {
				if (changed != istouched) {
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
	public void setChangeNotify(String name, SimpleEventCallback callback) {
		notifyName = name;
		notifyCallback = callback;
	}

}
