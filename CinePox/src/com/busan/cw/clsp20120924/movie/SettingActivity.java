package com.busan.cw.clsp20120924.movie;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.busan.cw.clsp20120924.R;

@SuppressWarnings("deprecation")
public class SettingActivity extends PreferenceActivity implements
		DialogInterface.OnClickListener, OnMultiChoiceClickListener {

	Preference mLoginPref;
	CheckBoxPreference mAutoLoginPref;
	Preference mNotiPref;
	Preference mAlarmPref;

	Builder mAlarmDialog;
	Builder mAlarmIntervalDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.cinepox_setting);
		allocPref();
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshPref();
	}

	void allocPref() {
		mLoginPref = findPreference("islogin");
		mAutoLoginPref = (CheckBoxPreference) findPreference("autologin");
		mNotiPref = findPreference("noti");
		mAlarmPref = findPreference("interval");

		mAlarmDialog = new AlertDialog.Builder(this).setMultiChoiceItems(
				Config.AlarmArray, getConfig().getAlarm(), this)
				.setPositiveButton(R.string.done, null);
		mAlarmIntervalDialog = new AlertDialog.Builder(this)
				.setSingleChoiceItems(Config.IntervalArray, Config
						.transIntervalFlag(getConfig().getAlarmInterval()),
						this);

		refreshPref();
	}

	void refreshPref() {
		if (getConfig().isLogined()) {
			mAutoLoginPref.setEnabled(true);
			mLoginPref.setTitle(R.string.logout);
			mLoginPref.setSummary("로그아웃 하시려면 클릭하세요.");
		} else {
			mAutoLoginPref.setEnabled(false);
			mLoginPref.setTitle(R.string.login);
			mLoginPref
					.setSummary("씨네폭스의 서비스를 이용하기 위해 로그인이 필요합니다. 웹브라우저에 계정정보가 저장되어 있는 경우 클릭하면 자동으로 로그인됩니다.");
		}
		mAutoLoginPref.setChecked(getConfig().isAutoLogin());
	}

	Config getConfig() {
		return Config.getInstance(this);
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		if (preference.equals(mLoginPref)) {
			if (!getConfig().isLogined())
				startActivity(new Intent(this, LoginActivity.class));
		} else if (preference.equals(mAutoLoginPref)) {
			getConfig().setAutoLogin(mAutoLoginPref.isChecked());
		} else if (preference.equals(mNotiPref)) {
			mAlarmDialog.show();
		} else if (preference.equals(mAlarmPref)) {
			mAlarmIntervalDialog.show();
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		dialog.dismiss();
		getConfig().setAlarmInterval(Config.transIntervalFlag(which));
		mAlarmIntervalDialog = new AlertDialog.Builder(this)
				.setSingleChoiceItems(Config.IntervalArray, Config
						.transIntervalFlag(getConfig().getAlarmInterval()),
						this);
		sendBroadcast(new Intent(CinepoxService.ACTION_REFRESH_SERVICE));
	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		// TODO Auto-generated method stub
		getConfig().setAlarm(which, isChecked);
	}

}
