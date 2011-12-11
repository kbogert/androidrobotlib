package edu.uga.robots.android;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import edu.uga.robots.R;

public class PrimaryActivity extends TabActivity {
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    
	    registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
                Intent programStartIntent = new Intent(PrimaryActivity.this, CameraPhotoActivity.class);
                startActivityForResult(programStartIntent,0);
			}
	    	
	    	}, new IntentFilter("com.sevenbowlabs.robots.android.ACTIVATECAMERAPREVIEW") {
	    	
	    });
	    
	    setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, ProgramActivity.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("program").setIndicator("Program")
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, SensorsActivity.class);
	    spec = tabHost.newTabSpec("sensors").setIndicator("Sensors")
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, MotorsActivity.class);
	    spec = tabHost.newTabSpec("motors").setIndicator("Motors")
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, InternalsActivity.class);
	    spec = tabHost.newTabSpec("internals").setIndicator("Internals")
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    tabHost.setCurrentTab(0);
	}
	
}
