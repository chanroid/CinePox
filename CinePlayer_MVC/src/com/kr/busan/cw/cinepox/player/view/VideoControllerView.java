package com.kr.busan.cw.cinepox.player.view;

import kr.co.chan.util.Util;
import view.CCView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.kr.busan.cw.cinepox.R;
import com.kr.busan.cw.cinepox.player.iface.VideoControllerCallback;
import com.kr.busan.cw.cinepox.player.structs.PlayData;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : VideoControllerView.java
 * 3. Package  : com.kr.busan.cw.cinepox.player.view
 * 4. Comment  : 비디오 컨트롤러의 내용 표시
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 15. 오후 1:27:57
 * </PRE>
 */
@SuppressLint("HandlerLeak")
public class VideoControllerView extends CCView implements
		OnSeekBarChangeListener {

	private static final int HANDLE_WHAT_CONTROLLER_AUTO_HIDE = 201;
	private static final int AUTO_HIDE_DELAY = 3000;

	private boolean isTracking = false;
	private boolean isControllerVisible = true;
	private int mBuffer = 0;

	private TextView batteryText;
	private TextView timeText;
	private TextView currentText;
	private TextView durationText;
	private TextView qualityText;
	private TextView codecText;

	private ImageButton playPauseBtn;
	private ImageButton shakeBtn;
	private ImageButton bolumeBtn;
	private ImageButton brightBtn;
	private ImageButton screenModeBtn;
	private ImageButton captionBtn;

	private SeekBar seekBar;
	private VideoControllerCallback callback;

	private LinearLayout controllerTop;
	private LinearLayout controllerBottom;

	private Animation topShowAnimation;
	private Animation topHideAnimation;
	private Animation bottomShowAnimation;
	private Animation bottomHideAnimation;

	private Handler controllerHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLE_WHAT_CONTROLLER_AUTO_HIDE:
				hideController();
				break;
			}
		};
	};

	private ScaleGestureDetector mMultiTouchDetector;
	private SimpleOnScaleGestureListener mMultiTouchListener = new SimpleOnScaleGestureListener() {
		private float scaleFactor = 0.7f;

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			action = 2;
			controllerHandler.removeMessages(HANDLE_WHAT_CONTROLLER_AUTO_HIDE);
			scaleFactor *= detector.getScaleFactor();
			scaleFactor = Math.max(0.3f, Math.min(scaleFactor, 5.0f));
			if (callback != null)
				callback.onZoom(scaleFactor);
			return true;
		};
	};

	public VideoControllerView(Context context) {
		super(context);
		setOnSystemUiVisibilityChangeListener();
		mMultiTouchDetector = new ScaleGestureDetector(context,
				mMultiTouchListener);
	}

	public boolean isTracking() {
		return isTracking;
	}

	public void toggleController() {
		if (isControllerVisible) {
			hideController();
		} else {
			showController();
		}
	}

	public void setCinePoxMode(boolean mode) {
		int visiblity = (mode ? View.VISIBLE : View.GONE);
		qualityText.setVisibility(visiblity);
		// 2차개발분. 기능구현은 완료
		// shakeBtn.setVisibility(visiblity);
		// captionBtn.setVisibility(visiblity);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : showController
	 * 2. ClassName  : VideoControllerView
	 * 3. Comment   : 비디오 컨트롤러 표시
	 * 		3초후 다시 숨김
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 16. 오후 2:02:38
	 * </PRE>
	 * 
	 * @return void
	 */
	public void showController() {
		showController(AUTO_HIDE_DELAY);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : showController
	 * 2. ClassName  : VideoControllerView
	 * 3. Comment   : 지정된 시간동안 비디오 컨트롤러 표시
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 16. 오후 2:02:38
	 * </PRE>
	 * 
	 * @return void
	 */
	public void showController(int time) {
		isControllerVisible = true;
		controllerTop.startAnimation(topShowAnimation);
		controllerTop.setVisibility(View.VISIBLE);
		controllerBottom.startAnimation(bottomShowAnimation);
		controllerBottom.setVisibility(View.VISIBLE);
		controllerHandler.sendEmptyMessageDelayed(
				HANDLE_WHAT_CONTROLLER_AUTO_HIDE, time);
		if (callback != null)
			callback.onVisiblityChanged(View.VISIBLE);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : hideController
	 * 2. ClassName  : VideoControllerView
	 * 3. Comment   : 비디오 컨트롤러 숨김
	 * 		시스템 UI까지 숨김
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 16. 오후 2:03:07
	 * </PRE>
	 * 
	 * @return void
	 */
	public void hideController() {
		isControllerVisible = false;
		controllerTop.startAnimation(topHideAnimation);
		controllerTop.setVisibility(View.GONE);
		controllerBottom.startAnimation(bottomHideAnimation);
		controllerBottom.setVisibility(View.GONE);
		controllerHandler.removeMessages(HANDLE_WHAT_CONTROLLER_AUTO_HIDE);
		if (callback != null)
			callback.onVisiblityChanged(View.GONE);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : setPlayBtnState
	 * 2. ClassName  : VideoControllerView
	 * 3. Comment   : 비디오뷰의 재생상태를 받아서 재생 버튼 이미지를 변경
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 6:19:47
	 * </PRE>
	 * 
	 * @return void
	 * @param play
	 */
	public void setPlayBtnState(boolean play) {
		playPauseBtn.setImageResource(play ? R.drawable.bt_stop
				: R.drawable.bt_play);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : setBatteryText
	 * 2. ClassName  : VideoControllerView
	 * 3. Comment   : 배터리 용량 텍스트 변경
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 6:20:11
	 * </PRE>
	 * 
	 * @return void
	 * @param cs
	 */
	public void setBatteryText(CharSequence cs) {
		batteryText.setText(cs);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : setTimeText
	 * 2. ClassName  : VideoControllerView
	 * 3. Comment   : 현재 시간 텍스트 변경 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 6:20:34
	 * </PRE>
	 * 
	 * @return void
	 * @param cs
	 */
	public void setTimeText(CharSequence cs) {
		timeText.setText(cs);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : setQualityText
	 * 2. ClassName  : VideoControllerView
	 * 3. Comment   : 화질 표시 텍스트 변경 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 6:20:50
	 * </PRE>
	 * 
	 * @return void
	 * @param cs
	 */
	public void setQualityText(CharSequence cs) {
		qualityText.setText(cs);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : setCodecText
	 * 2. ClassName  : VideoControllerView
	 * 3. Comment   : 코덱 표시 텍스트 변경
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 6:21:09
	 * </PRE>
	 * 
	 * @return void
	 * @param cs
	 */
	public void setCodecText(CharSequence cs) {
		codecText.setText(cs);
	}

	public void setProgress(int progress) {
		seekBar.setProgress(progress);
	}

	public void setBufferProgress(int progress) {
		if (Math.abs(progress - mBuffer) < 10)
			mBuffer = progress;
		seekBar.setSecondaryProgress(progress);
	}

	public void setPlayData(PlayData data) {
		setProgress(Util.Math.getPercent(data.CURRENT, data.DURATION));
		currentText.setText(Util.Time.stringForTime(data.CURRENT));
		durationText.setText(Util.Time.stringForTime(data.DURATION));
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : setOnSeekBarChangeListener
	 * 2. ClassName  : VideoControllerView
	 * 3. Comment   : 영상 탐색바 리스너 설정
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 6:27:54
	 * </PRE>
	 * 
	 * @return void
	 * @param cb
	 */
	public void setCallback(VideoControllerCallback cb) {
		qualityText.setOnClickListener(cb);
		codecText.setOnClickListener(cb);

		playPauseBtn.setOnClickListener(cb);
		shakeBtn.setOnClickListener(cb);
		bolumeBtn.setOnClickListener(cb);
		brightBtn.setOnClickListener(cb);
		screenModeBtn.setOnClickListener(cb);
		captionBtn.setOnClickListener(cb);

		callback = cb;
	}

	public void setOnSystemUiVisibilityChangeListener() {
		if (Build.VERSION.SDK_INT >= 11) {
			setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
				@Override
				public void onSystemUiVisibilityChange(int visibility) {
					// TODO Auto-generated method stub
					if (visibility == View.SYSTEM_UI_FLAG_VISIBLE)
						showController();
				}
			});
		}
	};

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (callback != null)
			callback.onProgressChanged(seekBar, progress, fromUser);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		isTracking = true;
		if (callback != null)
			callback.onStartTrackingTouch(seekBar);
		controllerHandler.removeMessages(HANDLE_WHAT_CONTROLLER_AUTO_HIDE);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		isTracking = false;
		if (callback != null)
			callback.onStopTrackingTouch(seekBar);
		controllerHandler.sendEmptyMessageDelayed(
				HANDLE_WHAT_CONTROLLER_AUTO_HIDE, AUTO_HIDE_DELAY);
		callback.onStopTrackingTouch(seekBar);
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.mediacontroller;
	}

	float pointX = 0.0f;
	float pointY = 0.0f;
	boolean isMoving = false;
	long distanceX = 0;
	long distanceY = 0;
	int action = -1;
	int currentVol = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		mMultiTouchDetector.onTouchEvent(event);

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			controllerHandler.sendEmptyMessageDelayed(
					HANDLE_WHAT_CONTROLLER_AUTO_HIDE, AUTO_HIDE_DELAY);
			if (!(distanceX < 1000l && distanceX > -1000l) && action == 0) {
				if (callback != null)
					callback.onUp(distanceX);
			}
			if (action == -1)
				toggleController();
			if (isMoving) {
				distanceX = 0;
				distanceY = 0;
				pointX = 0.0f;
				pointY = 0.0f;
				action = -1;
				isMoving = false;
				return true;
			}
			return true;
		case MotionEvent.ACTION_DOWN:
			currentVol = Util.Phone.getVolume(getContext(),
					AudioManager.STREAM_MUSIC);
			pointX = event.getX();
			pointY = event.getY();
			return true;
		case MotionEvent.ACTION_MOVE:
			isMoving = true;
			float currentX = event.getX();
			float currentY = event.getY();
			distanceX = (long) (currentX - pointX) * 20;
			distanceY = (long) -(currentY - pointY) / 20;
			if (!(distanceX < 1000l && distanceX > -1000l)) {
				if (action == -1)
					action = 0;
				if (action == 0) {
					if (callback != null)
						callback.onScrollX(distanceX);
					return true;
				}
			} else if (!(distanceY < 1 && distanceY > -1)) {
				if (action == -1)
					action = 1;
				if (action == 1) {
					if (callback != null)
						callback.onScrollY(currentVol + distanceY);
					return true;
				}
			}
			return true;
		default:
			return false;
		}
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		batteryText = (TextView) findViewById(R.id.textView_video_battstat);
		timeText = (TextView) findViewById(R.id.textView_video_time);
		currentText = (TextView) findViewById(R.id.textView_video_currenttime);
		durationText = (TextView) findViewById(R.id.textView_video_duration);
		codecText = (TextView) findViewById(R.id.btn_video_changecodec);
		qualityText = (TextView) findViewById(R.id.btn_video_changequality);

		shakeBtn = (ImageButton) findViewById(R.id.btn_video_shake);
		bolumeBtn = (ImageButton) findViewById(R.id.btn_video_volumn);
		brightBtn = (ImageButton) findViewById(R.id.btn_video_bright);
		playPauseBtn = (ImageButton) findViewById(R.id.btn_video_playpause);
		screenModeBtn = (ImageButton) findViewById(R.id.btn_video_fullscreen);
		captionBtn = (ImageButton) findViewById(R.id.btn_video_caption);

		seekBar = (SeekBar) findViewById(R.id.seekBar_video_controller);
		seekBar.setOnSeekBarChangeListener(this);

		controllerTop = (LinearLayout) findViewById(R.id.video_controller_top);
		controllerBottom = (LinearLayout) findViewById(R.id.video_controller_bottom);

		topShowAnimation = AnimationUtils.loadAnimation(getContext(),
				R.anim.navi_top_up_ani);
		topHideAnimation = AnimationUtils.loadAnimation(getContext(),
				R.anim.navi_top_down_ani);
		bottomShowAnimation = AnimationUtils.loadAnimation(getContext(),
				R.anim.navi_up_ani);
		bottomHideAnimation = AnimationUtils.loadAnimation(getContext(),
				R.anim.navi_down_ani);
	}

}
