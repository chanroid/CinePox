package com.kr.busan.cw.cinepox.player.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

import kr.co.chan.util.ShakeListener;
import kr.co.chan.util.ShakeListener.OnShakeListener;
import kr.co.chan.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.kr.busan.cw.cinepox.R;
import com.kr.busan.cw.cinepox.player.iface.VideoCallback;
import com.kr.busan.cw.cinepox.player.iface.VideoControllerCallback;
import com.kr.busan.cw.cinepox.player.model.CaptionModel;
import com.kr.busan.cw.cinepox.player.model.PlayerConfigModel;
import com.kr.busan.cw.cinepox.player.model.PlayerModel.Const;
import com.kr.busan.cw.cinepox.player.model.VideoModel;
import com.kr.busan.cw.cinepox.player.structs.QualityData;
import com.kr.busan.cw.cinepox.player.view.VideoControllerView;
import com.kr.busan.cw.cinepox.player.view.VideoView;

import controller.CCActivity;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : PlayerActivity.java
 * 3. Package  : com.kr.busan.cw.cinepox.player.controller
 * 4. Comment  : 
 * 		비디오 재생과 컨트롤러를 제어 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 15. 오후 1:38:34
 * </PRE>
 */
@SuppressLint({ "HandlerLeak", "NewApi" })
public class PlayerActivity extends CCActivity implements
		VideoControllerCallback, VideoCallback, OnShakeListener,
		LocationListener {

	boolean isBack = false;
	int mErrorCount = 0;
	int mQuality = 0;
	long mStartTime = 0l;

	VideoView mVideoView;
	VideoControllerView mVideoController;
	ProgressDialog mShareDialog;

	VideoModel mPlayerModel;
	CaptionModel mCaptionModel;
	PlayerConfigModel mConfigModel;

	LocationManager mLocManager;
	Location mLocation;
	AudioManager mAudioManager;
	WindowManager mWindowManager;
	WindowManager.LayoutParams mWindowParams;

	UpdateThread updateThread;
	UpdateRunnable updateRunnable;
	UpdateReceiver updateReceiver;
	UpdateLoader updateLoader;
	ConfigLoader configLoader;
	ShakeListener mShaker;

	public static final int REQUEST_READ_QRCODE = 1;
	public static final int REQUEST_MODIFY_BRIGHTNESS = 2;
	// private final int REQUEST_ENABLE_BT = 3;

	private OnClickListener codecChangeListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			mConfigModel.setContinuePosition(mVideoView.getCurrentPosition());

			if (mVideoView.setCodec(which)) {
				mVideoController.setBufferProgress(0);
				if (which == VideoView.CODEC_HW)
					mVideoController.setCodecText("H/W");
				else if (which == VideoView.CODEC_SW)
					mVideoController.setCodecText("S/W");
				return;
			}

		}
	};

	private OnClickListener qualityChangeListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			if (which == mQuality)
				return;
			mQuality = which;
			mVideoController.setBufferProgress(0);
			mConfigModel.setContinuePosition(mVideoView.getCurrentPosition());
			mConfigModel.setDefaultQuality(mPlayerModel.getQualityArray().get(
					mQuality).TYPE);
			mVideoController.setQualityText(mPlayerModel.getQualityArray().get(
					which).NAME);
			mVideoView.setVideoURI(Uri.parse(mPlayerModel.getQualityArray()
					.get(which).URL));
		}
	};

	private Handler backHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0)
				isBack = false;
		}
	};

	private class UpdateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_BATTERY_CHANGED.equalsIgnoreCase(intent
					.getAction())) {
				updateBatt();
			} else if (Intent.ACTION_TIME_TICK.equalsIgnoreCase(intent
					.getAction())) {
				updateTime();
			}
		}
	}

	private class UpdateThread extends Thread {
		@Override
		public void run() {
			while (true) {
				if (mVideoView.isPlaying() && !mVideoView.isSeeking()
						&& !mVideoController.isTracking()) {
					runOnUiThread(updateRunnable);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		}
	}

	private class UpdateRunnable implements Runnable {
		@Override
		public void run() {
			updateController();
			// 2차개발분. 기능구현은 완료
			updateCaption();
		}
	}

	private class URLShareSender extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			mShareDialog.setMessage(getString(R.string.sending_video));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return mConfigModel.sendShareInfo(mLocation, mConfigModel
					.getCinepoxURI().toString());
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mShareDialog.dismiss();
			mShareDialog.setMessage(getString(R.string.noti_readymotion));
			if (result)
				Toast.makeText(PlayerActivity.this, R.string.send_success,
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(PlayerActivity.this, R.string.send_failed,
						Toast.LENGTH_SHORT).show();
		}

	}

	private class UpdateLoader extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mVideoView.showLoading();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			return mConfigModel.loadUpdate();
		}

		@Override
		protected void onPostExecute(final JSONObject result) {
			super.onPostExecute(result);
			mVideoView.hideLoading();
			if (result == null)
				return;

			float updateVer = 0.0f;
			float currentVer = 0.0f;

			try {
				updateVer = Float.valueOf(result.getString(Const.KEY_VERSION));
				currentVer = Util.App.getVersionNum(PlayerActivity.this);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			if (updateVer > currentVer) {
				try {
					DialogInterface.OnClickListener updateClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							try {
								startActivity(new Intent(Intent.ACTION_VIEW,
										Uri.parse(result
												.getString(Const.KEY_URL))));
							} catch (JSONException e) {
								sendErrorLog(e);
								e.printStackTrace();
							}
							finish();
						}
					};

					Util.Views.showAlert(PlayerActivity.this, "업데이트 발견",
							result.getString(Const.KEY_MSG), "업데이트",
							updateClickListener, false);
					return;

				} catch (JSONException e) {
					sendErrorLog(e);
					e.printStackTrace();
				}
			}

			if (getIntent().getData() == null) {
				// startActivityForResult(new Intent(CinePlayer.this,
				// ReadQRActivity.class), REQUEST_READ_QRCODE);
			} else {
				configLoader.execute(getIntent());
			}
		}
	}

	private class ConfigLoader extends AsyncTask<Intent, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mVideoView.showLoading();
		}

		@Override
		protected String doInBackground(Intent... params) {
			return mConfigModel.loadConfig(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null) {
				mVideoView.hideLoading();
				return;
			}

			mVideoController
					.setCinePoxMode(result.equals(Const.RESULT_SUCCESS));

			if (Build.VERSION.SDK_INT < 9)
				Toast.makeText(
						PlayerActivity.this,
						"운영체제를 안드로이드 2.3버전 이상으로 업그레이드 하시면 초고화질의 동영상을 더욱 원활하게 시청하실 수 있습니다.",
						Toast.LENGTH_LONG).show();
			else if (Util.Connection.getType(PlayerActivity.this) != ConnectivityManager.TYPE_WIFI
					&& !"".equals(mConfigModel.get3GMessage()))
				Toast.makeText(PlayerActivity.this,
						mConfigModel.get3GMessage(),
						mConfigModel.get3GMessageLength()).show();

			if (result.contains(Const.RESULT_ERROR)) {
				mVideoView.hideLoading();
				showError(result.replace(Const.RESULT_ERROR, ""));
			} else if (result.equals(Const.RESULT_MOVIE)) {
				prepareVideo();
			} else if (result.equals(Const.RESULT_SUCCESS)) {
				Criteria criteria = new Criteria();
				String provider = mLocManager.getBestProvider(criteria, true);
				if (provider == null)
					provider = LocationManager.NETWORK_PROVIDER;
				mLocation = mLocManager.getLastKnownLocation(provider);
				mLocManager.requestLocationUpdates(provider, 10000, 100,
						PlayerActivity.this);

				mShaker.setOnShakeListener(PlayerActivity.this);
				mShaker.pause();

				prepareVideo();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT < 11)
			setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		else
			setTheme(android.R.style.Theme_Holo_NoActionBar_Fullscreen);

		super.onCreate(savedInstanceState);
		startService(new Intent(this, PlayerService.class));

		mStartTime = System.currentTimeMillis();
		mErrorCount = getIntent().getIntExtra(Const.KEY_ERROR_COUNT, 0);
		if (mErrorCount >= 5)
			finish();
		else {
			mPlayerModel = VideoModel.getInstance(this);
			mConfigModel = PlayerConfigModel.getInstance(this);
			mCaptionModel = CaptionModel.getInstance();

			updateRunnable = new UpdateRunnable();
			updateThread = new UpdateThread();
			updateReceiver = new UpdateReceiver();
			updateLoader = new UpdateLoader();
			configLoader = new ConfigLoader();

			mVideoView = new VideoView(this);
			mVideoView.setCallback(this);
			mVideoController = new VideoControllerView(this);
			mVideoController.setCallback(this);

			mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
			mShaker = new ShakeListener(this);
			mShaker.pause();

			mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
			mWindowParams = new WindowManager.LayoutParams();
			mWindowParams.height = -1; // LayoutParams.MATCH_PARENT
			mWindowParams.width = -1; // LayoutParams.MATCH_PARENT
			mWindowParams.format = PixelFormat.RGBA_8888;
			mWindowParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
					| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

			setContentView(mVideoView);
			updateTime();
			updateBatt();

			updateLoader.execute();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		removeControllerView();
		unregisterReceiver(updateReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		addControllerView();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(updateReceiver, filter);
	};

	@Override
	public void onBackPressed() {
		if (isBack) {
			finish();
		} else {
			isBack = true;
			Toast.makeText(this, R.string.backtoexit, Toast.LENGTH_LONG).show();
			backHandler.sendEmptyMessageDelayed(0, 2000);
			if (mVideoView.getCurrentPosition() > 0l)
				sendPlayTime();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			showVolumeControl();
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_READ_QRCODE:
				finish();
				break;
			case REQUEST_MODIFY_BRIGHTNESS:
				Util.Display.setBrightness(this,
						data.getFloatExtra(Const.KEY_BRIGHT, 1.0f));
				break;
			}
		}
	}

	private void prepareVideo() {
		Util.Display.setBrightness(this, 1.0f);
		mQuality = mConfigModel.getInitialQuality();
		if (mQuality != -1) {
			String name = mPlayerModel.getQualityArray().get(mQuality).NAME;
			String url = mPlayerModel.getQualityArray().get(mQuality).URL;
			mVideoController.setQualityText(name);
			mVideoView.setVideoURI(Uri.parse(url));
		} else {
			if (mConfigModel.isSetURI())
				mVideoView.setVideoURI(mConfigModel.getSetURI());
		}
	}

	private void updateTime() {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int min = Calendar.getInstance().get(Calendar.MINUTE);
		StringBuilder mFormatBuilder = new StringBuilder();
		Formatter mFormatter = new Formatter(mFormatBuilder,
				Locale.getDefault());
		mVideoController.setTimeText(mFormatter.format("%02d:%02d", hour, min)
				.toString());
		mFormatter.close();
	}

	private void updateBatt() {
		mVideoController.setBatteryText(Util.Phone.getBattStat(this) + "%");
	}

	private void updateController() {
		mVideoController.setPlayData(mVideoView.getPlayData());
	}

	private void updateCaption() {
		if (mCaptionModel.isEnabled())
			mVideoView.setCaptionText(mCaptionModel.getCaption(mVideoView
					.getCurrentPosition()));
		else
			mVideoView.setCaptionText("");
	}

	private void addControllerView() {
		if (mVideoController != null)
			mWindowManager.addView(mVideoController, mWindowParams);
		mVideoController.showController();
	}

	private void buildShareDialog() {
		if (mShareDialog == null) {
			mShareDialog = new ProgressDialog(this);
			mShareDialog.setMessage(getString(R.string.noti_readymotion));
			mShareDialog.setCancelable(false);
		}
	}

	private void removeControllerView() {
		if (mVideoController != null)
			try {
				mWindowManager.removeView(mVideoController);
			} catch (IllegalArgumentException e) {
			}
	}

	private void showError(String message) {
		removeControllerView();
		Util.Views.showAlert(this, getString(R.string.alert), message,
				getString(R.string.done),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				}, false);
	}

	private void showVolumeControl() {
		startActivity(new Intent(this, VolumeActivity.class));
	}

	private void showShareDialog() {
		pause();
		buildShareDialog();
		mShaker.resume();
		mShareDialog.show();
	}

	private void showCaptionDialog() {
		ArrayList<String> names = mCaptionModel.getLangNames();
		if (names.size() > 1) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			String[] s = new String[names.size()];
			dialog.setSingleChoiceItems(names.toArray(s),
					mCaptionModel.getCCLangIndex(),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							mCaptionModel.setCCLang(which);
						}
					});
			dialog.show();
		}
	}

	private void showBrightControl() {
		Intent i = new Intent(this, BrightActivity.class);
		i.putExtra(Const.KEY_BRIGHT, Util.Display.getBrightness(this));
		startActivityForResult(i, REQUEST_MODIFY_BRIGHTNESS);
	}

	private void showCodecChange() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.change_codec);
		dialog.setSingleChoiceItems(new String[] {
				getString(R.string.hw_codec), getString(R.string.sw_codec) },
				mVideoView.getCodec(), codecChangeListener);
		dialog.show();
	}

	private void showQualityChange() {
		final ArrayList<QualityData> quality = mPlayerModel.getQualityArray();
		if (quality == null || quality.size() < 1)
			return;
		String[] qualArray = new String[quality.size()];
		for (int i = 0; i < qualArray.length; i++) {
			qualArray[i] = quality.get(i).NAME;
		}
		AlertDialog.Builder qosDialog = new AlertDialog.Builder(this);
		qosDialog.setSingleChoiceItems(qualArray, mQuality,
				qualityChangeListener);
		qosDialog.show();
	}

	private void togglePlayPause() {
		if (mVideoView.isPlaying())
			pause();
		else
			start();
	}

	private void toggleScreenMode() {
		switch (mVideoView.getVideoLayout()) {
		case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_ZOOM:
			mVideoView.setCenterText(R.string.scale_orign);
			break;
		case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_ORIGIN:
			mVideoView.setCenterText(R.string.scale_scale);
			break;
		case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_SCALE:
			mVideoView.setCenterText(R.string.scale_zoom);
			break;
		case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_STRETCH:
			mVideoView.setCenterText(R.string.scale_orign);
			break;
		case -1:
			return;
		}
		int layout = (mVideoView.getVideoLayout() + 1) % 3;
		mVideoView.setVideoLayout(layout);
	}

	private void sendErrorLog(String message) {
		Intent i = new Intent(PlayerService.ACTION_SEND_ERROR);
		i.putExtra(Const.KEY_MSG, message);
		i.putExtra(Const.KEY_MOVIE_URL, mVideoView.getVideoURI());
		sendBroadcast(i);
	}

	private void sendErrorLog(Throwable exception) {
		Intent i = new Intent(PlayerService.ACTION_SEND_ERROR);
		i.putExtra(Const.KEY_EXCEPTION, exception);
		i.putExtra(Const.KEY_MOVIE_URL, mVideoView.getVideoURI());
		sendBroadcast(i);
	}

	private void sendPlayTime() {
		Intent i = new Intent(PlayerService.ACTION_SEND_TIME);
		i.putExtra(Const.KEY_START_TIME, mStartTime);
		i.putExtra(Const.KEY_PLAY_TIME, mVideoView.getCurrentPosition());
		sendBroadcast(i);
	}

	private void start() {
		mVideoController.setPlayBtnState(true);
		mVideoView.start();
	}

	private void pause() {
		mVideoController.setPlayBtnState(false);
		mVideoView.pause();
	}

	/**
	 * VideoController callback interface methods
	 */

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			mVideoView.setCenterText(Util.Time.stringForTime(mVideoView
					.getDuration() / 100 * progress));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		pause();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mVideoView.seekTo(mVideoView.getDuration() / 100
				* seekBar.getProgress());
		start();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_video_changequality)
			showQualityChange();
		else if (v.getId() == R.id.btn_video_changecodec)
			showCodecChange();
		else if (v.getId() == R.id.btn_video_shake)
			showShareDialog();
		else if (v.getId() == R.id.btn_video_caption)
			showCaptionDialog();
		else if (v.getId() == R.id.btn_video_volumn)
			showVolumeControl();
		else if (v.getId() == R.id.btn_video_bright)
			showBrightControl();
		else if (v.getId() == R.id.btn_video_playpause)
			togglePlayPause();
		else if (v.getId() == R.id.btn_video_fullscreen)
			toggleScreenMode();
	}

	@Override
	public void onVisiblityChanged(int visiblity) {
		if (Build.VERSION.SDK_INT >= 14) {
			if (visiblity == View.VISIBLE) {
				getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_VISIBLE);
			} else {
				if (Util.Display.isTablet(this))
					getWindow().getDecorView().setSystemUiVisibility(
							View.SYSTEM_UI_FLAG_LOW_PROFILE);
				else
					getWindow().getDecorView().setSystemUiVisibility(
							View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
			}
		}
	}

	@Override
	public void onUp(long distance) {
		mVideoView.seekTo(mVideoView.getCurrentPosition() + distance < 0 ? 0
				: mVideoView.getCurrentPosition() + distance);
		start();
	}

	@Override
	public void onScrollX(long distance) {
		pause();
		long time = mVideoView.getCurrentPosition() + distance > 0l ? mVideoView
				.getCurrentPosition() + distance
				: 0l;
		if (time > mVideoView.getDuration())
			time = mVideoView.getDuration();
		else if (time < 0l)
			time = 0l;
		mVideoView.setCenterText(Util.Time.stringForTime(time));
		mVideoController.setProgress(Util.Math.getPercent(time,
				mVideoView.getDuration()));
	}

	@Override
	public void onScrollY(long distance) {
		Util.Phone.setVolume(this, AudioManager.STREAM_MUSIC, (int) distance,
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		distance = Util.Phone.getVolume(this, AudioManager.STREAM_MUSIC);
		mVideoView.setVolume((int) distance);
	}

	@Override
	public void onZoom(float scale) {
		// TODO Auto-generated method stub
		if (mVideoView.setVideoScale(scale))
			mVideoView.setCenterText((int) (scale * 100) + "%");
	}

	/**
	 * VideoView callback interface methods
	 */

	@Override
	public void onPrepared(VideoView view) {
		if (!updateThread.isAlive())
			updateThread.start();
		mVideoView.seekTo(mConfigModel.getContinuePosition());
		mVideoController.setPlayData(mVideoView.getPlayData());
		start();
	}

	@Override
	public void onInfo(VideoView view, int what, int extra) {
	}

	@Override
	public void onError(VideoView view, int what, int extra) {
		switch (what) {
		case 1:
			switch (extra) {
			case -1:
			case -17:
			case -1094995529:
				showError("해당 기기에서 재생이 불가능한 동영상입니다.");
			case -2147483648:
				removeControllerView();
				sendErrorLog("[" + getIntent().getDataString()
						+ "] player onError() : " + what + " : " + extra);
				break;
			default:
				Intent i = new Intent(this, PlayerActivity.class);
				i.setData(getIntent().getData());
				i.putExtra("set_time", mVideoView.getCurrentPosition() / 1000);
				i.putExtra("error_count", mErrorCount++);
				startActivity(i);
				sendErrorLog("[" + getIntent().getDataString()
						+ "] player onError() : " + what + ", " + extra);
				finish();
				break;
			}
			if (mVideoView.getCurrentPosition() > 0l)
				sendPlayTime();
			break;
		case 100:
			mVideoView.setVideoURI();
			break;
		case 200:
			sendErrorLog("[" + getIntent().getDataString()
					+ "] player onError() : " + what + " : " + extra);
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		mVideoView.setCodec(-1);
		super.finish();
	}

	@Override
	public void onSeekComplete(VideoView view) {
		mVideoController.setPlayData(view.getPlayData());
		start();
	}

	@Override
	public void onBufferingUpdate(VideoView view, int progress) {
		mVideoController.setBufferProgress(progress);
	}

	@Override
	public void onCompletion(VideoView view) {
		Toast.makeText(this, R.string.playback_completed, Toast.LENGTH_SHORT)
				.show();
		finish();
	}

	@Override
	public void onShake() {
		mShaker.pause();
		new URLShareSender().execute();
	}

	@Override
	public void onLocationChanged(Location arg0) {
		mLocation = arg0;
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
