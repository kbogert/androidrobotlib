package edu.uga.robots;

import android.bluetooth.BluetoothAdapter;

public class BluetoothAdapterHolderImpl implements BluetoothAdapterHolder {

	private BluetoothAdapter adapter;

	public BluetoothAdapterHolderImpl(BluetoothAdapter a) {
		adapter = a;
	}
	
	
	@Override
	public BluetoothAdapter getAdapter() {
		return adapter;
	}

}
