package com.kr.busan.cw.cinepox.movie;

import java.io.IOException;
import java.text.DecimalFormat;

import kr.co.chan.util.Util;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;

public abstract class ShareThread extends AsyncTask<String, Integer, String> {

	protected String DELETE_URL = Domain.ACCESS_DOMAIN + "player/shakeDelete?shake_key=%s";
	protected String REQUEST_URL = Domain.ACCESS_DOMAIN
			+ "player/shakeRequest?setting=response_type:json&shake_key=%s&url=%s";
	protected String RESPONSE_URL = Domain.ACCESS_DOMAIN
			+ "player/shakeResponse?setting=response_type:json&shake_key=%s";
	protected String GET_KEY_URL = Domain.ACCESS_DOMAIN
			+ "player/shakeGetKey?setting=response_type:json&shake_key=%s";

	private Location mLocation;

	public ShareThread(Location loc) {
		mLocation = loc;
	}

	public JSONObject getJson(String url) throws IOException, JSONException {
		return Util.Stream.jsonFromURL(url);
	}

	public void deleteRequest(String key) {
		try {
			Util.Stream.inStreamFromURL(String.format(DELETE_URL, key));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String generateShakeKey() throws IOException, JSONException {
		DecimalFormat format = new DecimalFormat(".#");
		String lng = format.format(mLocation.getLongitude());
		String lat = format.format(mLocation.getLatitude());
		String sendKey = Util.MD5(lng + lat);
		JSONObject js = getJson(String.format(GET_KEY_URL, sendKey));
		return js.getString("shake_key");
	}
}
