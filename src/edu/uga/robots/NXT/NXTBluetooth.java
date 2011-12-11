package edu.uga.robots.NXT;


public interface NXTBluetooth {

	public boolean connectTo(String mac);
	public void sendBTMessage(int delay, byte[] message);
	public void registerListener(int messageType, NXTBluetoothListener n);
	public boolean isConnected();
	
	public interface NXTBluetoothListener {
		public void btCallback(int msg, byte [] data);
	}
}
