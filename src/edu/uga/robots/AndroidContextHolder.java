package edu.uga.robots;

import android.content.Context;

public class AndroidContextHolder implements ContextHolder {

	private Context context;
	
	public AndroidContextHolder(Context context) {
		super();
		this.context = context;
	}

	@Override
	public Context getContext() {
		return context;
	}

}
