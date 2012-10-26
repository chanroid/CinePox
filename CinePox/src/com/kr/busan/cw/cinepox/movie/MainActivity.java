package com.kr.busan.cw.cinepox.movie;

import java.io.IOException;
import java.net.URLDecoder;

import kr.co.chan.util.ShakeListener.OnShakeListener;
import kr.co.chan.util.Util;
import kr.co.chan.util.l;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.busan.cw.clsp20120924.R;

/**
 * 이슈사항 정리
 * 
 * 갤럭시노트(LGT) - 웹브라우저에서 앱으로 복귀할때 오류창 뜨는 경우 - 휴대폰을 재부팅 하면 해결됨
 * 
 * @author CINEPOX
 * 
 */
@SuppressLint("HandlerLeak")
public class MainActivity extends FragmentActivity implements
		DialogInterface.OnClickListener, OnMultiChoiceClickListener,
		OnClickListener, OnShakeListener, LocationListener {

	ImageButton mBestBtn;
	ImageButton mMovieBtn;
	ImageButton mVideoBtn;
	ImageButton mMylistBtn;
	ImageButton mQRPlayBtn;
	ImageButton mPremiumBtn;
	ImageButton mSearchBtn;
	FragmentTransaction mFragmentTrans;
	BestFragment mBestFragment;
//	ShakeListener mShaker;
	LocationManager mLocManager;
	Location mLocation;
	ProgressDialog mProgress;
//	MenuItem mMotionMenu;
	static final int REQ_QRPLAY = 0;

	MenuItem mLoginoutMenu;
	MenuItem mJoinMenu;
	Builder mAlarmDialog;
	Builder mAlarmIntervalDialog;

	boolean isBack;
	boolean isShareing = false;

	private BroadcastReceiver mWebReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if ("login".equalsIgnoreCase(action))
				refreshPref();
		}
	};

	private Handler mTimehandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				isBack = false;
				break;
			case 1:
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT >= 11)
			setTheme(android.R.style.Theme_Holo_NoActionBar);
		else
			setTheme(android.R.style.Theme_NoTitleBar);
		super.onCreate(savedInstanceState);
		if (getConfig().isLogined())
			Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT)
					.show();
		IntentFilter filter = new IntentFilter("login");
		filter.addAction("qrplay");
		filter.addAction("play");
		registerReceiver(mWebReceiver, filter);
//		mShaker = new ShakeListener(this);
//		mShaker.setOnShakeListener(this);
//		if (getConfig().isUseMotion()) {
//			mShaker.resume();
//		} else {
//			mShaker.pause();
//		}
		mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = mLocManager.getBestProvider(criteria, true);
		if (provider == null) {
			provider = LocationManager.NETWORK_PROVIDER;
		}
		mLocation = mLocManager.getLastKnownLocation(provider);
		mLocManager.requestLocationUpdates(provider, 10000, 100, this);
		setContentView(R.layout.main);
		allocView();
		getConfig().setRunning(true);
		mProgress = new ProgressDialog(this);
		mProgress.setMessage(getString(R.string.readytoresponse));
		mProgress.setCancelable(false);
		if (getIntent() != null && getIntent().getData() != null) {
			if (WidgetProvider.ACTION_QRPLAY.equalsIgnoreCase(getIntent()
					.getAction())
					|| WidgetProvider.ACTION_QRPLAY
							.equalsIgnoreCase(getIntent().getData().getHost()))
				goQRPlay();
		} else if (getIntent() != null) {
			if (WidgetProvider.ACTION_QRPLAY.equalsIgnoreCase(getIntent()
					.getAction())) {
				goQRPlay();
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		l.i("new intent");
		setIntent(intent);
		if (intent != null && intent.getData() != null) {
			if (WidgetProvider.ACTION_QRPLAY.equalsIgnoreCase(intent.getData()
					.getHost()))
				goQRPlay();
		} else if (intent != null) {
			if (WidgetProvider.ACTION_QRPLAY.equalsIgnoreCase(intent
					.getAction())) {
				goQRPlay();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		getConfig().setRunning(false);
		unregisterReceiver(mWebReceiver);
		super.onDestroy();
	}

	void allocView() {
		mBestBtn = (ImageButton) findViewById(R.id.btn_main_best);
		mMovieBtn = (ImageButton) findViewById(R.id.btn_main_movie);
		mVideoBtn = (ImageButton) findViewById(R.id.btn_main_video);
		mMylistBtn = (ImageButton) findViewById(R.id.btn_main_mylist);
		mQRPlayBtn = (ImageButton) findViewById(R.id.btn_main_qrplay);
		mPremiumBtn = (ImageButton) findViewById(R.id.btn_main_premium);
		if (getConfig().getBannerImg() != null)
			mPremiumBtn.setImageBitmap(getConfig().getBannerImg());
		else
			mPremiumBtn.setVisibility(View.GONE);
		mSearchBtn = (ImageButton) findViewById(R.id.btn_main_search);

		mBestFragment = new BestFragment();
		mFragmentTrans = getSupportFragmentManager().beginTransaction();
		mFragmentTrans.replace(R.id.main_fragment_container, mBestFragment);
		mFragmentTrans.commit();
		mAlarmDialog = new AlertDialog.Builder(this).setMultiChoiceItems(
				Config.AlarmArray, getConfig().getAlarm(), this)
				.setPositiveButton(R.string.done, null);
		mAlarmIntervalDialog = new AlertDialog.Builder(this)
				.setSingleChoiceItems(Config.IntervalArray, Config
						.transIntervalFlag(getConfig().getAlarmInterval()),
						this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		mShaker.resume();
		mBestFragment = new BestFragment();
		mFragmentTrans = getSupportFragmentManager().beginTransaction();
		mFragmentTrans.replace(R.id.main_fragment_container, mBestFragment);
		try {
			mFragmentTrans.commit();
		} catch (Exception e) {
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		mShaker.pause();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (isBack) {
			super.onBackPressed();
		} else {
			isBack = true;
			Toast.makeText(this, R.string.backtoexit, Toast.LENGTH_SHORT)
					.show();
			mTimehandler.sendEmptyMessageDelayed(0, 2000);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
//		mLoginoutMenu = menu.add(R.string.login);
//		mJoinMenu = menu.add(R.string.join);
//		mMotionMenu = menu.add(R.string.motion_disable);
//		menu.add(R.string.motion_sensitive);
		// menu.add(R.string.noti_item);
		// menu.add(R.string.noti_interval);
//		refreshPref();
		return super.onCreateOptionsMenu(menu);
	}

	private void motionShare() {
		// TODO Auto-generated method stub
		if (!getConfig().isUseMotion())
			return;
		if (isShareing) {
			l.i("isShareing false");
			return;
		} else {
			l.i("isShareing true");
		}
		if (!Util.Loc.chkLocService(this)) {
			l.i("location Service disabled");
			mProgress.dismiss();
			showLocSettingDialog();
			return;
		}
		l.i("share receive");
		new UrlShareReceiveThread(mLocation).execute();
	}

	class UrlShareReceiveThread extends ShareThread {

		public UrlShareReceiveThread(Location loc) {
			super(loc);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			isShareing = true;
			mProgress.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			l.i("receive start");
			try {
				String shake_key = generateShakeKey();
				long startTime = System.currentTimeMillis();
				long currentTime = 0l;
				boolean result = false;
				while (currentTime - 5000 < startTime) {
					try {
						JSONObject js = getJson(String.format(RESPONSE_URL,
								shake_key));
						String resultText = js.getString("result");
						String is_response = js.getString("is_request");
						result = "Y".equalsIgnoreCase(is_response)
								&& "Y".equalsIgnoreCase(resultText);
						if (result)
							return URLDecoder.decode(js.getString("url"),
									HTTP.UTF_8);
						Thread.sleep(500);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Config.sendErrorLog(MainActivity.this,
								getConfig().ERROR_LOG_URL, e);
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						Config.sendErrorLog(MainActivity.this,
								getConfig().ERROR_LOG_URL, e);
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Config.sendErrorLog(MainActivity.this,
								getConfig().ERROR_LOG_URL, e);
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					currentTime = System.currentTimeMillis();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			isShareing = false;
			mProgress.dismiss();
			if (result != null) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			} else {
				Toast.makeText(MainActivity.this, R.string.receive_notfound,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

//	private void showSensitiveDialog() {
//		final AlertDialog.Builder mSensitiveDialog = new AlertDialog.Builder(
//				this);
//		mSensitiveDialog.setTitle(R.string.motion_sensitive);
//		mSensitiveDialog.setMessage(getString(R.string.sensitive_message));
//		LinearLayout linear = new LinearLayout(this);
//		linear.setOrientation(LinearLayout.VERTICAL);
//		SeekBar seek = new SeekBar(this);
//		seek.setPadding(10, 10, 10, 10);
//		final TextView text = new TextView(this);
//		text.setText(getConfig().getMotionSensitive() - 300 + "");
//		text.setPadding(10, 10, 10, 10);
//		seek.setMax(1000);
//		seek.setProgress(getConfig().getMotionSensitive() - 300);
//		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress,
//					boolean fromUser) {
//				text.setText(progress + "");
//				mShaker.setThreshold(progress + 300);
//				getConfig().setMotionSensitive(progress + 300);
//			}
//		});
//		linear.addView(seek);
//		linear.addView(text);
//		mSensitiveDialog.setView(linear);
//		mSensitiveDialog.setPositiveButton(R.string.done, null);
//		mSensitiveDialog.show();
//	}

	private void showLocSettingDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
		mDialog.setMessage(getString(R.string.noti_locservice));
		mDialog.setTitle(R.string.alert);
		mDialog.setPositiveButton(R.string.done,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startActivity(new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						dialog.dismiss();
					}
				});
		mDialog.setNegativeButton(R.string.cancel, null);
		mDialog.show();
	}

	void refreshPref() {
		if (getConfig().isLogined()) {
			mLoginoutMenu.setTitle(R.string.logout);
			mJoinMenu.setTitle(R.string.leave);
		} else {
			mLoginoutMenu.setTitle(R.string.login);
			mJoinMenu.setTitle(R.string.join);
		}
//		if (getConfig().isUseMotion())
//			mMotionMenu.setTitle(R.string.motion_disable);
//		else
//			mMotionMenu.setTitle(R.string.motion_enable);
	}

	void logoutConfirm() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.logout);
		dialog.setMessage(getString(R.string.noti_logout));
		dialog.setPositiveButton(R.string.done,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						getConfig().setLogined(false);
						getConfig().setAccount("", "");
						getConfig().setAutoLogin(false);
						refreshPref();
						Toast.makeText(MainActivity.this,
								R.string.logout_success, Toast.LENGTH_SHORT)
								.show();
					}
				});
		dialog.setNegativeButton(R.string.cancel, null);
		dialog.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
//		String title = item.getTitle().toString();
//		if (getString(R.string.login).equalsIgnoreCase(title)) {
//			if (!getConfig().isLogined())
//				startActivity(new Intent(this, LoginActivity.class));
//		} else if (getString(R.string.logout).equalsIgnoreCase(title)) {
//			if (getConfig().isLogined())
//				logoutConfirm();
//		} else if (getString(R.string.join).equalsIgnoreCase(title)) {
//			if (!getConfig().isLogined())
//				startActivity(new Intent(
//						Intent.ACTION_VIEW,
//						Uri.parse(WebDomain + "member/join.html?SET_DEVICE=android(APP)")));
//		} else if (getString(R.string.leave).equalsIgnoreCase(title)) {
//
//		} else if (getString(R.string.noti_item).equalsIgnoreCase(title)) {
//			 mAlarmDialog.show();
//		} else if (getString(R.string.noti_interval).equalsIgnoreCase(title)) {
//			 mAlarmIntervalDialog.show();
//		} else if (getString(R.string.motion_enable).equals(title)) {
//			getConfig().setUseMotion(true);
//			mShaker.resume();
//			mMotionMenu.setTitle(R.string.motion_disable);
//		} else if (getString(R.string.motion_disable).equals(title)) {
//			getConfig().setUseMotion(false);
//			mShaker.pause();
//			mMotionMenu.setTitle(R.string.motion_enable);
//		} else if (getString(R.string.motion_sensitive).equals(title)) {
//			showSensitiveDialog();
//		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQ_QRPLAY:
				Toast.makeText(this, data.getStringExtra("SCAN_RESULT"),
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	}

	void goQRPlay() {
		Intent i = new Intent(this, QRPlayActivity.class);
		startActivity(i);
	}

	void goMovie() {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.WebDomain
				+ "vod/movie/movieList.html" + "?SET_DEVICE=android(APP)"));
		startActivity(i);
	}

	void goVideo() {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.WebDomain
				+ "vod/tv/tvList.html" + "?SET_DEVICE=android(APP)"));
		startActivity(i);
	}

	void goSearch() {
		Intent i = new Intent(this, SearchActivity.class);
		startActivity(i);
	}

	void goMyPage() {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.WebDomain
				+ "mypage/wish_list.html" + "?SET_DEVICE=android(APP)"));
		startActivity(i);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_main_best:
			initBtnImage();
			mBestBtn.setImageResource(R.drawable.bt_main_best_on);
			mFragmentTrans = getSupportFragmentManager().beginTransaction();
			mFragmentTrans.replace(R.id.main_fragment_container, mBestFragment);
			mFragmentTrans.commit();
			break;
		case R.id.btn_main_movie:
			goMovie();
			break;
		case R.id.btn_main_mylist:
			goMyPage();
			break;
		case R.id.btn_main_premium:
			if (getConfig().getBannerUrl() != null)
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(getConfig().getBannerUrl())));
			break;
		case R.id.btn_main_qrplay:
			goQRPlay();
			break;
		case R.id.btn_main_search:
			goSearch();
			break;
		case R.id.btn_main_video:
			goVideo();
			break;
		default:
			break;
		}
	}

	Config getConfig() {
		return Config.getInstance(this);
	}

	void initBtnImage() {
		mBestBtn.setImageResource(R.drawable.btn_main_best);
		mMylistBtn.setImageResource(R.drawable.btn_main_mylist);
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

	@Override
	public void onShake() {
		// TODO Auto-generated method stub
		motionShare();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		mLocation = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
