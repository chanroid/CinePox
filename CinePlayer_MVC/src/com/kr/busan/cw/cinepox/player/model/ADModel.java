package com.kr.busan.cw.cinepox.player.model;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import utils.JSONUtils;
import utils.LogUtils.l;
import android.content.Context;

import com.kr.busan.cw.cinepox.player.structs.BannerData;

public class ADModel extends Model {
	private static ADModel instance;
	private Context mContext;
	private String getTextBannerUrl;
	private String getADBannerUrl;
	private ArrayList<BannerData> adList;

	public static ADModel getInstance(Context ctx) {
		if (instance == null)
			instance = new ADModel(ctx);
		return instance;
	}

	public static void clearInstance() {
		instance = null;
	}

	private ADModel(Context ctx) {
		mContext = ctx;

	}

	public void setTextBannerUrl(String url) {
		getTextBannerUrl = url;
	}

	public void setADBannerUrl(String url) {
		getADBannerUrl = url;
	}
	
	public ArrayList<BannerData> getAdList() {
		return adList;
	}

	public boolean loadAdInfo() {
		if (getTextBannerUrl == null || getADBannerUrl == null)
			return false;
		adList = new ArrayList<BannerData>();
		mContext.getApplicationContext();
		try {
			loadTextBanner();
			loadAdBanner();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean loadTextBanner() throws ClientProtocolException,
			IOException, JSONException {
		JSONObject bannerObject = JSONUtils.jsonFromURL(getTextBannerUrl);
		l.i(bannerObject.toString());
		if (!"Y".equals(bannerObject.getString(KEY_RESULT)))
			return false;
		return true;
	}

	private boolean loadAdBanner() throws ClientProtocolException, IOException,
			JSONException {
		JSONObject adObject = JSONUtils.jsonFromURL(getADBannerUrl);
		l.i(adObject.toString());
		if (!"Y".equals(adObject.getString(KEY_RESULT)))
			return false;
		return true;
	}
}
