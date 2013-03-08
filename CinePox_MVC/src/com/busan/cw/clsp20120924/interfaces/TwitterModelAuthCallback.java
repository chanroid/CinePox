package com.busan.cw.clsp20120924.interfaces;

import twitter4j.auth.AccessToken;

import com.busan.cw.clsp20120924.model.TwitterModel;

public interface TwitterModelAuthCallback {
	public void onTwitterBaseInfoStart(TwitterModel model);

	public void onTwitterBaseInfoLoaded(TwitterModel model, String loginUrl);

	public void onTwitterBaseInfoError(TwitterModel model, String message);

	public void onTwitterAuthStart(TwitterModel model);

	public void onTwitterAuthSuccess(TwitterModel model, AccessToken access);

	public void onTwitterAuthError(TwitterModel model, String message);
}
