/**
 * 0. Project  : CinePlayer_MVC
 *
 * 1. FileName : VideoControllerCallback.java
 * 2. Package : com.kr.busan.cw.cinepox.player.iface
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 16. 오후 8:22:16
 * 6. 변경이력 : 
 *		2012. 10. 16. 오후 8:22:16 : 신규
 *
 */
package com.kr.busan.cw.cinepox.player.iface;

import android.view.View.OnClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : VideoControllerCallback.java
 * 3. Package  : com.kr.busan.cw.cinepox.player.iface
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 16. 오후 8:22:16
 * </PRE>
 */
public interface VideoControllerCallback extends OnClickListener,
		OnSeekBarChangeListener {
	public void onUp(long distance);
	public void onScrollX(long distance);
	public void onScrollY(long distance);
}
