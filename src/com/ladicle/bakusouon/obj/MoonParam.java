package com.ladicle.bakusouon.obj;

import java.io.Serializable;

public class MoonParam implements Serializable {
	private static final long serialVersionUID = 776328702687041190L;
	
	private double lat, lng;
	private int x, y;
	private String name;

	public MoonParam(String name, double lat, double lon) {
		this.lat = lat;
		this.lng = lon;
		this.name = name;
	}

	public void setDataSize(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public String getURL() {
		StringBuffer url = new StringBuffer();
		url.append("http://www25006ue.sakura.ne.jp/kaguya/V1/get.php?lat=");
		url.append(lat);
		url.append("&lng=");
		url.append(lng);
		url.append("&x=");
		url.append(x);
		url.append("&y=");
		url.append(y);
		return url.toString();
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
