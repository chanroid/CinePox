package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.LoginPageView;

public interface LoginPageViewCallback {
	public void onBackClick(LoginPageView view);
	public void onAutoLoginCheck(LoginPageView view, boolean checked);
	public void onLoginClick(LoginPageView view);
	public void onMissedIdClick(LoginPageView view);
	public void onMissedPwClick(LoginPageView view);
	public void onJoinClick(LoginPageView view);
}
