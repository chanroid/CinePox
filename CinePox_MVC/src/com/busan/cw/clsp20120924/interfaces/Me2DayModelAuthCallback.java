package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.Me2DayModel;

public interface Me2DayModelAuthCallback {
	public void onMe2BaseInfoStart(Me2DayModel model);
	public void onMe2BaseInfoLoaded(Me2DayModel model, String loginUrl);
	public void onMe2BaseInfoError(Me2DayModel model, String code, String message);
	public void onMe2AuthStart(Me2DayModel model);
	public void onMe2AuthSuccess(Me2DayModel model, String authKey, String userId);
	public void onMe2AuthError(Me2DayModel model, String message);
}
