package com.busan.cw.clsp20120924.controller;

import kr.co.chan.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.base.Domain;
import com.busan.cw.clsp20120924.model.ConfigModel;
import com.busan.cw.clsp20120924.structs.ConfigActionData;

public class IntroActivity extends Activity implements Constants {

	private ConfigModel mConfigModel;

	@Override
	public void onBackPressed() {
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (Util.Display.isTablet(this)) {
			if (Build.VERSION.SDK_INT >= 9)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			else
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		super.onCreate(savedInstanceState);
		// l.setEnabled(false);
		setContentView(R.layout.intro);
		new ConfigLoader().execute();
		setupShortcut();
	}

	private void setupShortcut() {
		Intent intent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.cinepox));
		Parcelable icon = Intent.ShortcutIconResource.fromContext(
				getApplicationContext(), R.drawable.ic_launcher);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
				getApplicationContext(), this.getClass()));
		intent.putExtra("duplicate", false);
		sendBroadcast(intent);
	}

	private class ConfigLoader extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return mConfigModel.loadConfig();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (result != null) {
				// error
			} else {
				if (mConfigModel.getActionData() != null)
					startAction(mConfigModel.getActionData());
				else
					goMain();
			}
		}
	}

	private void showUpdate(final ConfigActionData data) {
		AlertDialog.Builder updateAlert = new AlertDialog.Builder(
				IntroActivity.this);
		updateAlert.setTitle(R.string.update);
		updateAlert.setMessage(data.message);
		updateAlert.setCancelable(false);
		updateAlert.setPositiveButton(R.string.update,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse(data.url)));
						finish();
					}
				});
		if ("confirm".equalsIgnoreCase(data.type)) {
			updateAlert.setNegativeButton(R.string.later,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							goMain();
						}
					});
		}
		updateAlert.show();
	}

	private void showAd(ConfigActionData data) {
		Intent adIntent = new Intent(IntroActivity.this, ADActivity.class);
		adIntent.putExtra(KEY_AD_NUM, data.num);
		if ("webView".equalsIgnoreCase(data.type))
			adIntent.putExtra(KEY_WEB_URL, data.url);
		else if ("image".equalsIgnoreCase(data.type)) {
			adIntent.putExtra(KEY_IMG_URL, data.image);
			adIntent.putExtra(KEY_WEB_URL, data.url);
		}
		startActivity(adIntent);
	}

	private void showMessage(final ConfigActionData data) {
		AlertDialog.Builder messageAlert = new AlertDialog.Builder(
				IntroActivity.this);
		messageAlert.setTitle(R.string.msg);
		messageAlert.setCancelable(false);
		messageAlert.setPositiveButton(R.string.done,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if (data.url != null) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse(data.url)));
						}
						goMain();
					}
				});
		if ("confirm".equalsIgnoreCase(data.type)) {
			messageAlert.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							goMain();
						}
					});
		}
		messageAlert.show();
	}

	private void startAction(ConfigActionData data) {
		if (Util.Display.isTablet(this)) {
			goMain();
			return;
		}

		if (data.action.equals("update")) {
			showUpdate(data);
		} else if (data.action.equals("ad")) {
			showAd(data);
		} else if (data.action.equals("message")) {
			showMessage(data);
		} else {
			// 정의되지 않은 액션
		}
	}

	private void goMain() {
		startService(new Intent(this, CinePoxService.class));
		if (ACTION_QRPLAY.equalsIgnoreCase(getIntent().getAction())) {
			Intent i = new Intent(this, QRPlayActivity.class);
			startActivity(i);
			finish();
			return;
		} else if (getIntent().getData() != null
				&& ACTION_QRPLAY.equalsIgnoreCase(getIntent().getData()
						.getHost())) {
			Intent i = new Intent(this, QRPlayActivity.class);
			startActivity(i);
			finish();
			return;
		}

		if (Util.Display.isTablet(this)) {
			Intent webintent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(Domain.WEB_DOMAIN));
			startActivity(webintent);
		} else {
			Intent mainintent = new Intent(this, MainActivity.class);
			startActivity(mainintent);
		}
		finish();
	}
}
