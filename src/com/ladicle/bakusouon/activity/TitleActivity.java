package com.ladicle.bakusouon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ladicle.bakusouon.R;

public class TitleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_title);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(TitleActivity.this,
						SelectActivity.class));
			}
		}, 1);
	}
}
