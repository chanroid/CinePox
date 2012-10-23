package com.kr.busan.cw.cinepox.player;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.kr.busan.cw.cinepox.R;

public class VolumeActivity extends Activity implements
		OnSeekBarChangeListener, OnClickListener {

	private SeekBar mVolumeSeekBar;
	private ImageView mMuteBtn;

	private AudioManager mAudioManager;

	private int max;
	private int lastvar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.volumn);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mVolumeSeekBar = (SeekBar) findViewById(R.id.seekBar_volume);
		mVolumeSeekBar.setMax(max);
		mVolumeSeekBar.setOnSeekBarChangeListener(this);
		mMuteBtn = (ImageView) findViewById(R.id.imageView1);
		mMuteBtn.setOnClickListener(this);
		sync();
		mHandler.sendEmptyMessageDelayed(0, 2000);
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
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
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
		mVolumeSeekBar.setProgress(mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC));
		if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
			mute();
			
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		switch (event.getAction()) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (volume > 0)
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume--, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, 2000);
			sync();
			return false;
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (volume < max)
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume++, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);			
			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, 2000);
			sync();
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (fromUser) {
			if (seekBar.getId() == R.id.seekBar_volume) {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}
			mHandler.removeMessages(0);
		}
	}

	private void mute() {
		lastvar = mVolumeSeekBar.getProgress();
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		mVolumeSeekBar.setProgress(0);
	}

	private void unmute() {
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, lastvar,
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		mVolumeSeekBar.setProgress(lastvar > 0 ? lastvar : 1);
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
			switch (mVolumeSeekBar.getProgress()) {
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
