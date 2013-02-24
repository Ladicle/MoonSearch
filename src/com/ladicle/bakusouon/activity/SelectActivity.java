package com.ladicle.bakusouon.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ladicle.bakusouon.R;
import com.ladicle.bakusouon.db.DBAdaptor;
import com.ladicle.bakusouon.obj.MoonParam;
import com.ladicle.util.Log;

public class SelectActivity extends Activity {
	private static final Log log = new Log("SelectActivity");

	// データリスト
	private List<MoonParam> moonList = new ArrayList<MoonParam>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
	}

	public void onClickImage(View view) {
		setContentView(R.layout.activity_moon_list);
		String kind = (String) view.getTag();

		// 月データの取得
		Cursor cursor;
		DBAdaptor dbAdaptor = new DBAdaptor(this);
		dbAdaptor.open();

		if (kind.equals("i")) {
			cursor = dbAdaptor.getMoonData();
		} else {
			cursor = dbAdaptor.getMoonData(kind);
		}

		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			log.e("cursor count is 0");
			finish();
		}

		// リストへの追加
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);
		do {
			String name = cursor.getString(0);
			double lat = cursor.getDouble(2);
			double lng = cursor.getDouble(1);
			moonList.add(new MoonParam(name, lat, lng));
			adapter.add(name);

		} while (cursor.moveToNext());

		cursor.close();
		dbAdaptor.close();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// 次画面へ移動
				Intent intent = new Intent(SelectActivity.this,
						MainActivity.class);
				MoonParam param = moonList.get(position);
				param.setDataSize(20, 30); // サイズ設定
				intent.putExtra("MoonParam", param);
				startActivity(intent);
				finish();
			}
		});

	}
}
