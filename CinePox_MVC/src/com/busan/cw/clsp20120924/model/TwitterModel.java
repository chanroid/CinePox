package com.busan.cw.clsp20120924.model;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import utils.LogUtils.l;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.busan.cw.clsp20120924.base.BaseModel;
import com.busan.cw.clsp20120924.interfaces.TwitterModelAuthCallback;
import com.busan.cw.clsp20120924.interfaces.TwitterModelPostCallback;
import com.busan.cw.clsp20120924.interfaces.TwitterModelUserInfoCallback;

public class TwitterModel extends BaseModel {

	public static final String CONSUMER_KEY = "ALmsxjzJ8UWi6CaWr4r5UQ";
	public static final String CONSUMER_SECRET = "eI3b3QAjET3nIc2tgIJzVI6PBkzdK1CG5qCN5Dmg";

	// 핸들러 통신 식별값
	private static final int HANDLE_TWIT_BASE_INFO_START = 100;
	private static final int HANDLE_TWIT_BASE_INFO_SUCCESS = 101;
	private static final int HANDLE_TWIT_BASE_INFO_ERROR = 102;

	private static final int HANDLE_TWIT_AUTH_INFO_START = 200;
	private static final int HANDLE_TWIT_AUTH_INFO_ERROR = 201;
	private static final int HANDLE_TWIT_AUTH_INFO_SUCCESS = 202;

	private static final int HANDLE_TWIT_POST_START = 300;
	private static final int HANDLE_TWIT_POST_ERROR = 301;
	private static final int HANDLE_TWIT_POST_SUCCESS = 302;

	private static final int HANDLE_TWIT_USER_START = 400;
	private static final int HANDLE_TWIT_USER_ERROR = 401;
	private static final int HANDLE_TWIT_USER_SUCCESS = 402;

	private TwitterModelAuthCallback authcallback;
	private TwitterModelUserInfoCallback usercallback;
	private TwitterModelPostCallback postcallback;

	private Twitter twit;
	private RequestToken request;

	public void setAuthCallback(TwitterModelAuthCallback callback) {
		authcallback = callback;
	}

	public void setUserInfoCallback(TwitterModelUserInfoCallback callback) {
		usercallback = callback;
	}

	public void setPostCallback(TwitterModelPostCallback callback) {
		postcallback = callback;
	}

	public TwitterModel(Context ctx) {
		super(ctx);
		twit = new TwitterFactory().getInstance();
		twit.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		// TODO Auto-generated constructor stub
	}

	public Activity getActivity() {
		return (Activity) getContext();
	}

	public void loadTwitAuthUrl() {
		new Thread(new TwitBaseInfoRunnable()).start();
	}

	public void loadTwitAccessToken(String pin) {
		new Thread(new TwitAuthInfoRunnable(pin)).start();
	}

	public void loadTwitUserInfo(AccessToken token) {
		new Thread(new TwitUserInfoRunnable(token)).start();
	}

	public void postTwitter(AccessToken access, String status) {
		new Thread(new TwitPostRunnable(access, status)).start();
	}

	private class TwitBaseInfoRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getActivity().runOnUiThread(
					new TwitCallbackPoster(HANDLE_TWIT_BASE_INFO_START));
			try {
				request = twit.getOAuthRequestToken();
				// String authUrl = request.getAuthorizationURL();
				String authUrl = request.getAuthenticationURL();
				getActivity().runOnUiThread(
						new TwitCallbackPoster(HANDLE_TWIT_BASE_INFO_SUCCESS,
								authUrl));
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new TwitCallbackPoster(HANDLE_TWIT_BASE_INFO_ERROR, e
								.getErrorMessage()));
			}
		}
	}

	private class TwitAuthInfoRunnable implements Runnable {

		private String pinUrl;

		private TwitAuthInfoRunnable(String pinUrl) {
			this.pinUrl = pinUrl;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getActivity().runOnUiThread(
					new TwitCallbackPoster(HANDLE_TWIT_AUTH_INFO_START));

			try {
				Uri url = Uri.parse(pinUrl);
				String code = url.getQueryParameter(EXTRA_VERIFIER);
				l.d("twit oauth verifier : " + code);
				AccessToken access = twit.getOAuthAccessToken(request, code);
				getActivity().runOnUiThread(
						new TwitCallbackPoster(HANDLE_TWIT_AUTH_INFO_SUCCESS,
								access));
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new TwitCallbackPoster(HANDLE_TWIT_AUTH_INFO_ERROR, e
								.getErrorMessage()));
			}
		}

	}

	private class TwitUserInfoRunnable implements Runnable {

		private AccessToken token;

		private TwitUserInfoRunnable(AccessToken token) {
			this.token = token;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getActivity().runOnUiThread(
					new TwitCallbackPoster(HANDLE_TWIT_USER_START));
			try {
				twit.setOAuthAccessToken(token);
				SNSLoginData data = app().getUserConfig().loginData;
				String userId = twit.getScreenName();
				User userInfo = twit.showUser(userId);
				data.userId = userId;
				data.nickName = userInfo.getName();
				data.profileImage = userInfo.getProfileImageURL();
				data.sns = EXTRA_AUTO_LOGIN_TWITTER;
				data.snsHome = userInfo.getURL();
				getActivity().runOnUiThread(
						new TwitCallbackPoster(HANDLE_TWIT_USER_SUCCESS, data));
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new TwitCallbackPoster(HANDLE_TWIT_USER_ERROR, e
								.getMessage()));
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				getActivity().runOnUiThread(
						new TwitCallbackPoster(HANDLE_TWIT_USER_ERROR, e
								.getErrorMessage()));
				e.printStackTrace();
			}
		}

	}

	private class TwitPostRunnable implements Runnable {

		private AccessToken access;
		private String status;

		private TwitPostRunnable(AccessToken access, String status) {
			this.access = access;
			this.status = status;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getActivity().runOnUiThread(
					new TwitCallbackPoster(HANDLE_TWIT_POST_START));
			try {
				twit.setOAuthAccessToken(access);
				twit.updateStatus(status);
				getActivity().runOnUiThread(
						new TwitCallbackPoster(HANDLE_TWIT_POST_SUCCESS));
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new TwitCallbackPoster(HANDLE_TWIT_POST_ERROR, e
								.getErrorMessage()));
			}
		}

	}

	private class TwitCallbackPoster implements Runnable {

		private int what;
		private Object[] args;

		private TwitCallbackPoster(int what, Object... args) {
			this.what = what;
			this.args = args;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			switch (what) {
			case HANDLE_TWIT_BASE_INFO_ERROR:
				String msg = (String) args[0];
				if (authcallback != null)
					authcallback.onTwitterBaseInfoError(TwitterModel.this, msg);
				break;
			case HANDLE_TWIT_BASE_INFO_START:
				if (authcallback != null)
					authcallback.onTwitterBaseInfoStart(TwitterModel.this);
				break;
			case HANDLE_TWIT_BASE_INFO_SUCCESS:
				String authUrl = (String) args[0];
				if (authcallback != null)
					authcallback.onTwitterBaseInfoLoaded(TwitterModel.this,
							authUrl);
				break;

			case HANDLE_TWIT_AUTH_INFO_ERROR:
				String authErrorMsg = (String) args[0];
				if (authcallback != null)
					authcallback.onTwitterAuthError(TwitterModel.this,
							authErrorMsg);
				break;
			case HANDLE_TWIT_AUTH_INFO_START:
				if (authcallback != null)
					authcallback.onTwitterAuthStart(TwitterModel.this);
				break;
			case HANDLE_TWIT_AUTH_INFO_SUCCESS:
				AccessToken access = (AccessToken) args[0];
				if (authcallback != null)
					authcallback
							.onTwitterAuthSuccess(TwitterModel.this, access);
				break;

			case HANDLE_TWIT_USER_ERROR:
				String userErrorMsg = (String) args[0];
				if (usercallback != null)
					usercallback.onTwitterUserInfoError(TwitterModel.this,
							userErrorMsg);
				break;
			case HANDLE_TWIT_USER_START:
				if (usercallback != null)
					usercallback.onTwitterUserInfoStart(TwitterModel.this);
				break;
			case HANDLE_TWIT_USER_SUCCESS:
				SNSLoginData data = (SNSLoginData) args[0];
				if (usercallback != null)
					usercallback.onTwitterUserInfoComplete(TwitterModel.this,
							data);
				break;

			case HANDLE_TWIT_POST_ERROR:
				String message = (String) args[0];
				if (postcallback != null)
					postcallback.onTwitterPostError(TwitterModel.this, message);
				break;
			case HANDLE_TWIT_POST_START:
				if (postcallback != null)
					postcallback.onTwitterPostStart(TwitterModel.this);
				break;
			case HANDLE_TWIT_POST_SUCCESS:
				if (postcallback != null)
					postcallback.onTwitterPostComplete(TwitterModel.this);
				break;

			}
		}

	}
}
