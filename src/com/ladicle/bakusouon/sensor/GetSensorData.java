package com.ladicle.bakusouon.sensor;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.ladicle.bakusouon.R;

public class GetSensorData extends Activity implements SensorEventListener {
	private SensorManager manager;
	private TextView values;
	private int x;
	private Boolean xBool;
	private int y;
	private Boolean yBool;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		values = (TextView) findViewById(R.id.textView1);
		manager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// Listenerの登録解除
		manager.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// Listenerの登録
		List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			Sensor s = sensors.get(0);
			manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		String str = "";
		if (7 < event.values[0]) {
			if (xBool) {
				x++;// 右
			}
			xBool = false;
		} else if (event.values[0] < -7) {
			if (xBool) {
				x--;// 左
			}
			xBool = false;
		} else {
			xBool = true;
		}

		if (7 < event.values[1]) {
			if (yBool) {
				y--;// 後
			}
			yBool = false;
		} else if (event.values[1] < -7) {
			if (yBool) {
				y++;// 前
			}
			yBool = false;
		} else {
			yBool = true;
		}
	}
}
