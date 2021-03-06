package com.busan.cw.clsp20120924.movie;

import static com.busan.cw.clsp20120924.movie.Constants.KEY_RESULT;
import static com.busan.cw.clsp20120924.movie.Constants.KEY_TITLE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.BitmapUtils;
import utils.DisplayUtils;
import utils.JSONUtils;
import utils.LogUtils.l;
import utils.ViewUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.TypedValue;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.busan.cw.clsp20120924.R;

public class IntroActivity extends Activity {

	static final String CONFIG_URL = Domain.ACCESS_DOMAIN
			+ "cinepoxAPP/getStartConfig/?app_version=%s&setting=response_type:json&SET_DEVICE=android(APP)";
	static final String AUTOLOGIN_URL = Domain.ACCESS_DOMAIN
			+ "member/login/?id=%s&password=%s&setting=response_type:text&SET_DEVICE=android(APP)";
	boolean isBlock = false;
	boolean isShowAd = false;
	boolean isShowMessage;
	private AlertDialog.Builder mMainAlert;
	private CheckBox mCheckBox;
	private TextView mMsgTv;
	private Intent mAdIntent;

	String mAdUrl;
	String mMessage;
	String mMsgNum;

	private AsyncTask<String, Integer, String> mDataloader = new AsyncTask<String, Integer, String>() {

		@Override
		protected String doInBackground(String... param) {
			try {
				File dir = new File(Environment.getExternalStorageDirectory()
						+ "/cinepox");
				if (!dir.exists())
					dir.mkdirs();

				String url = Domain.ACCESS_DOMAIN
						+ "cinepoxAPP/getStartConfig/";
				String version = "2.16";
				try {
					version = getPackageManager().getPackageInfo(
							getPackageName(), PackageManager.GET_META_DATA).versionName;
				} catch (Exception e) {

				}
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("app_version", version));
				params.add(new BasicNameValuePair("setting",
						"response_type:json"));
				params.add(new BasicNameValuePair("SET_DEVICE", "android(APP)"));
				JSONObject o = JSONUtils.jsonFromURL(url, params);
				l.i(o.toString());
				if ("N".equalsIgnoreCase(o.getString(KEY_RESULT))) {
					return o.getString(Parser.KEY_MSG);
				}

				JSONObject dataObject = o.getJSONObject("data");
				// String version = dataObject.getString("ver");
				// String player_config = dataObject.getString("player_config");
				// getPlayerConfig().setPlayerConfigUrl(player_config);
				// String access_log_url =
				// dataObject.getString("access_log_url");
				String error_log_url = dataObject.getString("error_log_url");
				String widget_url = dataObject.getString("widget_data_url");
				getConfig().setWidgetUrl(widget_url);
				getConfig().setErrorLogUrl(error_log_url);

				String is_app_recommend = dataObject
						.getString("is_app_recommend");
				getConfig().setAppRecommend(
						"Y".equalsIgnoreCase(is_app_recommend));

				if (!DisplayUtils.isTablet(IntroActivity.this)) {
					JSONArray bestList = dataObject.getJSONArray("bestList");
					for (int i = 0; i < bestList.length(); i++)
						parseCategory(i, bestList.getString(i));
				}

				JSONObject banner = dataObject.getJSONObject("top_banner");
				String banner_img = banner.getString("top_banner_img");
				String banner_url = banner.getString("top_banner_url");
				Bitmap b = BitmapUtils.bitmapFromURL(banner_img, null);
				getConfig().setBannerInfo(b, banner_url);

				JSONArray action = dataObject.getJSONArray("action");
				for (int i = 0; i < action.length(); i++) {
					isBlock = true;
					if (!parseAction(action.getJSONObject(i)))
						break;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			}
			return null;
		};

		private boolean parseAction(JSONObject o) throws JSONException,
				IOException {
			// TODO Auto-generated method stub
			mMsgNum = o.getString("code");
			String what = o.getString("what");
			String type = o.getString("type");
			String msg = o.getString("msg");
			String image = o.getString("image");
			String url = o.getString("url");
			boolean result = true;

			if (isAbliableMessage(mMsgNum)) {
				if ("update".equalsIgnoreCase(what)) {
					buildUpdateDialog(type, msg, url);
					result = false;
				} else if ("message".equalsIgnoreCase(what)) {
					buildMessageDialog(type, msg, url);
				} else if ("ad".equalsIgnoreCase(what)) {
					buildAdIntent(type, msg, image, url);
				}
			}

			return result;
		}

		private boolean isAbliableMessage(String code) {
			return getConfig().isReadMessage(code);
		}

		private void buildUpdateDialog(String type, String msg, final String url) {
			// TODO Auto-generated method stub
			mMainAlert = new AlertDialog.Builder(IntroActivity.this);
			mMainAlert.setTitle(R.string.update);
			mMainAlert.setMessage(msg);
			mMainAlert.setCancelable(false);
			mMainAlert.setPositiveButton(R.string.update,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse(url)));
							finish();
						}
					});
			if ("confirm".equalsIgnoreCase(type)) {
				mMainAlert.setNegativeButton(R.string.later,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								goMain();
							}
						});
			}
		}

		private void buildMessageDialog(String type, String msg,
				final String url) {
			// TODO Auto-generated method stub
			isShowMessage = true;
			mMessage = msg;
			mMainAlert = new AlertDialog.Builder(IntroActivity.this);
			mMainAlert.setTitle(R.string.msg);
			mMainAlert.setCancelable(false);
			mMainAlert.setPositiveButton(R.string.done,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							if (url != null) {
								startActivity(new Intent(Intent.ACTION_VIEW,
										Uri.parse(url)));
							}
							goMain();
						}
					});
			if ("confirm".equalsIgnoreCase(type)) {
				mMainAlert.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								goMain();
							}
						});
			}
		}

		private void buildAdIntent(String type, String msg, String image,
				String url) throws IOException {
			// TODO Auto-generated method stub
			isShowAd = true;
			mAdIntent = new Intent(IntroActivity.this, ADActivity.class);
			mAdIntent.putExtra(ADActivity.EXTRA_AD_NUM, mMsgNum);
			if ("webView".equalsIgnoreCase(type)) {
				mAdUrl = url;
				mAdIntent.putExtra(ADActivity.EXTRA_WEB_URL, mAdUrl);
			} else if ("image".equalsIgnoreCase(type)) {
				mAdUrl = image;
				mAdIntent.putExtra(ADActivity.EXTRA_IMG_URL, mAdUrl);
				mAdIntent.putExtra(ADActivity.EXTRA_WEB_URL, url);
			} else {
				return;
			}
		}

		void parseCategory(int index, String url)
				throws ClientProtocolException, IOException, JSONException {
			JSONObject o = JSONUtils.jsonFromURL(url);
			String title = o.getString(KEY_TITLE);
			getConfig().addCategoryName(index, title);
			getConfig().addCategoryUrl(index, url);
			if (index == 0)
				getConfig().setCurrentCategory(url);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				showError(result);
				return;
			}
			if (!isBlock || DisplayUtils.isTablet(IntroActivity.this)) {
				goMain();
			} else {
				if (isShowAd) {
					startActivityForResult(mAdIntent, 0);
				} else if (mMainAlert != null) {
					LinearLayout layout = (LinearLayout) getLayoutInflater()
							.inflate(R.layout.main_popup, null);
					mCheckBox = (CheckBox) layout
							.findViewById(R.id.checkBox_main_popup);
					mCheckBox
							.setOnCheckedChangeListener(new OnCheckedChangeListener() {
								@Override
								public void onCheckedChanged(
										CompoundButton buttonView,
										boolean isChecked) {
									// TODO Auto-generated method stub
									if (isChecked) {
										getConfig().addReadMessage(mMsgNum);
									} else {
										getConfig().removeReadMessage(mMsgNum);
									}
								}
							});
					if (isShowMessage) {
						if (mMessage != null) {
							mMsgTv = new TextView(IntroActivity.this);
							mMsgTv.setText(mMessage);
							mMsgTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
							mMsgTv.setTextColor(Color.WHITE);
							mMsgTv.setPadding(30, 15, 30, 15);
							layout.addView(mMsgTv, 0);
							mMainAlert.setView(layout);
							mMainAlert.show();
						}
					} else {
						mMainAlert.show();
					}
				} else {
					goMain();
				}
			}
		};
	};

	void goMain() {
		startService(new Intent(this, CinepoxService.class));
		if (WidgetProvider.ACTION_QRPLAY.equalsIgnoreCase(getIntent()
				.getAction())) {
			Intent i = new Intent(this, QRPlayActivity.class);
			startActivity(i);
			finish();
			return;
		} else if (getIntent().getData() != null
				&& WidgetProvider.ACTION_QRPLAY.equalsIgnoreCase(getIntent()
						.getData().getHost())) {
			Intent i = new Intent(this, QRPlayActivity.class);
			startActivity(i);
			finish();
			return;
		}

		if (DisplayUtils.isTablet(this)) {
			Intent webintent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(Domain.WEB_DOMAIN));
			startActivity(webintent);
		} else {
			Intent mainintent = new Intent(this, MainActivity.class);
			startActivity(mainintent);
		}
		finish();
	}

	void showError(String msg) {
		msg = getString(R.string.error_connect);
		ViewUtils.showAlert(this, getString(R.string.alert), msg,
				getString(R.string.done),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						finish();
					}
				}, false);
	}

	Config getConfig() {
		return Config.getInstance(this);
	}

	// PlayerConfig getPlayerConfig() {
	// return PlayerConfig.getInstance(this);
	// }

	void setupShortcut() {
		if (getConfig().isFirstRun() && Build.VERSION.SDK_INT < 14) {
			Intent intent = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
					getString(R.string.cinepox));
			Parcelable icon = Intent.ShortcutIconResource.fromContext(
					getApplicationContext(), R.drawable.ic_launcher);
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
					getApplicationContext(), this.getClass()));
			intent.putExtra("duplicate", false);

			sendBroadcast(intent);
			getConfig().setFirstRun(false);
		}
	}

	void destroyMember() {
		mMainAlert = null;
		isBlock = false;
		isShowAd = false;
		isShowMessage = false;
		mAdUrl = null;
		mMessage = null;
		mMsgNum = null;
	}

	@Override
	public void onBackPressed() {
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (DisplayUtils.isTablet(this)) {
			if (Build.VERSION.SDK_INT >= 9)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			else
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);
		if (!DisplayUtils.isTablet(this))
			findViewById(R.id.introbg).setBackgroundResource(R.drawable.bg_poster_em);
		else
			findViewById(R.id.introbg).setBackgroundResource(
					R.drawable.bg_poster_em);
		mDataloader.execute();
		setupShortcut();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		goMain();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		destroyMember();
		super.finish();
	}

}
