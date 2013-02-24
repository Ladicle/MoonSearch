package com.ladicle.bakusouon.obj;

public class MoonPoint {
	private double lat, lng;
	private int height;

	public MoonPoint(double lat, double lng, int height) {
		this.lat = lat;
		this.lng = lng;
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}
}
