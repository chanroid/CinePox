package com.kr.busan.cw.cinepox.player;

import kr.co.chan.util.Util;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.VideoView;

public class HWVideoView extends VideoView implements OnPreparedListener {

	MediaPlayer mMediaPlayer;

	OnPreparedListener mPreparedListener;
	OnBufferingUpdateListener mBufferingUpdateListener;
	OnCompletionListener mCompletionListener;
	OnErrorListener mErrorListener;
	OnInfoListener mInfoListener;
	OnSeekCompleteListener mSeekCompleteListener;

	int mLayout = io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_ORIGIN;

	public HWVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}

	public HWVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}

	public HWVideoView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}

	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// widthMeasureSpec = getDefaultSize(
	// Util.Display.getWindowSize(getContext())[0], widthMeasureSpec);
	// heightMeasureSpec = getDefaultSize(
	// Util.Display.getWindowSize(getContext())[1], heightMeasureSpec);
	// setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	// }

	void init() {
		super.setOnPreparedListener(this);
	}

	void reset() {
		if (mMediaPlayer != null)
			mMediaPlayer.reset();
	}

	void stop() {
		if (mMediaPlayer != null)
			mMediaPlayer.stop();
	}

	int getVideoLayout() {
		return mLayout;
	}

	void setVideoLayout(int layout) {
		// 이 공식에 대해 면밀히 조사할 필요가 있을듯 ㅠㅠ
		if (mMediaPlayer == null)
			return;

		try {
			
			ViewGroup.LayoutParams localLayoutParams = getLayoutParams();

			int width = 0, height = 0;
			width = Util.Display.getWindowSize(getContext())[0];
			height = Util.Display.getWindowSize(getContext())[1];

			switch (layout) {
			case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_ORIGIN:
				width = mMediaPlayer.getVideoWidth();
				height = mMediaPlayer.getVideoHeight();
				break;
			case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_ZOOM:
				width = Util.Display.getWindowSize(getContext())[0];
				height = Util.Display.getWindowSize(getContext())[1];
				break;
			case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_SCALE:
				height = Util.Display.getWindowSize(getContext())[1];
				width = (height / mMediaPlayer.getVideoHeight())
						* mMediaPlayer.getVideoWidth();
				break;
			case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_STRETCH:
				width = Util.Display.getWindowSize(getContext())[0];
				height = Util.Display.getWindowSize(getContext())[1];
				break;
			default:
				return;
			}

			localLayoutParams.height = height;
			localLayoutParams.width = width;

			setLayoutParams(localLayoutParams);
			getHolder().setFixedSize(width, height);

		} catch (IllegalStateException e) {
		}

		mLayout = layout;
	}

	float getVideoAspectRatio() {
		if (mMediaPlayer != null)
			return mMediaPlayer.getVideoWidth() / mMediaPlayer.getVideoHeight();
		else
			return 0.0f;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		changeMedia(mp);
		if (mPreparedListener != null)
			mPreparedListener.onPrepared(mp);
	}

	/* VideoView callbacks */

	@Override
	public void setOnPreparedListener(OnPreparedListener l) {
		// TODO Auto-generated method stub
		mPreparedListener = l;
	}

	/* MediaPlayer callbacks */

	public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
		mBufferingUpdateListener = l;
		changeMediaCallback();
	}

	public void setOnInfoListener(OnInfoListener l) {
		mInfoListener = l;
		changeMediaCallback();
	}

	public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
		mSeekCompleteListener = l;
		changeMediaCallback();
	}

	void changeMedia(MediaPlayer mp) {
		if (mMediaPlayer != null) {
			mMediaPlayer.setOnBufferingUpdateListener(null);
			mMediaPlayer.setOnInfoListener(null);
			mMediaPlayer.setOnSeekCompleteListener(null);
		}
		mMediaPlayer = mp;
		changeMediaCallback();
	}

	void changeMediaCallback() {
		if (mMediaPlayer != null) {
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mMediaPlayer.setOnInfoListener(mInfoListener);
			mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
		}
	}

}
