package com.busan.cw.clsp20120924.model;

import java.util.ArrayList;

public class AppConfigData {

	// 앱 환경설정 변수
	protected String recentVerName; // 표시용 버전명
	protected int recentVerCode; // 내부처리용 버전코드
	protected boolean isUpdate;
	protected String updateUrl;
	protected boolean debugMode;
	protected ArrayList<String> debugIds = new ArrayList<String>();

	// API url
	protected String domain;
	protected String debugDomain;
	protected String accessLogUrl;
	protected String widgetDataUrl;
	protected String errorLogUrl;
	protected String loginUrl;
	protected String loginSNSUrl;
	protected String periodInfoUrl;
	protected String pointInfoUrl;
	protected String movieListUrl;
	protected String categoryListUrl;
	protected String countryListUrl;
	protected String sortListUrl;
	protected String movieDetailUrl;
	protected String pointsortListUrl;

	// 광고 관련정보
	protected String adImageUrl;
	protected String adImageTargetUrl;
	protected int adHideDay;

	// 모바일웹 공용 url
	protected String mWebDomain;
	protected String mWebDebugDomain;
	protected String findIdUrl;
	protected String findPwUrl;

	// 웹뷰 전용 url
	protected String webViewDomain;
	protected String webViewDebugDomain;
	protected String snsJoinUrl;
	protected String joinUrl;
	protected String buyTicketUrl;
	protected String buyPointUrl;
	protected String memberoutUrl;

	public String getRecentVerName() {
		return recentVerName;
	}

	public int getRecentVerCode() {
		return recentVerCode;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public ArrayList<String> getDebugIds() {
		return debugIds;
	}

	public String getAccessLogUrl() {
		return accessLogUrl;
	}

	public String getWidgetDataUrl() {
		return widgetDataUrl;
	}

	public String getErrorLogUrl() {
		return errorLogUrl;
	}

	public String getAdImageUrl() {
		return adImageUrl;
	}

	public String getAdImageTargetUrl() {
		return adImageTargetUrl;
	}

	public int getAdHideDay() {
		return adHideDay;
	}

	public String getDomain() {
		return domain;
	}

	public String getDebugDomain() {
		return debugDomain;
	}

	public String getmWebDomain() {
		return mWebDomain;
	}

	public String getmWebDebugDomain() {
		return mWebDebugDomain;
	}

	public String getWebViewDomain() {
		return webViewDomain;
	}

	public String getWebViewDebugDomain() {
		return webViewDebugDomain;
	}

	public String getFindIdUrl() {
		return findIdUrl;
	}

	public String getFindPwUrl() {
		return findPwUrl;
	}

	public String getBuyTicketUrl() {
		return buyTicketUrl;
	}

	public String getBuyPointUrl() {
		return buyPointUrl;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public String getLoginSNSUrl() {
		return loginSNSUrl;
	}

	public String getSnsJoinUrl() {
		return snsJoinUrl;
	}

	public String getJoinUrl() {
		return joinUrl;
	}

	public String getMemberoutUrl() {
		return memberoutUrl;
	}

	public String getPeriodInfoUrl() {
		return periodInfoUrl;
	}

	public String getPointInfoUrl() {
		return pointInfoUrl;
	}

	public String getMovieListUrl() {
		return movieListUrl;
	}

	public String getCategoryListUrl() {
		return categoryListUrl;
	}

	public String getCountryListUrl() {
		return countryListUrl;
	}

	public String getSortListUrl() {
		return sortListUrl;
	}

	public String getMovieDetailUrl() {
		// TODO Auto-generated method stub
		return movieDetailUrl;
	}

	public String getPointsortListUrl() {
		return pointsortListUrl;
	}

}
