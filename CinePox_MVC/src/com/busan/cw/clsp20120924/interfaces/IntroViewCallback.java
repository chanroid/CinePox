package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.IntroView;

public interface IntroViewCallback {
	public void onUpdateConfirm(IntroView view);
	public void onUpdateCancel(IntroView view);
	public void onErrorConfirm(IntroView view);
}
