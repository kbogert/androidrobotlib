package edu.uga.robots;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothTransmitterImpl implements BluetoothTransmitter {

	private BluetoothAdapterHolder bluetooth;
	private BluetoothSocket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private boolean connected;
	
	@Inject
	public void setBluetoothAdapterHolder(BluetoothAdapterHolder b) {
		bluetooth = b;
		connected = false;
	}
	

	
	@Override
	public void transmit(byte[] b) {
		if (! connected)
			return;
		
		byte [] transmitLength = new byte[4];
		transmitLength[0] = (byte)b.length;
		transmitLength[1] = (byte) (b.length >>> 8);
		transmitLength[2] = (byte) (b.length >>> 16);
		transmitLength[3] = (byte) (b.length >>> 24);
		
		Log.e("Bluetooth Transmitter", "Sending Bytes: " + b.length);
		Log.e("Bluetooth Transmitter", "Sending Bytes2: " + transmitLength[0] + " " + transmitLength[1] + " " + transmitLength[2] + " " + transmitLength[3]);
		try {
			outputStream.write(transmitLength);
			outputStream.write(b);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public boolean connectTo(String mac) {
        try {
			BluetoothDevice bluetoothDevice = null;
			bluetoothDevice = bluetooth.getAdapter().getRemoteDevice(mac);

			if (bluetoothDevice == null) {
				return false;
			}

			try {
				Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
				socket = (BluetoothSocket)m.invoke(bluetoothDevice, Integer.valueOf(3));
			} catch (SecurityException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IllegalArgumentException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (NoSuchMethodException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IllegalAccessException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (InvocationTargetException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			try {
			    socket.connect();
			}
			catch (IOException e) {  

			    // try another method for connection, this should work on the HTC desire, credits to Michael Biermann
			    try {
			        Method mMethod = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
			        socket = (BluetoothSocket) mMethod.invoke(bluetoothDevice, Integer.valueOf(1));            
			        socket.connect();
			    }
			    catch (Exception e1){
			       return false;
			    }
			}

			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			connected = true;
		} catch (IOException e) {
			return false;
		}

        return true;
	}

}
