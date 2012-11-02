package com.kr.busan.cw.cinepox.player.view;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.VitamioInstaller;
import kr.co.chan.util.Util;
import kr.co.chan.util.VerticalProgressBar;
import kr.co.chan.util.Classes.AnimatedImageView;
import view.CCBaseView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kr.busan.cw.cinepox.R;
import com.kr.busan.cw.cinepox.player.iface.VideoCallback;
import com.kr.busan.cw.cinepox.player.structs.PlayData;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : VideoView.java
 * 3. Package  : com.kr.busan.cw.cinepox.player.view
 * 4. Comment  : 코덱전환, 프로그레스 표시, 볼륨조절 뷰
 * 		(주의 : 비디오뷰 특성상 모델의 성질도 일부 가지고 있음)
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 16. 오전 9:46:35
 * </PRE>
 */
@SuppressLint("HandlerLeak")
public class VideoView extends CCBaseView implements OnPreparedListener,
		io.vov.vitamio.MediaPlayer.OnPreparedListener,
		OnBufferingUpdateListener,
		io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener,
		OnSeekCompleteListener,
		io.vov.vitamio.MediaPlayer.OnSeekCompleteListener, OnErrorListener,
		io.vov.vitamio.MediaPlayer.OnErrorListener, OnInfoListener,
		io.vov.vitamio.MediaPlayer.OnInfoListener, OnCompletionListener,
		android.media.MediaPlayer.OnCompletionListener {

	public VideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private VideoCallback callback;
	private HWVideoView hwVideoView;
	private SWVideoView swVideoView;
	private RelativeLayout videoContainer;
	private AnimatedImageView progressImage;
	private TextView captionView;
	private TextView centerText;
	private RelativeLayout volumeController;
	private VerticalProgressBar volumeProgress;
	private TextView volumeText;
	private AlertDialog pluginDialog;

	private Uri videoURI;

	/**
	 * <PRE>
	 * 1. Type : int
	 * 3. Comment  : 하드웨어 코덱을 나타내는 상수
	 * 4. 작성자   : 박찬우
	 * 5. 작성일   : 2012. 10. 16. 오전 9:55:30
	 * </PRE>
	 */
	public static final int CODEC_HW = 0;

	/**
	 * <PRE>
	 * 1. Type : int
	 * 3. Comment  : 소프트웨어 코덱을 나타내는 상수
	 * 4. 작성자   : 박찬우
	 * 5. 작성일   : 2012. 10. 16. 오전 9:56:05
	 * </PRE>
	 */
	public static final int CODEC_SW = 1;

	/**
	 * <PRE>
	 * 1. Type : int
	 * 3. Comment  : 핸들러 중앙 알림 텍스트 숨김 플래그
	 * 4. 작성자   : 박찬우
	 * 5. 작성일   : 2012. 10. 16. 오후 8:06:26
	 * </PRE>
	 */
	private static final int HANDLE_WHAT_CENTERTEXT_AUTO_HIDE = 301;

	/**
	 * <PRE>
	 * 1. Type : int
	 * 3. Comment  : 핸들러 볼륨표시 뷰 숨김 플래그 
	 * 4. 작성자   : 박찬우
	 * 5. 작성일   : 2012. 10. 16. 오후 8:07:00
	 * </PRE>
	 */
	private static final int HANDLE_WHAT_VOLUMEVIEW_AUTO_HIDE = 302;

	private static final int HANDLE_WHAT_SEEK_DEBUG = 303;

	/**
	 * <PRE>
	 * 1. Type : int
	 * 3. Comment  : 알림 및 볼륨표시 자동숨김 딜레이
	 * 4. 작성자   : 박찬우
	 * 5. 작성일   : 2012. 10. 16. 오후 8:07:19
	 * </PRE>
	 */
	private static final int AUTO_HIDE_DELAY = 1500;

	private int currentCodec = CODEC_HW;
	private boolean isSeeking = false;

	/**
	 * <PRE>
	 * 1. Type : Handler
	 * 3. Comment  : 알림 텍스트 및 볼륨표시 뷰 자동숨김 핸들러
	 * 4. 작성자   : 박찬우
	 * 5. 작성일   : 2012. 10. 16. 오후 8:01:23
	 * </PRE>
	 */
	private Handler videoHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLE_WHAT_CENTERTEXT_AUTO_HIDE:
				centerText.setVisibility(View.GONE);
				break;
			case HANDLE_WHAT_VOLUMEVIEW_AUTO_HIDE:
				volumeController.setVisibility(View.GONE);
				break;
			case HANDLE_WHAT_SEEK_DEBUG:
				if (callback != null)
					callback.onSeekComplete(VideoView.this);
				isSeeking = false;
				break;
			}
		};
	};

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.player_main;
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		videoContainer = (RelativeLayout) findViewById(R.id.video_container);
		addHWVideo();
		addVolumeView();
		if (!addSWVideo())
			currentCodec = CODEC_HW;
		progressImage = (AnimatedImageView) findViewById(R.id.progressBar_video_loading);
		captionView = (TextView) findViewById(R.id.textView_video_caption);
		centerText = (TextView) findViewById(R.id.textView_video_center);
	}

	public void setVideoLayout(int layout) {
		if (currentCodec == CODEC_HW)
			hwVideoView.setVideoLayout(layout);
		else if (currentCodec == CODEC_SW)
			swVideoView.setVideoLayout(layout);
	}

	public int getVideoLayout() {
		if (currentCodec == CODEC_HW)
			return hwVideoView.getVideoLayout();
		else if (currentCodec == CODEC_SW)
			return swVideoView.getVideoLayout();
		return io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_ZOOM;
	}

	public void setSystemUiVisibility(int visibility) {
		super.setSystemUiVisibility(visibility);
		if (currentCodec == CODEC_HW)
			hwVideoView.setSystemUiVisibility(visibility);
		else if (currentCodec == CODEC_SW)
			swVideoView.setSystemUiVisibility(visibility);
	};

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : setCallback
	 * 2. ClassName  : VideoView
	 * 3. Comment   : 콜백 인터페이스 설정 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 16. 오후 2:51:53
	 * </PRE>
	 * 
	 * @return void
	 * @param cb
	 */
	public void setCallback(VideoCallback cb) {
		callback = cb;
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : getCallback
	 * 2. ClassName  : VideoView
	 * 3. Comment   : 콜백 인터페이스 객체를 얻음 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 16. 오후 2:51:41
	 * </PRE>
	 * 
	 * @return VideoCallback
	 * @return
	 */
	public VideoCallback getCallback() {
		return callback;
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : setCenterText
	 * 2. ClassName  : VideoView
	 * 3. Comment   : 플레이어 중간의 상태알림 텍스트 표시 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 8:26:19
	 * </PRE>
	 * 
	 * @return void
	 * @param text
	 */
	public void setCenterText(CharSequence text) {
		volumeController.setVisibility(View.GONE);
		videoHandler.removeMessages(HANDLE_WHAT_CENTERTEXT_AUTO_HIDE);
		if (progressImage.getVisibility() != View.VISIBLE)
			centerText.setVisibility(View.VISIBLE);
		if ("".equals(text))
			centerText.setVisibility(View.GONE);
		centerText.setText(text);
		videoHandler.sendEmptyMessageDelayed(HANDLE_WHAT_CENTERTEXT_AUTO_HIDE,
				AUTO_HIDE_DELAY);
	}

	public void setCenterText(int resid) {
		setCenterText(getContext().getString(resid));
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : showLoading
	 * 2. ClassName  : VideoView
	 * 3. Comment   : 로딩바(씨네폭스 로고) 표시 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 8:20:20
	 * </PRE>
	 * 
	 * @return void
	 */
	public void showLoading() {
		progressImage.setVisibility(View.VISIBLE);
		centerText.setVisibility(View.GONE);
		volumeController.setVisibility(View.GONE);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : hideLoading
	 * 2. ClassName  : VideoView
	 * 3. Comment   : 로딩바(씨네폭스 로고) 숨김 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 8:20:42
	 * </PRE>
	 */
	public void hideLoading() {
		progressImage.setVisibility(View.GONE);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : addVolumeView
	 * 2. ClassName  : VideoView
	 * 3. Comment   : 볼륨을 표시하는 레이아웃을 추가함
	 * 				일반적인 경우 따로 호출해줄 필요는 없음
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 8:14:50
	 * </PRE>
	 * 
	 * @return boolean
	 * @return
	 */
	private boolean addVolumeView() {
		if (volumeController == null) {
			volumeController = (RelativeLayout) findViewById(R.id.layout_player_volumecontrol);
			volumeProgress = (VerticalProgressBar) findViewById(R.id.seekbar_player_volume);
			volumeText = (TextView) findViewById(R.id.textView_player_volume);
			initVolumeView();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : initVolumeView
	 * 2. ClassName  : VideoView
	 * 3. Comment   : 볼륨 레이아웃 표시값 초기화 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 8:18:31
	 * </PRE>
	 */
	private void initVolumeView() {
		volumeProgress.setMax(Util.Phone.getMaxVolume(getContext(),
				AudioManager.STREAM_MUSIC));
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : setVolume
	 * 2. ClassName  : VideoView
	 * 3. Comment   : 볼륨 표시 레이아웃에 현재 볼륨을 표시함 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 8:14:31
	 * </PRE>
	 * 
	 * @param volume
	 */
	public void setVolume(int volume) {
		centerText.setVisibility(View.GONE);
		volumeController.setVisibility(View.VISIBLE);
		videoHandler.removeMessages(HANDLE_WHAT_VOLUMEVIEW_AUTO_HIDE);
		volumeText.setText(volume + "");
		volumeProgress.setProgress(volume);
		videoHandler.sendEmptyMessageDelayed(HANDLE_WHAT_VOLUMEVIEW_AUTO_HIDE,
				AUTO_HIDE_DELAY);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : addSWVideo
	 * 2. ClassName  : VideoView
	 * 3. Comment   : 소프트웨어 코덱이 설치되어 있을 시 호출하면
	 * 				소프트웨어 비디오 뷰를 추가함 (중복호출해도 상관없음)
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 7:19:26
	 * </PRE>
	 * 
	 * @return boolean
	 */
	public boolean addSWVideo() {
		if (isPluginInstalled() && swVideoView == null) {
			swVideoView = new SWVideoView(getContext());
			swVideoView.setVisibility(View.INVISIBLE);
			videoContainer.addView(swVideoView, 0, new LayoutParams(-1, -1));
			initSWVideo();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : initSWVideo
	 * 2. ClassName  : VideoView
	 * 3. Comment   : 소프트웨어 비디오 뷰 설정 초기화(리스너 등)
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 7:38:58
	 * </PRE>
	 * 
	 */
	private void initSWVideo() {
		swVideoView.setOnPreparedListener(this);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : addHWVideo
	 * 2. ClassName  : VideoView
	 * 3. Comment   : 하드웨어 코덱을 사용하는 비디오뷰를 추가함
	 * 				일반적인 경우 따로 호출해줄 필요는 없음
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 7:35:59
	 * </PRE>
	 * 
	 * @return boolean
	 */
	private boolean addHWVideo() {
		if (hwVideoView == null) {
			hwVideoView = (HWVideoView) findViewById(R.id.surfaceView_video_view_hw);
			initHWVideo();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : initHWVideo
	 * 2. ClassName  : VideoView
	 * 3. Comment   : 하드웨어 비디오 뷰 설정 초기화(리스너 등)
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 7:38:58
	 * </PRE>
	 */
	public void initHWVideo() {
		hwVideoView.setOnPreparedListener(this);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : isPluginInstalled
	 * 2. ClassName  : VideoView
	 * 3. Comment   : Vitamio 플러그인 설치여부 확인
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 7:41:25
	 * </PRE>
	 * 
	 * @return boolean
	 */
	public boolean isPluginInstalled() {
		try {
			VitamioInstaller.checkVitamioInstallation(getContext());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void showPluginInstall() {
		if (pluginDialog == null)
			buildPluginInstallDialog();
		pluginDialog.show();
	}

	private void buildPluginInstallDialog() {
		if (pluginDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setTitle(R.string.alert);
			builder.setMessage(getContext().getString(R.string.noti_plugin));
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.next,
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
							Util.App.goMarket(getContext(), pkgname);
						}
					});
			pluginDialog = builder.create();
		}
	}

	static {
		System.loadLibrary("helloneon");
	}

	/**
	 * <PRE>
	 * 1. MethodName : isNeon
	 * 2. ClassName  : VideoModel
	 * 3. Comment   : 
	 * 		arm7-vfp와 arm7-neon 구분을 위한 JNI 메서드
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 1:46:05
	 * </PRE>
	 * 
	 * @return boolean
	 * @return
	 */
	public native boolean isNeon();

	public boolean setCodec(int codec) {
		if (codec == currentCodec)
			return false;

		if (codec == CODEC_SW && !isPluginInstalled()) {
			showPluginInstall();
			return false;
		}

		currentCodec = codec;

		if (currentCodec == CODEC_HW)
			swVideoView.pause();
		else if (currentCodec == CODEC_SW) {
			addSWVideo();
			hwVideoView.pause();
		}

		swVideoView.setVisibility(currentCodec == CODEC_SW ? VISIBLE
				: INVISIBLE);
		hwVideoView.setVisibility(currentCodec == CODEC_HW ? VISIBLE
				: INVISIBLE);
		setVideoURI();
		return true;
	}

	public int getCodec() {
		return currentCodec;
	}

	public boolean isSeeking() {
		return isSeeking;
	}

	public boolean canPause() {
		if (currentCodec == CODEC_HW) {
			return hwVideoView.canPause();
		} else if (currentCodec == CODEC_SW) {
			return swVideoView.canPause();
		} else {
			return false;
		}
	}

	public boolean canSeek() {
		if (currentCodec == CODEC_HW) {
			return hwVideoView.canSeekBackward()
					|| hwVideoView.canSeekForward();
		} else if (currentCodec == CODEC_SW) {
			return swVideoView.canSeekBackward()
					|| swVideoView.canSeekForward();
		} else {
			return false;
		}
	}

	public int getBufferPercentage() {
		if (currentCodec == CODEC_HW) {
			return hwVideoView.getBufferPercentage();
		} else if (currentCodec == CODEC_SW) {
			return swVideoView.getBufferPercentage();
		} else {
			return 0;
		}
	}

	public long getCurrentPosition() {
		if (currentCodec == CODEC_HW) {
			return hwVideoView.getCurrentPosition();
		} else if (currentCodec == CODEC_SW) {
			return swVideoView.getCurrentPosition();
		} else {
			return 0l;
		}
	}

	public long getDuration() {
		if (currentCodec == CODEC_HW) {
			return hwVideoView.getDuration();
		} else if (currentCodec == CODEC_SW) {
			return swVideoView.getDuration();
		} else {
			return 0l;
		}
	}

	public boolean isPlaying() {
		if (currentCodec == CODEC_HW) {
			return hwVideoView.isPlaying();
		} else if (currentCodec == CODEC_SW) {
			return swVideoView.isPlaying();
		} else {
			return false;
		}
	}

	public PlayData getPlayData() {
		PlayData data = new PlayData();
		long current = getCurrentPosition();
		if (current > getDuration())
			current %= getDuration();
		data.CURRENT = current;
		data.DURATION = getDuration();
		return data;
	}

	public void pause() {
		if (currentCodec == CODEC_HW) {
			hwVideoView.pause();
		} else if (currentCodec == CODEC_SW) {
			swVideoView.pause();
		} else
			return;
		setCenterText(getContext().getString(R.string.pause));
	}

	public void resume() {
		if (currentCodec == CODEC_HW) {
			hwVideoView.resume();
		} else if (currentCodec == CODEC_SW) {
			swVideoView.resume();
		}
	}

	public boolean seekTo(long l) {
		if (!canSeek())
			return false;

		if (currentCodec == CODEC_HW) {
			hwVideoView.seekTo((int) l);
		} else if (currentCodec == CODEC_SW) {
			swVideoView.seekTo(l);
		} else {
			return false;
		}
		isSeeking = true;
		showLoading();
		return true;
	}

	public Uri getVideoURI() {
		return videoURI;
	}

	public void setVideoURI() {
		setVideoURI(videoURI);
	}

	public void setVideoURI(Uri uri) {
		if (currentCodec == CODEC_HW) {
			hwVideoView.setVideoURI(uri);
		} else if (currentCodec == CODEC_SW) {
			swVideoView.setVideoURI(uri);
		} else
			return;
		videoURI = uri;
		showLoading();
	}

	public void setCaptionText(CharSequence cs) {
		captionView.setText(Html.fromHtml(cs.toString()));
	}

	public void start() {
		if (currentCodec == CODEC_HW) {
			hwVideoView.start();
		} else if (currentCodec == CODEC_SW) {
			swVideoView.start();
		} else
			return;
		setCenterText(getContext().getString(R.string.play));
	}

	/**
	 * MediaPlayer callback interface methods
	 */

	@Override
	public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
		onInfo(arg1, arg2);
		return false;
	}

	@Override
	public boolean onInfo(android.media.MediaPlayer mp, int what, int extra) {
		onInfo(what, extra);
		return false;
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		onError(arg1, arg2);
		return false;
	}

	@Override
	public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
		onError(what, extra);
		return false;
	}

	@Override
	public void onSeekComplete(MediaPlayer arg0) {
		onSeekComplete();
	}

	@Override
	public void onSeekComplete(android.media.MediaPlayer mp) {
		onSeekComplete();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
		onBufferingUpdate(arg1);
	}

	@Override
	public void onBufferingUpdate(android.media.MediaPlayer mp, int percent) {
		onBufferingUpdate(percent);
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		arg0.setOnBufferingUpdateListener(this);
		arg0.setOnCompletionListener(this);
		arg0.setOnErrorListener(this);
		arg0.setOnInfoListener(this);
		arg0.setOnSeekCompleteListener(this);
		onPrepared();
	}

	@Override
	public void onPrepared(android.media.MediaPlayer mp) {
		mp.setOnBufferingUpdateListener(this);
		mp.setOnCompletionListener(this);
		mp.setOnErrorListener(this);
		mp.setOnInfoListener(this);
		mp.setOnSeekCompleteListener(this);
		onPrepared();
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		onCompletion();
	}

	@Override
	public void onCompletion(android.media.MediaPlayer mp) {
		onCompletion();
	}

	/**
	 * interface wrapper methods
	 */

	public void onPrepared() {
		if (callback != null)
			callback.onPrepared(this);
		hideLoading();
	}

	public void onInfo(int what, int extra) {
		if (callback != null)
			callback.onInfo(this, what, extra);
		switch (what) {
		case 703:
		case 701:
			showLoading();
			break;
		case 700:
		case 702:
			hideLoading();
			break;
		}
	}

	public void onError(int what, int extra) {
		if (callback != null)
			callback.onError(this, what, extra);
	}

	public void onSeekComplete() {
		videoHandler.sendEmptyMessageDelayed(HANDLE_WHAT_SEEK_DEBUG, 1000);
		hideLoading();
		start();
	}

	public void onBufferingUpdate(int progress) {
		if (callback != null)
			callback.onBufferingUpdate(this, progress);
	}

	public void onCompletion() {
		if (callback != null)
			callback.onCompletion(this);
	}

}
