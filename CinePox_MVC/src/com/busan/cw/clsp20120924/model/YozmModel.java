package com.busan.cw.clsp20120924.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import sns.me2day.FilePart;
import sns.me2day.MultipartEntity;
import sns.me2day.Part;
import sns.me2day.StringPart;
import sns.yozm.YozmPostData;
import twitter4j.auth.AccessToken;
import utils.LogUtils.l;
import utils.StringUtils;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.busan.cw.clsp20120924.base.BaseModel;
import com.busan.cw.clsp20120924.interfaces.YozmModelAuthCallback;
import com.busan.cw.clsp20120924.interfaces.YozmModelPostCallback;
import com.busan.cw.clsp20120924.interfaces.YozmModelUserInfoCallback;

/**
 * 2012-02-12 인증키 문제로 작업 잠시 중단
 * 2012-02-18 인증키 문제 해결
 * 
 * @author CINEPOX
 * 
 */
public class YozmModel extends BaseModel {

	static final String REQUEST_TOKEN_URL = "https://apis.daum.net/oauth/requestToken";
	static final String AUTHORIZE_URL = "https://apis.daum.net/oauth/authorize";
	static final String ACCESS_TOKEN_URL = "https://apis.daum.net/oauth/accessToken";
	static final String USERINFO_URL = "https://apis.daum.net/yozm/v1_0/user/show.json";
	static final String POST_URL = "https://apis.dault.net/yozm/v1_0/message/add.json";

	private String CONSUMER_KEY = "3c836c69-1b29-4e9d-a53c-5e34c81e03f6";
	private String CONSUMER_SECRET = "hEFQRwp2iqba5vzFlw-Csq56F7w1u7ZBHJE9-iHFMb1kj4Ol9BeNgA00";

	private static final String EXTRA_USER = "user";
	private static final String EXTRA_RESULT = "result_msg";
	private static final String EXTRA_STATUS = "status";
	private static final String EXTRA_NICKNAME = "nickname";
	private static final String EXTRA_BLOG = "blog_url";
	private static final String EXTRA_PROFILE = "profile_img_url";
	private static final String EXTRA_ID = "url_name";

	private static final String EXTRA_MESSAGE = "message";
	private static final String EXTRA_SHORT_URL = "url_shorten";
	private static final String EXTRA_PARENT_MSG_ID = "parent_msg_id";
	private static final String EXTRA_IMG_URL = "img_url";
	private static final String EXTRA_IMG_DATA = "img_data";

	// 핸들러 통신 식별값
	private static final int HANDLE_YOZM_BASE_INFO_START = 100;
	private static final int HANDLE_YOZM_BASE_INFO_SUCCESS = 101;
	private static final int HANDLE_YOZM_BASE_INFO_ERROR = 102;

	private static final int HANDLE_YOZM_AUTH_INFO_START = 200;
	private static final int HANDLE_YOZM_AUTH_INFO_ERROR = 201;
	private static final int HANDLE_YOZM_AUTH_INFO_SUCCESS = 202;

	private static final int HANDLE_YOZM_POST_START = 300;
	private static final int HANDLE_YOZM_POST_ERROR = 301;
	private static final int HANDLE_YOZM_POST_SUCCESS = 302;

	private static final int HANDLE_YOZM_USER_START = 400;
	private static final int HANDLE_YOZM_USER_ERROR = 401;
	private static final int HANDLE_YOZM_USER_SUCCESS = 402;

	private OAuthProvider provider;
	private OAuthConsumer consumer;

	private YozmModelAuthCallback authcallback;
	private YozmModelUserInfoCallback usercallback;
	private YozmModelPostCallback postcallback;

	public void setAuthCallback(YozmModelAuthCallback callback) {
		authcallback = callback;
	}

	public void setUserInfoCallback(YozmModelUserInfoCallback callback) {
		usercallback = callback;
	}

	public void setPostCallback(YozmModelPostCallback callback) {
		postcallback = callback;
	}

	public Activity getActivity() {
		return (Activity) getContext();
	}

	public YozmModel(Context ctx) {
		super(ctx);
		provider = new DefaultOAuthProvider(REQUEST_TOKEN_URL,
				ACCESS_TOKEN_URL, AUTHORIZE_URL);
		consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
	}

	public void loadYozmBaseInfo() {
		new Thread(new YozmBaseInfoRunnable()).start();
	}

	public void loadYozmAuthInfo(String verifyUrl) {
		new Thread(new YozmAuthRunnable(verifyUrl)).start();
	}

	public void loadYozmUserInfo(OAuthConsumer consumer) {
		new Thread(new YozmUserInfoRunnable(consumer)).start();
	}

	public void loadYozmUserInfo(AccessToken token) {
		OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY,
				CONSUMER_SECRET);
		consumer.setTokenWithSecret(token.getToken(), token.getTokenSecret());
		loadYozmUserInfo(consumer);
	}

	public void yozmPost(YozmPostData data) {
		new Thread(new YozmPostRunnable(data)).start();
	}

	private class YozmBaseInfoRunnable implements Runnable {

		@Override
		public void run() {
			getActivity().runOnUiThread(
					new YozmCallbackPoster(HANDLE_YOZM_BASE_INFO_START));
			try {
				String authUrl = provider.retrieveRequestToken(consumer,
						"http://m.cinepox.com/");
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_BASE_INFO_SUCCESS,
								authUrl));
			} catch (OAuthMessageSignerException e) {
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_BASE_INFO_ERROR, e
								.getMessage()));
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_BASE_INFO_ERROR, e
								.getMessage()));
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_BASE_INFO_ERROR, e
								.getMessage()));
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_BASE_INFO_ERROR, e
								.getMessage()));
				e.printStackTrace();
			}
		}
	}

	private class YozmAuthRunnable implements Runnable {

		private String verifyUrl;

		private YozmAuthRunnable(String verifyUrl) {
			this.verifyUrl = verifyUrl;
		}

		@Override
		public void run() {
			getActivity().runOnUiThread(
					new YozmCallbackPoster(HANDLE_YOZM_AUTH_INFO_START));
			try {
				Uri uri = Uri.parse(verifyUrl);
				String code = uri.getQueryParameter(EXTRA_VERIFIER);
				provider.retrieveAccessToken(consumer, code);
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_AUTH_INFO_SUCCESS,
								consumer));
			} catch (OAuthMessageSignerException e) {
				// 여기서부터 인증오류
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_AUTH_INFO_ERROR, e
								.getMessage()));
			} catch (OAuthNotAuthorizedException e) {
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_AUTH_INFO_ERROR, e
								.getMessage()));
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_AUTH_INFO_ERROR, e
								.getMessage()));
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_AUTH_INFO_ERROR, e
								.getMessage()));
			}
		}

	}

	private class YozmUserInfoRunnable implements Runnable {

		private OAuthConsumer consumer;

		public YozmUserInfoRunnable(OAuthConsumer consumer) {
			this.consumer = consumer;
		}

		@Override
		public void run() {
			getActivity().runOnUiThread(
					new YozmCallbackPoster(HANDLE_YOZM_USER_START));
			try {
				URL url = new URL(USERINFO_URL);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();

				// 빌어먹을 서버가 GET만 지원한다고 함. 어차피 키인증만 되어 있으면 상관없으므로 그냥 진행
				consumer.sign(conn);
				// 연결에 OAuth를 적용하기 위해 서명함
				conn.connect();

				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					String infoJsonString = StringUtils.stringFromStream(conn
							.getInputStream());
					l.d("yozm user info : " + infoJsonString);
					JSONObject infoJson = new JSONObject(infoJsonString);
					if (infoJson.getInt(EXTRA_STATUS) != 200) {
						String msg = infoJson.getString(EXTRA_RESULT);
						getActivity().runOnUiThread(
								new YozmCallbackPoster(HANDLE_YOZM_USER_ERROR,
										msg));
						// 에러
					} else {
						JSONObject userJson = infoJson
								.getJSONObject(EXTRA_USER);
						SNSLoginData data = app().getUserConfig().loginData;
						data.nickName = userJson.getString(EXTRA_NICKNAME);
						data.profileImage = userJson.getString(EXTRA_PROFILE);
						data.snsHome = userJson.getString(EXTRA_BLOG);
						data.userId = userJson.getString(EXTRA_ID);
						data.sns = EXTRA_AUTO_LOGIN_YOZM;
						// 완료 콜백
						getActivity().runOnUiThread(
								new YozmCallbackPoster(
										HANDLE_YOZM_USER_SUCCESS, data));
					}
				} else {
					// 연결 에러
					getActivity().runOnUiThread(
							new YozmCallbackPoster(HANDLE_YOZM_USER_ERROR,
									"값이 이상해요 ㅠㅠ"));
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_USER_ERROR, e
								.getMessage()));
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_USER_ERROR, e
								.getMessage()));
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_USER_ERROR, e
								.getMessage()));
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_USER_ERROR, e
								.getMessage()));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_USER_ERROR, e
								.getMessage()));
			}
		}
	}

	private class YozmPostRunnable implements Runnable {

		private YozmPostData data;

		private YozmPostRunnable(YozmPostData data) {
			this.data = data;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getActivity().runOnUiThread(
					new YozmCallbackPoster(HANDLE_YOZM_POST_START));
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(POST_URL);
			Part[] partArray = new Part[5];
			partArray[0] = new StringPart(EXTRA_MESSAGE, data.message);
			partArray[1] = new StringPart(EXTRA_SHORT_URL,
					data.isShortenUrl ? "durl" : null);
			partArray[2] = new StringPart(EXTRA_PARENT_MSG_ID,
					data.parentMsgId == 0 ? "" : data.parentMsgId + "");
			partArray[3] = new StringPart(EXTRA_IMG_URL, data.imageUrl);
			try {
				partArray[4] = new FilePart(EXTRA_IMG_DATA, data.imageData);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MultipartEntity entity = new MultipartEntity(partArray);
			post.setEntity(entity);
			try {
				InputStream in = client.execute(post).getEntity().getContent();
				JSONObject postJson = new JSONObject(
						StringUtils.stringFromStream(in));
				if ("OK".equals(postJson.getString(EXTRA_RESULT))) {
					// 성공
					getActivity().runOnUiThread(
							new YozmCallbackPoster(HANDLE_YOZM_POST_SUCCESS));
				} else {
					// 에러
					getActivity().runOnUiThread(
							new YozmCallbackPoster(HANDLE_YOZM_POST_ERROR,
									postJson.getString(EXTRA_RESULT)));
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_POST_ERROR, e
								.getMessage()));
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_POST_ERROR, e
								.getMessage()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_POST_ERROR, e
								.getMessage()));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new YozmCallbackPoster(HANDLE_YOZM_POST_ERROR, e
								.getMessage()));
			}
		}
	}

	private class YozmCallbackPoster implements Runnable {

		private int what;
		private Object[] args;

		private YozmCallbackPoster(int what, Object... args) {
			this.what = what;
			this.args = args;
		}

		@Override
		public void run() {

			switch (what) {
			case HANDLE_YOZM_AUTH_INFO_ERROR:
				String authErrorMsg = (String) args[0];
				if (authcallback != null)
					authcallback.onYozmAuthError(YozmModel.this, authErrorMsg);
				break;
			case HANDLE_YOZM_AUTH_INFO_START:
				if (authcallback != null)
					authcallback.onYozmAuthStart(YozmModel.this);
				break;
			case HANDLE_YOZM_AUTH_INFO_SUCCESS:
				OAuthConsumer consumer = (OAuthConsumer) args[0];
				if (authcallback != null)
					authcallback.onYozmAuthLoaded(YozmModel.this, consumer);
				break;

			case HANDLE_YOZM_BASE_INFO_ERROR:
				String baseErrorMsg = (String) args[0];
				if (authcallback != null)
					authcallback.onYozmBaseInfoError(YozmModel.this,
							baseErrorMsg);
				break;
			case HANDLE_YOZM_BASE_INFO_START:
				if (authcallback != null)
					authcallback.onYozmBaseInfoStart(YozmModel.this);
				break;
			case HANDLE_YOZM_BASE_INFO_SUCCESS:
				String yozmLoginUrl = (String) args[0];
				if (authcallback != null)
					authcallback.onYozmBaseInfoLoaded(YozmModel.this,
							yozmLoginUrl);
				break;

			case HANDLE_YOZM_POST_ERROR:
				String message = (String) args[0];
				if (postcallback != null)
					postcallback.onYozmPostError(YozmModel.this, message);
				break;
			case HANDLE_YOZM_POST_START:
				if (postcallback != null)
					postcallback.onYozmPostStart(YozmModel.this);
				break;
			case HANDLE_YOZM_POST_SUCCESS:
				if (postcallback != null)
					postcallback.onYozmPostComplete(YozmModel.this);
				break;

			case HANDLE_YOZM_USER_ERROR:
				String userErrorMsg = (String) args[0];
				if (usercallback != null)
					usercallback.onYozmUserInfoError(YozmModel.this,
							userErrorMsg);
				break;
			case HANDLE_YOZM_USER_START:
				if (usercallback != null)
					usercallback.onYozmUserInfoStart(YozmModel.this);
				break;
			case HANDLE_YOZM_USER_SUCCESS:
				SNSLoginData data = (SNSLoginData) args[0];
				if (usercallback != null)
					usercallback.onYozmUserInfoComplete(YozmModel.this, data);
				break;
			}
		}

	}
}
