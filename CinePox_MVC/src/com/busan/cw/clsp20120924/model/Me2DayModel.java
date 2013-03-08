package com.busan.cw.clsp20120924.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sns.me2day.FilePart;
import sns.me2day.Me2PostData;
import sns.me2day.MultipartEntity;
import sns.me2day.Part;
import sns.me2day.StringPart;
import utils.EncryptUtils;
import utils.JSONUtils;
import utils.LogUtils.l;
import utils.StringUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.busan.cw.clsp20120924_beta.R;
import com.busan.cw.clsp20120924.base.BaseModel;
import com.busan.cw.clsp20120924.interfaces.Me2DayModelAuthCallback;
import com.busan.cw.clsp20120924.interfaces.Me2DayModelPostCallback;
import com.busan.cw.clsp20120924.interfaces.Me2DayModelUserInfoCallback;

public class Me2DayModel extends BaseModel {

	public static final String APIKEY_ME2DAY = "4e19bc31dfeb694da5b8b8f3138d63ec"; // 테스트용
																					// 키
	public static final String ME2_TOKEN_URL = "http://me2day.net/api/get_auth_url.json?";
	public static final String ME2_AUTH_URL = "http://me2day.net/api/get_full_auth_token.json?";
	public static final String ME2_POST_URL = "http://me2day.net/api/create_post.json?";
	public static final String ME2_PERSON_URL = "http://me2day.net/api/get_person/%s.json?";

	public static final String ME2_AUTH_SUBMIT_POPUP_URL_FALSE = "http://me2day.net/api/auth_submit_popup?result=false";
	public static final String ME2_AUTH_SUBMIT_POPUP_URL_TRUE = "http://me2day.net/api/auth_submit_popup?result=true";

	// json 파싱할때 쓰는 키값
	public static final String EXTRA_ME2_NICKNAME = "nickname";
	public static final String EXTRA_ME2_PROFILE_IMAGE = "face";
	public static final String EXTRA_ME2_HOME = "me2dayHome";
	public static final String EXTRA_ME2_ERROR_CODE = "code";
	public static final String EXTRA_ME2_ID = "id";
	public static final String EXTRA_ME2_ERROR_MESSAGE = "message";
	public static final String EXTRA_ME2_TOKEN = "token";
	public static final String EXTRA_ME2_AUTH_TOKEN = "auth_token";
	public static final String EXTRA_ME2_USER_ID = "user_id";
	public static final String EXTRA_ME2_API_KEY = "Me2_application_key";
	public static final String EXTRA_ME2_API_KEY_PARAM = "akey";
	public static final String EXTRA_ME2_ERROR_DESCRIPTION = "description";

	// 핸들러 통신 식별값
	private static final int HANDLE_ME2_BASE_INFO_START = 100;
	private static final int HANDLE_ME2_BASE_INFO_SUCCESS = 101;
	private static final int HANDLE_ME2_BASE_INFO_ERROR = 102;

	private static final int HANDLE_ME2_AUTH_INFO_START = 200;
	private static final int HANDLE_ME2_AUTH_INFO_ERROR = 201;
	private static final int HANDLE_ME2_AUTH_INFO_SUCCESS = 202;

	private static final int HANDLE_ME2_POST_START = 300;
	private static final int HANDLE_ME2_POST_ERROR = 301;
	private static final int HANDLE_ME2_POST_SUCCESS = 302;

	private static final int HANDLE_ME2_USER_START = 400;
	private static final int HANDLE_ME2_USER_ERROR = 401;
	private static final int HANDLE_ME2_USER_SUCCESS = 402;

	private Me2DayModelAuthCallback authcallback;
	private Me2DayModelPostCallback postcallback;
	private Me2DayModelUserInfoCallback usercallback;

	private String me2BaseToken;

	public Me2DayModel(Context ctx) {
		super(ctx);
	}

	public Activity getActivity() {
		return (Activity) getContext();
	}

	public void setAuthCallback(Me2DayModelAuthCallback callback) {
		authcallback = callback;
	}

	public void setPostCallback(Me2DayModelPostCallback callback) {
		postcallback = callback;
	}

	public void setUserInfoCallback(Me2DayModelUserInfoCallback callback) {
		usercallback = callback;
	}

	/**
	 * 미투데이 로그인 url, 인증요청 토큰 가져오기
	 * 
	 */
	public void loadMe2BaseInfo() {
		new Thread(new Me2BaseInfoThread()).start();
	}

	/**
	 * 미투데이 엑세스 토큰과 유저 아이디 가져오기
	 * 
	 * 
	 */
	public void loadMe2AuthInfo() {
		new Thread(new Me2AuthInfoThread()).start();
	}

	/**
	 * 미투데이 사용자 정보 가져오기
	 * 
	 * @param userId
	 */
	public void loadMe2UserInfo(String userId) {
		new Thread(new Me2PersonInfoThread(userId)).start();
	}

	/**
	 * 미투데이에 글 올리기
	 * 
	 * @param data
	 * @param authToken
	 * @param userId
	 */
	@SuppressLint("DefaultLocale")
	public void me2Post(Me2PostData data, String authToken, String userId) {
		new Thread(new Me2PostThread(data, authToken, userId)).start();
	}

	private class Me2BaseInfoThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getActivity().runOnUiThread(
					new Me2CallbackPoster(HANDLE_ME2_AUTH_INFO_START));
			try {
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(EXTRA_ME2_API_KEY_PARAM,
						APIKEY_ME2DAY));

				JSONObject infoObject = JSONUtils.jsonFromURL(ME2_TOKEN_URL,
						params);
				try {
					l.d(infoObject.toString());
					me2BaseToken = infoObject.getString(EXTRA_ME2_TOKEN);
					String me2LoginUrl = infoObject.getString(EXTRA_URL);

					getActivity().runOnUiThread(
							new Me2CallbackPoster(HANDLE_ME2_BASE_INFO_SUCCESS,
									me2LoginUrl));
				} catch (JSONException e) {
					// 해당 키값이 없다면 에러가 발생한 것이므로 이에 맞는 처리
					e.printStackTrace();
					l.d("me2baseinfo error : " + infoObject.toString());
					String code = infoObject.getString(EXTRA_ME2_ERROR_CODE);
					String message = infoObject
							.getString(EXTRA_ME2_ERROR_MESSAGE);

					l.d("미투데이 토큰 로드 에러 - 코드 : "
							+ infoObject.getString(EXTRA_ME2_ERROR_CODE));
					l.d("미투데이 토큰 로드 에러 - 메시지 : "
							+ infoObject.getString(EXTRA_ME2_ERROR_MESSAGE));
					l.d("미투데이 토큰 로드 에러 - 내용 : "
							+ infoObject.getString(EXTRA_ME2_ERROR_DESCRIPTION));

					getActivity().runOnUiThread(
							new Me2CallbackPoster(HANDLE_ME2_BASE_INFO_ERROR,
									code, message));

				}
			} catch (JSONException e) {
				e.printStackTrace();
				getActivity().runOnUiThread(
						new Me2CallbackPoster(HANDLE_ME2_BASE_INFO_ERROR,
								getContext().getString(
										R.string.error_server_data)));
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new Me2CallbackPoster(HANDLE_ME2_BASE_INFO_ERROR,
								getContext().getString(R.string.error_device)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity()
						.runOnUiThread(
								new Me2CallbackPoster(
										HANDLE_ME2_BASE_INFO_ERROR,
										getContext().getString(
												R.string.error_connect)));
			}
		}

	}

	private class Me2AuthInfoThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			getActivity().runOnUiThread(
					new Me2CallbackPoster(HANDLE_ME2_AUTH_INFO_START,
							(Object[]) null));
			if (me2BaseToken == null) {
				getActivity().runOnUiThread(
						new Me2CallbackPoster(HANDLE_ME2_AUTH_INFO_ERROR,
								"you must call loadMe2BaseInfo()."));
				return;
			}

			try {
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(EXTRA_ME2_TOKEN, me2BaseToken));
				params.add(new BasicNameValuePair(EXTRA_ME2_API_KEY_PARAM,
						APIKEY_ME2DAY));

				JSONObject infoObject = JSONUtils.jsonFromURL(ME2_AUTH_URL,
						params);

				try {
					String me2LoginId = infoObject.getString(EXTRA_ME2_USER_ID);
					String me2AuthToken = infoObject
							.getString(EXTRA_ME2_AUTH_TOKEN);
					getActivity().runOnUiThread(
							new Me2CallbackPoster(HANDLE_ME2_AUTH_INFO_SUCCESS,
									me2AuthToken, me2LoginId));
				} catch (JSONException e) {
					// 해당 키값이 없다면 에러가 발생한 것이므로 이에 맞는 처리
					e.printStackTrace();
					l.d("me2auth error result : " + infoObject.toString());
					l.d("미투데이 토큰 로드 에러 - 코드 : "
							+ infoObject.getString(EXTRA_ME2_ERROR_CODE));
					l.d("미투데이 토큰 로드 에러 - 메시지 : "
							+ infoObject.getString(EXTRA_ME2_ERROR_MESSAGE));
					l.d("미투데이 토큰 로드 에러 - 내용 : "
							+ infoObject.getString(EXTRA_ME2_ERROR_DESCRIPTION));
					getActivity()
							.runOnUiThread(
									new Me2CallbackPoster(
											HANDLE_ME2_AUTH_INFO_ERROR,
											infoObject
													.getString(EXTRA_ME2_ERROR_DESCRIPTION)));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				getActivity().runOnUiThread(
						new Me2CallbackPoster(HANDLE_ME2_AUTH_INFO_ERROR,
								getContext().getString(
										R.string.error_server_data)));
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new Me2CallbackPoster(HANDLE_ME2_AUTH_INFO_ERROR,
								getContext().getString(R.string.error_device)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity()
						.runOnUiThread(
								new Me2CallbackPoster(
										HANDLE_ME2_AUTH_INFO_ERROR,
										getContext().getString(
												R.string.error_connect)));
			}
		}

	}

	private class Me2PostThread implements Runnable {

		private Me2PostData data;
		private String authToken;
		private String userId;

		private Me2PostThread(Me2PostData data, String authToken, String userId) {
			this.data = data;
			this.authToken = authToken;
			this.userId = userId;
		}

		@Override
		public void run() {

			// Basic [아이디:full_auth_token 로그인인증토큰(Base64 암호화)]
			// 아이디 인증 정보 헤더
			getActivity().runOnUiThread(
					new Me2CallbackPoster(HANDLE_ME2_POST_START,
							(Object[]) null));
			HttpPost post = new HttpPost(ME2_POST_URL);
			String authHeader = String.format("%s:full_auth_token %s", userId,
					authToken);
			String encodeHeader = "Basic "
					+ EncryptUtils.encodeString(authHeader);
			post.setHeader("Authorization", encodeHeader);

			// 데이터 압축 사용여부
			if (data.useGZip)
				post.setHeader("Accept-Encoding", "gzip");
			else
				post.setHeader("Accept-Encoding", "");

			// 접속 장비 정보
			post.setHeader("User-Agent", data.getUserAgent());

			l.d(String.format("loginId(%s), fullAuthToken(%s), auth(%s)",
					userId, authToken, encodeHeader));

			ArrayList<Part> partArray = new ArrayList<Part>();

			l.d(String.format("settingHttpClient body(%s), attachment(%s)",
					data.body, data.attachment));

			// 글 내용
			if (data.body != null) {
				partArray.add(new StringPart("post[body]", data.body, "UTF8"));
			}

			// 글 태그
			if (data.tag != null) {
				partArray.add(new StringPart("post[tags]", data.tag, "UTF8"));
			}

			// 아이콘(?)
			if (data.icon > 0) {
				partArray.add(new StringPart("post[icon]", "" + data.icon,
						"UTF8"));
			}

			if (data.attachment != null) {
				try {
					// 파일첨부
					FilePart filePart = new FilePart("attachment", new File(
							data.attachment));
					if (data.headerFileName != null)
						// 첨부파일 이름
						filePart.setHeaderFileName(data.headerFileName);
					partArray.add(filePart);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

			if (data.longitude != null) {
				partArray.add(new StringPart("longitude", data.longitude,
						"UTF8"));
			}

			if (data.latitude != null) {
				partArray
						.add(new StringPart("latitude", data.latitude, "UTF8"));
			}

			// ??
			if (data.spotId != null && data.spotId.length() > 0) {
				partArray.add(new StringPart("domain", "me2spot", "UTF8"));
				partArray.add(new StringPart("key", data.spotId, "UTF8"));
				l.d(">>>>>> key=" + data.spotId + ", domain=me2spot <<<<<<");
			}

			// ??
			if (data.location != null) {
				partArray
						.add(new StringPart("location", data.location, "UTF8"));
			}

			// ??
			if (data.isCloseComment == true) {
				partArray.add(new StringPart("close_comment", "true", "UTF8"));
			}

			int count = partArray.size();
			Part[] parts = new Part[count];
			for (int i = 0; i < count; i++) {
				parts[i] = partArray.get(i);
			}
			try {
				MultipartEntity entity = new MultipartEntity(parts);
				post.setEntity(entity);
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(post);
				InputStream in = response.getEntity().getContent();
				String message = StringUtils.stringFromStream(in);
				l.i("me2post result : " + message);
				if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
					getActivity().runOnUiThread(
							new Me2CallbackPoster(HANDLE_ME2_POST_SUCCESS,
									(Object[]) null));
				} else {
					getActivity().runOnUiThread(
							new Me2CallbackPoster(HANDLE_ME2_POST_ERROR,
									message));
				}
			} catch (IOException e) {
				e.printStackTrace();
				getActivity()
						.runOnUiThread(
								new Me2CallbackPoster(HANDLE_ME2_POST_ERROR,
										getContext().getString(
												R.string.error_connect)));
			}
		}

	}

	private class Me2PersonInfoThread implements Runnable {

		private String userId;

		private Me2PersonInfoThread(String userId) {
			this.userId = userId;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getActivity().runOnUiThread(
					new Me2CallbackPoster(HANDLE_ME2_USER_START,
							(Object[]) null));
			try {
				String personUrl = String.format(ME2_PERSON_URL, userId);
				l.i("me2userInfo url : " + personUrl);
				JSONObject personJson = JSONUtils.jsonFromURL(personUrl);
				SNSLoginData data = app().getUserConfig().loginData;
				data.nickName = personJson.getString(EXTRA_ME2_NICKNAME);
				data.snsHome = personJson.getString(EXTRA_ME2_HOME);
				data.profileImage = personJson
						.getString(EXTRA_ME2_PROFILE_IMAGE);
				data.userId = personJson.getString(EXTRA_ME2_ID);
				data.sns = EXTRA_AUTO_LOGIN_ME2DAY;

				getActivity().runOnUiThread(
						new Me2CallbackPoster(HANDLE_ME2_USER_SUCCESS, data));
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new Me2CallbackPoster(HANDLE_ME2_USER_ERROR, e
								.getMessage()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new Me2CallbackPoster(HANDLE_ME2_USER_ERROR, e
								.getMessage()));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				getActivity().runOnUiThread(
						new Me2CallbackPoster(HANDLE_ME2_USER_ERROR, e
								.getMessage()));
			}
		}

	}

	private class Me2CallbackPoster implements Runnable {

		private int what;
		private Object[] args;

		private Me2CallbackPoster(int what, Object... args) {
			this.what = what;
			this.args = args;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			switch (what) {
			case HANDLE_ME2_AUTH_INFO_ERROR:
				String authErrorMsg = (String) args[0];
				if (authcallback != null)
					authcallback.onMe2AuthError(Me2DayModel.this, authErrorMsg);
				break;
			case HANDLE_ME2_AUTH_INFO_START:
				if (authcallback != null)
					authcallback.onMe2AuthStart(Me2DayModel.this);
				break;
			case HANDLE_ME2_AUTH_INFO_SUCCESS:
				String me2AuthToken = (String) args[0];
				String me2LoginId = (String) args[1];
				if (authcallback != null)
					authcallback.onMe2AuthSuccess(Me2DayModel.this,
							me2AuthToken, me2LoginId);
				break;

			case HANDLE_ME2_BASE_INFO_ERROR:
				String code = (String) args[0];
				String baseErrorMsg = (String) args[1];
				if (authcallback != null)
					authcallback.onMe2BaseInfoError(Me2DayModel.this, code,
							baseErrorMsg);
				break;
			case HANDLE_ME2_BASE_INFO_START:
				if (authcallback != null)
					authcallback.onMe2BaseInfoStart(Me2DayModel.this);
				break;
			case HANDLE_ME2_BASE_INFO_SUCCESS:
				String me2LoginUrl = (String) args[0];
				if (authcallback != null)
					authcallback.onMe2BaseInfoLoaded(Me2DayModel.this,
							me2LoginUrl);
				break;

			case HANDLE_ME2_POST_ERROR:
				String message = (String) args[0];
				if (postcallback != null)
					postcallback.onMe2PostError(Me2DayModel.this, message);
				break;
			case HANDLE_ME2_POST_START:
				if (postcallback != null)
					postcallback.onMe2PostStart(Me2DayModel.this);
				break;
			case HANDLE_ME2_POST_SUCCESS:
				if (postcallback != null)
					postcallback.onMe2PostComplete(Me2DayModel.this);
				break;

			case HANDLE_ME2_USER_ERROR:
				String userErrorMsg = (String) args[0];
				if (usercallback != null)
					usercallback.onMe2UserInfoError(Me2DayModel.this,
							userErrorMsg);
				break;
			case HANDLE_ME2_USER_START:
				if (usercallback != null)
					usercallback.onMe2UserInfoStart(Me2DayModel.this);
				break;
			case HANDLE_ME2_USER_SUCCESS:
				SNSLoginData data = (SNSLoginData) args[0];
				if (usercallback != null)
					usercallback.onMe2UserInfoComplete(Me2DayModel.this, data);
				break;
			}
		}

	}

}
