package edu.uga.robots.test;

import javax.inject.Singleton;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import edu.uga.robots.AndroidContextHolder;
import edu.uga.robots.BluetoothAdapterHolder;
import edu.uga.robots.BluetoothAdapterHolderImpl;
import edu.uga.robots.BluetoothTransmitter;
import edu.uga.robots.BluetoothTransmitterImpl;
import edu.uga.robots.CameraControl;
import edu.uga.robots.ContextHolder;
import edu.uga.robots.DifferentialDriveMovement;
import edu.uga.robots.DistanceSensor;
import edu.uga.robots.Planner;
import edu.uga.robots.NXT.NXTBluetooth;
import edu.uga.robots.NXT.NXTBluetoothImpl;
import edu.uga.robots.NXT.NXTDifferentialDrive;
import edu.uga.robots.android.AndroidCameraControl;
import edu.uga.robots.android.UltrasonicSensor;

public class TestRobotModule extends AbstractModule {

	Context context;
	BluetoothAdapter bluetooth;
	
	public TestRobotModule(Context c, BluetoothAdapter a) {
		context = c;
		bluetooth = a;
	}
	
	
	@Override
	protected void configure() {
		
		bind(Planner.class).to(SendPhotoTest.class).in(Singleton.class);
		bind(DifferentialDriveMovement.class).to(NXTDifferentialDrive.class).in(Singleton.class);
		bind(NXTBluetooth.class).to(NXTBluetoothImpl.class).in(Singleton.class);
		bind(BluetoothTransmitter.class).to(BluetoothTransmitterImpl.class).in(Singleton.class);
		bind(DistanceSensor.class).to(UltrasonicSensor.class).in(Singleton.class);
		bind(CameraControl.class).to(AndroidCameraControl.class).in(Singleton.class);
	}

	@Provides
	public BluetoothAdapterHolder provideBluetoothAdapterHolder() {
		return new BluetoothAdapterHolderImpl(bluetooth);
	}

	@Provides @Singleton
	public ContextHolder provideContextHolder() {
		ContextHolder a = new AndroidContextHolder(context);
		return a;
		
	}
}
