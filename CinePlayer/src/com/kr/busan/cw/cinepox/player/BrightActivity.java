package com.kr.busan.cw.cinepox.player;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.kr.busan.cw.cinepox.R;

public class BrightActivity extends Activity implements OnSeekBarChangeListener {

	private SeekBar mBrightSeekBar;
	private WindowManager.LayoutParams lp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mBrightSeekBar = (SeekBar) findViewById(R.id.seekBar_brightness);
		mBrightSeekBar.setMax(90);
		mBrightSeekBar.setOnSeekBarChangeListener(this);
		setContentView(mBrightSeekBar);
		setBrightness(getIntent().getFloatExtra("bright", 1.0f));
		sync();
		mHandler.sendEmptyMessageDelayed(0, 2000);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		Rect dialogBounds = new Rect();
		getWindow().getDecorView().getHitRect(dialogBounds);
		if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
			// Tapped outside so we finish the activity
			finish();
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		i.putExtra("bright", lp.screenBrightness);
		setResult(RESULT_OK, i);
		super.finish();
	}

	private void sync() {
		mBrightSeekBar
				.setProgress((int) (getWindow().getAttributes().screenBrightness * 100.0f));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		sync();
		return super.onKeyDown(keyCode, event);
	}

	private void setBrightness(float bright) {
		lp = getWindow().getAttributes();
		lp.screenBrightness = Math.abs(bright);
		getWindow().setAttributes(lp);
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

	private void setBrightness(int bright) {
		setBrightness(((bright / 100.0f) + 0.1f) / 1.0f);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (fromUser) {
			setBrightness(progress);
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
