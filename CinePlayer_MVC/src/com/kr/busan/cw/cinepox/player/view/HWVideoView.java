package com.kr.busan.cw.cinepox.player.view;

import utils.DisplayUtils;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.VideoView;

public class HWVideoView extends VideoView implements OnPreparedListener {

	private MediaPlayer mMediaPlayer;

	private OnPreparedListener mPreparedListener;
	private OnBufferingUpdateListener mBufferingUpdateListener;
	private OnCompletionListener mCompletionListener;
	private OnErrorListener mErrorListener;
	private OnInfoListener mInfoListener;
	private OnSeekCompleteListener mSeekCompleteListener;

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
	// DisplayUtils.getWindowSize(getContext())[0], widthMeasureSpec);
	// heightMeasureSpec = getDefaultSize(
	// DisplayUtils.getWindowSize(getContext())[1], heightMeasureSpec);
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

			int width = 0, height = 0;
			width = DisplayUtils.getWindowSize(getContext())[0];
			height = DisplayUtils.getWindowSize(getContext())[1];

			switch (layout) {
			case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_ORIGIN:
				width = mMediaPlayer.getVideoWidth();
				height = mMediaPlayer.getVideoHeight();
				break;
			case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_ZOOM:
				width = DisplayUtils.getWindowSize(getContext())[0];
				height = DisplayUtils.getWindowSize(getContext())[1];
				break;
			case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_SCALE:
				height = DisplayUtils.getWindowSize(getContext())[1];
				width = (height / mMediaPlayer.getVideoHeight())
						* mMediaPlayer.getVideoWidth();
				break;
			case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_STRETCH:
				width = DisplayUtils.getWindowSize(getContext())[0];
				height = DisplayUtils.getWindowSize(getContext())[1];
				break;
			default:
				return;
			}
			setVideoSize(width, height);
		} catch (IllegalStateException e) {
		}

		mLayout = layout;
	}

	public boolean setScale(float scale) {
		// TODO Auto-generated method stub
		if (mMediaPlayer == null)
			return false;

		try {
			int width = (int) ((float) mMediaPlayer.getVideoWidth() * scale);
			int height = (int) ((float) mMediaPlayer.getVideoHeight() * scale);
			return setVideoSize(width, height);
		} catch (IllegalStateException e) {
			return false;
		}

	}

	public boolean setVideoSize(int width, int height) {

		if (Build.VERSION.SDK_INT < 11) {
			int[] windowsize = DisplayUtils.getWindowSize(getContext());
			int windowwidth = windowsize[0];
			int windowheight = windowsize[1];
			if (width > windowwidth || height > windowheight)
				return false;
		}

		ViewGroup.LayoutParams localLayoutParams = getLayoutParams();
		localLayoutParams.height = height;
		localLayoutParams.width = width;

		setLayoutParams(localLayoutParams);
		getHolder().setSizeFromLayout();
		invalidate();
		return true;
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

	@Override
	public void setOnCompletionListener(OnCompletionListener l) {
		mCompletionListener = l;
		changeMediaCallback();
	}

	@Override
	public void setOnErrorListener(OnErrorListener l) {
		mErrorListener = l;
		changeMediaCallback();
	}

	void changeMedia(MediaPlayer mp) {
		if (mMediaPlayer != null) {
			mMediaPlayer.setOnBufferingUpdateListener(null);
			mMediaPlayer.setOnInfoListener(null);
			mMediaPlayer.setOnSeekCompleteListener(null);
			mMediaPlayer.setOnCompletionListener(null);
			mMediaPlayer.setOnErrorListener(null);
		}
		mMediaPlayer = mp;
		changeMediaCallback();
	}

	void changeMediaCallback() {
		if (mMediaPlayer != null) {
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mMediaPlayer.setOnInfoListener(mInfoListener);
			mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
			mMediaPlayer.setOnCompletionListener(mCompletionListener);
			mMediaPlayer.setOnErrorListener(mErrorListener);
		}
	}

}
