package com.kr.busan.cw.cinepox.player;

import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

/**
 * MediaPlayer 관련 모든 인터페이스가 implements 되어 있음.
 * 
 * @author chanroid in CinePox
 * 
 */
@SuppressLint("HandlerLeak")
public abstract class AbsCinePlayer extends Activity implements
		OnErrorListener, OnBufferingUpdateListener, OnCompletionListener,
		OnPreparedListener, OnSeekCompleteListener, OnInfoListener,
		android.media.MediaPlayer.OnErrorListener,
		android.media.MediaPlayer.OnBufferingUpdateListener,
		android.media.MediaPlayer.OnCompletionListener,
		android.media.MediaPlayer.OnPreparedListener,
		android.media.MediaPlayer.OnSeekCompleteListener,
		android.media.MediaPlayer.OnInfoListener {

	TimeSyncThread mTimeSyncThread;
	Handler mTimeSyncHandler;
	UpdateReceiver mTimeBattUpdateReceiver;

	class UpdateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Intent.ACTION_BATTERY_CHANGED.equalsIgnoreCase(intent
					.getAction()))
				updateBatt();
			else if (Intent.ACTION_TIME_TICK.equalsIgnoreCase(intent
					.getAction()))
				updateTime();
		}
	}

	class TimeSyncThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					mTimeSyncHandler.sendEmptyMessage(0);
					Thread.sleep(500);
				} catch (InterruptedException ie) {
					return;
				}
			}
		}
	}

	class TimeSyncHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				if (!isSeeking() && isPlaying() && !isTracking())
					updateInfo();
				break;
			}
		}
	}

	class ControllerTouchListener implements OnTouchListener {

		float pointX = 0.0f;
		float pointY = 0.0f;
		boolean isMoving = false;
		long distanceX = 0;
		long distanceY = 0;
		int action = -1;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if (isMoving) {
					onUp();
					distanceX = 0;
					distanceY = 0;
					pointX = 0.0f;
					pointY = 0.0f;
					action = -1;
					isMoving = false;
					return true;
				}
				toggleController();
				return true;
			case MotionEvent.ACTION_DOWN:
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
					if (action == 0)
						return onScrollX(distanceX);
				} else if (!(distanceY < 2 && distanceY > -2)) {
					if (action == -1)
						action = 1;
					if (action == 1)
						return onScrollY(distanceY);
				}
				return true;
			default:
				return false;
			}
		}
	};

	/**
	 * 액티비티 라이프 사이클
	 */

	@Override
	protected void onResume() {
		super.onResume();
		mTimeSyncThread = new TimeSyncThread();
		mTimeSyncThread.start();
		mTimeSyncHandler = new TimeSyncHandler();
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(mTimeBattUpdateReceiver, filter);
		resume();
		addControllerWindow();
	};

	@Override
	protected void onPause() {
		super.onPause();
		mTimeSyncThread.interrupt();
		mTimeSyncThread = null;
		mTimeSyncHandler.removeMessages(0);
		mTimeSyncHandler = null;
		unregisterReceiver(mTimeBattUpdateReceiver);
		pause();
		removeControllerWindow();
	};

	@Override
	protected void onDestroy() {
		release();
		super.onDestroy();
	};

	/** 미디어플레이어 콜백 함수 */

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

	@Override
	public boolean onInfo(io.vov.vitamio.MediaPlayer arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return onInfo(arg1, arg2);
	}

	@Override
	public void onSeekComplete(io.vov.vitamio.MediaPlayer arg0) {
		// TODO Auto-generated method stub
		onSeekComplete();
	}

	@Override
	public void onPrepared(io.vov.vitamio.MediaPlayer arg0) {
		// TODO Auto-generated method stub
		onPrepared();
	}

	@Override
	public void onCompletion(io.vov.vitamio.MediaPlayer arg0) {
		// TODO Auto-generated method stub
		onCompletion();
	}

	@Override
	public void onBufferingUpdate(io.vov.vitamio.MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub
		onBufferingUpdate(arg1);
	}

	@Override
	public boolean onError(io.vov.vitamio.MediaPlayer arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return onError(arg1, arg2);
	}

	abstract boolean onError(int what, int extra);

	abstract boolean onInfo(int what, int extra);

	abstract void onBufferingUpdate(int progress);

	abstract void onCompletion();

	abstract void onPrepared();

	abstract void onSeekComplete();

	/** 컨트롤러 윈도우 관련 함수 */

	void toggleController() {
		if (isControllerVisible())
			hideController();
		else
			showController();
	}

	abstract boolean isControllerVisible();

	abstract void showController();

	abstract void hideController();

	/**
	 * 컨트롤러 안에 사용될 뷰 선언
	 */
	abstract void loadControllerViews();

	/**
	 * 컨트롤러에 적용될 애니메이션 선언
	 */
	abstract void loadControllerAnimations();

	/**
	 * loadControllerViews() 로 미리 선언된 뷰를 사용하여 컨트롤러가 전개될 윈도우를 화면에 추가
	 */
	abstract void addControllerWindow();

	/**
	 * 컨트롤러가 추가된 윈도우를 제거
	 */
	abstract void removeControllerWindow();

	/**
	 * @return 컨트롤러 윈도우 파라미터
	 */
	WindowManager.LayoutParams getControllerWindowParams() {
		WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
		wlp.height = LayoutParams.MATCH_PARENT;
		wlp.width = LayoutParams.MATCH_PARENT;
		wlp.format = PixelFormat.RGBA_8888;
		wlp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		return wlp;
	}

	/** 컨트롤러 상태 업데이트 함수 */

	/**
	 * 1초마다 업데이트 할 항목을 추가하고 싶으면 이 메서드를 오버라이드 할것 오버라이드 해서 사용할시 슈퍼클래스의 메서드를 반드시
	 * 호출해야 함 onPause 에서 정지되고 onResume에서 시작됨 Null 체크 등등은 알아서...
	 */
	void updateInfo() {
		updatePlayInfo();
		updateProgress();
		updateCaption();
	}

	/**
	 * 상태 변경시 자동으로 호출됨
	 */
	abstract void updateTime();

	/**
	 * 상태 변경시 자동으로 호출됨
	 */
	abstract void updateBatt();

	/**
	 * 플레이어 활성화시 1초마다 updateInfo에서 자동으로 호출됨 pause, tracking, seeking 상태에서는 호출되지
	 * 않음
	 */
	abstract void updatePlayInfo();

	/**
	 * 플레이어 활성화시 1초마다 updateInfo에서 자동으로 호출됨 pause, tracking, seeking 상태에서는 호출되지
	 * 않음
	 */
	abstract void updateProgress();

	/**
	 * 플레이어 활성화시 1초마다 updateInfo에서 자동으로 호출됨 pause, tracking, seeking 상태에서는 호출되지
	 * 않음
	 */
	abstract void updateCaption();

	/** 기타 뷰 관련 함수 */

	/**
	 * 컨트롤러 이외의 뷰 선언
	 */
	abstract void loadViews();

	/**
	 * 플레이어 버퍼링이나 네트워크 작업시 로딩중 알림 뷰 표시
	 */
	abstract void showLoadingBar();

	/**
	 * 로딩작업 등이 끝나면 알림 뷰 숨김
	 */
	abstract void hideLoadingBar();

	/**
	 * 로딩 작업시 뷰가 아닌 다이얼로그가 필요할 시 사용
	 * 
	 * @param title
	 * @param message
	 */
	abstract void showLoadingDialog(String title, String message);

	abstract void dismissLoadingDialog();

	/** 초기화 관련 함수 */

	/**
	 * 해당 url에서 업데이트 정보를 가져와 처리. 네트워크 스레드에서 실행되므로 UI 작업은 할 수 없음.
	 * 
	 * @param url
	 *            업데이트 정보를 가져올 url
	 * @return 처리 결과를 나타낼 수 있는 문자열
	 */
	abstract String prepareUpdateInfo(String url);

	/**
	 * prepareUpdateInfo 에서 리턴된 결과에 의한 처리
	 * 
	 * @param result
	 */
	abstract void handleUpdateInfoResult(String result);

	/**
	 * Custom scheme 또는 주어진 url로 각종 정보 파싱. 네트워크 스레드에서 실행되므로 UI 작업은 할 수 없음
	 * 
	 * @param uri
	 *            Custom scheme 또는 네트워크로 파싱할 url
	 * @return 처리 결과를 나타낼 수 있는 문자열
	 */
	abstract String preparePlayerInfo(String uri);

	/**
	 * preparePlayerInfo 에서 리턴된 결과에 의한 처리
	 * 
	 * @param result
	 *            preparePlayerInfo 에서 리턴된 결과 문자열
	 */
	abstract void handlePlayerInfoResult(String result);

	/**
	 * super.onCreate 이전에 호출될 수 있는 생성자로 멤버변수 초기화
	 */
	abstract void initMemberVariable();

	/**
	 * super.onCreate 이후에 호출되어야 하는 생성자로 멤버변수 초기화
	 */
	abstract void initMemberObjects();

	/**
	 * super.onCreate 이후에 호출되어야 하는 각종 시스템 서비스 생성
	 */
	abstract void loadSystemServices();

	/** 미디어플레이어, 비디오뷰 관련 함수 */

	void togglePlayPause() throws IllegalStateException {
		if (isPlaying()) {
			pause();
		} else {
			start();
		}
	}

	abstract void onUp();

	abstract boolean onScrollX(long distance);

	abstract boolean onScrollY(long distance);

	abstract void hideSystemUIViblity();

	abstract void toggleDisplayMode();

	abstract void setVideoLayout(int layout);

	abstract int getVideoLayout();

	abstract long getCurrentPosition();

	abstract long getDuration();

	abstract boolean isPlaying();

	abstract boolean isSeeking();

	abstract boolean isTracking();

	abstract void pause() throws IllegalStateException;

	abstract void resume();

	abstract void release();

	abstract void reset();

	abstract void seekTo(long msec) throws IllegalStateException;

	abstract void setVideoURI(Uri uri);

	abstract void setVolume(int volume);

	abstract void start() throws IllegalStateException;

	abstract void stop() throws IllegalStateException;

	PlayerConfig getConfig() {
		return PlayerConfig.getInstance(this);
	}

}
