/**
 * 0. Project  : CinePlayer_MVC
 *
 * 1. FileName : VolumeControllView.java
 * 2. Package : com.kr.busan.cw.cinepox.player.view
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 18. 오후 2:48:57
 * 6. 변경이력 : 
 *		2012. 10. 18. 오후 2:48:57 : 신규
 *
 */
package com.kr.busan.cw.cinepox.player.view;

import kr.co.chan.util.Util;
import view.CCBaseView;
import android.content.Context;
import android.media.AudioManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.kr.busan.cw.cinepox.R;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : VolumeControllView.java
 * 3. Package  : com.kr.busan.cw.cinepox.player.view
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 18. 오후 2:48:57
 * </PRE>
 */
public class VolumeControllView extends CCBaseView {

	private SeekBar volumeSeekbar;
	private ImageView muteButton;

	public VolumeControllView(Context context) {
		super(context);
	}

	@Override
	public void allocViews() {
		volumeSeekbar = (SeekBar) findViewById(R.id.seekBar_volume);
		volumeSeekbar.setMax(Util.Phone.getMaxVolume(getContext(),
				AudioManager.STREAM_MUSIC));
		muteButton = (ImageView) findViewById(R.id.imageView1);
	}

	public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
		volumeSeekbar.setOnSeekBarChangeListener(l);
	}

	public void setOnClickListener(OnClickListener l) {
		muteButton.setOnClickListener(l);
	}

	public void setMax(int max) {
		volumeSeekbar.setMax(max);
	}

	public void setVolume(int volume) {
		volumeSeekbar.setProgress(volume);
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.volumn;
	}

}
