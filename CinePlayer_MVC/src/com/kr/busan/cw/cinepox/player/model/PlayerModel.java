package com.kr.busan.cw.cinepox.player.model;


/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : PlayerModel.java
 * 3. Package  : com.kr.busan.cw.cinepox.player.model
 * 4. Comment  :
 * 		기본 형식의 모델.
 * 		모든 모델에서 공통으로 사용되는 기능 및 상수 정의 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 15. 오후 1:51:43
 * </PRE>
 */
public class PlayerModel {

	/**
	 * <PRE>
	 * 1. ClassName : PlayerModel
	 * 2. FileName  : PlayerModel.java
	 * 3. Package  : com.kr.busan.cw.cinepox.player.model
	 * 4. Comment  : 
	 * 		모델에서 사용되는 상수 집합 클래스
	 * 5. 작성자   : 박찬우
	 * 6. 작성일   : 2012. 10. 15. 오후 1:46:49
	 * </PRE>
	 */
	public static final class Const {
		public static final String ACCESS_DOMAIN = "http://access.cinepox.com/";

		public static final String ACTION_QRPLAY = "com.kr.cinepox.player.QRPLAY";
		public static final String ACTION_SEND_TIME = "com.kr.busan.cw.cinepox.service.sendshowtime";

		public static final String CAPTION_EXT_SMI = "smi";
		public static final String CAPTION_EXT_SRT = "srt";

		public static final String DEVICE = "android";

		public static final String KEY_3G_MESSAGE = "3g_message";
		public static final String KEY_3G_MESSAGE_LENGTH = "3g_message_length";
		public static final String KEY_AD_NUM = "ad_seq";
		public static final String KEY_BOOKMARK_DATE = "regdate";
		public static final String KEY_BOOKMARK_DELETE_URL = "bookmark_delete_url";
		public static final String KEY_BOOKMARK_INSERT_URL = "bookmark_insert_url";
		public static final String KEY_BOOKMARK_NUM = "bookmark_seq";
		public static final String KEY_BOOKMARK_PUBLIC = "is_public";
		public static final String KEY_BOOKMARK_TIME = "bookmark_time";
		public static final String KEY_BRIGHT = "bright";
		public static final String KEY_BUG_INSERT_URL = "bug_insert_url";
		public static final String KEY_BUG_MEMO = "memo";
		public static final String KEY_BUG_STATE_INFO = "state_info";
		public static final String KEY_BUG_TITLE = "bug_title";
		public static final String KEY_CLICK_CHECK_URL = "click_url";
		public static final String KEY_CODEC = "codec";
		public static final String KEY_CONFIG = "config";
		public static final String KEY_CUSTOM_URL = "cinepox_url";
		public static final String KEY_DATA = "data";
		public static final String KEY_DEVICE_TYPE = "device_type";
		public static final String KEY_ERROR_COUNT = "error_count";
		public static final String KEY_EXCEPTION = "exceptions";
		public static final String KEY_GET_AD_URL = "get_ad_url";
		public static final String KEY_GET_BOOKMARK_LIST = "get_bookmark_list";
		public static final String KEY_GET_TEXT_BANNER_URL = "get_text_banner_url";
		public static final String KEY_INTERVAL = "interval_time";
		public static final String KEY_IS_RESPONSE = "is_response";
		public static final String KEY_KEY = "key";
		public static final String KEY_LANG = "lang";
		public static final String KEY_LIST = "list";
		public static final String KEY_LOG_MSG = "msg";
		public static final String KEY_LOG_URL = "log_url";
		public static final String KEY_MEMBER_NUM = "member_seq";
		public static final String KEY_MEMO = "memo";
		public static final String KEY_MOVIE = "movie";
		public static final String KEY_MOVIE_LIST = "movie_list";
		public static final String KEY_MOVIE_NUM = "productInfo_seq";
		public static final String KEY_MOVIE_URL = "movie_url";
		public static final String KEY_MSG = "msg";
		public static final String KEY_NAME = "name";
		public static final String KEY_NEXT_TIME = "next_time";
		public static final String KEY_NUM = "num";
		public static final String KEY_ORDER_CODE = "order_code";
		public static final String KEY_PLAY_TIME = "play_time";
		public static final String KEY_PLAY_TIME_URL = "play_time_url";
		public static final String KEY_PLAYER_ERRORCNT = "error_count";
		public static final String KEY_PROTOCOL_TYPE = "p_type";
		public static final String KEY_QUALITY = "quality";
		public static final String KEY_RESULT = "result";
		public static final String KEY_SET_TIME = "set_time";
		public static final String KEY_SET_URL = "set_url";
		public static final String KEY_SETTING = "setting";
		public static final String KEY_START_TIME = "start_time";
		public static final String KEY_SHAKE_KEY = "shake_key";
		public static final String KEY_SHOW_TIME = "show_time";
		public static final String KEY_START_VIEW = "is_start_view";
		public static final String KEY_TEXT_AD_NUM = "text_banner_seq";
		public static final String KEY_THUMBIMG = "sn_image";
		public static final String KEY_TITLE = "title";
		public static final String KEY_TYPE = "type";
		public static final String KEY_URL = "url";
		public static final String KEY_VERSION = "ver";
		public static final String KEY_VIEW_CHECK_URL = "view_check_url";
		public static final String KEY_VIEW_TIME = "view_time";

		public static final String RESPONSE_JSON = "response_type:json";

		public static final String RESULT_ERROR = "error : ";
		public static final String RESULT_MOVIE = "movie";
		public static final String RESULT_SUCCESS = "success";

		public static final String SCAN_MODE = "SCAN_MODE";
		public static final String SCAN_MODE_QRCODE = "QR_CODE_MODE";
		public static final String SCAN_RESULT = "SCAN_RESULT";

		public static final String SHAKE_DELETE_URL = ACCESS_DOMAIN
				+ "player/shakeDelete";
		public static final String SHAKE_REQUEST_URL = ACCESS_DOMAIN
				+ "player/shakeRequest";
		public static final String SHAKE_RESPONSE_URL = ACCESS_DOMAIN
				+ "player/shakeResponse";
		public static final String SHAKE_GET_KEY_URL = ACCESS_DOMAIN
				+ "player/shakeGetKey";

		public static final String URI_HOST_PLAY = "play";
		public static final String URI_SCHEME = "cinepox";
		public static final String URI_SCHEME_CONTENT = "content";
	}

}
