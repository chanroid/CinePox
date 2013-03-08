package com.busan.cw.clsp20120924.interfaces;

import oauth.signpost.OAuthConsumer;

import com.busan.cw.clsp20120924.model.YozmModel;

public interface YozmModelAuthCallback {
	public void onYozmBaseInfoStart(YozmModel model);

	public void onYozmBaseInfoLoaded(YozmModel model, String authUrl);

	public void onYozmBaseInfoError(YozmModel model, String message);

	public void onYozmAuthStart(YozmModel model);

	public void onYozmAuthLoaded(YozmModel model, OAuthConsumer consumer);

	public void onYozmAuthError(YozmModel model, String error);
}
