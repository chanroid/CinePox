package com.busan.cw.clsp20120924.controller;

import utils.ThemeUtils;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.base.DomainManager;
import com.busan.cw.clsp20120924.interfaces.LoginModelCallback;
import com.busan.cw.clsp20120924.interfaces.LoginPageViewCallback;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.model.LoginModel;
import com.busan.cw.clsp20120924.model.PreferenceModel;
import com.busan.cw.clsp20120924.view.LoginPageView;

public class CinepoxLoginActivity extends Activity implements Constants,
		LoginModelCallback, LoginPageViewCallback {
	private LoginPageView view;
	private LoginModel loginModel;
	private PreferenceModel prefModel;

	private static final int REQUEST_JOIN = 100;

	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		ThemeUtils.darkNoTitle(this);
		super.onCreate(savedInstanceState);
		loginModel = new LoginModel(this);
		loginModel.setCallback(this);
		prefModel = new PreferenceModel(this);
		view = new LoginPageView(this);
		view.setCallback(this);
		setContentView(view);
		checkAutologin();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_JOIN:
				// 뭔가 받아서 로그인 처리
				String id = data.getStringExtra(EXTRA_ID);
				String pw = data.getStringExtra(EXTRA_PASSWORD);
				view.setIdText(id);
				view.setPwText(pw);
				loginModel.login(id, pw);
				break;
			}
		} else if (resultCode == RESULT_CANCELED) {
			switch (requestCode) {
			case REQUEST_JOIN:
				break;
			}
		}

	}

	private void checkAutologin() {
		if (prefModel.isAutoLogin()
				&& EXTRA_AUTO_LOGIN_CINEPOX.equals(prefModel
						.getAutoLoginAction())) {
			String id = prefModel.getAutoLoginId();
			String pw = prefModel.getAutoLoginPw();
			view.setIdText(id);
			view.setPwText(pw);
			loginModel.login(id, pw);
		}
	}

	private CinepoxAppModel app() {
		return (CinepoxAppModel) getApplication();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, SelectLoginMethodActivity.class));
		super.onBackPressed();
	}

	@Override
	public void onLoginSuccess(LoginModel model) {
		// TODO Auto-generated method stub
		view.dismissLoaing();
		if (view.isAutologinChecked()) {
			prefModel.setAutoLogin(true);
			prefModel.setAutoLoginAction(EXTRA_AUTO_LOGIN_CINEPOX);
		} else {
			prefModel.setAutoLogin(false);
			prefModel.setAutoLoginAction(null);
		}
		prefModel.setAutoLoginId(view.getIdText());
		prefModel.setAutoLoginPw(view.getPwText());
		startActivity(new Intent(this, MainFragmentActivity.class));
		finish();
		// 메인으로 이동
	}

	@Override
	public void onLoginStart(LoginModel model) {
		// TODO Auto-generated method stub
		view.showLoading();
	}

	@Override
	public void onLoginError(LoginModel model, String message) {
		// TODO Auto-generated method stub
		view.dismissLoaing();
		view.showError(message);
	}

	@Override
	public void onBackClick(LoginPageView view) {
		// TODO Auto-generated method stub
		onBackPressed();
	}

	@Override
	public void onLoginClick(LoginPageView view) {
		// TODO Auto-generated method stub
		loginModel.login(view.getIdText(), view.getPwText());
	}

	@Override
	public void onMissedIdClick(LoginPageView view) {
		// TODO Auto-generated method stub
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DomainManager
				.getWebDomain(this) + app().getAppConfig().getFindIdUrl())));
	}

	@Override
	public void onMissedPwClick(LoginPageView view) {
		// TODO Auto-generated method stub
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DomainManager
				.getWebDomain(this) + app().getAppConfig().getFindPwUrl())));
	}

	@Override
	public void onJoinClick(LoginPageView view) {
		// TODO Auto-generated method stub
		startActivityForResult(new Intent(this, CinepoxJoinActivity.class),
				REQUEST_JOIN);
	}

	@Override
	public void onAutoLoginCheck(LoginPageView view, boolean checked) {
		// TODO Auto-generated method stub

	};
}
