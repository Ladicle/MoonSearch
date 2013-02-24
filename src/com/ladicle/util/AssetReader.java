package com.ladicle.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

public class AssetReader {
	private static final Log log = new Log("AssetReader");
	private AssetManager assetManager;

	public AssetReader(Context context) {
		assetManager = context.getAssets();
	}

	public InputStream filetoString(String fileName) {
		InputStream is = null;

		try {
			is = assetManager.open(fileName);
		} catch (IOException e) {
			log.e(e.getMessage());
		}
		return is;
	}
}
