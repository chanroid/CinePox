package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.SNSLoginData;
import com.busan.cw.clsp20120924.model.YozmModel;

public interface YozmModelUserInfoCallback {
	public void onYozmUserInfoStart(YozmModel model);

	public void onYozmUserInfoError(YozmModel model, String message);

	public void onYozmUserInfoComplete(YozmModel model, SNSLoginData data);
}
