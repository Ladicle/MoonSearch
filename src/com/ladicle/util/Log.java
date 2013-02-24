package com.ladicle.util;

public class Log {
	private String TAG;

	public Log(String TAG) {
		this.TAG = TAG;
	}

	public void d(String message) {
		android.util.Log.d(TAG, message);
	}

	public void i(String message) {
		android.util.Log.i(TAG, message);
	}

	public void e(String message) {
		android.util.Log.e(TAG, message);
	}
}
