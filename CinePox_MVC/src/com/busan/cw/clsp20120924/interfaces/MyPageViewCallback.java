package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.MyPageView;

public interface MyPageViewCallback {
	public void onBackClick(MyPageView view);

	public void onProfileImageClick(MyPageView view);

	public void onProfileImageChangeClick(MyPageView view);

	public void onNicknameEdited(MyPageView view, CharSequence text);

	public void onTicketButtonClick(MyPageView view);

	public void onPointDetailClick(MyPageView view);

	public void onPopcornTransClick(MyPageView view);

	public void onAutologinCheck(MyPageView view, boolean checked);

	public void onLeaveClick(MyPageView view);

	public void onLogoutClick(MyPageView view);

	public void onLogoutConfirm(MyPageView view);

	public void onLogoutCancel(MyPageView view);

	public void onBonusDetailClick(MyPageView myPageView);

	public void onMessageDetailClick(MyPageView myPageView);

	public void onProfileEditClick(MyPageView myPageView);

	public void onPasswordEditClick(MyPageView myPageView);
}
