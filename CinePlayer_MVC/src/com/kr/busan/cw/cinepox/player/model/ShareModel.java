package com.kr.busan.cw.cinepox.player.model;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import utils.EncryptUtils;
import utils.JSONUtils;
import utils.StreamUtils;
import android.content.Context;
import android.location.Location;

public class ShareModel extends Model {

	private static ShareModel instance;
	private Context mContext;

	public static ShareModel getInstance(Context ctx) {
		if (instance == null)
			instance = new ShareModel(ctx);
		return instance;
	}

	public static void clearInstance() {
		instance = null;
	}

	private ShareModel(Context ctx) {
		mContext = ctx;
		mContext.getApplicationContext();
	}

	public synchronized boolean sendShareInfo(Location loc, String sendurl) {
		if (loc == null)
			return false;
		if (sendurl == null)
			return false;

		try {
			String shake_key = generateShareKey(loc);
			long startTime = System.currentTimeMillis();
			long currentTime = 0l;
			boolean result = false;
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(KEY_SHAKE_KEY, shake_key));
			params.add(new BasicNameValuePair(KEY_URL, sendurl));
			while (currentTime - 5000 < startTime) {
				try {
					JSONObject js = JSONUtils.jsonFromURL(SHAKE_REQUEST_URL,
							params);
					String resultText = js.getString("result");
					String is_response = js.getString("is_response");
					result = "Y".equalsIgnoreCase(is_response)
							&& "Y".equalsIgnoreCase(resultText);
					if (result)
						break;
					Thread.sleep(500);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				currentTime = System.currentTimeMillis();
			}

			if (result) {
				removeShareRequest(shake_key);
				return true;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	public synchronized void removeShareRequest(String key)
			throws IllegalStateException, IOException {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_SHAKE_KEY, key));
		StreamUtils.inStreamFromURL(SHAKE_DELETE_URL, params);
	}

	public synchronized String generateShareKey(Location loc)
			throws ClientProtocolException, JSONException, IOException {
		if (loc == null)
			return null;
		DecimalFormat format = new DecimalFormat(".#");
		String lng = format.format(loc.getLongitude());
		String lat = format.format(loc.getLatitude());
		String sendKey = EncryptUtils.encodeMD5(lng + lat);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_SHAKE_KEY, sendKey));
		JSONObject js = JSONUtils.jsonFromURL(SHAKE_GET_KEY_URL, params);
		return js.getString("shake_key");
	}

}
