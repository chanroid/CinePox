package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.YozmModel;

public interface YozmModelPostCallback {
	public void onYozmPostStart(YozmModel model);

	public void onYozmPostError(YozmModel model, String message);

	public void onYozmPostComplete(YozmModel model);
}
