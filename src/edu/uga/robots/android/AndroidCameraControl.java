package edu.uga.robots.android;

import javax.inject.Inject;

import edu.uga.robots.CameraControl;
import edu.uga.robots.ContextHolder;
import edu.uga.robots.CameraControl.CameraControlCallback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class AndroidCameraControl implements CameraControl {

	
	ContextHolder contextHolder;
	boolean cameraReady;
	byte [] data;
	
	@Inject
	public void setContextHolder(ContextHolder c) {
		contextHolder = c;
	}
	
	public AndroidCameraControl() {

		cameraReady = false;

	}


	@Override
	public byte[] takeJpegPictureSync() {
		Context mContext = contextHolder.getContext();
	    mContext.registerReceiver(new BroadcastReceiver() {
	    	// TODO REPLACE THIS WITH HANDLERS (one in the PrimaryActivity, one here.  The camera activity calls the handler here to pass the data)
			@Override
			public void onReceive(Context context, Intent intent) {
				data = intent.getByteArrayExtra("JPEGPHOTO");
				synchronized(AndroidCameraControl.this) {
					AndroidCameraControl.this.notify();
				}
			}
	    	
	    	}, new IntentFilter("com.sevenbowlabs.robots.android.CAMERAPHOTO") { 
	    	
	    });
		mContext.sendBroadcast(new Intent("com.sevenbowlabs.robots.android.ACTIVATECAMERAPREVIEW"));
		
		synchronized(this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				return null;
			}

		}
		byte [] tempData = data;
		data = null;
		return tempData;
	}


	@Override
	public void takeJpegPictureAsync(CameraControlCallback c) {
		// TODO Auto-generated method stub
		
	}
	
	

}
