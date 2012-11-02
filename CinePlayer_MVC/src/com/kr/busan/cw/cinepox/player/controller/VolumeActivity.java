package com.kr.busan.cw.cinepox.player.controller;

import kr.co.chan.util.Util;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.kr.busan.cw.cinepox.R;
import com.kr.busan.cw.cinepox.player.view.VolumeControllView;

import controller.CCBaseActivity;

@SuppressLint("HandlerLeak")
public class VolumeActivity extends CCBaseActivity implements
		OnSeekBarChangeListener, OnClickListener {

	private VolumeControllView mVolumeView;
	private AudioManager mAudioManager;

	private int max = 1;
	private int lastvar = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mVolumeView = new VolumeControllView(this);
		mVolumeView.setOnSeekBarChangeListener(this);
		mVolumeView.setOnClickListener(this);
		mVolumeView.setMax(max);
		setContentView(mVolumeView);
		mHandler.sendEmptyMessageDelayed(0, 2000);
		sync();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		Rect dialogBounds = new Rect();
		getWindow().getDecorView().getHitRect(dialogBounds);
		if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
			finish();
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				finish();
				break;
			}
		}
	};

	private void sync() {
		mVolumeView.setVolume(mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC));
		if (Util.Phone.getVolume(this, AudioManager.STREAM_MUSIC) != 0)
			lastvar = Util.Phone.getVolume(this, AudioManager.STREAM_MUSIC);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (volume > 0) {
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_LOWER,
						AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				mVolumeView.setVolume(volume--);
			}
			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, 2000);
			sync();
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (volume < max) {
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_RAISE,
						AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				mVolumeView.setVolume(volume++);
			}
			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, 2000);
			sync();
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (fromUser) {
			if (progress > 0)
				lastvar = progress;
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			mHandler.removeMessages(0);
		}
	}

	private void mute() {
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		mVolumeView.setVolume(0);
	}

	private void unmute() {
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, lastvar,
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		mVolumeView.setVolume(lastvar > 0 ? lastvar : 1);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		mHandler.removeMessages(0);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 1000);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		mHandler.removeMessages(0);
		if (v.getId() == R.id.imageView1) {
			switch (Util.Phone.getVolume(this, AudioManager.STREAM_MUSIC)) {
			case 0:
				unmute();
				break;
			default:
				mute();
				break;
			}
		}
		mHandler.sendEmptyMessageDelayed(0, 2000);
	}
}
