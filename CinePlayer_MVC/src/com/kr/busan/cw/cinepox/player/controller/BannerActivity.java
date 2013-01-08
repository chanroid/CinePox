package com.kr.busan.cw.cinepox.player.controller;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import com.kr.busan.cw.cinepox.player.structs.BannerData;

import controller.CCActivity;

public class BannerActivity extends CCActivity {

	private BannerData bannerData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT < 11) {
			setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setTheme(android.R.style.Theme_Holo_NoActionBar_Fullscreen);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}
		super.onCreate(savedInstanceState);
		bannerData = getIntent().getParcelableExtra(BannerData.EXTRA_KEY);
		if (bannerData == null)
			finish();
	}
	
	public BannerData getBannerData() {
		return bannerData;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
