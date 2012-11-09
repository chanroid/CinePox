/**
	FFmpeg is licensed under the GNU Lesser General Public License (LGPL) version 2.1 or later. 
	However, FFmpeg incorporates several optional parts and optimizations that are covered by the 
	GNU General Public License (GPL) version 2 or later. If those parts get used the GPL applies to all of FFmpeg.
	Read the license texts to learn how this affects programs built on top of FFmpeg or reusing FFmpeg. 
	You may also wish to have a look at the GPL FAQ.
	Note that FFmpeg is not available under any other licensing terms, 
	especially not proprietary/commercial ones, not even in exchange for payment.
 **/

package com.kr.busan.cw.cinepox.player.view;

import kr.co.chan.util.Util;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class SWVideoView extends io.vov.vitamio.widget.VideoView {

	private int mVideoLayout = 1;

	public SWVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SWVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SWVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public boolean setScale(float scale) {
		// TODO Auto-generated method stub
		try {
			int width = (int) ((float) getVideoWidth() * scale);
			int height = (int) ((float) getVideoHeight() * scale);
			return setVideoSize(width, height);
		} catch (IllegalStateException e) {
			return false;
		}

	}

	public boolean setVideoSize(int width, int height) {

		if (Build.VERSION.SDK_INT < 11) {
			int[] windowsize = Util.Display.getWindowSize(getContext());
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

	public int getVideoLayout() {
		return mVideoLayout;
	}

	@Override
	public void setSystemUiVisibility(int visibility) {
		// TODO Auto-generated method stub
		super.setSystemUiVisibility(visibility);
		setVideoLayout(mVideoLayout);
	}

	@Override
	public void setVideoLayout(int layout, float aspectRatio) {
		// TODO Auto-generated method stub

		switch (layout) {
		case VIDEO_LAYOUT_ZOOM:
			ViewGroup.LayoutParams localLayoutParams = getLayoutParams();
			localLayoutParams.width = Util.Display.getWindowSize(getContext())[0];
			localLayoutParams.height = Util.Display.getWindowSize(getContext())[1];
			setLayoutParams(localLayoutParams);
			getHolder().setFixedSize(localLayoutParams.width,
					localLayoutParams.height);
			break;
		default:
			super.setVideoLayout(layout, aspectRatio);
			break;
		}

		mVideoLayout = layout;
	}

	public void setVideoLayout(int layout) {
		// TODO Auto-generated method stub
		setVideoLayout(layout, getVideoAspectRatio());
	}

}
