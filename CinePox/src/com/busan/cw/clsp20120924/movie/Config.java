package com.busan.cw.clsp20120924.movie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import utils.LogUtils.l;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.busan.cw.clsp20120924.movie.WidgetProvider.UpdateData;

import extend.ShakeListener;

public class Config {

	public static String[] IntervalArray;
	public static String[] AlarmArray;

	private static Config instance;

	private SharedPreferences mPref;
	private SharedPreferences.Editor mEdit;
	
	private ArrayList<UpdateData> mWidgetUpdateData;

	private boolean isLogined = false;
	private boolean isRunning = false;
	private boolean isRecommend = false;

	private Bitmap mBannerImg;
	private String mBannerUrl;
	private String mWidgetUrl;
	Context mContext;

	public String ERROR_LOG_URL = null;

	/**
	 * 상수를 넣으면 인덱스를 반환하고 인덱스를 넣으면 상수를 반환
	 * 
	 * @param constant
	 * @return
	 */
	public static int transIntervalFlag(int constant) {
		switch (constant) {
		case 0:
			return 0;
		case 1:
			return 30000;
		case 2:
			return 60000;
		case 3:
			return 180000;
		case 4:
			return 300000;
		case 5:
			return 600000;
		case 6:
			return 900000;
		case 7:
			return 1800000;
		case 8:
			return 3600000;
		case 30000:
			return 1;
		case 60000:
			return 2;
		case 180000:
			return 3;
		case 300000:
			return 4;
		case 600000:
			return 5;
		case 900000:
			return 6;
		case 1800000:
			return 7;
		case 3600000:
			return 8;
		default:
			return 0;
		}
	}

	public static Config getInstance(Context ctx) {
		if (instance == null)
			instance = new Config(ctx);
		return instance;
	}

	private Config(Context ctx) {
		mContext = ctx;
		mPref = ctx.getSharedPreferences("setting", 0);
		mEdit = mPref.edit();
		IntervalArray = new String[] { "사용안함", "30초", "1분", "3분", "5분", "10분",
				"15분", "30분", "1시간" };
		AlarmArray = new String[] { "최신영화", "드라마", "애니메이션", "성인" };
	}
	
	public void setWidgetData(ArrayList<UpdateData> data) {
		mWidgetUpdateData = data;
	}
	
	public ArrayList<UpdateData> getWidgetData() {
		return mWidgetUpdateData;
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

	public boolean isAppRecommend() {
		return isRecommend;
	}

	public void setAppRecommend(boolean flag) {
		isRecommend = flag;
	}

	public Bitmap getBannerImg() {
		return mBannerImg;
	}

	public String getBannerUrl() {
		return mBannerUrl;
	}

	public void setLogined(boolean flag) {
		isLogined = flag;
	}

	public boolean isLogined() {
		return isLogined;
	}

	public void setRunning(boolean flag) {
		isRunning = flag;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public synchronized void setBannerInfo(Bitmap img, String url) {
		mBannerImg = img;
		mBannerUrl = url;
	}

	public void addCategoryUrl(int index, String url) {
		mEdit.putString("categoryurl" + index, url).commit();
	}

	public void addCategoryName(int index, String name) {
		mEdit.putString("categoryname" + index, name).commit();
	}

	public void setCurrentCategory(String name) {
		mEdit.putString("categoryname", name).commit();
	}

	public String getCurrentCategory() {
		return mPref.getString("categoryname", null);
	}

	public String getCategoryName(int index) {
		return mPref.getString("categoryname" + index, null);
	}

	public String getCategoryUrl(int index) {
		return mPref.getString("categoryurl" + index, null);
	}

	public List<String> getCategoryNames() {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			list.add(getCategoryName(i));
		}
		return list;
	}

	public boolean isFirstRun() {
		return mPref.getBoolean("first", true);
	}

	public void setFirstRun(boolean flag) {
		mEdit.putBoolean("first", flag).commit();
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

	public void setShowntime(String url, long time) {
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

	public void setMemnum(String num) {
		mEdit.putString(Constants.KEY_MEMBER_NUM, num).commit();
	}

	public String getMemnum() {
		return mPref.getString(Constants.KEY_MEMBER_NUM, null);
	}

	public void setAlarm(int position, boolean checked) {
		String key = "alarm" + position;
		mEdit.putBoolean(key, checked).commit();
	}

	public boolean[] getAlarm() {
		return new boolean[] { mPref.getBoolean("alarm0", false),
				mPref.getBoolean("alarm1", false),
				mPref.getBoolean("alarm2", false),
				mPref.getBoolean("alarm3", false) };
	}

	public void setAlarmInterval(int interval) {
		mEdit.putInt("interval", interval).commit();
	}

	public int getAlarmInterval() {
		return mPref.getInt("interval", 60000);
	}

	public boolean isReadMessage(String num) {
		int orign = mPref.getInt(num, 0);
		if (orign <= 0)
			return true;
		int today = Calendar.getInstance(Locale.getDefault()).get(
				Calendar.DAY_OF_YEAR);
		if (today < 8)
			today += 365;
		int remain = today - orign;
		l.i("num : " + today + " + " + orign);
		if (remain < 8)
			return false;
		return true;
	}

	public void addReadMessage(String num) {
		mEdit.putInt(
				num,
				Calendar.getInstance(Locale.getDefault()).get(
						Calendar.DAY_OF_YEAR)).commit();
	}

	public boolean isReadPush(String num) {
		String result = mPref.getString(num, "");
		if ("".equalsIgnoreCase(result)) {
			return false;
		} else
			return true;
	}

	public void addReadPush(String num) {
		mEdit.putString(num, "read").commit();
	}

	public void removeReadMessage(String num) {
		l.i("remove : " + num);
		mEdit.putInt(num, 0).commit();
	}

	public String getSendTimeURL() {
		// TODO Auto-generated method stub
		return mPref.getString("time", null);
	}

	public void setSendTimeURL(String mPlaytimeUrl) {
		// TODO Auto-generated method stub
		mEdit.putString("time", mPlaytimeUrl).commit();
	}

	public String getWidgetUrl() {
		return mWidgetUrl;
	}

	public void setWidgetUrl(String url) {
		mWidgetUrl = url;
	}

	public void setErrorLogUrl(String url) {
		ERROR_LOG_URL = url;
	}

	public String getErrorUrl() {
		return ERROR_LOG_URL;
	}

}
