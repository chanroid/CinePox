package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.Me2DayModel;
import com.busan.cw.clsp20120924.model.SNSLoginData;

public interface Me2DayModelUserInfoCallback {
	public void onMe2UserInfoStart(Me2DayModel model);
	public void onMe2UserInfoError(Me2DayModel model, String message);
	public void onMe2UserInfoComplete(Me2DayModel model, SNSLoginData data);
}
