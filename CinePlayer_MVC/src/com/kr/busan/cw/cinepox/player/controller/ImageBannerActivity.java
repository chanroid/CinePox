package com.kr.busan.cw.cinepox.player.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

import com.kr.busan.cw.cinepox.player.view.ImageAdView;

@SuppressLint("HandlerLeak")
public class ImageBannerActivity extends BannerActivity implements
		OnClickListener {
	
	private ImageAdView mImageView;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mImageView = (ImageAdView) loadView(ImageAdView.class);
		mImageView.setOnClickListener(this);
		setContentView(mImageView);
		mHandler.sendEmptyMessageDelayed(0, getBannerData().SHOWTIME);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse(getBannerData().CONTENTS)));
	}
}
