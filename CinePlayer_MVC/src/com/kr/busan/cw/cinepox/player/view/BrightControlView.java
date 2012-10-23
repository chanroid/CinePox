/**
 * 0. Project  : CinePlayer_MVC
 *
 * 1. FileName : BrightControlView.java
 * 2. Package : com.kr.busan.cw.cinepox.player.view
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 18. 오후 2:49:15
 * 6. 변경이력 : 
 *		2012. 10. 18. 오후 2:49:15 : 신규
 *
 */
package com.kr.busan.cw.cinepox.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.kr.busan.cw.cinepox.R;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : BrightControlView.java
 * 3. Package  : com.kr.busan.cw.cinepox.player.view
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 18. 오후 2:49:15
 * </PRE>
 */
public class BrightControlView extends LinearLayout {

	private SeekBar brightSeekbar;

	public BrightControlView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BrightControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BrightControlView(Context context) {
		super(context);
		init();
	}

	private void init() {
		RelativeLayout brightlayout = (RelativeLayout) View.inflate(getContext(),
				R.layout.bright, null);
		addView(brightlayout, -1, -1);
		
		brightSeekbar = (SeekBar) findViewById(R.id.seekBar_brightness);
		brightSeekbar.setMax(90);
	}
	
	public void setOnSeekbarChangeListener(OnSeekBarChangeListener l) {
		brightSeekbar.setOnSeekBarChangeListener(l);
	}
	
	public void setProgress(int progress) {
		brightSeekbar.setProgress(progress);
	}

}
