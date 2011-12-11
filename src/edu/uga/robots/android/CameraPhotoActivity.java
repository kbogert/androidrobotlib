package edu.uga.robots.android;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import edu.uga.robots.R;

public class CameraPhotoActivity extends Activity implements  Camera.AutoFocusCallback, PictureCallback {
	private static final String TAG = "CameraDemo";
	Preview preview;
	Timer testTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.camerapreview);
		
		preview = new Preview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);

	}
	
	/** Handles data for jpeg picture */
	public void onPictureTaken(byte[] data, Camera camera) {

		Intent notifyReceiver = new Intent("com.sevenbowlabs.robots.android.CAMERAPHOTO");
		notifyReceiver.putExtra("JPEGPHOTO", data);
		sendBroadcast(notifyReceiver);
		
		preview.close();
		finish();
	}


	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		
		camera.takePicture(null, null, this);
		
	}

	
	// Stolen from: http://marakana.com/forums/android/examples/39.html
	class Preview extends SurfaceView implements SurfaceHolder.Callback {
		private static final String TAG = "Preview";

		SurfaceHolder mHolder;
		public Camera camera;
		int counter;

		Preview(Context context) {
			super(context);

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			counter = 0;
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, acquire the camera and tell it where
			// to draw.

			camera = Camera.open();
			try {
				camera.setPreviewDisplay(holder);

				camera.setPreviewCallback(new PreviewCallback() {

					public void onPreviewFrame(byte[] data, Camera camera) {
						Preview.this.invalidate();
						counter ++;
						if (counter == 5) {
							camera.autoFocus(CameraPhotoActivity.this);
						}
						
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null)
				close();
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			// Now that the size is known, set up the camera parameters and begin
			// the preview.

			camera.startPreview();

		}
		
		public void close() {
			
			// Surface will be destroyed when we return, so stop the preview.
			// Because the CameraDevice object is not a shared resource, it's very
			// important to release it when the activity is paused.
			camera.stopPreview();
			camera.release();
			camera = null;			
		}
		
		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
			Paint p = new Paint(Color.RED);
			canvas.drawText("PREVIEW", canvas.getWidth() / 2,
					canvas.getHeight() / 2, p);
		}
	}


}
