package com.kr.busan.cw.cinepox.player;

import io.vov.vitamio.MediaPlayer;
import kr.co.chan.util.ShakeListener;
import kr.co.chan.util.CinePox.LogPost;

import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.kr.busan.cw.cinepox.R;

public class PlayerConfig {

	public static final String[] CharsetArray = { HTTP.UTF_8, HTTP.ASCII,
			HTTP.US_ASCII, HTTP.ISO_8859_1, HTTP.UTF_16, "EUC-KR", "MS949" };
	public static final String[] TextsizeArray = { "SMALL", "MEDIUM", "LARGE" };
	public static final String[] QualityArray = { "MEDIUM", "HIGH", "LOW" };
	public static String[] ScreenArray;

//	public static final String Domain = "http://mtest4.cinepox.com/";
	public static final String Domain = "http://access.cinepox.com/";

	private static PlayerConfig instance;
	Context mContext;

	private SharedPreferences mPref;
	private SharedPreferences.Editor mEdit;

	private boolean isLogined = false;

	String mCinepoxUrl;

	String mPlayerConfigUrl;

	/**
	 * 상수를 넣으면 인덱스를 반환하고 인덱스를 넣으면 상수를 반환
	 * 
	 * @param constant
	 * @return
	 */
	public static int transQualityFlag(int constant) {
		switch (constant) {
		case MediaPlayer.VIDEOQUALITY_HIGH:
			return 1;
		case MediaPlayer.VIDEOQUALITY_LOW:
			return 2;
		case 1:
			return MediaPlayer.VIDEOQUALITY_HIGH;
		case 2:
			return MediaPlayer.VIDEOQUALITY_LOW;
		default:
			return 0;
		}
	}


	public static PlayerConfig getInstance(Context ctx) {
		if (instance == null)
			instance = new PlayerConfig(ctx.getApplicationContext());
		return instance;
	}

	private PlayerConfig(Context ctx) {
		mContext = ctx.getApplicationContext();
		mPref = ctx.getSharedPreferences("setting", 0);
		mEdit = mPref.edit();
		ScreenArray = new String[] { ctx.getString(R.string.scale_orign),
				ctx.getString(R.string.scale_stretch),
				ctx.getString(R.string.scale_zoom),
				ctx.getString(R.string.scale_scale) };
	}
	
	public void setDefaultQuality(String ctype) {
		mEdit.putString("quality", ctype).commit();
	}
	
	public String getDefaultQuality() {
		return mPref.getString("quality", "C600");
	}

	public void setUseMotion(boolean flag) {
		mEdit.putBoolean("motion", flag).commit();
	}

	public boolean isUseMotion() {
		return mPref.getBoolean("motion", true);
	}
	
	public void setMotionSensitive(int sensitive) {
		mEdit.putInt("sensitive", sensitive).commit();
	}
	
	public int getMotionSensitive() {
		return mPref.getInt("sensitive", ShakeListener.DEFAULT_SHAKE_THRESHOLD);
	}
	
	public void setCinepoxUrl(String url) {
		mCinepoxUrl = url;
	}

	public String getCinepoxUrl() {
		return mCinepoxUrl;
	}

	public void setLogined(boolean flag) {
		isLogined = flag;
	}

	public boolean isLogined() {
		return isLogined;
	}

	public void setPlayerConfigUrl(String player_config) {
		// TODO Auto-generated method stub
		mPlayerConfigUrl = player_config;
	}

	public String getPlayerConfigUrl() {
		return mPlayerConfigUrl;
	}

	public void setDefaultCodec(int codec) {
		mEdit.putInt("codec", codec).commit();
	}

	public int getDefaultCodec() {
		return mPref.getInt("codec", 0);
	}

	public int getCharset() {
		// TODO Auto-generated method stub
		return mPref.getInt("charset", 0);
	}

	public void setCharset(int which) {
		// TODO Auto-generated method stub
		mEdit.putInt("charset", which).commit();
	}

	public int getTextsize() {
		// TODO Auto-generated method stub
		return mPref.getInt("textsize", 1);
	}

	public void setTextsize(int which) {
		// TODO Auto-generated method stub
		mEdit.putInt("textsize", which).commit();
	}

	public int getQuality() {
		// TODO Auto-generated method stub
		return transQualityFlag(mPref.getInt("quality",
				MediaPlayer.VIDEOQUALITY_MEDIUM));
	}

	public void setQuality(int which) {
		// TODO Auto-generated method stub
		mEdit.putInt("quality", transQualityFlag(which)).commit();
	}

	public int getScreenMode() {
		// TODO Auto-generated method stub
		return mPref.getInt("screen",
				io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_SCALE);
	}

	public void setScreenMode(int which) {
		// TODO Auto-generated method stub
		mEdit.putInt("screen", which).commit();
	}

	public int getCCColor() {
		// TODO Auto-generated method stub
		return mPref.getInt("cccolor", Color.WHITE);
	}

	public void setCCColor(int mColor) {
		// TODO Auto-generated method stub
		mEdit.putInt("cccolor", mColor).commit();
	}

	public void setShowntime(String url, long time) {
		url = url.split("=")[0];
		mEdit.putLong(url, time).commit();
	}

	public long getShowntime(String url) {
		url = url.split("=")[0];
		return mPref.getLong(url, 0L);
	}

	public void setAutoLogin(boolean flag) {
		mEdit.putBoolean("autologin", flag).commit();
	}

	public boolean isAutoLogin() {
		return mPref.getBoolean("autologin", false);
	}

	public void setAccount(String id, String pw) {
		mEdit.putString("id", id).putString("pw", pw).commit();
	}

	public String[] getAccount() {
		return new String[] { mPref.getString("id", null),
				mPref.getString("pw", null) };
	}

	public void setMemnum(String num) {
		mEdit.putString("memnum", num).commit();
	}

	public String getMemnum() {
		return mPref.getString("memnum", null);
	}

	public String getSendTimeURL() {
		// TODO Auto-generated method stub
		return mPref.getString("time", null);
	}

	public void setSendTimeURL(String mPlaytimeUrl) {
		// TODO Auto-generated method stub
		mEdit.putString("time", mPlaytimeUrl).commit();
	}

	public static void sendErrorLog(Context ctx, String url, Throwable t) {
		LogPost lp = new LogPost(ctx, ctx.getPackageName(), t);
		lp.setServerUrl(url);
		lp.start();
	}
}
