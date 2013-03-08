package com.busan.cw.clsp20120924.model;

import model.CCSetting;
import utils.LogUtils.l;
import android.app.Application;

import com.busan.cw.clsp20120924.base.Constants;

public class CinepoxAppModel extends Application implements Constants {

	private AppConfigData appConfig;
	private PlayerConfigData playerConfig;
	private UserConfigData userConfig;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CCSetting.isTestMode = true;
		CCSetting.DEBUG_TAG = "Cinepox 3.0";
		appConfig = new AppConfigData();
		playerConfig = new PlayerConfigData();
		userConfig = new UserConfigData();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		l.d("app is low memory!!");
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		l.d("app is terminated");
	}

	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		super.onTrimMemory(level);
		if (level == TRIM_MEMORY_COMPLETE)
			l.d("app memory trimed");
	}

	public boolean isDebugMode() {
		return appConfig.isDebugMode()
				&& appConfig.debugIds.contains(userConfig.accountName);
	}

	public AppConfigData getAppConfig() {
		return appConfig;
	}

	public PlayerConfigData getPlayerConfig() {
		return playerConfig;
	}

	public UserConfigData getUserConfig() {
		return userConfig;
	}

	public void processLogout() {
		// TODO Auto-generated method stub
		userConfig = new UserConfigData();
	}

}
