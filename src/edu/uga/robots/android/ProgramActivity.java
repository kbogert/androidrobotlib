package edu.uga.robots.android;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.uga.robots.R;
import edu.uga.robots.Planner;
import edu.uga.robots.PrimaryRobotModule;

public class ProgramActivity extends ListActivity implements OnItemClickListener  {

	Injector injector;
	Planner planner;
	Thread robotThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, DefaultItems));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(this);

	}

	public void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		
		switch (position) {
		case 0:
			if (DefaultItems[0].equals(((TextView) view).getText()))
				start();
			else
				pause();
			break;
		case 1:
			stop();
			break;
		
		}
	}
	
	protected void start() {

		Toast.makeText(getApplicationContext(), "Starting...",
				Toast.LENGTH_SHORT).show();
		
		String [] items = DefaultItems.clone();
		items[0] = "Pause";
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,items));
		
		injector = Guice.createInjector(new PrimaryRobotModule(this, BluetoothAdapter.getDefaultAdapter()));
		
		planner = injector.getInstance(Planner.class);
		
		robotThread = planner.start();

	}

	protected void pause() {
		
		Toast.makeText(getApplicationContext(), "Paused",
				Toast.LENGTH_SHORT).show();
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, DefaultItems));
		
		if (planner != null)
			planner.pause();

		String [] items = DefaultItems.clone();
		items[0] = "Resume";

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,items));
	}

	protected void resume() {
		
		Toast.makeText(getApplicationContext(), "Resuming...",
				Toast.LENGTH_SHORT).show();
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, DefaultItems));
		
		if (planner != null)
			planner.resume();

		String [] items = DefaultItems.clone();
		items[0] = "Pause";

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,items));
	}
	
	protected void stop() {
		
		Toast.makeText(getApplicationContext(), "Stopping...",
				Toast.LENGTH_SHORT).show();

		if (robotThread != null) {  // This is REALLY bad, if the NXT motors are running they may continue forever if the robot thread is killed! need a formal emergency stop procedure
			robotThread.interrupt();
			robotThread = null;
			planner = null;
		}
		
		String [] items = DefaultItems.clone();
		items[0] = "Start";
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,items));
	}
	
	static final String [] DefaultItems = new String[] {
		"Start", "Stop"
	};
}
