package com.kr.busan.cw.cinepox.player;

import static com.kr.busan.cw.cinepox.player.Parser.*;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.VitamioInstaller;
import io.vov.vitamio.VitamioInstaller.VitamioNotCompatibleException;
import io.vov.vitamio.VitamioInstaller.VitamioNotFoundException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

import kr.co.chan.util.ShakeListener.OnShakeListener;
import kr.co.chan.util.Util;
import kr.co.chan.util.VerticalProgressBar;
import kr.co.chan.util.l;
import kr.co.chan.util.CinePox.LogPost;
import kr.co.chan.util.Classes.AnimatedImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.kr.busan.cw.cinepox.R;

/**
 * 이유없이 플레이가 안될때는 1. 재부팅 2. 인터넷 껐다 켜기 3. 와이파이
 * 
 * @author CINEPOX
 * 
 */
@SuppressLint("HandlerLeak")
@SuppressWarnings("deprecation")
public class PlayerActivity extends Activity implements OnErrorListener,
		OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
		OnSeekCompleteListener, OnInfoListener, OnSeekBarChangeListener,
		android.media.MediaPlayer.OnErrorListener,
		android.media.MediaPlayer.OnBufferingUpdateListener,
		android.media.MediaPlayer.OnCompletionListener,
		android.media.MediaPlayer.OnPreparedListener,
		android.media.MediaPlayer.OnSeekCompleteListener,
		android.media.MediaPlayer.OnInfoListener, OnShakeListener,
		LocationListener, OnClickListener {

	// private BluetoothAdapter mBTAdapter;
	private Uri mPath;
	private Uri mCinepoxPath;
	private String mOrderCode;
	private String mPlaytimeUrl;
	private String mTitle;
	private String mTextBannerUrl;
	private String mADUrl;
	private String mBookmarkListUrl;
	private String mBookmarkInsertUrl;
	private String mBookmarkDeleteUrl;
	private String mBugReportUrl;
	private String mLogReportUrl;
	private String mCaptionUrl;

	private String m3GMessage;
	private int m3GMessageLength;

	private String mMovieSeq;
	private String mMemberSeq;

	private ArrayList<QualityData> mQualityMap;

	private final int REQUEST_READ_QRCODE = 1;
	private final int REQUEST_MODIFY_BRIGHTNESS = 2;
	// private final int REQUEST_ENABLE_BT = 3;
	private final int REQUEST_CHECK_PLUGIN = 5;

	private static final int HW_CODEC = 0;
	private static final int SW_CODEC = 1;

	private long mCurrent = 0l;
	private long mStartTime = 0l;
	private int mCodec = -1;
	private long mDuration = 1l;
	private int mBuffer = 0;
	private int mQuality;
	private int mErrorCount;
	private boolean isBlock = false;
	private boolean isMotionShareEnabled = false;
	private boolean isTracking = false;
	private boolean isUseCC = false;
	private boolean isSeeking = false;
	private boolean isShareing = false;
	private boolean isBack = false;
	private boolean isControlVisible = false;
	private boolean isCriticalError = false;

	private SWVideoView mVideoView;
	// private BluetoothChatService mBTService;
	private HWVideoView mHWVideoView;
	private SeekBar mSeekBar;
	private AnimatedImageView mLoadingBar;
	private ProgressDialog mProgress;
	private WindowManager mWindowManager;
	private AudioManager mAudioManager;
	private RelativeLayout mVolumeControl;
	private LinearLayout mWindow;
	private VerticalProgressBar mVolumeSeekbar;
	private TextView mVolumeText;
	private RelativeLayout mController;
	private TextView mBattText;
	// private TextView mSpeedText;
	private TextView mTimeText;
	private TextView mCurrentText;
	private TextView mDurationText;
	private TextView mCenterText;
	private TextView mCaptionText;
	private TextView mQOSBtn;
	// private ImageButton mBTBtn;
	private ImageButton mCCBtn;
	// private ImageButton mShareBtn;
	private ImageButton mSettingBtn;
	private ImageButton mFullScreenBtn;
	private ImageButton mVolumnBtn;
	private ImageButton mBrightBtn;
	private TextView mCodecBtn;
	// private ImageButton mMoveFirstBtn;
	// private ImageButton mBookmarkBtn;
	// private ImageButton mReadQRBtn;
	private ImageButton mPlaypauseBtn;
	private LinearLayout mTopController;
	private LinearLayout mBottomController;
	private Calendar mCal;
	// private ShakeListener mShaker;
	private LocationManager mLocManager;
	private Location mLocation;

	private Animation mTopShowAni;
	private Animation mTopHideAni;
	private Animation mBottomShowAni;
	private Animation mBottomHideAni;

	private PlayerConfig mConfig;
	private CaptionParser mCCParser;

	// private DownloadSpeedThread mSpeedThread;
	// private DownThread mDownThread;

	/**
	 * 액티비티 라이프 사이클
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT < 12)
			setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
		else
			setTheme(android.R.style.Theme_Holo_NoActionBar_Fullscreen);

		mStartTime = System.currentTimeMillis();
		mConfig = PlayerConfig.getInstance(this);
		mCal = Calendar.getInstance(Locale.getDefault());
		mCCParser = new CaptionParser();
		// mBTAdapter = BluetoothAdapter.getDefaultAdapter();

		// sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// accelerormeterSensor = sensorManager
		// .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// if (Build.VERSION.SDK_INT < 9)
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// else
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		// bindService(new Intent(this, CinepoxService.class), this,
		// BIND_AUTO_CREATE);

		super.onCreate(savedInstanceState);
		Intent i = new Intent();
		i.setData(Uri.parse("cinepox://startservice"));
		startActivity(i);
		mErrorCount = getIntent().getIntExtra("error_count", 0);
		if (mErrorCount > 4) {
			finish();
			return;
		}
		mProgress = new ProgressDialog(this);
		loadSystemServices();
		loadControllerAnimations();
		initLocationSettings();
		// mShaker = new ShakeListener(this);
		// mShaker.pause();
		// mShaker.setOnShakeListener(this);

		// checkPlugin();

		setContentView(R.layout.player_main);
		allocView();
		allocController();
		new UpdateSync().execute();
	}

	void loadSystemServices() {
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	void loadControllerAnimations() {
		mTopShowAni = AnimationUtils
				.loadAnimation(this, R.anim.navi_top_up_ani);
		mTopHideAni = AnimationUtils.loadAnimation(this,
				R.anim.navi_top_down_ani);
		mBottomShowAni = AnimationUtils.loadAnimation(this, R.anim.navi_up_ani);
		mBottomHideAni = AnimationUtils.loadAnimation(this,
				R.anim.navi_down_ani);

		mBottomHideAni.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				hideController();
			}
		});
	}

	void initLocationSettings() {
		Criteria criteria = new Criteria();
		String provider = mLocManager.getBestProvider(criteria, true);
		if (provider == null) {
			provider = LocationManager.NETWORK_PROVIDER;
		}
		mLocation = mLocManager.getLastKnownLocation(provider);
		mLocManager.requestLocationUpdates(provider, 10000, 100, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			if (!isBlock)
				removeControllerWindow();
		} catch (Throwable t) {
		}
		pause();
		mConfig.setCinepoxUrl(null);
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			if (isBlock)
				return;
			else {
				if (mWindow != null)
					addControllerWindow();
				resume();
				addSWVideo();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void resume() {
		if (mHWVideoView != null && mCurrent > 0l) {
			seekTo(mCurrent);
			start();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_READ_QRCODE:
				finish();
				break;
			case REQUEST_MODIFY_BRIGHTNESS:
				Util.Display.setBrightness(this,
						data.getFloatExtra("bright", 1.0f));
				break;
			// case REQUEST_CHECK_PLUGIN:
			// setContentView(R.layout.player_main);
			// allocView();
			// new UpdateSync().execute();
			// isBlock = false;
			// break;
			}
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (isBack) {
			finish();
		} else {
			isBack = true;
			Toast.makeText(this, R.string.backtoexit, Toast.LENGTH_LONG).show();
			mTimeHandler.sendEmptyMessageDelayed(2, 2000);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			unregisterReceiver(mReceiver);
			removeControllerWindow();
			if (mQualityMap.size() > 0)
				mQualityMap.clear();
			if (mTimeSync != null && !mTimeSync.isInterrupted()) {
				mTimeSync.interrupt();
			}
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	// public void checkPlugin() {
	// Intent i = new Intent(this, PluginInstallActivity.class);
	// startActivityForResult(i, REQUEST_CHECK_PLUGIN);
	// }

	public boolean captionParse(String path) {
		try {
			mCCParser.parse(path);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private void init() {
		// mCodec = mConfig.getDefaultCodec();
		mCodec = 0;
		initQuality();
		// hideController();
		setAllListener();
		refreshView();
		updateTime();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(mReceiver, filter);
		Util.Display.setBrightness(this, 1.0f);
	}

	private void selectLang() {
		if (isUseCC && mCCParser.getCCLanglist().size() > 0) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			String[] s = new String[mCCParser.getCCLanglist().size()];
			dialog.setSingleChoiceItems(mCCParser.getCCLanglist().toArray(s),
					mCCParser.getCurrentLang(),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							mCCParser.setCCLang(which);
						}
					});
			dialog.show();
		}
	}

	private void selectQuality() {
		if (mQualityMap == null || mQualityMap.size() < 1)
			return;
		String[] qualArray = new String[mQualityMap.size()];
		for (int i = 0; i < qualArray.length; i++) {
			qualArray[i] = mQualityMap.get(i).getName();
		}
		AlertDialog.Builder mQOSDialog;
		mQOSDialog = new AlertDialog.Builder(this);
		mQOSDialog.setSingleChoiceItems(qualArray, mQuality,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if (which == mQuality)
							return;
						mQuality = which;
						mConfig.setDefaultQuality(mQualityMap.get(mQuality)
								.getType());
						isSeeking = true;
						mLoadingBar.setVisibility(View.VISIBLE);
						mQOSBtn.setText(mQualityMap.get(which).getName());
						setVideoURI((mPath = Uri.parse(mQualityMap.get(which)
								.getUrl())));
						// new QualitySync().execute();
					}
				});
		mQOSDialog.show();
	}

	// private void share() {
	// Intent intent = new Intent(Intent.ACTION_SEND);
	// intent.putExtra(Intent.EXTRA_TEXT, mPath.toString());
	// intent.setType("text/plain");
	// startActivity(Intent.createChooser(intent, "공유하기"));
	// }

	// private void showSensitiveDialog() {
	// final AlertDialog.Builder mSensitiveDialog = new AlertDialog.Builder(
	// this);
	// mSensitiveDialog.setTitle(R.string.motion_sensitive);
	// mSensitiveDialog.setMessage(getString(R.string.sensitive_message));
	// LinearLayout linear = new LinearLayout(this);
	// linear.setOrientation(LinearLayout.VERTICAL);
	// SeekBar seek = new SeekBar(this);
	// seek.setPadding(10, 10, 10, 10);
	// final TextView text = new TextView(this);
	// text.setText(mConfig.getMotionSensitive() - 300 + "");
	// text.setPadding(10, 10, 10, 10);
	// seek.setMax(700);
	// seek.setProgress(mConfig.getMotionSensitive() - 300);
	// seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	//
	// @Override
	// public void onStopTrackingTouch(SeekBar seekBar) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onStartTrackingTouch(SeekBar seekBar) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onProgressChanged(SeekBar seekBar, int progress,
	// boolean fromUser) {
	// // TODO Auto-generated method stub
	// text.setText(progress + "");
	// // mShaker.setThreshold(progress + 300);
	// mConfig.setMotionSensitive(progress + 300);
	// }
	// });
	// linear.addView(seek);
	// linear.addView(text);
	// mSensitiveDialog.setView(linear);
	// mSensitiveDialog.setPositiveButton(R.string.done, null);
	// mSensitiveDialog.show();
	// }

	private void showContinueDialog() {
		if (mConfig.getShowntime(mPath.toString()) > 1L) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("이어보기");
			dialog.setMessage("이전에 "
					+ Util.Time.stringForTime(mConfig.getShowntime(mPath.toString()))
					+ " 까지 시청하셨습니다. 계속해서 보시겠습니까?");
			dialog.setNegativeButton(R.string.cancel, null);
			dialog.setPositiveButton("이어보기",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							seekTo(mConfig.getShowntime(mPath.toString()));
						}
					});
			dialog.show();
		}
	}

	private void sendShowntime() {
		try {
			if (mPath == null)
				return;
			if (mCinepoxPath == null) {
				if ("content".equalsIgnoreCase(mPath.getScheme()))
					mConfig.setShowntime(mPath.toString(), mCurrent);
			} else {
				long play_time = System.currentTimeMillis() - mStartTime;
				String url = mConfig.getSendTimeURL() + "?order_code="
						+ mOrderCode + "&play_time=" + (play_time / 1000)
						+ "&soon_time=" + (mCurrent / 1000);
				Intent i = new Intent(
						"com.kr.busan.cw.cinepox.service.sendshowtime");
				i.putExtra("url", url);
				sendBroadcast(i);
			}
		} catch (Exception e) {

		}
	}

	public void showInstallPluginDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.alert);
		dialog.setMessage(getString(R.string.noti_plugin));
		dialog.setCancelable(false);
		dialog.setPositiveButton(R.string.next,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						String pkgname;
						if (isNeon()) {
							pkgname = VitamioInstaller.VITAMIO_PACKAGE_ARMV7_NEON;
						} else {
							pkgname = VitamioInstaller.VITAMIO_PACKAGE_ARMV7_VFPV3;
						}
						Util.App.goMarket(PlayerActivity.this, pkgname);
					}
				});
		dialog.show();
	}

	class PluginInstaller extends AsyncTask<Void, Integer, File> {

		ProgressDialog mLoading;

		PluginInstaller() {
			mLoading = new ProgressDialog(PlayerActivity.this);
			mLoading.setCancelable(false);
			mLoading.setMessage("플레이어 초기 설정 중입니다...");
		}

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
				if (file.createNewFile()) {
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
				}
				file.deleteOnExit();
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
			mLoading.dismiss();
			if (result != null) {
				// showInstallPluginDialog(Uri.fromFile(result));
			}
		}
	}

	/**
	 * 뷰 조작 관련 메서드
	 */

	private void toggleDisplayMode() {
		// TODO Auto-generated method stub
		mTimeHandler.removeMessages(1);
		switch (getVideoLayout()) {
		case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_ZOOM:
			mCenterText.setText(R.string.scale_orign);
			break;
		case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_ORIGIN:
			mCenterText.setText(R.string.scale_scale);
			break;
		case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_SCALE:
			mCenterText.setText(R.string.scale_zoom);
			break;
		case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_STRETCH:
			mCenterText.setText(R.string.scale_orign);
			break;
		case -1:
			return;
		}
		int layout = (getVideoLayout() + 1) % 3;
		setVideoLayout(layout);
		mConfig.setScreenMode(layout);
		mCenterText.setVisibility(View.VISIBLE);
		mTimeHandler.sendEmptyMessageDelayed(1, 3000);
	}

	private void showControllerAnimation() {
		isControlVisible = true;
		mTopController.startAnimation(mTopShowAni);
		mBottomController.startAnimation(mBottomShowAni);
	}

	private void hideControllerAnimation() {
		isControlVisible = false;
		mTopController.startAnimation(mTopHideAni);
		mBottomController.startAnimation(mBottomHideAni);
	}

	OnTouchListener mTouchListener = new OnTouchListener() {

		float pointX = 0.0f;
		float pointY = 0.0f;
		boolean isMoving = false;
		long distanceX = 0;
		long distanceY = 0;
		int currentVol = 0;
		int action = -1;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if (isMoving) {
					if (!(distanceX < 1000l && distanceX > -1000l)
							&& action == 0) {
						seekTo(mCurrent + distanceX < 0 ? 0 : mCurrent
								+ distanceX);
						start();
					}
					mVolumeControl.setVisibility(View.GONE);
					mTimeHandler.removeMessages(1);
					mTimeHandler.sendEmptyMessageDelayed(1, 3000);
					distanceX = 0;
					distanceY = 0;
					pointX = 0.0f;
					pointY = 0.0f;
					action = -1;
					isMoving = false;
					return true;
				}
				if (isControlVisible) {
					hideControllerAnimation();
				} else {
					showControllerAnimation();
					showController();
				}
				return true;
			case MotionEvent.ACTION_DOWN:
				currentVol = mAudioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				pointX = event.getX();
				pointY = event.getY();
				return true;
			case MotionEvent.ACTION_MOVE:
				showController();
				isMoving = true;
				float currentX = event.getX();
				float currentY = event.getY();
				distanceX = (long) (currentX - pointX) * 20;
				distanceY = (long) -(currentY - pointY) / 20;
				if (!(distanceX < 2000l && distanceX > -2000l)) {
					if (action == -1)
						action = 0;
					if (action == 0) {
						if (isPlaying())
							pause();
						long time = mCurrent + distanceX > 0 ? mCurrent
								+ distanceX : 0;
						if (time > mDuration)
							return true;
						mCenterText.setText(Util.Time.stringForTime(time));
						mCurrentText.setText(Util.Time.stringForTime(time));
						mSeekBar.setProgress(Util.Math.getPercent(time,
								mDuration));
						mTimeHandler.removeMessages(1);
						mCenterText.setVisibility(View.VISIBLE);
						mTimeHandler.sendEmptyMessageDelayed(1, 3000);
					}
				} else if (!(distanceY < 2 && distanceY > -2)) {
					if (action == -1)
						action = 1;
					if (action == 1) {
						mAudioManager.setStreamVolume(
								AudioManager.STREAM_MUSIC, currentVol
										+ (int) distanceY,
								AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
						mVolumeSeekbar.setProgress(mAudioManager
								.getStreamVolume(AudioManager.STREAM_MUSIC));
						mVolumeText.setText(mAudioManager
								.getStreamVolume(AudioManager.STREAM_MUSIC)
								+ "");
						mVolumeControl.setVisibility(View.VISIBLE);
						mCenterText.setVisibility(View.GONE);
					}
				}

				return true;
			default:
				return false;
			}
		}
	};

	OnKeyListener mKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_BACK)
				onBackPressed();
			return false;
		}
	};

	private void allocController() {
		LinearLayout linear = new LinearLayout(this);
		mController = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.mediacontroller, null);
		linear.setOnTouchListener(mTouchListener);
		linear.setOnKeyListener(mKeyListener);
		mFullScreenBtn = (ImageButton) mController
				.findViewById(R.id.btn_video_fullscreen);
		mSettingBtn = (ImageButton) mController
				.findViewById(R.id.btn_video_setting);
		mCCBtn = (ImageButton) mController.findViewById(R.id.btn_video_caption);
		mPlaypauseBtn = (ImageButton) mController
				.findViewById(R.id.btn_video_playpause);
		mVolumnBtn = (ImageButton) mController
				.findViewById(R.id.btn_video_volumn);
		mBrightBtn = (ImageButton) mController
				.findViewById(R.id.btn_video_bright);
		mCodecBtn = (TextView) mController
				.findViewById(R.id.btn_video_changecodec);
		// mBTBtn = (ImageButton) mController
		// .findViewById(R.id.btn_video_bluetooth);
		mTimeText = (TextView) mController
				.findViewById(R.id.textView_video_time);
		// mSpeedText = (TextView) mController
		// .findViewById(R.id.textView_video_speed);
		mBattText = (TextView) mController
				.findViewById(R.id.textView_video_battstat);
		mSeekBar = (SeekBar) mController
				.findViewById(R.id.seekBar_video_controller);
		mCurrentText = (TextView) mController
				.findViewById(R.id.textView_video_currenttime);
		mDurationText = (TextView) mController
				.findViewById(R.id.textView_video_duration);
		mQOSBtn = (TextView) mController
				.findViewById(R.id.btn_video_changequality);
		// mReadQRBtn = (ImageButton) mController
		// .findViewById(R.id.btn_video_readqrcode);
		mTopController = (LinearLayout) mController
				.findViewById(R.id.video_controller_top);
		mBottomController = (LinearLayout) mController
				.findViewById(R.id.video_controller_bottom);
		// hideController();
		linear.addView(mController);
		mWindow = linear;
	}

	private void builtInController() {
		mWindowManager.addView(mWindow, getControllerWindowParams());
	}

	WindowManager.LayoutParams getControllerWindowParams() {
		WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
		wlp.height = LayoutParams.FILL_PARENT;
		wlp.width = LayoutParams.FILL_PARENT;
		wlp.format = PixelFormat.RGBA_8888;
		wlp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		return wlp;
	}

	private void addSWVideo() {
		if (checkPlugin()) {
			if (mVideoView == null) {
				mVideoView = new SWVideoView(this);
				mVideoView.setOnPreparedListener(this);
				((RelativeLayout) findViewById(R.id.video_container)).addView(
						mVideoView, LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
			}
		}
	}

	private void allocView() {
		mHWVideoView = (HWVideoView) findViewById(R.id.surfaceView_video_view_hw);
		mHWVideoView.setOnPreparedListener(this);
		mLoadingBar = (AnimatedImageView) findViewById(R.id.progressBar_video_loading);
		mCaptionText = (TextView) findViewById(R.id.textView_video_caption);
		// mMoveFirstBtn = (Button)
		// findViewById(R.id.btn_video_movetofirst);
		// mBookmarkBtn = (Button) findViewById(R.id.btn_video_bookmark);
		// mShareBtn = (Button) findViewById(R.id.btn_video_share);
		mCenterText = (TextView) findViewById(R.id.textView_video_center);
		mVolumeControl = (RelativeLayout) findViewById(R.id.layout_player_volumecontrol);
		mVolumeSeekbar = (VerticalProgressBar) findViewById(R.id.seekbar_player_volume);
		mVolumeText = (TextView) findViewById(R.id.textView_player_volume);
		mVolumeSeekbar.setMax(mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		mVolumeSeekbar.setProgress(mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC));
		mVolumeText.setText(mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC) + "");
	}

	private void setAllListener() {
		// mBTBtn.setOnClickListener(this);
		mSeekBar.setOnSeekBarChangeListener(this);
		mPlaypauseBtn.setOnClickListener(this);
		mCCBtn.setOnClickListener(this);
		mSettingBtn.setOnClickListener(this);
		mFullScreenBtn.setOnClickListener(this);
		mCodecBtn.setOnClickListener(this);
		mQOSBtn.setOnClickListener(this);
		// mReadQRBtn.setOnClickListener(this);
		// mShareBtn.setOnClickListener(this);
		// mMoveFirstBtn.setOnClickListener(this);
		// mBookmarkBtn.setOnClickListener(this);
		mVolumnBtn.setOnClickListener(this);
		mBrightBtn.setOnClickListener(this);
	}

	private void refreshView() {
		initBattStat();
		// if (!isUseCC)
		// mCCBtn.setVisibility(View.GONE);
		// else
		// mCCBtn.setVisibility(View.VISIBLE);
		mCaptionText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
				(mConfig.getTextsize() + 1) * 10);
		mCaptionText.setTextColor(mConfig.getCCColor());
		setVideoLayout(mConfig.getScreenMode());
	}

	private void initQuality() {
		if (mQualityMap != null) {
			if (mConfig.getCinepoxUrl() != null) {
				String ctype = mCinepoxPath.getQueryParameter("ctype");
				l.i("ctype = " + ctype);
				if (ctype != null) {
					for (int i = 0; i < mQualityMap.size(); i++) {
						if (ctype
								.equalsIgnoreCase(mQualityMap.get(i).getType())) {
							mQuality = i;
						}
					}
				} else {
					if (mQualityMap.size() > 1)
						mQuality = mQualityMap.size() / 2;
					else if (mQualityMap.size() > 2)
						mQuality = mQualityMap.size() / 2 + 1;
					else {
						mQuality = 0;
					}

					for (int i = 0; i < mQualityMap.size(); i++) {
						if (mConfig.getDefaultQuality().equalsIgnoreCase(
								mQualityMap.get(i).getType())) {
							mQuality = i;
							mConfig.setDefaultQuality(mQualityMap.get(mQuality)
									.getType());
						}
					}
				}
			}
			mQOSBtn.setVisibility(View.VISIBLE);
			mQOSBtn.setText(mQualityMap.get(mQuality).getName());
			mPath = Uri.parse(mQualityMap.get(mQuality).getUrl());
		} else {
			mQOSBtn.setVisibility(View.GONE);
			mQuality = 0;
		}
		setVideoURI(mPath);
	}

	void setVideoLayout(int layout) {
		mHWVideoView.setVideoLayout(layout);
		mConfig.setScreenMode(layout);
		if (checkPlugin())
			mVideoView.setVideoLayout(layout);
	}

	int getVideoLayout() {
		switch (mCodec) {
		case SW_CODEC:
			if (checkPlugin())
				return mVideoView.getVideoLayout();
			else
				return 0;
		case HW_CODEC:
			return mHWVideoView.getVideoLayout();
		default:
			return 0;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		showController();
		if (v.getId() == R.id.btn_video_volumn) {
			showVolumeControl();
		} else if (v.getId() == R.id.btn_video_bright) {
			showBrightnessControl();
		} else if (v.getId() == R.id.btn_video_playpause) {
			togglePlayPause();
		} else if (v.getId() == R.id.btn_video_fullscreen) {
			toggleDisplayMode();
		} else if (v.getId() == R.id.btn_video_caption) {
			if (isUseCC)
				selectLang();
		} else if (v.getId() == R.id.btn_video_setting) {
			showSetting();
		} else if (v.getId() == R.id.btn_video_changecodec) {
			showCodecChange();
		} else if (v.getId() == R.id.btn_video_changequality) {
			selectQuality();
		}
		// else if (v.getId() == R.id.btn_video_bluetooth) {
		// motionShare();
		// }
	}

	private void motionShare() {
		// TODO Auto-generated method stub
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
		pause();
		isShareing = true;
		// mShaker.resume();
		mProgress.setCancelable(false);
		mProgress.setMessage(getString(R.string.noti_readymotion));
		mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				// mShaker.pause();
			}
		});
		mProgress.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				// mShaker.pause();
			}
		});
		mProgress.show();
	}

	private void showLocSettingDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
		mDialog.setMessage(R.string.noti_locservice);
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

	/**
	 * 컨트롤러 상태 출력 관련 메서드
	 */

	private void updateTime() {
		int hour = mCal.get(Calendar.HOUR_OF_DAY);
		int min = mCal.get(Calendar.MINUTE);
		StringBuilder mFormatBuilder = new StringBuilder();
		Formatter mFormatter = new Formatter(mFormatBuilder,
				Locale.getDefault());
		mTimeText.setText(mFormatter.format("%02d:%02d", hour, min).toString());
	}

	private void updateBatt() {
		try {
			mBattText.setText(Util.Phone.getBattStat(this) + "%");
		} catch (ArithmeticException e) {
			e.printStackTrace();
		}
	}

	private void initBattStat() {
		updateBatt();
	}

	private void showCodecChange() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.change_codec);
		dialog.setSingleChoiceItems(new String[] {
				getString(R.string.hw_codec), getString(R.string.sw_codec) },
				mCodec, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if (which == 1 && !checkPlugin()) {
							// 플러그인 설치 고고
							showInstallPluginDialog();
						} else if (mCodec != which) {
							pause();
							codecChange(which);
						}
					}
				});
		dialog.show();
	}

	boolean checkPlugin() {
		try {
			VitamioInstaller.checkVitamioInstallation(this);
			return true;
		} catch (VitamioNotCompatibleException e) {
			e.printStackTrace();
			return false;
		} catch (VitamioNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	void codecChange(int which) {
		// TODO Auto-generated method stub
		if (which < 0 || which > 1)
			return;
		addSWVideo();
		mCodec = which;
		mConfig.setDefaultCodec(which);
		mLoadingBar.setVisibility(View.VISIBLE);
		setVideoURI();
	}

	private void showBrightnessControl() {
		Intent it = new Intent(this, BrightActivity.class);
		it.putExtra("bright", getWindow().getAttributes().screenBrightness);
		startActivityForResult(it, REQUEST_MODIFY_BRIGHTNESS);
	}

	private void showVolumeControl() {
		startActivity(new Intent(this, VolumeActivity.class));
	}

	private void showSetting() {
		startActivity(new Intent(this, SettingActivity.class));
	}

	private void showController() {
		showController(3000);
	}

	private void showController(int time) {
		try {
			mBottomController.setVisibility(View.VISIBLE);
			mTopController.setVisibility(View.VISIBLE);
			isControlVisible = true;
			mTimeHandler.removeMessages(3);
			mTimeHandler.removeMessages(4);
			mTimeHandler.sendEmptyMessageDelayed(3, time);
		} catch (Exception e) {
		}
	}

	private void hideController() {
		try {
			mTimeHandler.removeMessages(4);
			mBottomController.setVisibility(View.GONE);
			mTopController.setVisibility(View.GONE);
			isControlVisible = false;
			mTimeHandler.sendEmptyMessageDelayed(4, 2000);
		} catch (Exception e) {
		}
	}

	/**
	 * 재생 관련 컨트롤 메서드
	 */

	private void setVideoURI() {
		setVideoURI(mPath);
	}

	void setOnSystemUiVisibilityChangeListener() {
		if (Build.VERSION.SDK_INT >= 11) {
			OnSystemUiVisibilityChangeListener listen = new OnSystemUiVisibilityChangeListener() {
				boolean flag = true;

				@Override
				public void onSystemUiVisibilityChange(int visibility) {
					// TODO Auto-generated method stub
					mCenterText.setVisibility(View.GONE);
					switch (visibility) {
					case View.SYSTEM_UI_FLAG_VISIBLE:
						if (flag) {
							showControllerAnimation();
							mTimeHandler.removeMessages(3);
							mTimeHandler.removeMessages(4);
							mTimeHandler.sendEmptyMessageDelayed(3, 3000);
							mBottomController.setVisibility(View.VISIBLE);
							mTopController.setVisibility(View.VISIBLE);
							flag = false;
						}
						break;
					default:
						flag = true;
						break;
					}
				}
			};
			switch (mCodec) {
			case HW_CODEC:
				mHWVideoView.setOnSystemUiVisibilityChangeListener(listen);
				break;
			case SW_CODEC:
				if (checkPlugin())
					mVideoView.setOnSystemUiVisibilityChangeListener(listen);
				break;
			}
		}
	}

	private void setVideoURI(Uri uri) {
		try {
			switch (mCodec) {
			case HW_CODEC:
				mHWVideoView.setOnBufferingUpdateListener(this);
				mHWVideoView.setOnCompletionListener(this);
				mHWVideoView.setOnErrorListener(this);
				mHWVideoView.setOnInfoListener(this);
				mHWVideoView.setOnSeekCompleteListener(this);
				setOnSystemUiVisibilityChangeListener();
				mHWVideoView.setVisibility(View.VISIBLE);
				mHWVideoView.setVideoURI((mPath = uri));
				mCodecBtn.setText("H/W");
				if (checkPlugin())
					mVideoView.setVisibility(View.GONE);
				break;
			case SW_CODEC:
				mHWVideoView.setVisibility(View.GONE);
				setOnSystemUiVisibilityChangeListener();
				if (checkPlugin()) {
					mCodecBtn.setText("S/W");
					mVideoView.setOnBufferingUpdateListener(this);
					mVideoView.setOnCompletionListener(this);
					mVideoView.setOnErrorListener(this);
					mVideoView.setOnInfoListener(this);
					mVideoView.setOnSeekCompleteListener(this);
					mVideoView.setVisibility(View.VISIBLE);
					mVideoView.setVideoURI((mPath = uri));
				}
				break;
			}
		} catch (IllegalStateException e) {
			Toast.makeText(this, "처리할 수 없는 상태입니다. 잠시후에 시도해 주세요.",
					Toast.LENGTH_SHORT).show();
		}
	}

	boolean isPlaying() {
		try {
			switch (mCodec) {
			case HW_CODEC:
				return mHWVideoView.isPlaying();
			case SW_CODEC:
				if (checkPlugin())
					return mVideoView.isPlaying();
				else
					return false;
			default:
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public void start() {
		if (!isPlaying()) {
			mPlaypauseBtn.setImageResource(R.drawable.bt_stop);
			switch (mCodec) {
			case HW_CODEC:
				mHWVideoView.start();
				break;
			case SW_CODEC:
				if (checkPlugin())
					mVideoView.start();
				break;
			default:
				break;
			}
			mTimeHandler.removeMessages(1);
			mCenterText.setText(R.string.play);
			mCenterText.setVisibility(View.VISIBLE);
			mTimeHandler.sendEmptyMessageDelayed(1, 3000);
		}
	}

	long getDuration() {
		switch (mCodec) {
		case HW_CODEC:
			return mHWVideoView.getDuration();
		case SW_CODEC:
			if (checkPlugin())
				return mVideoView.getDuration();
			else
				return 0l;
		default:
			return 0l;
		}
	}

	long getCurrentPosition() {
		switch (mCodec) {
		case HW_CODEC:
			return mHWVideoView.getCurrentPosition();
		case SW_CODEC:
			if (checkPlugin())
				return mVideoView.getCurrentPosition();
			else
				return 0l;
		default:
			return 0l;
		}
	}

	public void pause() {
		if (isPlaying()) {
			switch (mCodec) {
			case HW_CODEC:
				mCurrent = mHWVideoView.getCurrentPosition();
				mHWVideoView.pause();
				break;
			case SW_CODEC:
				if (checkPlugin()) {
					mCurrent = mVideoView.getCurrentPosition();
					mVideoView.pause();
				}
				break;
			default:
				break;
			}
			mPlaypauseBtn.setImageResource(R.drawable.bt_play);
			mTimeHandler.removeMessages(1);
			mCenterText.setText(R.string.pause);
			mCenterText.setVisibility(View.VISIBLE);
			mTimeHandler.sendEmptyMessageDelayed(1, 3000);
		}
	}

	public void togglePlayPause() {
		if (isPlaying()) {
			pause();
		} else {
			start();
		}
	}

	private synchronized void seekTo(long msec) {
		isSeeking = true;
		switch (mCodec) {
		case SW_CODEC:
			if (checkPlugin())
				mVideoView.seekTo((mCurrent = msec));
			break;
		case HW_CODEC:
			mHWVideoView.seekTo((int) (mCurrent = msec));
			break;
		}
	}

	/**
	 * 재생구간 조정 관련 탐색바 인터페이스
	 */

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		mCurrentText
				.setText(Util.Time.stringForTime(getDuration() / 100 * progress));
		if (fromUser) {
			isSeeking = true;
			mTimeHandler.removeMessages(1);
			mCenterText.setText(Util.Time.stringForTime(getDuration() / 100
					* progress));
			mCenterText.setVisibility(View.VISIBLE);
			mTimeHandler.sendEmptyMessageDelayed(1, 3000);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		showController(360000);
		isTracking = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		isTracking = false;
		showController();
		mCenterText.setVisibility(View.GONE);
		mCurrent = getDuration() / 100 * seekBar.getProgress();
		seekTo(mCurrent);
	}

	/**
	 * 비디오 상태변화 관련 인터페이스 (SW)
	 */

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return onInfo(what, extra);
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub
		onSeekComplete();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		l.i("vitamio prepared");
		onPrepared();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		onCompletion();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		onBufferingUpdate(percent);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return onError(what, extra);
	}

	/**
	 * 비디오 상태변화 관련 인터페이스 (HW)
	 */

	@Override
	public boolean onInfo(android.media.MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return onInfo(what, extra);
	}

	@Override
	public void onSeekComplete(android.media.MediaPlayer mp) {
		// TODO Auto-generated method stub
		onSeekComplete();
	}

	@Override
	public void onPrepared(android.media.MediaPlayer mp) {
		// TODO Auto-generated method stub
		l.i("native prepared");
		onPrepared();
	}

	@Override
	public void onCompletion(android.media.MediaPlayer mp) {
		// TODO Auto-generated method stub
		onCompletion();
	}

	@Override
	public void onBufferingUpdate(android.media.MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		onBufferingUpdate(percent);
	}

	@Override
	public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return onError(what, extra);
	}

	/*
	 * 비디오 상태변화 관련 인터페이스 (통합)
	 */

	void onPrepared() {
		if (mWindow == null)
			builtInController();
		else
			addControllerWindow();
		mLoadingBar.setVisibility(View.GONE);
		mDuration = getDuration();
		mDurationText.setText(Util.Time.stringForTime(mDuration));
		if (mDuration / 1000 <= mCurrent / 1000)
			mCurrent = 0;
		if (mCurrent > 0)
			seekTo(mCurrent);
		start();
		mSeekBar.setProgress(0);
		if (!mTimeSync.isAlive())
			mTimeSync.start();
		switch (mCodec) {
		case SW_CODEC:
			mHWVideoView.setOnBufferingUpdateListener(null);
			mHWVideoView.setOnCompletionListener(null);
			mHWVideoView.setOnErrorListener(null);
			mHWVideoView.setOnInfoListener(null);
			mHWVideoView.setOnSeekCompleteListener(null);
			mHWVideoView.setVisibility(View.GONE);
			setOnSystemUiVisibilityChangeListener();
			if (checkPlugin()) {
				mVideoView.setOnBufferingUpdateListener(this);
				mVideoView.setOnCompletionListener(this);
				mVideoView.setOnErrorListener(this);
				mVideoView.setOnInfoListener(this);
				mVideoView.setOnSeekCompleteListener(this);
				mVideoView.setVisibility(View.VISIBLE);
			}
			break;
		case HW_CODEC:
			if (checkPlugin()) {
				mVideoView.setOnBufferingUpdateListener(null);
				mVideoView.setOnCompletionListener(null);
				mVideoView.setOnErrorListener(null);
				mVideoView.setOnInfoListener(null);
				mVideoView.setOnSeekCompleteListener(null);
				mVideoView.setVisibility(View.GONE);
			}
			mHWVideoView.setOnBufferingUpdateListener(this);
			mHWVideoView.setOnCompletionListener(this);
			mHWVideoView.setOnErrorListener(this);
			mHWVideoView.setOnInfoListener(this);
			mHWVideoView.setOnSeekCompleteListener(this);
			mHWVideoView.setVisibility(View.VISIBLE);
			setOnSystemUiVisibilityChangeListener();
			break;
		}
	}

	boolean onInfo(int what, int extra) {
		if (mCodec == HW_CODEC) {
			switch (what) {
			case 703:
			case 701:
				mLoadingBar.setVisibility(View.VISIBLE);
				break;
			case 702:
				mLoadingBar.setVisibility(View.GONE);
				break;
			}
		} else {

		}
		return false;
	}

	void onSeekComplete() {
		mBuffer = 0;
		mLoadingBar.setVisibility(View.GONE);
		start();
		mTimeHandler.sendEmptyMessageDelayed(5, 1000);
	}

	void onCompletion() {
		Toast.makeText(this, R.string.playback_completed, Toast.LENGTH_SHORT)
				.show();
		finish();
	}

	@Override
	public void finish() {
		sendShowntime();
		super.finish();
	}

	void onBufferingUpdate(int percent) {
		if (percent > mBuffer)
			mSeekBar.setSecondaryProgress(mBuffer = percent);
		if (percent == 99)
			mSeekBar.setSecondaryProgress(100);
	}

	boolean onError(int what, int extra) {
		switch (what) {
		case 1:
			switch (extra) {
			case -1094995529:
			case -2147483648: // KOMI
			case -17:
			case -1:
				showErrorDialog("죄송합니다. 재생할 수 없는 동영상입니다.", false, true);
				break;
			default:
				Intent i = new Intent(this, PlayerActivity.class);
				i.setData(getIntent().getData());
				i.putExtra("set_time", mCurrent / 1000);
				i.putExtra("error_count", mErrorCount++);
				startActivity(i);
				finish();
				PlayerConfig.sendErrorLog(this, mBugReportUrl,
						new IllegalStateException(getIntent().getDataString()
								+ " : " + extra));
				break;
			}
			break;
		case 100:
			setVideoURI();
			break;
		case 200:
			break;
		}
		return true;
	}

	void showErrorDialog(final String message, boolean cancelable,
			final boolean isCritical) {
		removeControllerWindow();
		Util.Views.showAlert(PlayerActivity.this, getString(R.string.alert),
				message, getString(R.string.done),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if (isCritical)
							finish();
					}
				}, cancelable);
	}

	void removeControllerWindow() {
		try {
			if (mWindow != null)
				mWindowManager.removeView(mWindow);
		} catch (IllegalArgumentException e) {

		}
	}

	void addControllerWindow() {
		try {
			if (mWindow != null)
				mWindowManager.addView(mWindow, getControllerWindowParams());
		} catch (Exception e) {

		}
	}

	public String parseInfo(int code, JSONObject o) {
		try {
			if ("N".equalsIgnoreCase(o.getString(KEY_RESULT))) {
				String msg = o.getString(KEY_MSG);
				return msg;
			}
			switch (code) {
			case REQ_GET_CONTENTS:
				try {
					JSONObject movie_list = o.getJSONArray(KEY_MOVIE_LIST)
							.getJSONObject(0);
					String title = movie_list.getString(KEY_NAME);
					String lang = movie_list.getString(KEY_LANG);
					// String lang =
					// "http://adappstore.co.kr/AD/banner/Black.Ransom.2010.BDRip.x264.AC3-Zoo.smi";
					mTitle = title;
					if (lang == null || "".equalsIgnoreCase(lang))
						isUseCC = false;
					mCaptionUrl = lang;
					if (mCaptionUrl != null
							|| !"".equalsIgnoreCase(mCaptionUrl)) {
						if (captionParse(mCaptionUrl))
							isUseCC = true;
						else
							isUseCC = false;
					}
					JSONArray quality = movie_list.getJSONArray(KEY_QUALITY);
					mQualityMap = new ArrayList<QualityData>();
					for (int i = 0; i < quality.length(); i++) {
						JSONObject qual = quality.getJSONObject(i);
						String type = qual.getString(KEY_TYPE);
						String name = qual.getString(KEY_NAME);
						String url = qual.getString(KEY_URL);
						String key = qual.getString("key");
						QualityData data = new QualityData(name, type, url, key);
						mQualityMap.add(data);
					}
					JSONObject urlArray = o.getJSONObject(KEY_URL);
					mTextBannerUrl = urlArray
							.getString(KEY_GET_TEXT_BANNER_URL);
					mADUrl = urlArray.getString(KEY_GET_AD_URL);
					mBookmarkListUrl = urlArray
							.getString(KEY_GET_BOOKMARK_LIST);
					mBookmarkInsertUrl = urlArray
							.getString(KEY_BOOKMARK_INSERT_URL);
					mBookmarkDeleteUrl = urlArray
							.getString(KEY_BOOKMARK_DELETE_URL);
					mBugReportUrl = urlArray.getString(KEY_BUG_INSERT_URL);
					mLogReportUrl = urlArray.getString(KEY_LOG_URL);
					mPlaytimeUrl = urlArray.getString("play_time_url");
					mConfig.setSendTimeURL(mPlaytimeUrl);

					m3GMessage = urlArray.getString("3g_message");
					m3GMessageLength = urlArray.getInt("3g_message_length");

				} catch (JSONException e) {
					isCriticalError = true;
					e.printStackTrace();
				}
				break;
			/**
			 * 2차개발분
			 */
			// case REQ_AD_VIEW_CHECK:
			// break;
			// case REQ_GET_AD_LIST:
			// break;
			// case REQ_AD_CLICK_CHECK:
			// JSONArray config_list = o.getJSONArray(KEY_CONFIG);
			// for (int i = 0; i < config_list.length(); i++) {
			// JSONObject config = config_list.getJSONObject(i);
			// String num = config.getString(KEY_NUM);
			// String is_start_view = config.getString(KEY_START_VIEW);
			// String next_time = config.getString(KEY_NEXT_TIME);
			// String view_check_url = config
			// .getString(KEY_VIEW_CHECK_URL);
			// String click_url = config.getString(KEY_CLICK_CHECK_URL);
			// }
			// JSONArray ad_list = o.getJSONArray(KEY_LIST);
			// for (int i = 0; i < ad_list.length(); i++) {
			// JSONObject ad = ad_list.getJSONObject(i);
			// String ad_seq = ad.getString(KEY_AD_NUM);
			// String type = ad.getString(KEY_TYPE);
			// String show_time = ad.getString(KEY_SHOW_TIME);
			// String title = ad.getString(KEY_TITLE);
			// String url = ad.getString(KEY_URL);
			// }
			// break;
			// case REQ_SEND_LOG:
			// break;
			// case REQ_REPORT_BUG:
			// break;
			// case REQ_SEND_BOOKMARK:
			// String bookmark_num = o.getString(KEY_BOOKMARK_NUM);
			// String movieProduct_num = o.getString(KEY_MOVIE_NUM);
			// String member_seq = o.getString(KEY_MEMBER_NUM);
			// String is_public = o.getString(KEY_BOOKMARK_PUBLIC);
			// String memo = o.getString(KEY_MEMO);
			// String sn_image = o.getString(KEY_THUMBIMG);
			// String bookmark_time = o.getString(KEY_BOOKMARK_TIME);
			// String regdate = o.getString(KEY_BOOKMARK_DATE);
			// break;
			// case REQ_DELETE_BOOKMARK:
			// break;
			// case REQ_GET_BOOKMARK:
			// for (int i = 0; i < o.getJSONArray(KEY_LIST).length(); i++) {
			// JSONObject list_item = o.getJSONArray(KEY_LIST)
			// .getJSONObject(i);
			// String bookmark_seq = list_item.getString(KEY_BOOKMARK_NUM);
			// String movie_num = list_item.getString(KEY_BOOKMARK_NUM);
			// String member_num = list_item.getString(KEY_MEMBER_NUM);
			// String is_pub = list_item.getString(KEY_BOOKMARK_PUBLIC);
			// String memoo = list_item.getString(KEY_MEMO);
			// String snimage = list_item.getString(KEY_THUMBIMG);
			// String bookmark_timee = list_item
			// .getString(KEY_BOOKMARK_TIME);
			// String regdatee = list_item.getString(KEY_BOOKMARK_DATE);
			// }
			// break;
			// case REQ_TEXT_AD_VIEW_CHECK:
			// break;
			// case REQ_GET_TEXT_AD_LIST:
			// JSONArray conf_array = o.getJSONArray(KEY_CONFIG);
			// for (int i = 0; i < conf_array.length(); i++) {
			// JSONObject conf_item = conf_array.getJSONObject(i);
			// String view_time = conf_item.getString(KEY_VIEW_TIME);
			// String interval_time = conf_item.getString(KEY_INTERVAL);
			// String view_check = conf_item.getString(KEY_VIEW_CHECK_URL);
			// String click_check = conf_item
			// .getString(KEY_CLICK_CHECK_URL);
			// }
			// JSONArray listt = o.getJSONArray(KEY_LIST);
			// for (int i = 0; i < listt.length(); i++) {
			// JSONObject list_item = listt.getJSONObject(i);
			// String text_ad_num = list_item.getString(KEY_TEXT_AD_NUM);
			// String title = list_item.getString(KEY_TITLE);
			// String memooo = list_item.getString(KEY_MEMO);
			// }
			// break;
			// case REQ_TEXT_AD_CLICK_CHECK:
			// break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			new LogPost(this, getPackageName(), e).start();
			Util.Views.showAlert(this, null, e.getMessage(), null, null, false);
			return "error : " + e.getMessage();
		}
		return "success";
	}

	boolean isMovieUrl() {
		return isMovieUrl(mPath);
	}

	boolean isMovieUrl(Uri uri) {
		if ("cinepox".equalsIgnoreCase(uri.getScheme())) {
			return false;
		}
		return true;
	}

	public class UrlShareSendThread extends ShareThread {

		public UrlShareSendThread(Location loc) {
			super(loc);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			isShareing = true;
			mProgress.setCancelable(true);
			mProgress.setMessage(getString(R.string.sending_video));
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			l.i("send start");
			try {
				String shake_key = generateShakeKey();
				String url = params[0];
				// deleteRequest(shake_key);
				long startTime = System.currentTimeMillis();
				long currentTime = 0l;
				boolean result = false;
				while (currentTime - 5000 < startTime) {
					try {
						JSONObject js = getJson(String.format(REQUEST_URL,
								shake_key, URLEncoder.encode(url, HTTP.UTF_8)));
						String resultText = js.getString("result");
						String is_response = js.getString("is_response");
						result = "Y".equalsIgnoreCase(is_response)
								&& "Y".equalsIgnoreCase(resultText);
						if (result)
							break;
						Thread.sleep(500);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					currentTime = System.currentTimeMillis();
				}

				if (result) {
					deleteRequest(shake_key);
				} else {
					return "error";
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
			resume();
			if ("error".equals(result)) {
				Toast.makeText(PlayerActivity.this, R.string.send_failed,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(PlayerActivity.this, R.string.send_success,
						Toast.LENGTH_SHORT).show();
			}
			l.i("send complete");
		}
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

	@Override
	public void onShake() {
		// TODO Auto-generated method stub
		new UrlShareSendThread(mLocation).execute(mConfig.getCinepoxUrl());
		// mShaker.pause();
	}

	private Thread mTimeSync = new Thread() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					mTimeHandler.sendEmptyMessage(0);
					Thread.sleep(600);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					return;
				}
			}
		}
	};

	private Handler mTimeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (!isSeeking && isPlaying() && !isTracking) {
					try {
						if (mCinepoxPath != null) {
							String url = "cinepox://play?set_time=%s&order_code=%s&productInfo_seq=%s&member_seq=%s";
							mConfig.setCinepoxUrl(url = String.format(url,
									mCurrent / 1000, mOrderCode, mMovieSeq,
									mMemberSeq));
						}
						updatePlayInfo();
						updateProgress();
						updateCaption();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case 1:
				mCenterText.setVisibility(View.GONE);
				break;
			case 2:
				isBack = false;
				break;
			case 3:
				hideControllerAnimation();
				break;
			case 4:
				hideSystemUi();
				break;
			case 5:
				isSeeking = false;
				break;
			}
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if ("set".equalsIgnoreCase(intent.getAction())) {
				refreshView();
			} else if (Intent.ACTION_BATTERY_CHANGED.equalsIgnoreCase(intent
					.getAction())) {
				updateBatt();
			} else if (Intent.ACTION_TIME_TICK.equalsIgnoreCase(intent
					.getAction())) {
				updateTime();
			}
		}
	};

	class PrepareSync extends AsyncTask<String, Long, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			hideController();
			mLoadingBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			mPath = getIntent().getData();
			l.i("path : " + mPath);
			if (isMovieUrl(mPath)) {
				if ("content".equalsIgnoreCase(mPath.getScheme())) {
					String filePath = Util.File.getMediaPathfromUri(
							PlayerActivity.this, mPath);
					l.i(Util.File.changeExtension(filePath, "smi"));
					if (captionParse(Util.File.changeExtension(filePath, "smi"))
							|| captionParse(Util.File.changeExtension(filePath,
									"srt")))
						isUseCC = true;
				}
				return "movie";
			}
			try {
				mCinepoxPath = mPath;
				mConfig.setCinepoxUrl(mCinepoxPath.toString());
				String set_url = mPath.getQueryParameter("set_url");
				String set_time = mPath.getQueryParameter("set_time");
				mOrderCode = mPath.getQueryParameter("order_code");

				if (getIntent().getLongExtra("set_time", 0l) > 0l)
					mCurrent = getIntent().getLongExtra("set_time", 0l) * 1000l;
				else if (set_time != null)
					mCurrent = Long.parseLong(set_time) * 1000l;

				if (set_url != null)
					mPath = Uri.parse(set_url);

				String movie_num = mPath.getQueryParameter(KEY_MOVIE_NUM);
				String member_seq = mPath.getQueryParameter(KEY_MEMBER_NUM);
				if (movie_num != null || member_seq != null) {
					mMovieSeq = movie_num;
					mMemberSeq = member_seq;
					String url = PlayerConfig.Domain + "player/getContents/";
					ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
					param.add(new BasicNameValuePair(KEY_MOVIE_NUM, movie_num));
					param.add(new BasicNameValuePair(KEY_MEMBER_NUM, member_seq));
					param.add(new BasicNameValuePair("setting",
							"response_type:json"));
					param.add(new BasicNameValuePair("p_type", "http"));
					param.add(new BasicNameValuePair("device_type", "android"));
					param.add(new BasicNameValuePair("order_code", mOrderCode));
					JSONObject config = Util.Stream.jsonFromURLbyPOST(url,
							param);
					return parseInfo(REQ_GET_CONTENTS, config);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "error : " + e.getMessage();
			}
			return "success";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			// mDownThread = new DownThread();
			// mSpeedThread = new DownloadSpeedThread();
			// mDownThread.start();
			// mSpeedThread.start();

			if (result.contains("error")) { // 로딩중 에러 발생시
				l.i("player error");
				boolean cancelable = false;
				showErrorDialog(getString(R.string.error_config), cancelable,
						true);
			} else if ("movie".equalsIgnoreCase(result)) { // 일반 동영상 파일
															// 로딩시(스트리밍, 로컬)
				l.i("player movie");
				init();
				if ("content".equalsIgnoreCase(mPath.getScheme())) {
					showContinueDialog();
				}
				mQOSBtn.setVisibility(View.GONE);
				// mCCBtn.setVisibility(View.GONE);
				// mBTBtn.setVisibility(View.GONE);
			} else if ("success".equalsIgnoreCase(result)) { // 씨네폭스 영상 로딩시
				l.i("player success");
				init();
				// if (isUseCC)
				// mCaptionText.setVisibility(View.VISIBLE);
				// else
				// mCCBtn.setVisibility(View.GONE);
				if (Util.Connection.getType(PlayerActivity.this) != ConnectivityManager.TYPE_WIFI) {
					if (m3GMessage != null)
						Toast.makeText(PlayerActivity.this, m3GMessage,
								m3GMessageLength).show();
				}
			} else {
				showErrorDialog(result, false, true);
			}
		}
	}

	class UpdateSync extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			hideController();
			mLoadingBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = PlayerConfig.Domain + "player/mobUpdateCheck";
			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("setting", "response_type:json"));
			param.add(new BasicNameValuePair("device_type", "android"));
			try {
				return Util.Stream.stringFromURLbyPOST(url, param);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mLoadingBar.setVisibility(View.GONE);
			if (result != null) {
				try {
					final JSONObject json = new JSONObject(result);
					float updateVer = Float.valueOf(json.getString("ver"));
					float currentVer = Util.App
							.getVersionNum(PlayerActivity.this);
					if (updateVer > currentVer) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								PlayerActivity.this);
						dialog.setMessage(json.getString(KEY_MSG));
						dialog.setTitle("업데이트 발견");
						dialog.setCancelable(false);
						dialog.setPositiveButton("업데이트",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
										try {
											startActivity(new Intent(
													Intent.ACTION_VIEW,
													Uri.parse(json
															.getString(KEY_URL))));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										finish();
									}
								});
						dialog.show();
						return;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (getIntent().getData() == null) {
					Toast.makeText(PlayerActivity.this,
							"파라미터 오류입니다. 다시 실행해 주세요.", Toast.LENGTH_LONG)
							.show();
					finish();
				} else
					new PrepareSync().execute();
			}
		}
	}
	
	void sendError(Throwable t) {
		Intent errorIntent = new Intent(
				"com.kr.busan.cw.cinepox.service.errorlog");
		errorIntent.putExtra("url", mBugReportUrl);
		errorIntent.putExtra("error", t);
		sendBroadcast(errorIntent);
	}

	// boolean login() {
	// String id = mConfig.getAccount()[0];
	// String pw = mConfig.getAccount()[1];
	// if (id == null || pw == null)
	// return false;
	// return true;
	// }

	static {
		System.loadLibrary("helloneon");
	}

	private native boolean isNeon();

	void updatePlayInfo() {
		updatePlayInfo(getCurrentPosition(), getDuration());
	}

	void updatePlayInfo(long current, long duration) {
		mDuration = duration;
		if (current > duration)
			current %= duration;
		mCurrent = current;
		mCurrentText.setText(Util.Time.stringForTime(mCurrent));
		mDurationText.setText(Util.Time.stringForTime(mDuration));
	}

	void updateProgress() {
		mSeekBar.setProgress(Util.Math.getPercent(mCurrent, mDuration));
	}

	void updateCaption() {
		// if (isUseCC)
		// mCaptionText.setText(Html.fromHtml(mCCParser
		// .getCaption(getCurrentPosition())));
	}

	void hideSystemUi() {
		if (Build.VERSION.SDK_INT >= 14)
			hideSystemUi(true);
		else if (Build.VERSION.SDK_INT >= 11)
			hideSystemUi(false);
	}

	@TargetApi(11)
	void hideSystemUi(boolean hide) {
		if (hide)
			setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		else
			setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
	}

	private void setSystemUiVisibility(int systemUiFlagHideNavigation) {
		// TODO Auto-generated method stub
		try {
			switch (mCodec) {
			case HW_CODEC:
				mHWVideoView.setSystemUiVisibility(systemUiFlagHideNavigation);
				break;
			case SW_CODEC:
				if (checkPlugin())
					mVideoView
							.setSystemUiVisibility(systemUiFlagHideNavigation);
				break;
			}
		} catch (Exception e) {
		}
	}

}

/**
 * 주석처리는 해 놓았으나 나중에 쓸모가 있을 법한 코드들
 */

/** 다운로드 속도 체크 */

// static long downLength;
// static boolean isDownloading;
//
// public void fileDown() throws ClientProtocolException, IOException {
// String url = DUMMY_FILE_URL;
//
// downLength = 0;
// isDownloading = true;
// URLConnection con = new URL(url).openConnection();
// InputStream is = con.getInputStream();
// FileOutputStream fos = new FileOutputStream(DUMMY_FILE_PATH);
//
// int len = -1;
// byte buffer[] = new byte[1024];
// while ((len = is.read(buffer)) != -1) {
// fos.write(buffer, 0, len);
// downLength += len;
// }
//
// fos.close();
// is.close();
// isDownloading = false;
// }

// private class DownThread extends Thread {
// @Override
// public void run() {
// setPriority(Thread.MIN_PRIORITY);
// try {
// while (true) {
// sleep(1000);
// try {
// fileDown();
// } catch (Exception e) {
// e.printStackTrace();
// } finally {
// if (new File(DUMMY_FILE_PATH).exists())
// new File(DUMMY_FILE_PATH).delete();
// }
// }
// } catch (InterruptedException e1) {
// e1.printStackTrace();
// new File(DUMMY_FILE_PATH).delete();
// }
// }
// }

// private class DownloadSpeedThread extends Thread {
// long prevLength = 0l;
//
// public void run() {
// setPriority(Thread.MIN_PRIORITY);
// try {
// while (true) {
// prevLength = 0l;
// while (isDownloading) {
// sleep(1000);
// runOnUiThread(mRunnable);
// prevLength = downLength;
// if (isInterrupted())
// return;
// }
// }
// } catch (InterruptedException e) {
// e.printStackTrace();
// }
// }
//
// private Runnable mRunnable = new Runnable() {
// @Override
// public void run() {
// long down = downLength - prevLength;
// setDownSpeed(down);
// }
// };
//
// }

// ArrayList<Long> mSpeedAvrArray = new ArrayList<Long>();
//
// void setDownSpeed(long down) {
// if (down <= 0)
// return;
// mSpeedAvrArray.add(down);
// if (mSpeedAvrArray.size() > 30)
// mSpeedAvrArray.remove(0);
// down = Util.Math.avr(mSpeedAvrArray) * 2l;
// if (down < 1024)
// mSpeedText.setText(down + "byte");
// else if (down < 1024 * 1024)
// mSpeedText.setText(down / 1024 + "KB");
// else
// mSpeedText.setText(down / 1024 / 1024 + "MB");
// }
