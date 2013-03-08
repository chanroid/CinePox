package com.busan.cw.clsp20120924.model;

public class MovieListRequestData {

	public static final String EXTRA_LIST_COUNT = "sc_list_count";
	public static final String EXTRA_PAGE_COUNT = "page_count";
	public static final String EXTRA_PAGE = "page";
	public static final String EXTRA_SEARCH_TYPE = "sc_type";
	public static final String EXTRA_SEARCH_ORDER = "sc_order";
	public static final String EXTRA_SEARCH_IS_MONTH = "sc_is_month";
	public static final String EXTRA_DISPLAY_CLASS = "sc_max_disp_class";
	public static final String EXTRA_SEARCH_QUERY = "sc_query";
	public static final String EXTRA_SEARCH_CATEGORY = "sc_category";
	public static final String EXTRA_SEARCH_COUNTRY = "sc_country";
	public static final String EXTRA_SEARCH_PRICE = "sc_set_price";

	public int sc_list_count = 20; // 한 페이지당 출력할 갯수
	public int page_count = 5; // 한번에 출력할 페이지 갯수
	public int page = 1; // 이동할 페이지 번호

	public String sc_type = "movie"; // 영상 메뉴 분류(ex: 영화, tv, 애니)

	public boolean sc_is_month = false; // 월정액영화만 요청할지의 여부

	// 특정 관람가의 영화만 요청
	// int sc_disp_class;
	public int sc_max_disp_class = 1; // 특정 관람가 이하의 영화만 요청

	public boolean sc_display; // 판매가능한 영화만 요청

	public String search_query = ""; // 검색어
	public String sc_order = ""; // 정렬기준
	public String sc_category = ""; // 검색할 카테고리
	public String sc_country = ""; // 검색할 국가

	public int sc_set_price = 0; // 특정 가격 이하의 영화만 요청

	// 제외할 메뉴
	// 클라이언트 상에서 처리
	// String sc_not_type;

	// 서비스 형식(스트리밍, 다운로드)
	// String sc_svs_type;

	// 요청하는 플랫폼에 따라 변경
	// 2013-02-26 그냥 서버에서 처리하기로 했음
	// boolean is_web_streaming;
	// boolean is_mobile_streaming;
	// boolean is_wifi_streaming;
	// boolean is_3g_streaming;
	// boolean is_foreign_streaming;
	// boolean is_platform_web_streaming;
	// boolean is_platform_mobile_streaming;
	// boolean is_platform_web_download;
	// boolean is_platform_mobile_download;

	// 영화관 동시 개봉
	// boolean is_cinema;
	// hd
	// boolean is_hd;
	// tv방영중
	// boolean is_tvlive;
	// 온라인 최초 개봉
	// boolean is_online;
	// 신작
	// boolean is_new;

	// 검색 타입
	// String sc_query_type;

	// 네티즌 평점이 기준 이상인 영화만 요청
	// float sc_ntz_count;

	/** 별도의 build과정이 필요한 변수들 **/
	// 검색할 장르
	// String sc_genre;
	// 제외할 국가
	// String sc_not_country;
	// 제외할 카테고리
	// 클라이언트 상에서 해당 카테고리 숨김처리
	// String sc_not_category;
	// 배급사 정보(?)
	// String sc_distributor;

}
