package com.kr.busan.cw.cinepox.player;

import io.vov.vitamio.VitamioInstaller;
import io.vov.vitamio.VitamioInstaller.VitamioNotCompatibleException;
import io.vov.vitamio.VitamioInstaller.VitamioNotFoundException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import kr.co.chan.util.l;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.widget.Toast;

import com.kr.busan.cw.cinepox.R;

public class PluginInstallActivity extends Activity {

	ProgressDialog mLoading;
	AlertDialog mSettingDialog;
	AlertDialog mInstallDialog;
	Uri mAPKUri;

	static {
		System.loadLibrary("helloneon");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLoading = new ProgressDialog(PluginInstallActivity.this);
		mLoading.setCancelable(false);
		mLoading.setMessage("플러그인 로드 중입니다...");

		AlertDialog.Builder settingDialogBuilder = new AlertDialog.Builder(this)
				.setTitle(R.string.alert)
				.setMessage(getString(R.string.noti_unknown_setting))
				.setCancelable(false)
				.setPositiveButton(R.string.done,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								Intent intent = new Intent(
										Settings.ACTION_SECURITY_SETTINGS);
								if (Build.VERSION.SDK_INT < 11)
									intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
								startActivity(intent);
							}
						});
		mSettingDialog = settingDialogBuilder.create();

		AlertDialog.Builder installDialogBuilder = new AlertDialog.Builder(this)
				.setTitle(R.string.alert)
				.setMessage(getString(R.string.noti_plugin))
				.setCancelable(false)
				.setPositiveButton(R.string.next,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setDataAndType(mAPKUri,
										"application/vnd.android.package-archive");
								startActivityForResult(i, 0);
							}
						});
		mInstallDialog = installDialogBuilder.create();
	}

	protected void onPause() {
		super.onPause();
		mLoading.dismiss();
		mSettingDialog.dismiss();
		mInstallDialog.dismiss();
	};

	@Override
	protected void onResume() {
		super.onResume();
		if (isPluginInstalled()) {
			setResult(RESULT_OK);
			finish();
			return;
		} else {
			if (!isUnknownSource()) {
				showUnknownSourceSetting();
			} else {
				new PluginInstaller().execute();
			}
		}
	}

	boolean isPluginInstalled() {
		try {
			VitamioInstaller.checkVitamioInstallation(this);
			return true;
		} catch (VitamioNotCompatibleException e) {
			return false;
		} catch (VitamioNotFoundException e) {
			return false;
		}
	}

	boolean isUnknownSource() {
		int result = Settings.Secure.getInt(getContentResolver(),
				Secure.INSTALL_NON_MARKET_APPS, 0);
		l.i("unknown setting : " + result);
		return (result == 1 ? true : false);
	}

	void showUnknownSourceSetting() {
		mSettingDialog.show();
	}

	void showPluginInstall() {
		mInstallDialog.show();
	}

	private native boolean isNeon();

	class PluginInstaller extends AsyncTask<Void, Integer, File> {

		@Override
		protected void onPreExecute() {
			mLoading.show();
		};

		@Override
		protected File doInBackground(Void... params) {
			// String baseURL = "http://m.cinepox.com/APP/apk.list/";
			String fileName;
			if (isNeon())
				fileName = "Vitamio-71.apk";
			else
				fileName = "Vitamio-70.apk";
			File file = new File(Environment.getExternalStorageDirectory()
					.getPath(), fileName);
			if (file.exists())
				return file;
			InputStream in = null;
			FileOutputStream out = null;
			// HttpURLConnection conn = null;
			try {
				file.createNewFile();
				// conn = (HttpURLConnection) new URL(baseURL + fileName)
				// .openConnection();
				// if (conn.getResponseCode() == -1)
				// return null;
				// in = conn.getInputStream();
				in = getAssets().open(fileName);
				out = new FileOutputStream(file);
				byte[] buffer = new byte[in.available()];
				while (in.read(buffer) != -1)
					out.write(buffer);
				out.flush();
				return file;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				try {
					// if (in != null && out != null && conn != null) {
					if (in != null && out != null) {
						in.close();
						out.close();
						// conn.disconnect();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void onPostExecute(File result) {
			super.onPostExecute(result);
			if (result != null) {
				mAPKUri = Uri.fromFile(result);
				showPluginInstall();
			} else {
				Toast.makeText(PluginInstallActivity.this,
						"플러그인 설치 중 오류가 발생했습니다. 다시 시도해 주세요.", Toast.LENGTH_LONG)
						.show();
				setResult(RESULT_CANCELED);
				finish();
			}
		}
	}

}
