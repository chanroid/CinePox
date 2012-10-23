/**
	FFmpeg is licensed under the GNU Lesser General Public License (LGPL) version 2.1 or later. 
	However, FFmpeg incorporates several optional parts and optimizations that are covered by the 
	GNU General Public License (GPL) version 2 or later. If those parts get used the GPL applies to all of FFmpeg.
	Read the license texts to learn how this affects programs built on top of FFmpeg or reusing FFmpeg. 
	You may also wish to have a look at the GPL FAQ.
	Note that FFmpeg is not available under any other licensing terms, 
	especially not proprietary/commercial ones, not even in exchange for payment.
 **/

package com.kr.busan.cw.cinepox.player;

import kr.co.chan.util.Util;
import android.content.Context;
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
		case io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_ZOOM:
			ViewGroup.LayoutParams localLayoutParams = getLayoutParams();
			int width = Util.Display.getWindowSize(getContext())[0];
			int height = Util.Display.getWindowSize(getContext())[1];
			localLayoutParams.height = height;
			localLayoutParams.width = width;
			setLayoutParams(localLayoutParams);
			getHolder().setFixedSize(width, height);
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
