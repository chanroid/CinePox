package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.LoginModel;

public interface LoginModelCallback {
	public void onLoginSuccess(LoginModel model);

	public void onLoginStart(LoginModel model);

	public void onLoginError(LoginModel model, String message);
}
