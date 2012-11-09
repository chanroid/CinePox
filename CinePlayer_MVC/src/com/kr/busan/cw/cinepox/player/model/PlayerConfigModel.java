package com.kr.busan.cw.cinepox.player.model;

import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.ACCESS_DOMAIN;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.CAPTION_EXT_SMI;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.CAPTION_EXT_SRT;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.DEVICE;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_3G_MESSAGE;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_3G_MESSAGE_LENGTH;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_BUG_INSERT_URL;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_DEVICE_TYPE;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_EXCEPTION;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_KEY;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_LANG;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_MEMBER_NUM;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_MOVIE_LIST;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_MOVIE_NUM;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_MOVIE_URL;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_MSG;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_NAME;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_ORDER_CODE;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_PLAY_TIME;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_PLAY_TIME_URL;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_PROTOCOL_TYPE;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_QUALITY;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_RESULT;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_SETTING;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_SET_TIME;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_SET_URL;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_SHAKE_KEY;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_START_TIME;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_TYPE;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.KEY_URL;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.RESPONSE_JSON;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.RESULT_ERROR;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.RESULT_MOVIE;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.RESULT_SUCCESS;
import static com.kr.busan.cw.cinepox.player.model.PlayerModel.Const.SHAKE_REQUEST_URL;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import kr.co.chan.util.Util;
import kr.co.chan.util.l;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.widget.Toast;

import com.kr.busan.cw.cinepox.player.structs.QualityData;

public class PlayerConfigModel extends PlayerModel {

	private static PlayerConfigModel instance;

	public static PlayerConfigModel getInstance(Context ctx) {
		if (instance == null)
			instance = new PlayerConfigModel(ctx);
		return instance;
	}

	private long continuePos = 0l;

	private SharedPreferences mPref;
	private SharedPreferences.Editor mEdit;

	private Uri mCinepoxURI;
	private Uri mSetURI;
	private Context mContext;
	private String mMovieNum;
	private String mMemNum;
	private String mOrderCode;
	private CaptionModel mCaptionModel;
	private VideoModel mVideoModel;

	private String m3GMessage = "WI-FI 환경에서 감상하시는 것을 권장합니다.";
	private int m3GMessageLength = Toast.LENGTH_LONG;

	private String mBugReportUrl;
	private String mPlaytimeUrl;
//	private String mTextBannerUrl;
//	private String mADUrl;
//	private String mBookmarkListUrl;
//	private String mBookmarkInsertUrl;
//	private String mBookmarkDeleteUrl;
//	private String mLogReportUrl;

	private PlayerConfigModel(Context ctx) {
		mContext = ctx;
		mCaptionModel = CaptionModel.getInstance();
		mVideoModel = VideoModel.getInstance(ctx);
		mPref = ctx.getSharedPreferences(Const.KEY_SETTING, 0);
		mEdit = mPref.edit();
	}

	public int getInitialQuality() {
		ArrayList<QualityData> qualityArray = mVideoModel.getQualityArray();
		if (qualityArray != null && isCinepoxURI()) {
			String ctype = getCinepoxURI().getQueryParameter("ctype");
			if (ctype != null) {
				for (int i = 0; i < qualityArray.size(); i++) {
					if (ctype.equalsIgnoreCase(qualityArray.get(i).TYPE)) {
						return i;
					}
				}
			} else {
				int quality = 0;
				if (qualityArray.size() > 1)
					quality = qualityArray.size() / 2;
				else if (qualityArray.size() > 2)
					quality = qualityArray.size() / 2 + 1;

				for (int i = 0; i < qualityArray.size(); i++) {
					if (getDefaultQuality().equalsIgnoreCase(
							qualityArray.get(i).TYPE)) {
						setDefaultQuality(qualityArray.get(i).TYPE);
						quality = i;
					}
				}

				return quality;
			}
		}
		return -1;
	}

	public int get3GMessageLength() {
		return m3GMessageLength;
	}

	public String get3GMessage() {
		return m3GMessage;
	}

	public void setDefaultQuality(String ctype) {
		mEdit.putString(Const.KEY_QUALITY, ctype).commit();
	}

	public String getDefaultQuality() {
		return mPref.getString(Const.KEY_QUALITY, "C600");
	}

	public boolean isContinue() {
		return continuePos > 0l;
	}

	public long getContinuePosition() {
		return continuePos;
	}

	public void setContinuePosition(long pos) {
		l.i("setContinuePos : " + pos);
		continuePos = pos;
	}

	public boolean isSetURI() {
		return mSetURI != null;
	}

	public Uri getSetURI() {
		return mSetURI;
	}

	public Uri getCinepoxURI() {
		return mCinepoxURI;
	}

	public String getMemberNum() {
		return mMemNum;
	}

	public String getMovieNum() {
		return mMovieNum;
	}

	public String getOrderCode() {
		return mOrderCode;
	}

	public boolean isCinepoxURI(Uri uri) {
		return "cinepox".equalsIgnoreCase(uri.getScheme());
	}

	public boolean isCinepoxURI() {
		return isCinepoxURI(mCinepoxURI);
	}

	public boolean isContentURI(Uri uri) {
		return "content".equalsIgnoreCase(uri.getScheme());
	}

	public synchronized boolean sendShareInfo(Location loc, String sendurl) {
		try {
			String shake_key = generateShareKey(loc);
			long startTime = System.currentTimeMillis();
			long currentTime = 0l;
			boolean result = false;
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(KEY_SHAKE_KEY, shake_key));
			params.add(new BasicNameValuePair(KEY_URL, sendurl));
			while (currentTime - 5000 < startTime) {
				try {
					JSONObject js = Util.Stream.jsonFromURLbyPOST(
							SHAKE_REQUEST_URL, params);
					String resultText = js.getString("result");
					String is_response = js.getString("is_response");
					result = "Y".equalsIgnoreCase(is_response)
							&& "Y".equalsIgnoreCase(resultText);
					if (result)
						break;
					Thread.sleep(500);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				currentTime = System.currentTimeMillis();
			}

			if (result) {
				removeShareRequest(shake_key);
				return true;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	public synchronized void removeShareRequest(String key)
			throws IllegalStateException, IOException {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_SHAKE_KEY, key));
		Util.Stream.inStreamFromURLbyPOST(Const.SHAKE_DELETE_URL, params);
	}

	public synchronized String generateShareKey(Location loc)
			throws ClientProtocolException, JSONException, IOException {
		DecimalFormat format = new DecimalFormat(".#");
		String lng = format.format(loc.getLongitude());
		String lat = format.format(loc.getLatitude());
		String sendKey = Util.MD5(lng + lat);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_SHAKE_KEY, sendKey));
		JSONObject js = Util.Stream.jsonFromURLbyPOST(Const.SHAKE_GET_KEY_URL,
				params);
		return js.getString("shake_key");
	}

	public synchronized void sendErrorLog(Intent intent)
			throws IllegalStateException, IOException {
		// 서버작업 후 작성할것
		StringBuffer sb = new StringBuffer();
		sb.append("APP Ver : " + Util.App.getVersionNum(mContext) + "\n");
		sb.append("Time : " + Util.Time.getCurrentDateTimeString() + "\n");
		sb.append("Model Name : " + Build.MODEL + "\n");
		sb.append("Android Ver. : " + Build.VERSION.RELEASE + "\n");
		sb.append("SDK Ver. : " + Build.VERSION.SDK_INT + "\n");
		sb.append("aviliable Memory : " + Debug.getGlobalFreedSize() + "\n");

		sb.append("movieProductInfo_seq : " + getMovieNum() + "\n");
		sb.append("member_seq : " + getMemberNum() + "\n");
		sb.append("cinepox url : " + getCinepoxURI().toString() + "\n");
		sb.append("order code : " + mOrderCode + "\n");

		Throwable exception = (Throwable) intent
				.getSerializableExtra(KEY_EXCEPTION);
		String video_url = intent.getStringExtra(KEY_MOVIE_URL);

		if (video_url != null)
			sb.append("video url : " + video_url + "\n");

		if (exception != null) {
			sb.append("Exception type : " + exception.getClass().getName()
					+ "\n");
			sb.append("================= Stack trace =================");
			sb.append(Util.Stream.stringFromThrowable(exception));
		} else {
			String message = intent.getStringExtra(KEY_MSG);
			if (message != null) {
				sb.append("Exception type : unknown\n");
				sb.append("================= Error Message =================");
				sb.append(message);
			}
		}

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_TYPE, KEY_EXCEPTION));
		params.add(new BasicNameValuePair(KEY_MSG, sb.toString()));
		Util.Stream.inStreamFromURLbyPOST(mBugReportUrl, params);

	}

	public synchronized void sendPlayTime(long starttime, long playtime)
			throws IllegalStateException, IOException {
		long play_time = System.currentTimeMillis() - starttime;
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("soon_time", (playtime / 1000) + ""));
		params.add(new BasicNameValuePair(KEY_PLAY_TIME, (play_time / 1000)
				+ ""));
		params.add(new BasicNameValuePair(KEY_ORDER_CODE, mOrderCode));
		Util.Stream.inStreamFromURLbyPOST(mPlaytimeUrl, params);
	}

	public synchronized void sendPlayTime(Intent intent)
			throws IllegalStateException, IOException {
		long starttime = intent.getLongExtra(KEY_START_TIME, 0l);
		long playtime = intent.getLongExtra(KEY_PLAY_TIME, 0l);
		sendPlayTime(starttime, playtime);
	}

	public synchronized JSONObject loadUpdate() {
		String url = ACCESS_DOMAIN + "player/mobUpdateCheck";
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair(KEY_SETTING, RESPONSE_JSON));
		param.add(new BasicNameValuePair(KEY_DEVICE_TYPE, DEVICE));
		try {
			return Util.Stream.jsonFromURLbyPOST(url, param);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String loadConfig(Intent params) {
		Uri path = params.getData();

		if (path == null)
			return RESULT_ERROR + "invaild url";

		if (!isCinepoxURI(path)) {
			if (isContentURI(path)) {
				String filePath = Util.File.getMediaPathfromUri(mContext, path);
				if (!mCaptionModel.loadCaption(Util.File.changeExtension(
						filePath, CAPTION_EXT_SMI)))
					mCaptionModel.loadCaption(Util.File.changeExtension(
							filePath, CAPTION_EXT_SRT));
			}
			mSetURI = path;
			return RESULT_MOVIE;
		} else {
			try {
				mCinepoxURI = path;
				String set_url = path.getQueryParameter(KEY_SET_URL);
				String set_time = path.getQueryParameter(KEY_SET_TIME);
				mOrderCode = path.getQueryParameter(KEY_ORDER_CODE);

				if (params.getLongExtra(KEY_SET_TIME, 0) > 0)
					continuePos = params.getLongExtra(KEY_SET_TIME, 0) * 1000;
				else if (set_time != null)
					continuePos = Long.parseLong(set_time) * 1000;

				if (set_url != null)
					mSetURI = Uri.parse(set_url);

				String movie_num = path.getQueryParameter(KEY_MOVIE_NUM);
				String member_seq = path.getQueryParameter(KEY_MEMBER_NUM);
				if (movie_num != null || member_seq != null) {
					mMovieNum = movie_num;
					mMemNum = member_seq;
					String config_url = ACCESS_DOMAIN + "player/getContents/";
					ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
					param.add(new BasicNameValuePair(KEY_MOVIE_NUM, movie_num));
					param.add(new BasicNameValuePair(KEY_MEMBER_NUM, member_seq));
					param.add(new BasicNameValuePair(KEY_SETTING, RESPONSE_JSON));
					if (Build.VERSION.SDK_INT > 8)
						param.add(new BasicNameValuePair(KEY_PROTOCOL_TYPE,
								"http"));
					else
						param.add(new BasicNameValuePair(KEY_PROTOCOL_TYPE,
								"rtsp"));
					param.add(new BasicNameValuePair(KEY_DEVICE_TYPE, DEVICE));
					param.add(new BasicNameValuePair(KEY_ORDER_CODE, mOrderCode));
					JSONObject config = Util.Stream.jsonFromURLbyPOST(
							config_url, param);
					l.i(config.toString());
					try {
						if ("N".equalsIgnoreCase(config.getString(KEY_RESULT)))
							return RESULT_ERROR + config.getString(KEY_MSG);
					} catch (JSONException e1) {
						e1.printStackTrace();
						return RESULT_ERROR + e1.getMessage();
					}

					JSONObject movie_list;

					try {
						movie_list = config.getJSONArray(KEY_MOVIE_LIST)
								.getJSONObject(0);
					} catch (Exception e) {
						e.printStackTrace();
						return RESULT_ERROR + e.getMessage();
					}

					try {
						String lang = movie_list.getString(KEY_LANG);
						// String title = movie_list.getString(KEY_NAME);
						// mTitle = title;
						if (lang != null || !"".equalsIgnoreCase(lang)) {
							mCaptionModel.loadCaption(lang);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					try {
						JSONArray quality = movie_list
								.getJSONArray(KEY_QUALITY);
						ArrayList<QualityData> mQualityMap = new ArrayList<QualityData>();
						for (int i = 0; i < quality.length(); i++) {
							JSONObject qual = quality.getJSONObject(i);
							String type = qual.getString(KEY_TYPE);
							String name = qual.getString(KEY_NAME);
							String url = qual.getString(KEY_URL);
							String key = qual.getString(KEY_KEY);
							QualityData data = new QualityData();
							data.KEY = key;
							data.TYPE = type;
							data.URL = url;
							data.NAME = name;
							mQualityMap.add(data);
						}
						mVideoModel.setQualityArray(mQualityMap);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					try {
						JSONObject urlArray = config.getJSONObject(KEY_URL);
						mBugReportUrl = urlArray.getString(KEY_BUG_INSERT_URL);
						mPlaytimeUrl = urlArray.getString(KEY_PLAY_TIME_URL);
						m3GMessage = urlArray.getString(KEY_3G_MESSAGE);
						m3GMessageLength = urlArray
								.getInt(KEY_3G_MESSAGE_LENGTH);
//						mBookmarkListUrl = urlArray
//								.getString(KEY_GET_BOOKMARK_LIST);
//						mBookmarkInsertUrl = urlArray
//								.getString(KEY_BOOKMARK_INSERT_URL);
//						mTextBannerUrl = urlArray
//								.getString(KEY_GET_TEXT_BANNER_URL);
//						mADUrl = urlArray.getString(KEY_GET_AD_URL);
//						mBookmarkDeleteUrl = urlArray
//								.getString(KEY_BOOKMARK_DELETE_URL);
//						mLogReportUrl = urlArray.getString(KEY_LOG_URL);
						return RESULT_SUCCESS;
					} catch (JSONException e) {
						e.printStackTrace();
						return RESULT_ERROR + e.getMessage();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return RESULT_ERROR + e.getMessage();
			}
		}
		return RESULT_SUCCESS;
	}
}
