package com.kr.busan.cw.cinepox.player;

import kr.co.chan.util.ColorPickerView;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.kr.busan.cw.cinepox.R;

public class ColorPickerActivity extends Activity implements ColorPickerView.OnColorChangedListener, OnClickListener {

	private ColorPickerView mPickerView;
	private Button mCurrentBtn;
	private Button mDesireBtn;
	private Button mBlackBtn;
	private Button mWhiteBtn;
	private Button mCommitBtn;
	private Button mCancelBtn;
	private PlayerConfig mConfig;
	
	private int mColor = Color.WHITE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		if (Build.VERSION.SDK_INT > 11)
			setTheme(android.R.style.Theme_Holo_Dialog_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.colorpicker);
		mConfig = PlayerConfig.getInstance(this);
		mColor = mConfig.getCCColor();
		initView();
	}
	
	private void initView() {
		mPickerView = (ColorPickerView) findViewById(R.id.colorPickerView1);
		mCurrentBtn = (Button) findViewById(R.id.button_colorpicker_currentcolor);
		mDesireBtn = (Button) findViewById(R.id.button_colorpicker_desirecolor);
		mBlackBtn = (Button) findViewById(R.id.button_colorpicker_black);
		mWhiteBtn = (Button) findViewById(R.id.button_colorpicker_white);
		mCommitBtn = (Button) findViewById(R.id.button_colorpicker_commit);
		mCancelBtn = (Button) findViewById(R.id.button_colorpicker_cancel);
		
		mPickerView.setOnColorChangedListener(this);
		mCurrentBtn.setOnClickListener(this);
		mDesireBtn.setOnClickListener(this);
		mWhiteBtn.setOnClickListener(this);
		mBlackBtn.setOnClickListener(this);
		mCommitBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		
		mPickerView.setDefaultColor(mColor);
		mCurrentBtn.setBackgroundColor(mColor);
		mDesireBtn.setBackgroundColor(mColor);
	}
	
	private void commit() {
		mConfig.setCCColor(mColor);
		finish();
	}
	
	@Override
	public void colorChanged(int color) {
		// TODO Auto-generated method stub
		mDesireBtn.setBackgroundColor(mColor = color);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_colorpicker_currentcolor) {
			finish();
		} else if (v.getId() == R.id.button_colorpicker_desirecolor) {
			commit();
		} else if (v.getId() == R.id.button_colorpicker_white) {
			mPickerView.setDefaultColor(mColor = Color.WHITE);
			mDesireBtn.setBackgroundColor(mColor);
		} else if (v.getId() == R.id.button_colorpicker_black) {
			mPickerView.setDefaultColor(mColor = Color.BLACK);
			mDesireBtn.setBackgroundColor(mColor);
		} else if (v.getId() == R.id.button_colorpicker_commit) {
			commit();
		} else if (v.getId() == R.id.button_colorpicker_cancel) {
			finish();
		}
	}

}
