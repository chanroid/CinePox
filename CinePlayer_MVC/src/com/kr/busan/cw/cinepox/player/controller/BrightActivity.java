package com.kr.busan.cw.cinepox.player.controller;

import kr.co.chan.util.Util;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.kr.busan.cw.cinepox.player.model.PlayerModel.Const;
import com.kr.busan.cw.cinepox.player.view.BrightControlView;

import controller.CCActivity;

@SuppressLint("HandlerLeak")
public class BrightActivity extends CCActivity implements OnSeekBarChangeListener {

	private BrightControlView mBrightView;
	private WindowManager.LayoutParams lp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mBrightView = new BrightControlView(this);
		mBrightView.setOnSeekbarChangeListener(this);
		setContentView(mBrightView);
		lp = getWindow().getAttributes();
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setBrightness(getIntent().getFloatExtra(Const.KEY_BRIGHT, 1.0f));
		sync();
		mHandler.sendEmptyMessageDelayed(0, 2000);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		i.putExtra(Const.KEY_BRIGHT, lp.screenBrightness);
		setResult(RESULT_OK, i);
		super.finish();
	}

	private void sync() {
		mBrightView
				.setProgress((int) (getWindow().getAttributes().screenBrightness * 100.0f));
	}

	private void setBrightness(float bright) {
		if (bright >= 0.0f && bright <= 1.0f)
			Util.Display.setBrightness(this, bright);
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

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (fromUser) {
			setBrightness(((progress / 100.0f) + 0.1f) / 1.0f);
			mHandler.removeMessages(0);
		}
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

}
