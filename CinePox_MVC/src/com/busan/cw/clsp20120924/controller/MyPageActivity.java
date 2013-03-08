package com.busan.cw.clsp20120924.controller;

import utils.ThemeUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.busan.cw.clsp20120924.interfaces.MyPageViewCallback;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.model.PreferenceModel;
import com.busan.cw.clsp20120924.model.SNSLoginData;
import com.busan.cw.clsp20120924.model.UserConfigData;
import com.busan.cw.clsp20120924.view.MyPageView;

public class MyPageActivity extends Activity implements MyPageViewCallback {

	private MyPageView view;
	private CinepoxAppModel appModel;
	private PreferenceModel prefModel;

	private static final int REQUEST_MEMBEROUT = 104;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ThemeUtils.darkNoTitle(this);
		super.onCreate(savedInstanceState);
		prefModel = new PreferenceModel(this);
		view = new MyPageView(this);
		view.setCallback(this);
		setContentView(view);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_MEMBEROUT:
				setResult(4444);
				finish();
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshView();
	}

	private void refreshView() {
		appModel = (CinepoxAppModel) getApplication();
		UserConfigData data = appModel.getUserConfig();
		view.setAutologinState(prefModel.isAutoLogin());
		view.setAccountName(data.getAccountName());
		view.setProfileUrl(data.getProfileImageUrl());
		view.setNickName(data.getNickName());
		view.setPoint(data.getPoint());
		view.setBonus(data.getBonus());
		view.setPremium(data.isPremium(), data.isTicketIsAuto());
		view.setPremiumPeriod(data.getTicketEndDateView());
		view.setUserLevelName(data.getLevelName());
	}

	@Override
	public void onBackClick(MyPageView view) {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onProfileImageClick(MyPageView view) {
		// 프로필사진 변경

	}

	@Override
	public void onProfileImageChangeClick(MyPageView view) {
		// 프로필사진 변경

	}

	@Override
	public void onNicknameEdited(MyPageView view, CharSequence text) {
		// 닉네임 중복확인

	}

	@Override
	public void onTicketButtonClick(MyPageView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPointDetailClick(MyPageView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPopcornTransClick(MyPageView view) {
		// 쓰이지 않는 버튼
	}

	@Override
	public void onAutologinCheck(MyPageView view, boolean checked) {
		// 자동로그인 해제/설정
		view.setAutologinState(checked);
		if (checked) {
			// 현재 로그인 정보를 설정으로 저장
			SNSLoginData data = appModel.getUserConfig().loginData;
			prefModel.setAutoLogin(true);
			prefModel.setAutoLoginAction(data.sns);
		} else {
			// 자동로그인 설정 해제
			prefModel.setAutoLogin(false);
			prefModel.setAutoLoginAction(null);
		}
	}

	@Override
	public void onLeaveClick(MyPageView view) {
		// 회원탈퇴
		startActivityForResult(new Intent(this, MemberOutActivity.class),
				REQUEST_MEMBEROUT);
	}

	@Override
	public void onLogoutClick(MyPageView view) {
		// 로그아웃
		view.showLogout();
	}

	@Override
	public void onLogoutConfirm(MyPageView view) {
		// TODO Auto-generated method stub
		prefModel.setAutoLogin(false);
		prefModel.setAutoLoginAction(null);
		appModel.processLogout();
		setResult(4444); // 완전종료
		startActivity(new Intent(this, SelectLoginMethodActivity.class));
		finish();
	}

	@Override
	public void onLogoutCancel(MyPageView view) {
		// do nothing

	}

	@Override
	public void onBonusDetailClick(MyPageView myPageView) {
		// 웹뷰

	}

	@Override
	public void onMessageDetailClick(MyPageView myPageView) {
		// 웹뷰

	}

	@Override
	public void onProfileEditClick(MyPageView myPageView) {
		// 웹뷰

	}

	@Override
	public void onPasswordEditClick(MyPageView myPageView) {
		// 웹뷰

	}

}
