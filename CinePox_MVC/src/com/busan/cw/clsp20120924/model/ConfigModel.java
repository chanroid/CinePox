/**
 * 0. Project  : CinePox_MVC
 *
 * 1. FileName : ConfigModel.java
 * 2. Package : com.busan.cw.clsp20120924.model
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 31. 오후 5:44:33
 * 6. 변경이력 : 
 *		2012. 10. 31. 오후 5:44:33 : 신규
 *
 */
package com.busan.cw.clsp20120924.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kr.co.chan.util.Util;
import kr.co.chan.util.l;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.busan.cw.clsp20120924.base.Domain;
import com.busan.cw.clsp20120924.base.Model;
import com.busan.cw.clsp20120924.structs.CategoryItemData;
import com.busan.cw.clsp20120924.structs.ConfigActionData;
import com.busan.cw.clsp20120924.structs.MainBannerData;
import com.busan.cw.clsp20120924.structs.WidgetUpdateData;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : ConfigModel.java
 * 3. Package  : com.busan.cw.clsp20120924.model
 * 4. Comment  : 환경설정 관리
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 5:44:33
 * </PRE>
 */
public class ConfigModel extends Model {

	private boolean isAppRecommend;
	
	private ArrayList<WidgetUpdateData> mWidgetDataArray = new ArrayList<WidgetUpdateData>();
	private ArrayList<CategoryItemData> mCategoryDataArray = new ArrayList<CategoryItemData>();
	private MainBannerData mMainBanner;
	private ConfigActionData mActionData;
	
	private Context mContext;
	private String mWidgetUpdateUrl;
	private String mErrorLogUrl;

	private SharedPreferences mPref;
	private SharedPreferences.Editor mEdit;

	private static ConfigModel instance;

	public static ConfigModel getInstance(Context ctx) {
		if (instance == null)
			instance = new ConfigModel(ctx);
		return instance;
	}

	private ConfigModel(Context ctx) {
		mContext = ctx;
		mPref = mContext.getSharedPreferences("config", 0);
		mEdit = mPref.edit();
	}

	public ArrayList<WidgetUpdateData> getWidgetData() {
		// TODO Auto-generated method stub
		return mWidgetDataArray;
	}
	
	public String getWidgetUpdateUrl() {
		return mWidgetUpdateUrl;
	}
	
	public String getErrorLogUrl() {
		return mErrorLogUrl;
	}
	
	public boolean isRecommend() {
		return isAppRecommend;
	}
	
	public MainBannerData getMainBannerData() {
		return mMainBanner;
	}

	public ArrayList<CategoryItemData> getCategoryData() {
		return mCategoryDataArray;
	}

	public String loadConfig() {
		try {
			String url = Domain.WEB_DOMAIN + CONFIG_PATH;
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("app_version", mContext
					.getPackageManager().getPackageInfo(
							mContext.getPackageName(), 0).versionName));
			params.add(new BasicNameValuePair(KEY_SETTING, RESPONSE_JSON));
			params.add(new BasicNameValuePair(KEY_SET_DEVICE,
					DEVICE_ANDROID_APP));
			JSONObject o = Util.Stream.jsonFromURLbyPOST(url, params);
			l.i(o.toString());
			if ("N".equalsIgnoreCase(o.getString(KEY_RESULT))) {
				return o.getString(KEY_MSG);
			}

			JSONObject dataObject = o.getJSONObject(KEY_DATA);
			// String version = dataObject.getString("ver");
			// String player_config = dataObject.getString("player_config");
			// getPlayerConfig().setPlayerConfigUrl(player_config);
			// String access_log_url =
			// dataObject.getString("access_log_url");
			mErrorLogUrl = dataObject.getString("error_log_url");
			mWidgetUpdateUrl = dataObject.getString("widget_data_url");

			String is_app_recommend = dataObject.getString("is_app_recommend");
			isAppRecommend = "Y".equalsIgnoreCase(is_app_recommend);

			if (!Util.Display.isTablet(mContext)) {
				JSONArray bestList = dataObject.getJSONArray("bestList");
				for (int i = 0; i < bestList.length(); i++)
					parseCategory(bestList.getString(i));
			}

			JSONObject banner = dataObject.getJSONObject("top_banner");
			String banner_img = banner.getString("top_banner_img");
			mMainBanner = new MainBannerData(
					Util.Stream.bitmapFromURL(banner_img),
					banner.getString("top_banner_url"));

			JSONArray action = dataObject.getJSONArray("action");
			for (int i = 0; i < action.length(); i++) {
				if (!parseAction(action.getJSONObject(i)))
					break;
			}

			// 로그인 기능 제거
			// if (getConfig().isAutoLogin()) {
			// url = String.format(AUTOLOGIN_URL,
			// getConfig().getAccount()[0], getConfig()
			// .getAccount()[1]);
			// String result = Util.Stream.stringFromURL(url);
			// l.i("login result : " + result);
			// if ("1".equalsIgnoreCase(result)) {
			// getConfig().setLogined(true);
			// } else {
			// getConfig().setLogined(false);
			// getConfig().setAccount("", "");
			// }
			// }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		return null;
	}

	private void parseCategory(String categoryurl)
			throws ClientProtocolException, IOException, JSONException {
		// TODO Auto-generated method stub
		JSONObject o = Util.Stream.jsonFromURL(categoryurl);
		String title = o.getString(KEY_TITLE);
		mCategoryDataArray.add(new CategoryItemData(title, categoryurl));
	}

	private boolean parseAction(JSONObject o) throws JSONException {
		String msgNum = o.getString("code");
		boolean result = true;

		if (isReadMessage(msgNum)) {
			mActionData = new ConfigActionData();
			mActionData.num = o.getString("code");
			mActionData.action = o.getString("what");
			mActionData.type = o.getString("type");
			mActionData.message = o.getString("msg");
			mActionData.image = o.getString("image");
			mActionData.url = o.getString("url");
			if ("update".equalsIgnoreCase(mActionData.action))
				result = false;
		}

		return result;
	}
	
	public ConfigActionData getActionData() {
		return mActionData;
	}

	public boolean isReadMessage(String num) {
		int orign = mPref.getInt(num, 0);
		if (orign <= 0)
			return true;
		int today = Calendar.getInstance(Locale.getDefault()).get(
				Calendar.DAY_OF_YEAR);
		if (today < 8)
			today += 365;
		int remain = today - orign;
		l.i("num : " + today + " + " + orign);
		if (remain < 8)
			return false;
		return true;
	}

	public void addReadMessage(String num) {
		mEdit.putInt(
				num,
				Calendar.getInstance(Locale.getDefault()).get(
						Calendar.DAY_OF_YEAR)).commit();
	}

}
