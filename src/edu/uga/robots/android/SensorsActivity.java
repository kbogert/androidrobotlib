package edu.uga.robots.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SensorsActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the Sensors tab");
        setContentView(textview);
    }
}
