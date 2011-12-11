package edu.uga.robots.android;

import javax.inject.Inject;

import edu.uga.robots.ContextHolder;
import edu.uga.robots.PositionSensor;
import edu.uga.robots.SimpleEventCallback;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

public class AndroidGPSPositionSensor implements PositionSensor, LocationListener {

	private ContextHolder contextHolder;
	private LocationManager mLocationManager;
	
	private double [] currentPosition;
	private double proxLat;
	private double proxLon;
	private float proxRadiusSquared;
	private String proxName;
	private SimpleEventCallback proxCallback;
	
	@Inject
	public AndroidGPSPositionSensor(ContextHolder c) {
		contextHolder = c;
		currentPosition = new double[3];
		
		/*
		 * Create a new thread to run the gps updates, needs a looper
		 */
		
		Thread loopThread = new Thread() {
		
			
			@Override
			public void run() {
				
				Looper.prepare();
				Context con = contextHolder.getContext();
				
				mLocationManager = (LocationManager)con.getSystemService(Context.LOCATION_SERVICE);
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, AndroidGPSPositionSensor.this, Looper.myLooper());
				
				Looper.loop();
			}

		};
		loopThread.setDaemon(true);
		loopThread.start();
		
	}
	
	
	@Override
	public synchronized double[] getLastPosition() {
		return currentPosition;
	}

	@Override
	public void setProximityAlert(double lat, double lon, float radius,
			String name, SimpleEventCallback callback) {

		proxLat = lat;
		proxLon = lon;
		proxRadiusSquared = radius * radius;
		proxName = name;
		proxCallback = callback;
	}


	@Override
	public void onLocationChanged(Location loc ) {
		synchronized(this) {
			currentPosition[0] = loc.getLatitude();
			currentPosition[1] = loc.getLongitude();
			currentPosition[2] = loc.getAccuracy();
		}
		
		if (proxCallback != null) {
			if (proxRadiusSquared > (proxLat-currentPosition[0])*(proxLat-currentPosition[0]) + (proxLon-currentPosition[1])*(proxLon-currentPosition[1])) {
				// we're close enough to the goal to notify the listener
				Thread notifyThread = new Thread() {
					
					@Override
					public void run() {
						proxCallback.eventNotification(proxName);
					}
					
				};
				notifyThread.setDaemon(true);
				notifyThread.start();
			}
		}
		
	}


	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
