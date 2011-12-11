package edu.uga.robots.android;

import javax.inject.Inject;

import edu.uga.robots.ContextHolder;
import edu.uga.robots.MovementSensor;
import edu.uga.robots.SimpleEventCallback;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MovementSensorImpl implements MovementSensor, SensorEventListener {
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final Sensor mMagnetometer;
    
    private float [] gravity;
    private float [] magneticField;
    private long lastEventTimestamp;
    
    private float [] accel;
    private float [] velocity;
    private float [] position;
    private float [] orientation;
    private float [] lastBump;

    private ContextHolder contextHolder;
    
    private float bumpListenerThresholdSquared;
    private String bumpListenerIdentifier;
    private SimpleEventCallback bumpListener;
    
    @Inject
	public MovementSensorImpl(ContextHolder ch) {
    	contextHolder = ch;
		Context c = contextHolder.getContext();
        mSensorManager = (SensorManager)c.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
     
        accel = new float[3];
        velocity = new float[3];
        position = new float[3];
        lastBump = new float[3];
        orientation = new float[3];
        
        bumpListenerThresholdSquared = Float.MAX_VALUE;
        
	}
	
	@Override
	public synchronized float[] getCurrentAccel() {
		return accel;
	}

	@Override
	public synchronized float[] getCurrentVelocity() {
		return velocity;		
	}

	@Override
	public synchronized float[] getCurrentPosition() {
		return position;
	}
	
	@Override
	public synchronized float[] getLastBump() {
		return lastBump;
	}

	@Override
	public synchronized float[] getOrientation() {
		return orientation;
	}
	

	@Override
	public synchronized void reset() {
		velocity = new float [3];
		position = new float [3];
	}

	@Override
	public void setBumpNotify(float threshold, String name,
			SimpleEventCallback callback) {
		
	    bumpListenerThresholdSquared = threshold * threshold;
	    bumpListenerIdentifier = name;
	    bumpListener = callback;
	}

	@Override
	public void setSensorSpeed(int speed) {
		mSensorManager.unregisterListener(this, mMagnetometer);
		mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.registerListener(this, mAccelerometer, speed);
        mSensorManager.registerListener(this, mMagnetometer, speed);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// get and save raw accel and magnetic field values ( since we only get one at a time)
		// if they're both there, calculate the orientation
		// 	rotate gravity by the orientation, subtract out 9.81 in Z direction, set accel vector
		// 	integrate to get velocity, integrate to get position
		//  if the accel vector's magnitude is greater than the threshold, fire off a thread calling the callback
		
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			gravity = event.values;
		case Sensor.TYPE_MAGNETIC_FIELD:
			magneticField = event.values;
		}

		if (gravity != null && magneticField != null) {
			float R[] = new float[16];
			float I[] = new float[16];
			boolean success = SensorManager.getRotationMatrix(R, I, gravity, magneticField);
			if (success) {

				float newGrav[] = new float[4];
				newGrav[0] = gravity[0];
				newGrav[1] = gravity[1];
				newGrav[2] = gravity[2];
				newGrav[3] = 1;
				float newAccel[] = new float[4];
				android.opengl.Matrix.multiplyMV(newAccel, 0, R, 0, newGrav, 0);

				synchronized(this) {
					SensorManager.getOrientation(R, orientation);

					accel[0] = newAccel[0];
					accel[1] = newAccel[1];
					accel[2] = newAccel[2];

					float dt = (event.timestamp - lastEventTimestamp) / 1000000000.0f; // dt in seconds

					velocity[0] = velocity[0] + accel[0] * dt;
					velocity[1] = velocity[1] + accel[1] * dt;
					velocity[2] = velocity[2] + accel[2] * dt;

					position[0] = position[0] + velocity[0] * dt;
					position[1] = position[1] + velocity[1] * dt;
					position[2] = position[2] + velocity[2] * dt;
				}
				if (bumpListenerThresholdSquared < (accel[0]*accel[0])+(accel[1]*accel[1])+(accel[2]*accel[2])) {
					synchronized(this) {
						lastBump = accel;
					}
					if (bumpListener != null) {
						Thread notifyThread = new Thread() {
	
							@Override
							public void run() {
								bumpListener.eventNotification(bumpListenerIdentifier);
							}
							
						};
						notifyThread.setDaemon(true);
						notifyThread.start();
					}
				}
				
			}
		}
		lastEventTimestamp = event.timestamp;
	}

}
