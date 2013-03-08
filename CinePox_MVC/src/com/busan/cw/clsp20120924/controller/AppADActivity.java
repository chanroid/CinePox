package com.busan.cw.clsp20120924.controller;

import utils.ThemeUtils;
import utils.LogUtils.l;
import android.app.Activity;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;

import com.busan.cw.clsp20120924.interfaces.AppADViewCallback;
import com.busan.cw.clsp20120924.model.AppConfigData;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.model.PreferenceModel;
import com.busan.cw.clsp20120924.view.AppADView;

public class AppADActivity extends Activity implements AppADViewCallback {

	private AppADView view;
	private AppConfigData data;
	private PreferenceModel prefModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ThemeUtils.darkNoTitle(this);
		super.onCreate(savedInstanceState);
		prefModel = new PreferenceModel(this);
		data = ((CinepoxAppModel) getApplication()).getAppConfig();

		if (data == null) {
			finish();
			return;
		}

		if (prefModel.isReadAD(data.getAdImageUrl())) {
			finish();
			return;
		}

		if (data.getAdImageUrl() == null && data.getAdHideDay() == 0) {
			finish();
			return;
		}

		view = new AppADView(this);
		view.setCallback(this);
		l.d("ad data : " + data.getAdImageUrl() + ", " + data.getAdHideDay());
		view.setADImageUrl(data.getAdImageUrl());
		view.setAdDuring(data.getAdHideDay());
		setContentView(view);
	}

	@Override
	public void onBackPressed() {
		// do nothing
	}

	@Override
	public void onNotseeCheck(AppADView view, boolean check) {
		// TODO Auto-generated method stub
		finish();
		if (check)
			prefModel
					.addReadAD(data.getAdImageTargetUrl(), data.getAdHideDay());
	}

	@Override
	public void onSkipClick(AppADView view) {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onADImageClick(AppADView view) {
		// TODO Auto-generated method stub
		if (!"".equals(data.getAdImageTargetUrl())) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data
					.getAdImageTargetUrl())));
			finish();
		}
	}

	@Override
	public void onADLoadError(AppADView view) {
		// TODO Auto-generated method stub
		finish();
	}

}
