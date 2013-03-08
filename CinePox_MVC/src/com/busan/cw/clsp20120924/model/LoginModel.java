package com.busan.cw.clsp20120924.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.JSONUtils;
import utils.LogUtils.l;
import utils.StreamUtils;
import utils.StringUtils;
import android.content.Context;
import android.os.AsyncTask;

import com.busan.cw.clsp20120924.base.BaseModel;
import com.busan.cw.clsp20120924.base.DomainManager;
import com.busan.cw.clsp20120924.interfaces.LoginModelCallback;

/**
 * http://tappserver.cinepox.com/member/
 * ?action=login&id=test2011&password=2013test
 * 
 * @author CINEPOX
 * 
 */
public class LoginModel extends BaseModel {

	public LoginModel(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	private LoginModelCallback callback;

	public void setCallback(LoginModelCallback callback) {
		this.callback = callback;
	}

	public void login(String id, String pw) {
		new LoginLoader().execute(id, pw);
	}

	public void loginSNS(SNSLoginData data) {
		// sns로그인시 사용하는 메서드. sns모듈 연동 완료 후 구현
		new SNSLoginLoader().execute(data);
	}

	private class SNSLoginLoader extends
			AsyncTask<SNSLoginData, Integer, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if (callback != null)
				callback.onLoginStart(LoginModel.this);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(SNSLoginData... params) {
			// TODO Auto-generated method stub
			// sns로그인시 사용하는 클래스.
			SNSLoginData data = params[0];
			ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams
					.add(new BasicNameValuePair(EXTRA_ACTION, EXTRA_LOGIN_SNS));
			postParams.add(new BasicNameValuePair(EXTRA_LOGIN_SNS_NAME,
					data.sns));
			postParams
					.add(new BasicNameValuePair(EXTRA_SNS_USERID, data.userId));
			postParams.add(new BasicNameValuePair(EXTRA_SNS_NICKNAME,
					data.nickName));
			postParams
					.add(new BasicNameValuePair(EXTRA_SNS_HOME, data.snsHome));
			postParams.add(new BasicNameValuePair(EXTRA_SNS_IMAGE,
					data.profileImage));
			String result;
			try {
				String loginUrl = DomainManager.getAppDomain(getContext())
						+ app().getAppConfig().getLoginSNSUrl();
				HttpURLConnection conn = (HttpURLConnection) new URL(loginUrl)
						.openConnection();
				DomainManager.signHeader(conn);
				StreamUtils.addPostParams(conn, postParams);
				JSONObject loginJson = new JSONObject(
						StringUtils.stringFromStream(conn.getInputStream()));
				l.d("cinepox login result : " + loginJson.toString());
				result = setConfig(loginJson);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				result = connectError();
			} catch (IOException e) {
				e.printStackTrace();
				result = connectError();
			} catch (JSONException e) {
				e.printStackTrace();
				result = dataError();
			} catch (NullPointerException e) {
				e.printStackTrace();
				result = deviceError();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (callback != null) {
				if (result == null)
					callback.onLoginSuccess(LoginModel.this);
				else
					callback.onLoginError(LoginModel.this, result);
			}
			super.onPostExecute(result);
		}
	}

	private class LoginLoader extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if (callback != null)
				callback.onLoginStart(LoginModel.this);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String id = params[0];
			String pw = params[1];

			l.d("cinepox login : " + id + ", " + pw);
			ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(EXTRA_ACTION, EXTRA_LOGIN));
			postParams.add(new BasicNameValuePair(EXTRA_ID, id));
			postParams.add(new BasicNameValuePair(EXTRA_PASSWORD, pw));
			String result = null;
			try {
				String loginUrl = DomainManager.getAppDomain(getContext())
						+ app().getAppConfig().getLoginUrl();
				HttpURLConnection conn = (HttpURLConnection) new URL(loginUrl)
						.openConnection();
				DomainManager.signHeader(conn);
				StreamUtils.addPostParams(conn, postParams);
				JSONObject loginJson = new JSONObject(
						StringUtils.stringFromStream(conn.getInputStream()));
				l.d("cinepox login result : " + loginJson.toString());
				result = setConfig(loginJson);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				result = connectError();
			} catch (IOException e) {
				e.printStackTrace();
				result = connectError();
			} catch (JSONException e) {
				e.printStackTrace();
				result = dataError();
			} catch (NullPointerException e) {
				e.printStackTrace();
				result = deviceError();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (callback != null) {
				if (result == null)
					callback.onLoginSuccess(LoginModel.this);
				else
					callback.onLoginError(LoginModel.this, result);
			}
			super.onPostExecute(result);
		}
	}

	/**
	 * 에러 발생하면 에러메시지 리턴
	 * 
	 * @param loginJson
	 * @return
	 * @throws JSONException
	 */
	private String setConfig(JSONObject loginJson) throws JSONException {

		boolean resultflag = isY(loginJson.getString(EXTRA_RESULT));
		if (!resultflag)
			return loginJson.getString(EXTRA_MSG);

		JSONObject dataJson = loginJson.getJSONObject(EXTRA_DATA);

		int isAdult;
		if ("Y".equals(dataJson.getString(EXTRA_IS_ADULT)))
			isAdult = 1;
		else if ("N".equals(dataJson.getString(EXTRA_IS_ADULT)))
			isAdult = 0;
		else
			isAdult = -1;
		boolean isAdmin = isY(dataJson.getString(EXTRA_IS_ADMIN));
		boolean isTestId = isY(dataJson.getString(EXTRA_IS_TEST));
		int memberSeq = dataJson.getInt(EXTRA_MEMBER_NUM);
		String accountType = dataJson.getString(EXTRA_TYPE);
		String accountName = dataJson.getString(EXTRA_ID);
		int siteSeq = dataJson.getInt(EXTRA_SITE_NUM);
		String userName = dataJson.getString(EXTRA_NAME);
		int groupSeq = dataJson.getInt(EXTRA_GROUP_NUM);
		String groupName = dataJson.getString(EXTRA_GROUP_NAME);
		int levelSeq = dataJson.getInt(EXTRA_USERLEVEL_NUM);
		String levelName = dataJson.getString(EXTRA_USERLEVEL_NAME);
		int activityQuantity = dataJson.getInt(EXTRA_ACTIVITY_QUALITY);
		boolean isSNS = isY(dataJson.getString(EXTRA_IS_SNS));
		String profileImageUrl = dataJson.getString(EXTRA_PROFILE_IMAGE_URL);
		String nickname = dataJson.getString(EXTRA_NICKNAME);

		UserConfigData config = app().getUserConfig();
		config.adult = isAdult;
		config.admin = isAdmin;
		config.testId = isTestId;
		config.memberSeq = memberSeq;
		config.accountName = accountName;
		config.accountType = accountType;
		config.siteSeq = siteSeq;
		config.userName = userName;
		config.groupSeq = groupSeq;
		config.groupName = groupName;
		config.levelSeq = levelSeq;
		config.levelName = levelName;
		config.activityQuantity = activityQuantity;
		config.snsLogin = isSNS;
		config.profileImageUrl = profileImageUrl;
		config.nickName = nickname;

		try {
			// 안심아이디 관련정보
			JSONObject safeIdJson = dataJson.getJSONObject(EXTRA_SETTING);
			int viewClass = safeIdJson.getInt(EXTRA_DISPLAY_CLASS);
			config.viewClass = viewClass;

			try {
				JSONArray notseeMenuJson = safeIdJson.getJSONArray(EXTRA_MENU);
				String[] notseeMenuArray = JSONUtils
						.jsonArrayToArray(notseeMenuJson);
				config.notseeMenus = notseeMenuArray;
			} catch (JSONException e) {
				e.printStackTrace();
				config.notseeMenus = null;
			}

			try {
				JSONArray notseeCategoryJson = safeIdJson
						.getJSONArray(EXTRA_CATEGORY);
				String[] notseeCategoryArray = JSONUtils
						.jsonArrayToArray(notseeCategoryJson);
				config.notseeCategorys = notseeCategoryArray;
			} catch (JSONException e) {
				e.printStackTrace();
				config.notseeCategorys = null;
			}

			config.safeId = true;
		} catch (JSONException e) {
			// 안심아이디 아님
			e.printStackTrace();
			config.viewClass = 0;
			config.notseeMenus = null;
			config.notseeCategorys = null;
		}

		// 씨네폭스 로그인에서 통합 사용을 위헤 로그인 데이터 다시 세팅
		SNSLoginData loginData = config.loginData;
		loginData.nickName = nickname;
		loginData.profileImage = profileImageUrl;
		loginData.sns = isSNS ? loginData.sns : EXTRA_AUTO_LOGIN_CINEPOX;
		loginData.userId = isSNS ? loginData.userId : accountName;

		return null;
	}

}
