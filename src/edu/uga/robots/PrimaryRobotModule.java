package edu.uga.robots;

import javax.inject.Singleton;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import edu.uga.robots.NXT.NXTBluetooth;
import edu.uga.robots.NXT.NXTBluetoothImpl;
import edu.uga.robots.NXT.NXTDifferentialDrive;
import edu.uga.robots.NXT.NXTLightSensor;
import edu.uga.robots.NXT.NXTSoundSensor;
import edu.uga.robots.NXT.NXTTouchSensor;
import edu.uga.robots.android.AndroidCameraControl;
import edu.uga.robots.android.AndroidGPSPositionSensor;
import edu.uga.robots.android.MovementSensorImpl;
import edu.uga.robots.android.UltrasonicSensor;

public class PrimaryRobotModule extends AbstractModule {

	Context context;
	BluetoothAdapter bluetooth;
	
	public PrimaryRobotModule(Context c, BluetoothAdapter b) {
		context = c;
		bluetooth = b;
	}
	
	
	@Override
	protected void configure() {
		
		bind(Planner.class).to(DumbPhotographAndSendPlanner.class).in(Singleton.class);
		bind(DifferentialDriveMovement.class).to(NXTDifferentialDrive.class).in(Singleton.class);
		bind(NXTBluetooth.class).to(NXTBluetoothImpl.class).in(Singleton.class);
		bind(BluetoothTransmitter.class).to(BluetoothTransmitterImpl.class).in(Singleton.class);
		bind(DistanceSensor.class).to(UltrasonicSensor.class).in(Singleton.class);
		bind(CameraControl.class).to(AndroidCameraControl.class).in(Singleton.class);
		bind(MovementSensor.class).to(MovementSensorImpl.class).in(Singleton.class);
		bind(PositionSensor.class).to(AndroidGPSPositionSensor.class).in(Singleton.class);
		bind(SoundSensor.class).to(NXTSoundSensor.class).in(Singleton.class);
		bind(TouchSensor.class).to(NXTTouchSensor.class).in(Singleton.class);
		bind(LightSensor.class).to(NXTLightSensor.class).in(Singleton.class);
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
