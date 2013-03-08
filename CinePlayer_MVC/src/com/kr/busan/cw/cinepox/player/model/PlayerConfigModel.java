package com.kr.busan.cw.cinepox.player.model;

import java.io.IOException;
import java.util.ArrayList;

import model.CCSetting;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.FileUtils;
import utils.JSONUtils;
import utils.LogUtils;
import utils.LogUtils.l;
import utils.ManifestUtils;
import utils.StreamUtils;
import utils.TimeUtils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.widget.Toast;

import com.kr.busan.cw.cinepox.player.base.Domain;
import com.kr.busan.cw.cinepox.player.structs.QualityData;

public class PlayerConfigModel extends Model {

	private static PlayerConfigModel instance;

	public static PlayerConfigModel getInstance(Context ctx) {
		if (instance == null)
			instance = new PlayerConfigModel(ctx);
		return instance;
	}

	public static void clearInstance() {
		instance = null;
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
	private ADModel mADModel;
	private BookmarkModel mBookmarkModel;

	private String m3GMessage = "WI-FI 환경에서 감상하시는 것을 권장합니다.";
	private int m3GMessageLength = Toast.LENGTH_LONG;

	private String mPlaytimeUrl;
	private String mBugReportUrl;

	// private String mLogReportUrl;

	private PlayerConfigModel(Context ctx) {
		mContext = ctx;
		mCaptionModel = CaptionModel.getInstance(ctx);
		mVideoModel = VideoModel.getInstance(ctx);
		mADModel = ADModel.getInstance(ctx);
		mBookmarkModel = BookmarkModel.getInstance(ctx);
		mPref = ctx.getSharedPreferences(KEY_SETTING, 0);
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
		mEdit.putString(KEY_QUALITY, ctype).commit();
	}

	public String getDefaultQuality() {
		return mPref.getString(KEY_QUALITY, "C600");
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

	public void setRestartPostion(String url, long pos) {
		if (url == null)
			return;
		mEdit.putLong(url, pos).commit();
	}

	public long getRestartPosition(String url) {
		if (url == null)
			return 0l;
		return mPref.getLong(url, 0l);
	}
	
	public void setURI(Uri url) {
		mSetURI = url;
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
		if (uri == null)
			return false;
		return "cinepox".equalsIgnoreCase(uri.getScheme());
	}

	public boolean isCinepoxURI() {
		return isCinepoxURI(mCinepoxURI);
	}

	public boolean isContentURI(Uri uri) {
		if (uri == null)
			return false;
		return "content".equalsIgnoreCase(uri.getScheme());
	}

	public synchronized void sendErrorLog(Intent intent)
			throws IllegalStateException, IOException {
		// 서버작업 후 작성할것
		if (intent == null)
			return;
		StringBuffer sb = new StringBuffer();
		sb.append("APP Ver : " + ManifestUtils.getVersionName(mContext) + "\n");
		sb.append("Time : " + TimeUtils.getCurrentDateTimeString() + "\n");
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
			sb.append(LogUtils.getStackTrace(exception));
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
		StreamUtils.inStreamFromURL(mBugReportUrl, params);

	}

	public synchronized void sendPlayTime(long starttime, long playtime)
			throws IllegalStateException, IOException {
		long play_time = System.currentTimeMillis() - starttime;
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("soon_time", (playtime / 1000) + ""));
		params.add(new BasicNameValuePair(KEY_PLAY_TIME, (play_time / 1000)
				+ ""));
		params.add(new BasicNameValuePair(KEY_ORDER_CODE, mOrderCode));
		StreamUtils.inStreamFromURL(mPlaytimeUrl, params);
	}

	public synchronized void sendPlayTime(Intent intent)
			throws IllegalStateException, IOException {
		if (intent == null)
			return;
		long starttime = intent.getLongExtra(KEY_START_TIME, 0l);
		long playtime = intent.getLongExtra(KEY_PLAY_TIME, 0l);
		sendPlayTime(starttime, playtime);
	}

	public synchronized JSONObject loadUpdate() {
		String url = Domain.ACCESS_DOMAIN + "player/mobUpdateCheck";
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair(KEY_SETTING, RESPONSE_JSON));
		param.add(new BasicNameValuePair(KEY_DEVICE_TYPE, DEVICE));
		try {
			return JSONUtils.jsonFromURL(url, param);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized String loadConfig(Intent params) {
		if (params == null)
			return null;
		Uri path = params.getData();

		if (path == null)
			return RESULT_ERROR + "invaild url";

		if (!isCinepoxURI(path)) {
			if (isContentURI(path)) {
				// 에러나는 부분
				if (mContext == null || path == null)
					return RESULT_ERROR;
				String filePath = FileUtils.getMediaPathfromUri(mContext, path);
				if (!mCaptionModel.loadCaption(FileUtils.changeExtension(
						filePath, CAPTION_EXT_SMI)))
					mCaptionModel.loadCaption(FileUtils.changeExtension(
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
				String domain = path.getQueryParameter("domain");
				if (domain != null && CCSetting.isTestMode) {
					Domain.ACCESS_DOMAIN = String.format("http://%s/", domain);
					l.i("ACCESS_DOMAIN : " + Domain.ACCESS_DOMAIN);
				}

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
					String config_url = Domain.ACCESS_DOMAIN
							+ "player/getContents/";
					ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
					param.add(new BasicNameValuePair(KEY_MOVIE_NUM, movie_num));
					param.add(new BasicNameValuePair(KEY_MEMBER_NUM, member_seq));
					param.add(new BasicNameValuePair(KEY_SETTING, RESPONSE_JSON));
					param.add(new BasicNameValuePair(KEY_PROTOCOL_TYPE,
							Build.VERSION.SDK_INT > 8 ? "http" : "rtsp"));
					param.add(new BasicNameValuePair(KEY_DEVICE_TYPE, DEVICE));
					param.add(new BasicNameValuePair(KEY_ORDER_CODE, mOrderCode));
					JSONObject config = JSONUtils
							.jsonFromURL(config_url, param);
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
						// mLogReportUrl = urlArray.getString(KEY_LOG_URL);
						mBookmarkModel.setDeleteUrl(urlArray
								.getString(KEY_BOOKMARK_DELETE_URL));
						mBookmarkModel.setListUrl(urlArray
								.getString(KEY_GET_BOOKMARK_LIST));
						mBookmarkModel.setInsertUrl(urlArray
								.getString(KEY_BOOKMARK_INSERT_URL));
						mADModel.setADBannerUrl(urlArray
								.getString(KEY_GET_AD_URL));
						mADModel.setTextBannerUrl(urlArray
								.getString(KEY_GET_TEXT_BANNER_URL));
						mADModel.loadAdInfo();
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
