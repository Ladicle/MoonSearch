package com.ladicle.bakusouon.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdaptor {

	private DatabaseOpenHelper dbHelper;
	private SQLiteDatabase db;

	public DBAdaptor(Context context) {
		dbHelper = new DatabaseOpenHelper(context);
	}

	//
	// Adapter Methods
	//

	/** Database opened */
	public DBAdaptor open() {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	/** Database closed */
	public void close() {
		dbHelper.close();
	}

	/** Get station data */
	public Cursor getMoonData(String kind) {
		return db.rawQuery(
				"select name, lon, lata from runa_location where flag=?;",
				new String[] { kind });
	}

	public Cursor getMoonData() {
		return db
				.rawQuery(
						"select name, lon, lata from runa_location where flag='i' or flag='n' or flag='m';",
						null);
	}
}
