package com.kr.busan.cw.cinepox.player.iface;

import com.kr.busan.cw.cinepox.player.view.VideoView;

public interface VideoCallback {
	public void onPrepared(VideoView view);
	public void onInfo(VideoView view, int what, int extra);
	public void onError(VideoView view, int what, int extra);
	public void onSeekComplete(VideoView view);
	public void onBufferingUpdate(VideoView view, int progress);
	public void onCompletion(VideoView view);
}
