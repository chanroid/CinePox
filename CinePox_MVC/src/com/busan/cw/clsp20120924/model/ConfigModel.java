package com.busan.cw.clsp20120924.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.LogUtils.l;
import utils.StringUtils;
import android.content.Context;
import android.os.AsyncTask;

import com.busan.cw.clsp20120924.base.BaseModel;
import com.busan.cw.clsp20120924.base.DomainManager;
import com.busan.cw.clsp20120924.interfaces.ConfigModelCallback;

/**
 * http://tappserver.cinepox.com/config/
 * 
 * @author CINEPOX
 * 
 */
public class ConfigModel extends BaseModel {

	public ConfigModel(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	public static final String CONFIG_URL = "http://tappserver.cinepox.com/config/";
	// 이건 진입점이므로 도메인이고 뭐고 그냥 fix
	public static final String UPDATE_DEFAULT = "default"; // 창 안띄우고 넘어감
	public static final String UPDATE_ALERT = "alert"; // 강제로 업데이트하도록 창 띄움
	public static final String UPDATE_CONFIRM = "confirm"; // 창 띄우고 선택 가능

	private String updateType = UPDATE_DEFAULT;

	public String getUpdateType() {
		return updateType;
	}

	private ConfigModelCallback callback;

	public void setCallback(ConfigModelCallback callback) {
		this.callback = callback;
	}

	public CinepoxAppModel playerConfig() {
		return (CinepoxAppModel) getContext().getApplicationContext();
	}

	public void loadConfig() {
		new ConfigLoader().execute();
	}

	private class ConfigLoader extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (callback != null)
				callback.onLoadConfigStart(ConfigModel.this);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject configJson = null;
			String result = null;
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(CONFIG_URL)
						.openConnection();
				DomainManager.signHeader(conn);
				configJson = new JSONObject(StringUtils.stringFromStream(conn
						.getInputStream()));
				l.d("config result : " + configJson.toString());

				// 기본정보
				String ver = configJson.getString(EXTRA_APP_VERSION);
				int ver_code = configJson.getInt(EXTRA_APP_VERSION_CODE);
				boolean isNotifyUpdate = isY(configJson
						.getString(EXTRA_IS_UPDATE));
				String updateType = configJson.getString(EXTRA_UPDATE_TYPE);
				String updateUrl = configJson.getString(EXTRA_UPDATE_URL);
				boolean isDebug = isY(configJson.getString(EXTRA_IS_DEBUG));
				ArrayList<String> debugIds = new ArrayList<String>();

				JSONArray debugIdJson = configJson
						.getJSONArray(EXTRA_DEBUG_IDS);
				for (int i = 0; i < debugIdJson.length(); i++) {
					debugIds.add(debugIdJson.getString(i));
				}

				// 팝업 광고창 관련 변수
				JSONObject popupJson = configJson
						.getJSONObject(EXTRA_POPUP_INFO);
				String popImageUrl = popupJson.getString(EXTRA_IMAGE_URL);
				String popClickUrl = popupJson.getString(EXTRA_CLICK_URL);
				int popHideDay = popupJson.getInt(EXTRA_HIDE_DAY);

				// 데이터 URL 관련 변수
				JSONObject accessUrlJson = configJson
						.getJSONObject(EXTRA_DATA_ACCESS);
				String domain = accessUrlJson.getString(EXTRA_DATA_DOMAIN);
				String debugDomain = accessUrlJson
						.getString(EXTRA_DATA_DEBUG_DOMAIN);
				String widgetUrl = accessUrlJson
						.getString(EXTRA_WIDGET_DATA_URL);
				String accessLogUrl = accessUrlJson
						.getString(EXTRA_ACCESS_LOG_URL);
				String errorLogUrl = accessUrlJson
						.getString(EXTRA_ERROR_LOG_URL);
				String loginUrl = accessUrlJson
						.getString(EXTRA_LOGIN_CHECK_URL);
				String loginSNSUrl = accessUrlJson
						.getString(EXTRA_LOGIN_SNS_CHECK_URL);
				String periodInfoUrl = accessUrlJson
						.getString(EXTRA_PERIOD_INFO_URL);
				String pointInfoUrl = accessUrlJson
						.getString(EXTRA_POINT_INFO_URL);
				String movieListUrl = accessUrlJson
						.getString(EXTRA_MOVIELIST_URL);
				String categoryListUrl = accessUrlJson
						.getString(EXTRA_CATEGORYLIST_URL);
				String countryListUrl = accessUrlJson
						.getString(EXTRA_COUNTRYLIST_URL);
				String sortListUrl = accessUrlJson
						.getString(EXTRA_SORTLIST_URL);
				String movieDetailUrl = accessUrlJson.getString(EXTRA_MOVIEDETAIL_URL);
				String pointsortListUrl = accessUrlJson.getString(EXTRA_POINT_SORTLIST_URL);

				// 플레이어 환경설정 관련 변수
				JSONObject playerconfig = configJson
						.getJSONObject(EXTRA_PLAYER_CONFIG);
				String playerDomain = playerconfig
						.getString(EXTRA_PLAYER_DOMAIN);
				String playerDebugDomain = playerconfig
						.getString(EXTRA_PLAYER_DEBUG_DOMAIN);
				String loadTextBannerUrl = playerconfig
						.getString(EXTRA_GET_TEXT_BANNER_URL);
				String loadAdBannerUrl = playerconfig
						.getString(EXTRA_GET_AD_URL);
				String loadBookmarkUrl = playerconfig
						.getString(EXTRA_GET_BOOKMARK_LIST);
				String insertBookmarkUrl = playerconfig
						.getString(EXTRA_BOOKMARK_INSERT_URL);
				String deleteBookmarkUrl = playerconfig
						.getString(EXTRA_BOOKMARK_DELETE_URL);
				String bugReportUrl = playerconfig
						.getString(EXTRA_BUG_INSERT_URL);
				String playerLogUrl = playerconfig.getString(EXTRA_LOG_URL);
				String sendPlayTimeUrl = playerconfig
						.getString(EXTRA_PLAY_TIME_URL);
				String gsmMessage = playerconfig.getString(EXTRA_3G_MESSAGE);
				int gsmMessageLength = playerconfig.getInt(EXTRA_3G_LENGTH);

				// 웹페이지 호출 관련 변수
				JSONObject mobwebJson = configJson
						.getJSONObject(EXTRA_MOBILE_WEB);

				String mobwebDomain = mobwebJson.getString(EXTRA_MOBILE_DOMAIN);
				String mobwebDebugDomain = mobwebJson
						.getString(EXTRA_MOBILE_DEBUG_DOMAIN);
				String findIdUrl = mobwebJson.getString(EXTRA_FIND_ID_URL);
				String findPwUrl = mobwebJson.getString(EXTRA_FIND_PW_URL);

				// 웹뷰 관련 변수
				JSONObject webUrlJson = configJson
						.getJSONObject(EXTRA_WEB_VIEW);
				String webViewDomain = webUrlJson
						.getString(EXTRA_WEB_VIEW_DOMAIN);
				String webViewDebugDomain = webUrlJson
						.getString(EXTRA_WEB_VIEW_DEBUG_DOMAIN);
				String snsjoinUrl = webUrlJson.getString(EXTRA_JOIN_SNS);
				String joinUrl = webUrlJson.getString(EXTRA_JOIN);
				String buyTicketUrl = webUrlJson
						.getString(EXTRA_BUY_TICKET_URL);
				String buyPointUrl = webUrlJson.getString(EXTRA_BUY_POINT_URL);
				String memberoutUrl = webUrlJson.getString(EXTRA_MEMBEROUT_URL);

				// 앱 환경설정 변수 세팅
				AppConfigData appConfig = app().getAppConfig();

				ConfigModel.this.updateType = updateType;
				appConfig.recentVerName = ver;
				appConfig.recentVerCode = ver_code;
				appConfig.isUpdate = isNotifyUpdate;
				appConfig.updateUrl = updateUrl;
				appConfig.debugMode = isDebug;
				appConfig.debugIds = debugIds;

				appConfig.domain = domain;
				appConfig.debugDomain = debugDomain;
				appConfig.widgetDataUrl = widgetUrl;
				appConfig.accessLogUrl = accessLogUrl;
				appConfig.errorLogUrl = errorLogUrl;
				appConfig.loginUrl = loginUrl;
				appConfig.loginSNSUrl = loginSNSUrl;
				appConfig.periodInfoUrl = periodInfoUrl;
				appConfig.pointInfoUrl = pointInfoUrl;
				appConfig.movieListUrl = movieListUrl;
				appConfig.categoryListUrl = categoryListUrl;
				appConfig.countryListUrl = countryListUrl;
				appConfig.sortListUrl = sortListUrl;
				appConfig.movieDetailUrl = movieDetailUrl;
				appConfig.pointsortListUrl = pointsortListUrl;

				appConfig.adImageUrl = popImageUrl;
				appConfig.adImageTargetUrl = popClickUrl;
				appConfig.adHideDay = popHideDay;

				appConfig.mWebDomain = mobwebDomain;
				appConfig.mWebDebugDomain = mobwebDebugDomain;
				appConfig.snsJoinUrl = snsjoinUrl;
				appConfig.joinUrl = joinUrl;
				appConfig.findIdUrl = findIdUrl;
				appConfig.findPwUrl = findPwUrl;

				appConfig.webViewDomain = webViewDomain;
				appConfig.webViewDebugDomain = webViewDebugDomain;
				appConfig.buyTicketUrl = buyTicketUrl;
				appConfig.buyPointUrl = buyPointUrl;
				appConfig.memberoutUrl = memberoutUrl;

				// 플레이어 환경설정 변수 세팅
				PlayerConfigData playerConfig = app().getPlayerConfig();

				playerConfig.playerDomain = playerDomain;
				playerConfig.playerDebugDomain = playerDebugDomain;

				playerConfig.loadTextBannerUrl = loadTextBannerUrl;
				playerConfig.loadADBannerUrl = loadAdBannerUrl;
				playerConfig.loadBookmarkListUrl = loadBookmarkUrl;
				playerConfig.insertBookmarkUrl = insertBookmarkUrl;
				playerConfig.deleteBookmarkUrl = deleteBookmarkUrl;
				playerConfig.bugReportUrl = bugReportUrl;
				playerConfig.sendPlayerLogUrl = playerLogUrl;
				playerConfig.sendPlayTimeUrl = sendPlayTimeUrl;
				playerConfig.gsmMessage = gsmMessage;
				playerConfig.gsmMessageLength = gsmMessageLength;

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
			super.onPostExecute(result);
			if (callback != null) {
				if (result == null)
					callback.onLoadConfigComplete(ConfigModel.this);
				else
					callback.onLoadConfigError(ConfigModel.this, result);
			}
		}
	}

}
