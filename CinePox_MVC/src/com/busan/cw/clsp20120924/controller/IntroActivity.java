package com.busan.cw.clsp20120924.controller;

import java.net.MalformedURLException;

import utils.LogUtils.l;
import utils.ManifestUtils;
import utils.ThemeUtils;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.interfaces.ConfigModelCallback;
import com.busan.cw.clsp20120924.interfaces.IntroViewCallback;
import com.busan.cw.clsp20120924.interfaces.LoginModelCallback;
import com.busan.cw.clsp20120924.interfaces.Me2DayModelUserInfoCallback;
import com.busan.cw.clsp20120924.interfaces.TwitterModelUserInfoCallback;
import com.busan.cw.clsp20120924.interfaces.YozmModelUserInfoCallback;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.model.ConfigModel;
import com.busan.cw.clsp20120924.model.FacebookModel;
import com.busan.cw.clsp20120924.model.LoginModel;
import com.busan.cw.clsp20120924.model.Me2DayModel;
import com.busan.cw.clsp20120924.model.PreferenceModel;
import com.busan.cw.clsp20120924.model.SNSLoginData;
import com.busan.cw.clsp20120924.model.TwitterModel;
import com.busan.cw.clsp20120924.model.YozmModel;
import com.busan.cw.clsp20120924.view.IntroView;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ImageRequest;

public class IntroActivity extends Activity implements Constants,
		ConfigModelCallback, IntroViewCallback, YozmModelUserInfoCallback,
		Me2DayModelUserInfoCallback, GraphUserCallback,
		TwitterModelUserInfoCallback, LoginModelCallback {

	private Me2DayModel me2Model;
	private YozmModel yozmModel;
	private FacebookModel fbModel;
	private TwitterModel twModel;
	private LoginModel loginModel;

	private ConfigModel confModel;
	private CinepoxAppModel appModel;
	private PreferenceModel prefModel;
	private IntroView view;

	private static final int REQUEST_FACEBOOK_LOGIN = 100;
	private static final int REQUEST_JOIN_ACTION = 101;

	private boolean isCritical = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// Uri 호출로 실행한 경우 테스트 모드로 전환
		ThemeUtils.darkNoTitle(this);
		super.onCreate(savedInstanceState);
		prefModel = new PreferenceModel(this);
		confModel = new ConfigModel(this);
		appModel = (CinepoxAppModel) getApplication();
		loginModel = new LoginModel(this);
		loginModel.setCallback(this);
		view = new IntroView(this);
		view.setCallback(this);

		setContentView(view);
		confModel.setCallback(this);
		confModel.loadConfig();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_FACEBOOK_LOGIN:
				fbModel = new FacebookModel(this);
				fbModel.loadFBUserInfo(Session.getActiveSession(), this);
				break;
			case REQUEST_JOIN_ACTION:
				goLogin();
				break;
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {
			switch (requestCode) {
			case REQUEST_JOIN_ACTION:
			case REQUEST_FACEBOOK_LOGIN:
				// 로그인 실패시 그냥 로그인창으로 이동
				startActivity(new Intent(this, SelectLoginMethodActivity.class));
				finish();
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void onLoadConfigStart(ConfigModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadConfigError(ConfigModel model, String error) {
		// TODO Auto-generated method stub
		isCritical = true;
		view.showError(error);
		// 창을 닫을때 바로 종료되므로 따로 처리할 필요 없음
	}

	@Override
	public void onLoadConfigComplete(ConfigModel model) {
		l.i("onLoadConfigComplete");
		// 업데이트 타입이 기본이 아니고, 업데이트를 띄워야 할 경우 업데이트 창 띄움
		if (appModel.getAppConfig().getRecentVerCode() > ManifestUtils
				.getVersionCode(this)
				&& appModel.getAppConfig().isUpdate()
				&& !ConfigModel.UPDATE_DEFAULT.equals(model.getUpdateType())) {
			l.d("update");
			view.showUpdate(ConfigModel.UPDATE_CONFIRM.equals(model
					.getUpdateType()));
			return;
		}
		// 광고 있으면 띄우고 없으면 넘어가고
		// String adimage = ((CinepoxAppModel) getApplication()).getAppConfig()
		// .getAdImageUrl();
		// if ("".equals(adimage))
		// goLogin();
		// else
		// startActivityForResult(new Intent(this, AppADActivity.class), 0);
		// 2013-02-20 광고는 메인에서 띄우는걸로 변경함
		goLogin();
	}

	private void goLogin() {
		l.i("gologin");
		// 자동로그인 여부 확인
		// 씨네폭스 로그인이면 씨네폭스 로그인 페이지로 이동
		// 소셜네트워크 로그인이면 로그인방법 선택 페이지로 이동
		// 자동로그인 관련 처리는 해당 페이지로 넘긴 후 실행
		//
		// 2013-02-20 자동로그인 관련 처리는 여기서 바로 실행하도록 변경
		if (prefModel.isAutoLogin()) {
			if (EXTRA_AUTO_LOGIN_FACEBOOK
					.equals(prefModel.getAutoLoginAction())) {
				l.i("facebook autologin");
				startActivityForResult(new Intent(this,
						FacebookLoginActivity.class), REQUEST_FACEBOOK_LOGIN);
			} else if (EXTRA_AUTO_LOGIN_ME2DAY.equals(prefModel
					.getAutoLoginAction())) {
				me2Model = new Me2DayModel(this);
				me2Model.setUserInfoCallback(this);
				me2Model.loadMe2UserInfo(prefModel.getMe2LoginId());
			} else if (EXTRA_AUTO_LOGIN_TWITTER.equals(prefModel
					.getAutoLoginAction())) {
				twModel = new TwitterModel(this);
				twModel.setUserInfoCallback(this);
				twModel.loadTwitUserInfo(prefModel.getTwitterAccessToken());
			} else if (EXTRA_AUTO_LOGIN_YOZM.equals(prefModel
					.getAutoLoginAction())) {
				yozmModel = new YozmModel(this);
				yozmModel.setUserInfoCallback(this);
				yozmModel.loadYozmUserInfo(prefModel.getYozmAccessToken());
			} else if (EXTRA_AUTO_LOGIN_CINEPOX.equals(prefModel
					.getAutoLoginAction())) {
				String id = prefModel.getAutoLoginId();
				String pw = prefModel.getAutoLoginPw();
				loginModel.login(id, pw);
			} else {
				startActivity(new Intent(this, SelectLoginMethodActivity.class));
				finish();
			}
		} else {
			startActivity(new Intent(this, SelectLoginMethodActivity.class));
			finish();
		}
		// if (prefModel.isAutoLogin()
		// && EXTRA_AUTO_LOGIN_CINEPOX.equals(prefModel
		// .getAutoLoginAction()))
		// startActivity(new Intent(this, CinepoxLoginActivity.class));
		// else
		// startActivity(new Intent(this, SelectLoginMethodActivity.class));
		// finish();
	}

	@Override
	public void onUpdateConfirm(IntroView view) {
		// TODO Auto-generated method stub
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appModel
				.getAppConfig().getUpdateUrl())));
		finish();
	}

	@Override
	public void onUpdateCancel(IntroView view) {
		// TODO Auto-generated method stub
		goLogin();
	}

	@Override
	public void onLoginSuccess(LoginModel model) {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, MainFragmentActivity.class));
		finish();
	}

	@Override
	public void onLoginStart(LoginModel model) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLoginError(LoginModel model, String message) {
		// TODO Auto-generated method stub
		if (EXTRA_JOIN_ACTION.equals(message)) {
			view.showAgree();
			startActivityForResult(
					new Intent(this, SNSJoinActionActivity.class),
					REQUEST_JOIN_ACTION);
			// 성공으로 간주하고 약관동의 페이지로 이동
		} else {
			prefModel.setAutoLogin(false);
			prefModel.setAutoLoginAction(null);
			isCritical = false;
			view.showError(message);
		}
	}

	@Override
	public void onErrorConfirm(IntroView view) {
		// TODO Auto-generated method stub
		if (!isCritical)
			startActivity(new Intent(this, SelectLoginMethodActivity.class));
		finish();
	}

	@Override
	public void onTwitterUserInfoStart(TwitterModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTwitterUserInfoError(TwitterModel model, String message) {
		// TODO Auto-generated method stub
		view.showError(message);
	}

	@Override
	public void onTwitterUserInfoComplete(TwitterModel model, SNSLoginData data) {
		// TODO Auto-generated method stub
		prefModel.setAutoLogin(true);
		prefModel.setAutoLoginAction(EXTRA_AUTO_LOGIN_TWITTER);
		loginModel.loginSNS(data);
	}

	@Override
	public void onCompleted(GraphUser user, Response response) {
		// TODO Auto-generated method stub
		if (user == null) {
			view.showError(response.getError().getErrorMessage());
		} else {
			l.d("facebook login result : "
					+ user.getInnerJSONObject().toString());
			SNSLoginData data = appModel.getUserConfig().loginData;
			data.nickName = user.getLastName() + user.getFirstName();
			data.sns = EXTRA_AUTO_LOGIN_FACEBOOK;
			data.snsHome = user.getLink();
			data.userId = (String) user.getProperty("email");
			try {
				data.profileImage = ImageRequest.getProfilePictureUrl(
						user.getId(), FacebookModel.PROFILE_IMAGE_SIZE,
						FacebookModel.PROFILE_IMAGE_SIZE).toString();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			prefModel.setAutoLogin(true);
			prefModel.setAutoLoginAction(EXTRA_AUTO_LOGIN_FACEBOOK);
			loginModel.loginSNS(data);
		}
	}

	@Override
	public void onMe2UserInfoStart(Me2DayModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMe2UserInfoError(Me2DayModel model, String message) {
		// TODO Auto-generated method stub
		view.showError(message);
	}

	@Override
	public void onMe2UserInfoComplete(Me2DayModel model, SNSLoginData data) {
		// TODO Auto-generated method stub
		prefModel.setAutoLogin(true);
		prefModel.setAutoLoginAction(EXTRA_AUTO_LOGIN_ME2DAY);
		loginModel.loginSNS(data);
	}

	@Override
	public void onYozmUserInfoStart(YozmModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onYozmUserInfoError(YozmModel model, String message) {
		// TODO Auto-generated method stub
		view.showError(message);
	}

	@Override
	public void onYozmUserInfoComplete(YozmModel model, SNSLoginData data) {
		// TODO Auto-generated method stub
		prefModel.setAutoLogin(true);
		prefModel.setAutoLoginAction(EXTRA_AUTO_LOGIN_YOZM);
		loginModel.loginSNS(data);
	}

}
