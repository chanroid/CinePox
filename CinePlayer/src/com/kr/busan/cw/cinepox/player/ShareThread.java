package com.kr.busan.cw.cinepox.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import kr.co.chan.util.Util;
import kr.co.chan.util.l;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;

public abstract class ShareThread extends AsyncTask<String, Integer, String> {

	protected String DELETE_URL = PlayerConfig.Domain + "player/shakeDelete?shake_key=%s";
	protected String REQUEST_URL = PlayerConfig.Domain
			+ "player/shakeRequest?setting=response_type:json&shake_key=%s&url=%s";
	protected String RESPONSE_URL = PlayerConfig.Domain
			+ "player/shakeResponse?setting=response_type:json&shake_key=%s";
	protected String GET_KEY_URL = PlayerConfig.Domain
			+ "player/shakeGetKey?setting=response_type:json&shake_key=%s";

	private Location mLocation;

	public ShareThread(Location loc) {
		mLocation = loc;
	}

	public JSONObject getJson(String url) throws IOException, JSONException {
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse res = client.execute(get);
		HttpEntity entity = res.getEntity();
		if (entity != null) {
			InputStream in = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			in.close();
		}
		return new JSONObject(sb.toString());
	}

	public void deleteRequest(String key) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(String.format(DELETE_URL, key));
			client.execute(get);
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
		l.i("location : " + lng + ", " + lat);
		String sendKey = Util.MD5(lng + lat);
		JSONObject js;
		js = getJson(String.format(GET_KEY_URL, sendKey));
		l.i("key : " + js.getString("shake_key"));
		return js.getString("shake_key");
	}
}
