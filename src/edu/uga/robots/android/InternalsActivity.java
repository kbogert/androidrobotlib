package edu.uga.robots.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class InternalsActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the Internals tab");
        setContentView(textview);
    }

}
