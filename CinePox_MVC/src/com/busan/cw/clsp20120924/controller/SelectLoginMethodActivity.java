package com.busan.cw.clsp20120924.controller;

import java.net.MalformedURLException;

import oauth.signpost.OAuthConsumer;
import twitter4j.auth.AccessToken;
import utils.LogUtils.l;
import utils.ThemeUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.interfaces.LoginModelCallback;
import com.busan.cw.clsp20120924.interfaces.Me2DayModelAuthCallback;
import com.busan.cw.clsp20120924.interfaces.Me2DayModelUserInfoCallback;
import com.busan.cw.clsp20120924.interfaces.SelectLoginMethodViewCallback;
import com.busan.cw.clsp20120924.interfaces.TwitterModelAuthCallback;
import com.busan.cw.clsp20120924.interfaces.TwitterModelUserInfoCallback;
import com.busan.cw.clsp20120924.interfaces.YozmModelAuthCallback;
import com.busan.cw.clsp20120924.interfaces.YozmModelUserInfoCallback;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.model.FacebookModel;
import com.busan.cw.clsp20120924.model.LoginModel;
import com.busan.cw.clsp20120924.model.Me2DayModel;
import com.busan.cw.clsp20120924.model.PreferenceModel;
import com.busan.cw.clsp20120924.model.SNSLoginData;
import com.busan.cw.clsp20120924.model.TwitterModel;
import com.busan.cw.clsp20120924.model.YozmModel;
import com.busan.cw.clsp20120924.view.SelectLoginMethodView;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ImageRequest;

public class SelectLoginMethodActivity extends Activity implements
		SelectLoginMethodViewCallback, YozmModelAuthCallback,
		YozmModelUserInfoCallback, Me2DayModelAuthCallback, GraphUserCallback,
		Me2DayModelUserInfoCallback, TwitterModelAuthCallback,
		TwitterModelUserInfoCallback, LoginModelCallback, Constants {

	private SelectLoginMethodView view;

	private Me2DayModel me2Model;
	private YozmModel yozmModel;
	private FacebookModel fbModel;
	private TwitterModel twModel;

	private LoginModel loginModel;
	private PreferenceModel prefModel;

	private static final int REQUEST_FACEBOOK_LOGIN = 100;
	private static final int REQUEST_TWITTER_LOGIN = 101;
	private static final int REQUEST_ME2DAY_LOGIN = 102;
	private static final int REQUEST_YOZM_LOGIN = 103;

	private static final int REQUEST_JOIN_ACTION = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ThemeUtils.darkNoTitle(this);
		super.onCreate(savedInstanceState);
		prefModel = new PreferenceModel(this);
		loginModel = new LoginModel(this);
		loginModel.setCallback(this);
		view = new SelectLoginMethodView(this);
		view.setCallback(this);
		setContentView(view);
		// 2013-02-20 자동로그인은 인트로에서 처리하도록 변경
		// checkAutoLogin();
	}

	private CinepoxAppModel app() {
		return (CinepoxAppModel) getApplication();
	}

	private void checkAutoLogin() {
		// 2013-02-20 자동로그인은 인트로에서 처리하도록 변경
		// 더이상 자동으로 호출되지 않음
		if (prefModel.isAutoLogin()) {
			// 씨네폭스 자동로그인은 여기로 안 넘어옴
			if (EXTRA_AUTO_LOGIN_FACEBOOK
					.equals(prefModel.getAutoLoginAction())) {
				startActivityForResult(new Intent(this,
						FacebookLoginActivity.class), REQUEST_FACEBOOK_LOGIN);
			} else if (EXTRA_AUTO_LOGIN_ME2DAY.equals(prefModel
					.getAutoLoginAction())) {
				me2Model = new Me2DayModel(this);
				me2Model.setAuthCallback(this);
				me2Model.setUserInfoCallback(this);
				me2Model.loadMe2UserInfo(prefModel.getMe2LoginId());
			} else if (EXTRA_AUTO_LOGIN_TWITTER.equals(prefModel
					.getAutoLoginAction())) {
				twModel = new TwitterModel(this);
				twModel.setAuthCallback(this);
				twModel.setUserInfoCallback(this);
				twModel.loadTwitUserInfo(prefModel.getTwitterAccessToken());
			} else if (EXTRA_AUTO_LOGIN_YOZM.equals(prefModel
					.getAutoLoginAction())) {
				yozmModel = new YozmModel(this);
				yozmModel.setAuthCallback(this);
				yozmModel.setUserInfoCallback(this);
				yozmModel.loadYozmUserInfo(prefModel.getYozmAccessToken());
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_FACEBOOK_LOGIN:
				view.showLoading();
				fbModel = new FacebookModel(this);
				fbModel.loadFBUserInfo(Session.getActiveSession(), this);
				break;
			case REQUEST_TWITTER_LOGIN:
				twModel.loadTwitAccessToken(data.getStringExtra(EXTRA_URL));
				break;
			case REQUEST_ME2DAY_LOGIN:
				me2Model.loadMe2AuthInfo();
				break;
			case REQUEST_YOZM_LOGIN:
				yozmModel.loadYozmAuthInfo(data.getStringExtra(EXTRA_URL));
				break;
			case REQUEST_JOIN_ACTION:
				checkAutoLogin();
				break;
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {
			switch (requestCode) {
			case REQUEST_FACEBOOK_LOGIN:
				view.showError(data.getStringExtra(EXTRA_MSG));
				break;
			case REQUEST_TWITTER_LOGIN:
				break;
			case REQUEST_ME2DAY_LOGIN:
				break;
			case REQUEST_YOZM_LOGIN:
				break;
			case REQUEST_JOIN_ACTION:
				break;
			}
		}
	}

	// 뷰 콜백

	@Override
	public void onClickCinepoxLogin(SelectLoginMethodView view) {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, CinepoxLoginActivity.class));
		finish();
	}

	@Override
	public void onClickFacebookLogin(SelectLoginMethodView view) {
		// TODO Auto-generated method stub
		// 넘기기만 하면 저기서 다 알아서 함
		startActivityForResult(new Intent(this, FacebookLoginActivity.class),
				REQUEST_FACEBOOK_LOGIN);
	}

	@Override
	public void onClickTwitterLogin(SelectLoginMethodView view) {
		// TODO Auto-generated method stub
		twModel = new TwitterModel(this);
		twModel.setAuthCallback(this);
		twModel.setUserInfoCallback(this);
		AccessToken token = prefModel.getTwitterAccessToken();
		if (token != null)
			twModel.loadTwitUserInfo(token);
		else
			twModel.loadTwitAuthUrl();
	}

	@Override
	public void onClickMe2dayLogin(SelectLoginMethodView view) {
		// TODO Auto-generated method stub
		me2Model = new Me2DayModel(this);
		me2Model.setAuthCallback(this);
		me2Model.setUserInfoCallback(this);
		String id = prefModel.getMe2LoginId();
		if (id != null)
			me2Model.loadMe2UserInfo(id);
		else
			me2Model.loadMe2BaseInfo();
	}

	@Override
	public void onClickYozmLogin(SelectLoginMethodView view) {
		// TODO Auto-generated method stub
		yozmModel = new YozmModel(this);
		yozmModel.setAuthCallback(this);
		yozmModel.setUserInfoCallback(this);
		AccessToken token = prefModel.getYozmAccessToken();
		if (token != null)
			yozmModel.loadYozmUserInfo(token);
		else
			yozmModel.loadYozmBaseInfo();
	}

	// 미투데이 인증 관련 콜백

	@Override
	public void onMe2BaseInfoStart(Me2DayModel model) {
		// TODO Auto-generated method stub
		view.showLoading();
	}

	@Override
	public void onMe2BaseInfoLoaded(Me2DayModel model, String loginUrl) {
		// TODO Auto-generated method stub
		// 아이디 인증 웹뷰 호출
		view.dismissLoading();
		Intent loginIntent = new Intent(this, Me2DayLoginActivity.class);
		loginIntent.putExtra(EXTRA_URL, loginUrl);
		startActivityForResult(loginIntent, REQUEST_ME2DAY_LOGIN);
		l.i("me2LoginUrl : " + loginUrl);
	}

	@Override
	public void onMe2BaseInfoError(Me2DayModel model, String code,
			String message) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		view.showError(message);
		prefModel.initMe2DayInfo();
	}

	@Override
	public void onMe2AuthStart(Me2DayModel model) {
		// TODO Auto-generated method stub
		view.showLoading();
	}

	@Override
	public void onMe2AuthSuccess(Me2DayModel model, String authKey,
			String userId) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		prefModel.setMe2LoginKey(authKey);
		prefModel.setMe2LoginId(userId);
		// 자동로그인 설정
		me2Model.setUserInfoCallback(this);
		me2Model.loadMe2UserInfo(userId);
	}

	@Override
	public void onMe2AuthError(Me2DayModel model, String message) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		view.showError(message);
		prefModel.initMe2DayInfo();
	}

	// 미투데이 사용자정보 관련 콜백

	@Override
	public void onMe2UserInfoStart(Me2DayModel model) {
		// TODO Auto-generated method stub
		view.showLoading();
	}

	@Override
	public void onMe2UserInfoError(Me2DayModel model, String message) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		view.showError(message);
	}

	@Override
	public void onMe2UserInfoComplete(Me2DayModel model, SNSLoginData data) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		prefModel.setAutoLogin(true);
		prefModel.setAutoLoginAction(EXTRA_AUTO_LOGIN_ME2DAY);
		loginModel.loginSNS(data);
	}

	// 로그인 관련 콜백

	@Override
	public void onLoginSuccess(LoginModel model) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		startActivity(new Intent(this, MainFragmentActivity.class));
		finish();
	}

	@Override
	public void onLoginStart(LoginModel model) {
		// TODO Auto-generated method stub
		view.showLoading();
	}

	@Override
	public void onLoginError(LoginModel model, String message) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		if (EXTRA_JOIN_ACTION.equals(message)) {
			view.showAgree();
			startActivityForResult(
					new Intent(this, SNSJoinActionActivity.class),
					REQUEST_JOIN_ACTION);
			// 성공으로 간주하고 약관동의 페이지로 이동
		} else
			view.showError(message);
	}

	// 요즘 인증관련 콜백

	@Override
	public void onYozmBaseInfoStart(YozmModel model) {
		// TODO Auto-generated method stub
		view.showLoading();
	}

	@Override
	public void onYozmBaseInfoLoaded(YozmModel model, String authUrl) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		l.i("yozm LoginUrl : " + authUrl);
		Intent loginIntent = new Intent(this, YozmLoginActivity.class);
		loginIntent.putExtra(EXTRA_URL, authUrl);
		startActivityForResult(loginIntent, REQUEST_YOZM_LOGIN);
	}

	@Override
	public void onYozmBaseInfoError(YozmModel model, String message) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		view.showError(message);
	}

	@Override
	public void onYozmAuthStart(YozmModel model) {
		// TODO Auto-generated method stub
		view.showLoading();
	}

	@Override
	public void onYozmAuthLoaded(YozmModel model, OAuthConsumer consumer) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		AccessToken token = new AccessToken(consumer.getToken(),
				consumer.getTokenSecret());
		prefModel.setYozmAccessToken(token);
		yozmModel.loadYozmUserInfo(consumer);
	}

	@Override
	public void onYozmAuthError(YozmModel model, String error) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		view.showError(error);
	}

	// 요즘 사용자정보 관련 콜백

	@Override
	public void onYozmUserInfoStart(YozmModel model) {
		// TODO Auto-generated method stub
		view.showLoading();
	}

	@Override
	public void onYozmUserInfoError(YozmModel model, String message) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		view.showError(message);
	}

	@Override
	public void onYozmUserInfoComplete(YozmModel model, SNSLoginData data) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		prefModel.setAutoLogin(true);
		prefModel.setAutoLoginAction(EXTRA_AUTO_LOGIN_YOZM);
		loginModel.loginSNS(data);
	}

	// 페이스북 사용자정보 관련 콜백

	@Override
	public void onCompleted(GraphUser user, Response response) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		if (user == null) {
			view.showError(response.getError().getErrorMessage());
		} else {
			l.d("facebook login result : "
					+ user.getInnerJSONObject().toString());
			SNSLoginData data = app().getUserConfig().loginData;
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

	// 트위터 사용자정보 관련 콜백

	@Override
	public void onTwitterUserInfoStart(TwitterModel model) {
		// TODO Auto-generated method stub
		view.showLoading();
	}

	@Override
	public void onTwitterUserInfoError(TwitterModel model, String message) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		view.showError(message);
	}

	@Override
	public void onTwitterUserInfoComplete(TwitterModel model, SNSLoginData data) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		prefModel.setAutoLogin(true);
		prefModel.setAutoLoginAction(EXTRA_AUTO_LOGIN_TWITTER);
		loginModel.loginSNS(data);
	}

	// 트위터 인증 URL 관련 콜백

	@Override
	public void onTwitterBaseInfoStart(TwitterModel model) {
		// TODO Auto-generated method stub
		view.showLoading();
	}

	@Override
	public void onTwitterBaseInfoLoaded(TwitterModel model, String loginUrl) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		Intent loginIntent = new Intent(this, TwitterLoginActivity.class);
		loginIntent.putExtra(EXTRA_URL, loginUrl);
		startActivityForResult(loginIntent, REQUEST_TWITTER_LOGIN);
		l.i("twitter auth url : " + loginUrl);
	}

	@Override
	public void onTwitterBaseInfoError(TwitterModel model, String message) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		view.showError(message);
	}

	// 트위터 액세스 키 관련 콜백

	@Override
	public void onTwitterAuthStart(TwitterModel model) {
		// TODO Auto-generated method stub
		view.showLoading();
	}

	@Override
	public void onTwitterAuthSuccess(TwitterModel model, AccessToken access) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		prefModel.setTwitterAccessToken(access);
		twModel.loadTwitUserInfo(access);
	}

	@Override
	public void onTwitterAuthError(TwitterModel model, String message) {
		// TODO Auto-generated method stub
		view.dismissLoading();
		view.showError(message);
	}

}
