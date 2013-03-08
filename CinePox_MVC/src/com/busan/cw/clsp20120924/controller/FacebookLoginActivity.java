package com.busan.cw.clsp20120924.controller;

import utils.LogUtils.l;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.model.FacebookModel;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;

/**
 * 페이스북 api의 구조가 변경되어 다른 액티비티와 구조가 약간 다름
 * 
 * @author CINEPOX
 * 
 */
public class FacebookLoginActivity extends Activity implements Constants,
		Session.StatusCallback {

	private FacebookModel fbModel;
	private UiLifecycleHelper uiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		// 액세스 토큰 얻는 작업을 하도록 지정
		fbModel = new FacebookModel(this);
		uiHelper = new UiLifecycleHelper(this, this);
		uiHelper.onCreate(savedInstanceState);

		Session session = fbModel.loadFBSession(this, savedInstanceState);
		// 활성 세션 가져옴
		if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
			// 이미 토큰이 로드되어 있으면
			session.openForRead(new Session.OpenRequest(this).setCallback(this));
			// 로그인 웹페이지 호출
		} else {
			if (!session.isOpened() && !session.isClosed()) {
				session.openForRead(new Session.OpenRequest(this)
						.setCallback(this));
				// 로그인 안 돼 있으면 로그인창 띄움
			} else {
				Session.openActiveSession(this, true, this);
				// 로그인 돼 있으면 앱 사용 승인창 띄움
			}
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private void resultError(String message) {
		Intent i = new Intent();
		i.putExtra(EXTRA_MSG, message);
		setResult(Activity.RESULT_CANCELED, i);
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		// TODO Auto-generated method stub
		l.d("Facebook session state : " + state);
		if (state.equals(SessionState.OPENED)) {
			setResult(Activity.RESULT_OK);
			finish();
		} else if (state.equals(SessionState.CLOSED_LOGIN_FAILED)) {
			resultError(exception.getMessage());
		}
	}

}
