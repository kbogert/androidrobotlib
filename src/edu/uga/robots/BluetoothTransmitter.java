package edu.uga.robots;


public interface BluetoothTransmitter {

	public boolean connectTo(String mac);
	public void transmit(byte[] b);
	
}
