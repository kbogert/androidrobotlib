package edu.uga.robots.NXT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import edu.uga.robots.BluetoothAdapterHolder;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


public class NXTBluetoothImpl implements NXTBluetooth {
	
	private BluetoothAdapterHolder bluetooth;
	private BluetoothSocket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private boolean connected;
    private Map<Integer, Collection<NXTBluetoothListener>> listeners;
    
    private Handler sendHandler;
    
	public NXTBluetoothImpl(BluetoothAdapterHolder b) {
		bluetooth = b;
		connected = false;
		
		listeners = new HashMap<Integer, Collection<NXTBluetoothListener>>();
		
	}
	
	@Override
	public void registerListener(int messageType, NXTBluetoothListener n) {

		synchronized(listeners) {
			if (! listeners.containsKey(messageType)) {
				listeners.put(messageType, new LinkedList<NXTBluetoothListener>());
			}

			Collection<NXTBluetoothListener> c = listeners.get(messageType);
			c.add(n);
		}
	}

	@Override
	public void sendBTMessage(int delay, byte[] message) {
		if (delay < 0) {
        	try {
				send(message);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
        	return;
		}
        
		Bundle myBundle = new Bundle();
        myBundle.putByteArray("data", message);
        Message myMessage = sendHandler.obtainMessage();
        myMessage.setData(myBundle);
        // handlers are good for delays
        
        if (delay == 0)
        	sendHandler.sendMessage(myMessage);
        else
            sendHandler.sendMessageDelayed(myMessage, delay);
        
		
		
	}	
	private Thread receiveThread = new Thread() {


	      public void run() {

	    	  while(connected) {
	    		  try {
	    			  byte [] msg = receive();
	    			  if (msg != null) { 
	    				  int type = msg[1];
	    				  Collection<NXTBluetoothListener> callbacks = new LinkedList<NXTBluetoothListener>();
	    				  synchronized(listeners) {
	    					  Collection<NXTBluetoothListener> list = listeners.get(type);
	    					  if (list != null)
	    						  callbacks.addAll(list);
	    				  }

	    				  for (NXTBluetoothListener listener : callbacks) {
	    					  listener.btCallback(type, msg);
	    				  }
	    			  }
	    		  } catch (IOException e) {
	    			  connected = false;
	    		  }
	    	  }	          


	      }

		
	};

	private Thread sendThread = new Thread() {


	      public void run() {
	          Looper.prepare();

	          sendHandler = new Handler() {
	        	  public void handleMessage(Message msg) {

	        		  byte [] data = msg.getData().getByteArray("data");
	        		  try {
	        			  send(data);
	        		  } catch (IOException e) {
	        			  connected = false;
	        			  Looper.myLooper().quit();
	        		  }
	        	  }
	          };

	          synchronized(NXTBluetoothImpl.this) {
	        	  NXTBluetoothImpl.this.notifyAll();
	          }
	          Looper.loop();
	      }

		
	};
	// Stolen from MINDdroid

	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	@Override
	public boolean connectTo(String mac) {
        try {
			BluetoothDevice nxtDevice = null;
			nxtDevice = bluetooth.getAdapter().getRemoteDevice(mac); 
			socket = nxtDevice.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);
			
			try {
			    socket.connect();
			}
			catch (IOException e) {  

			    // try another method for connection, this should work on the HTC desire, credits to Michael Biermann
			    try {
			        Method mMethod = nxtDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
			        socket = (BluetoothSocket) mMethod.invoke(nxtDevice, Integer.valueOf(1));            
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
		receiveThread.setDaemon(true);
		receiveThread.start();
		sendThread.setDaemon(true);
		sendThread.start();
		
		synchronized(this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
        return true;
		
	}
	
	private synchronized void send(byte [] msg) throws IOException {
        if (! connected || outputStream == null) throw new IOException();

        outputStream.write(msg.length);
        outputStream.write(msg.length >> 8);
        outputStream.write(msg, 0, msg.length);
	}
	
	
	private byte [] receive() throws IOException{
        if (! connected || inputStream == null) throw new IOException();

        int size = inputStream.read();
        size = size + (inputStream.read() << 8);
        byte[] returnval = new byte[size];
        inputStream.read(returnval);

        return returnval;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}
	
	
}
