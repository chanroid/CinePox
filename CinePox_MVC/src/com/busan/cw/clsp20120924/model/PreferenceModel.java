package com.busan.cw.clsp20120924.model;

import java.util.Calendar;
import java.util.Locale;

import twitter4j.auth.AccessToken;
import utils.LogUtils.l;

import model.CCModel;
import android.content.Context;
import android.content.SharedPreferences;

import com.busan.cw.clsp20120924.base.Constants;

public class PreferenceModel extends CCModel implements Constants {
	private SharedPreferences mPref;
	private SharedPreferences.Editor mEdit;

	public PreferenceModel(Context ctx) {
		mPref = ctx.getSharedPreferences(EXTRA_SETTING, Context.MODE_PRIVATE);
		mEdit = mPref.edit();
	}

	public boolean isFirstRun() {
		return mPref.getBoolean(EXTRA_FIRST_RUN, false);
	}

	public void setFirstRun(boolean flag) {
		mEdit.putBoolean(EXTRA_FIRST_RUN, flag).commit();
	}

	public void setAutoLogin(boolean flag) {
		mEdit.putBoolean(EXTRA_AUTO_LOGIN, flag).commit();
	}

	public boolean isAutoLogin() {
		return mPref.getBoolean(EXTRA_AUTO_LOGIN, false);
	}

	public void setAutoLoginAction(String action) {
		l.i("setAutologin : " + action);
		if (action == null)
			mEdit.remove(EXTRA_AUTO_LOGIN_ACTION).commit();
		else
			mEdit.putString(EXTRA_AUTO_LOGIN_ACTION, action).commit();
	}

	public String getAutoLoginAction() {
		return mPref.getString(EXTRA_AUTO_LOGIN_ACTION, null);
	}

	public void setAutoLoginId(String id) {
		mEdit.putString(EXTRA_ID, id).commit();
	}

	public void setAutoLoginPw(String pw) {
		mEdit.putString(EXTRA_PASSWORD, pw).commit();
	}

	public String getAutoLoginId() {
		return mPref.getString(EXTRA_ID, "");
	}

	public String getAutoLoginPw() {
		return mPref.getString(EXTRA_PASSWORD, "");
	}

	public boolean isReadAD(String url) {
		int during = mPref.getInt(url, 0);
		int today = Calendar.getInstance(Locale.getDefault()).get(
				Calendar.DAY_OF_YEAR);
		return during > today;
	}

	public void addReadAD(String url, int during) {
		int endDay = Calendar.getInstance(Locale.getDefault()).get(
				Calendar.DAY_OF_YEAR)
				+ during;
		if (endDay > 365)
			endDay -= 365;
		mEdit.putInt(url, endDay).commit();
	}

	public void initTwitterInfo() {
		setTwitterAccessToken(null);
	}

	public void setTwitterAccessToken(AccessToken token) {
		if (token == null) {
			mEdit.remove(EXTRA_TWIT_TOKEN).commit();
			mEdit.remove(EXTRA_TWIT_SECRET).commit();
		} else {
			mEdit.putString(EXTRA_TWIT_TOKEN, token.getToken()).commit();
			mEdit.putString(EXTRA_TWIT_SECRET, token.getTokenSecret()).commit();
		}
	}

	public AccessToken getTwitterAccessToken() {
		String token = mPref.getString(EXTRA_TWIT_TOKEN, null);
		String secret = mPref.getString(EXTRA_TWIT_SECRET, null);
		if (token == null || secret == null)
			return null;
		return new AccessToken(token, secret);
	}

	public void initMe2DayInfo() {
		setAutoLogin(false);
		setAutoLoginAction("");
		setMe2LoginKey("");
		setMe2LoginId("");
	}

	public void setMe2LoginKey(String key) {
		if (key == null)
			mEdit.remove(EXTRA_ME2_LOGIN_KEY).commit();
		else
			mEdit.putString(EXTRA_ME2_LOGIN_KEY, key).commit();
	}

	public String getMe2LoginKey() {
		return mPref.getString(EXTRA_ME2_LOGIN_KEY, null);
	}

	public void setMe2LoginId(String id) {
		if (id == null)
			mEdit.remove(EXTRA_ME2_LOGIN_ID).commit();
		else
			mEdit.putString(EXTRA_ME2_LOGIN_ID, id).commit();
	}

	public String getMe2LoginId() {
		return mPref.getString(EXTRA_ME2_LOGIN_ID, null);
	}

	public void initYozmInfo() {
		setAutoLogin(false);
		setAutoLoginAction(null);
		setYozmAccessToken(null);
	}

	public void setYozmAccessToken(AccessToken token) {
		// TODO Auto-generated method stub
		if (token == null) {
			mEdit.remove(EXTRA_YOZM_TOKEN).commit();
			mEdit.remove(EXTRA_YOZM_SECRET).commit();
		} else {
			mEdit.putString(EXTRA_YOZM_TOKEN, token.getToken()).commit();
			mEdit.putString(EXTRA_YOZM_SECRET, token.getTokenSecret()).commit();
		}
	}

	public AccessToken getYozmAccessToken() {
		String token = mPref.getString(EXTRA_YOZM_TOKEN, null);
		String secret = mPref.getString(EXTRA_YOZM_SECRET, null);
		if (token == null || secret == null)
			return null;
		else
			return new AccessToken(token, secret);
	}

}
