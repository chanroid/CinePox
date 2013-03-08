package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.AppADView;

public interface AppADViewCallback {
	public void onNotseeCheck(AppADView view, boolean check);
	public void onSkipClick(AppADView view);
	public void onADImageClick(AppADView view);
	public void onADLoadError(AppADView view);
}
