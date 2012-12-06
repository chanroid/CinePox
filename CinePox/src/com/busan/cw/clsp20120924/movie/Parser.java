package com.busan.cw.clsp20120924.movie;

import java.io.IOException;
import java.util.ArrayList;

import kr.co.chan.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class Parser {

	public static final int REQ_GET_CONTENTS = 0;
	public static final int REQ_AD_VIEW_CHECK = 1;
	public static final int REQ_GET_AD_LIST = 2;
	public static final int REQ_AD_CLICK_CHECK = 3;
	public static final int REQ_SEND_LOG = 4;
	public static final int REQ_REPORT_BUG = 5;
	public static final int REQ_SEND_BOOKMARK = 6;
	public static final int REQ_DELETE_BOOKMARK = 7;
	public static final int REQ_GET_BOOKMARK = 8;
	public static final int REQ_TEXT_AD_VIEW_CHECK = 9;
	public static final int REQ_GET_TEXT_AD_LIST = 10;
	public static final int REQ_TEXT_AD_CLICK_CHECK = 11;

	public static final String URL_GET_CONTENTS = Domain.ACCESS_DOMAIN
			+ "player/getContents/";

	public static final String KEY_MOVIE_NUM = "productInfo_seq";
	public static final String KEY_MEMBER_NUM = "member_seq";
	public static final String KEY_AD_NUM = "ad_seq";
	public static final String KEY_NUM = "num";
	public static final String KEY_LOG_MSG = "msg";
	public static final String KEY_DATA = "data";
	public static final String KEY_BUG_STATE_INFO = "state_info";
	public static final String KEY_BUG_MEMO = "memo";
	public static final String KEY_BUG_TITLE = "bug_title";
	public static final String KEY_BOOKMARK_TIME = "bookmark_time";
	public static final String KEY_BOOKMARK_PUBLIC = "is_public";
	public static final String KEY_BOOKMARK_NUM = "bookmark_seq";
	public static final String KEY_TEXT_AD_NUM = "text_banner_seq";
	public static final String KEY_RESULT = "result";
	public static final String KEY_MSG = "msg";
	public static final String KEY_URL = "url";
	public static final String KEY_GET_TEXT_BANNER_URL = "get_text_banner_url";
	public static final String KEY_GET_AD_URL = "get_ad_url";
	public static final String KEY_GET_BOOKMARK_LIST = "get_bookmark_list";
	public static final String KEY_BOOKMARK_INSERT_URL = "bookmark_insert_url";
	public static final String KEY_BOOKMARK_DELETE_URL = "bookmark_delete_url";
	public static final String KEY_BUG_INSERT_URL = "bug_insert_url";
	public static final String KEY_LOG_URL = "log_url";
	public static final String KEY_MOVIE_LIST = "movie_list";
	public static final String KEY_NAME = "name";
	public static final String KEY_LANG = "lang";
	public static final String KEY_QUALITY = "quality";
	public static final String KEY_TYPE = "type";
	public static final String KEY_CONFIG = "config";
	public static final String KEY_START_VIEW = "is_start_view";
	public static final String KEY_NEXT_TIME = "next_time";
	public static final String KEY_VIEW_CHECK_URL = "view_check_url";
	public static final String KEY_CLICK_CHECK_URL = "click_url";
	public static final String KEY_LIST = "list";
	public static final String KEY_SHOW_TIME = "show_time";
	public static final String KEY_VIEW_TIME = "view_time";
	public static final String KEY_INTERVAL = "interval_time";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MEMO = "memo";
	public static final String KEY_THUMBIMG = "sn_image";
	public static final String KEY_BOOKMARK_DATE = "regdate";

	public static JSONObject parse(int code, String... params)
			throws ClientProtocolException, IOException, JSONException {
		String setting = "response_type:json";
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("setting", setting));
		param.add(new BasicNameValuePair("SET_DEVICE", "android(APP)"));
		String movieProduct_seq, member_seq, ad_seq, num;
		String type, msg, state_info, memo, bug_title;
		String bookmark_time, is_public, bookmark_seq;
		String text_banner_seq, posturl;
		// String bug_image;
		// String bookmark_image;
		switch (code) {
		case REQ_GET_CONTENTS:
			posturl = URL_GET_CONTENTS;
			movieProduct_seq = params[0];
			member_seq = params[1];
			param.add(new BasicNameValuePair(KEY_MOVIE_NUM, movieProduct_seq));
			param.add(new BasicNameValuePair(KEY_MEMBER_NUM, member_seq));
			break;
		case REQ_AD_VIEW_CHECK:
			posturl = params[0];
			movieProduct_seq = params[1];
			member_seq = params[2];
			ad_seq = params[3];
			param.add(new BasicNameValuePair(KEY_AD_NUM, ad_seq));
			param.add(new BasicNameValuePair(KEY_MOVIE_NUM, movieProduct_seq));
			param.add(new BasicNameValuePair(KEY_MEMBER_NUM, member_seq));
			break;
		case REQ_GET_AD_LIST:
			posturl = params[0];
			movieProduct_seq = params[1];
			num = params[2];
			param.add(new BasicNameValuePair(KEY_MOVIE_NUM, movieProduct_seq));
			param.add(new BasicNameValuePair(KEY_NUM, num));
			break;
		case REQ_AD_CLICK_CHECK:
			posturl = params[0];
			ad_seq = params[1];
			movieProduct_seq = params[2];
			member_seq = params[3];
			param.add(new BasicNameValuePair(KEY_AD_NUM, ad_seq));
			param.add(new BasicNameValuePair(KEY_MOVIE_NUM, movieProduct_seq));
			param.add(new BasicNameValuePair(KEY_MEMBER_NUM, member_seq));
			break;
		case REQ_SEND_LOG:
			posturl = params[0];
			type = params[1];
			msg = params[2];
			param.add(new BasicNameValuePair(KEY_TYPE, type));
			param.add(new BasicNameValuePair(KEY_LOG_MSG, msg));
			break;
		case REQ_REPORT_BUG:
			posturl = params[0];
			movieProduct_seq = params[1];
			member_seq = params[2];
			state_info = params[3];
			memo = params[4];
			bug_title = params[5];
			// bug_image = params[5];
			param.add(new BasicNameValuePair(KEY_MOVIE_NUM, movieProduct_seq));
			param.add(new BasicNameValuePair(KEY_MEMBER_NUM, member_seq));
			param.add(new BasicNameValuePair(KEY_BUG_STATE_INFO, state_info));
			param.add(new BasicNameValuePair(KEY_BUG_MEMO, memo));
			param.add(new BasicNameValuePair(KEY_BUG_TITLE, bug_title));
			break;
		case REQ_SEND_BOOKMARK:
			posturl = params[0];
			movieProduct_seq = params[1];
			member_seq = params[2];
			is_public = params[3];
			memo = params[4];
			bookmark_time = params[5];
			// bookmark_image = params[5];
			param.add(new BasicNameValuePair(KEY_MOVIE_NUM, movieProduct_seq));
			param.add(new BasicNameValuePair(KEY_MEMBER_NUM, member_seq));
			param.add(new BasicNameValuePair(KEY_BOOKMARK_PUBLIC, is_public));
			param.add(new BasicNameValuePair(KEY_BUG_MEMO, memo));
			param.add(new BasicNameValuePair(KEY_BOOKMARK_TIME, bookmark_time));
			break;
		case REQ_DELETE_BOOKMARK:
			posturl = params[0];
			bookmark_seq = params[1];
			param.add(new BasicNameValuePair(KEY_BOOKMARK_NUM, bookmark_seq));
			break;
		case REQ_GET_BOOKMARK:
			posturl = params[0];
			movieProduct_seq = params[1];
			member_seq = params[2];
			param.add(new BasicNameValuePair(KEY_MOVIE_NUM, movieProduct_seq));
			param.add(new BasicNameValuePair(KEY_MEMBER_NUM, member_seq));
			break;
		case REQ_TEXT_AD_VIEW_CHECK:
			posturl = params[0];
			text_banner_seq = params[1];
			movieProduct_seq = params[2];
			member_seq = params[3];
			param.add(new BasicNameValuePair(KEY_TEXT_AD_NUM, text_banner_seq));
			param.add(new BasicNameValuePair(KEY_MOVIE_NUM, movieProduct_seq));
			param.add(new BasicNameValuePair(KEY_MEMBER_NUM, member_seq));
			break;
		case REQ_GET_TEXT_AD_LIST:
			posturl = params[0];
			break;
		case REQ_TEXT_AD_CLICK_CHECK:
			posturl = params[0];
			text_banner_seq = params[1];
			movieProduct_seq = params[2];
			member_seq = params[3];
			param.add(new BasicNameValuePair(KEY_TEXT_AD_NUM, text_banner_seq));
			param.add(new BasicNameValuePair(KEY_MOVIE_NUM, movieProduct_seq));
			param.add(new BasicNameValuePair(KEY_MEMBER_NUM, member_seq));
			break;
		default:
			throw new IllegalArgumentException("not defined action");
		}
		return Util.Stream.jsonFromURLbyPOST(posturl, param);
	}

}
