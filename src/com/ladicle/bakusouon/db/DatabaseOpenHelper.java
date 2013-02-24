package com.ladicle.bakusouon.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ladicle.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	private static final String TAG = "DatabaseOpenHelper";
	private static final String SRC_DATABASE_NAME = "moondata.db";
	private static final String DATABASE_NAME = "moondata.db";
	private static final int DATABASE_VERSION = 1;

	private final Log log = new Log(TAG);
	private final Context context;
	private final File databasePath;
	private boolean createDatabase = false;

	public DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		this.databasePath = context.getDatabasePath(DATABASE_NAME);
	}

	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		SQLiteDatabase database = super.getWritableDatabase();
		if (createDatabase) {
			try {
				database = copyDatabase(database);
			} catch (IOException e) {
				log.e(e.getMessage());
			}
		}
		return database;
	}

	private SQLiteDatabase copyDatabase(SQLiteDatabase database)
			throws IOException {
		database.close();

		InputStream input = context.getAssets().open(SRC_DATABASE_NAME);
		OutputStream output = new FileOutputStream(databasePath);
		copy(input, output);

		createDatabase = false;
		return super.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		super.onOpen(db);
		this.createDatabase = true;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	private static int copy(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[1024 * 4];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

}