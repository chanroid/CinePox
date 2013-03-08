package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.Me2DayModel;

public interface Me2DayModelPostCallback {
	public void onMe2PostStart(Me2DayModel model);
	public void onMe2PostComplete(Me2DayModel model);
	public void onMe2PostError(Me2DayModel model, String message);
}
