package edu.uga.robots.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MotorsActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the Motors tab");
        setContentView(textview);
    }
}
