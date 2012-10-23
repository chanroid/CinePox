package com.kr.busan.cw.cinepox.player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.kr.busan.cw.cinepox.R;

public class SettingActivity extends PreferenceActivity {

	private PlayerConfig mConfig;

	// private AlertDialog.Builder mCharsetDialog = new
	// AlertDialog.Builder(this)
	// .setSingleChoiceItems(Config.CharsetArray, mConfig.getCharset(),
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	// mConfig.setCharset(which);
	// l.i(Config.CharsetArray[which]);
	// dialog.dismiss();
	// }
	// }).setTitle("자막 인코딩 설정");
	private AlertDialog.Builder mTextsizeDialog = new AlertDialog.Builder(this)
			.setSingleChoiceItems(PlayerConfig.TextsizeArray, mConfig.getTextsize(),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							mConfig.setTextsize(which);
							dialog.dismiss();
						}
					});

	// private AlertDialog.Builder mQualityDialog = new
	// AlertDialog.Builder(this)
	// .setSingleChoiceItems(Config.QualityArray, mConfig.getQuality(),
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	// mConfig.setQuality(which);
	// dialog.dismiss();
	// }
	// }).setTitle("화질 설정");
	// private AlertDialog.Builder mScreenDialog = new AlertDialog.Builder(this)
	// .setSingleChoiceItems(Config.ScreenArray, mConfig.getScreenMode(),
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	// mConfig.setScreenMode(which);
	// dialog.dismiss();
	// }
	// }).setTitle("화면비율 설정");

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		if (Build.VERSION.SDK_INT > 11)
			setTheme(android.R.style.Theme_Holo_Dialog_NoActionBar);
		if (Build.VERSION.SDK_INT < 9)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		else
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		super.onCreate(savedInstanceState);
		mConfig = PlayerConfig.getInstance(this);
		addPreferencesFromResource(R.xml.setting);
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

	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		String key = preference.getKey();
		// TODO Auto-generated method stub
		if ("quality".equalsIgnoreCase(key)) {
			// setQuality();
		} else if ("screen".equalsIgnoreCase(key)) {
			// setScreen();
		} else if ("ccshow".equalsIgnoreCase(key)) {

		} else if ("ccsize".equalsIgnoreCase(key)) {
			setTextsize();
		} else if ("cccolor".equalsIgnoreCase(key)) {
			pickColor();
		} else if ("cccharset".equalsIgnoreCase(key)) {
//			setCharset();
		} else if ("help".equalsIgnoreCase(key)) {

		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	private void setTextsize() {
		mTextsizeDialog.setTitle(R.string.caption_size).show();
	}

	// private void setScreen() {
	// mScreenDialog.show();
	// }

	// private void setQuality() {
	// mQualityDialog.show();
	// }

	// private void setCharset() {
	// mCharsetDialog.show();
	// }

	private void pickColor() {
		startActivity(new Intent(this, ColorPickerActivity.class));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		sendBroadcast(new Intent("set"));
		super.onPause();
	}

}
