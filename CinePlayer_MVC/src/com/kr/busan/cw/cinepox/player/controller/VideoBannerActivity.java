package com.kr.busan.cw.cinepox.player.controller;

import android.net.Uri;
import android.os.Bundle;

import com.kr.busan.cw.cinepox.player.iface.VideoCallback;
import com.kr.busan.cw.cinepox.player.view.VideoView;

public class VideoBannerActivity extends BannerActivity implements
		VideoCallback {

	private VideoView mVideoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mVideoView = (VideoView) loadView(VideoView.class);
		setContentView(mVideoView);
		mVideoView.setCallback(this);
		mVideoView.setVideoURI(Uri.parse(getBannerData().CONTENTS));
	}

	@Override
	public void onPrepared(VideoView view) {
		// TODO Auto-generated method stub
		view.start();
	}

	@Override
	public void onInfo(VideoView view, int what, int extra) {
		// 사용안함
	}

	@Override
	public void onError(VideoView view, int what, int extra) {
		finish();
	}

	@Override
	public void onSeekComplete(VideoView view) {
		// 사용안함
	}

	@Override
	public void onBufferingUpdate(VideoView view, int progress) {
		// 사용안함
	}

	@Override
	public void onCompletion(VideoView view) {
		finish();
	}
}
