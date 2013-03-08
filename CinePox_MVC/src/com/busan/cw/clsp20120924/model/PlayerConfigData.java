package com.busan.cw.clsp20120924.model;

public class PlayerConfigData {
	
	// 플레이어 환경설정 변수
	protected String playerDomain;
	protected String playerDebugDomain;

	protected String loadTextBannerUrl;
	protected String loadADBannerUrl;
	protected String loadBookmarkListUrl;
	protected String insertBookmarkUrl;
	protected String deleteBookmarkUrl;
	protected String bugReportUrl;
	protected String sendPlayerLogUrl;
	protected String sendPlayTimeUrl;
	protected String gsmMessage;
	protected int gsmMessageLength;

	public String getPlayerDomain() {
		return playerDomain;
	}

	public String getPlayerDebugDomain() {
		return playerDebugDomain;
	}

	public String getLoadTextBannerUrl() {
		return loadTextBannerUrl;
	}

	public String getLoadADBannerUrl() {
		return loadADBannerUrl;
	}

	public String getLoadBookmarkListUrl() {
		return loadBookmarkListUrl;
	}

	public String getInsertBookmarkUrl() {
		return insertBookmarkUrl;
	}

	public String getDeleteBookmarkUrl() {
		return deleteBookmarkUrl;
	}

	public String getBugReportUrl() {
		return bugReportUrl;
	}

	public String getSendPlayerLogUrl() {
		return sendPlayerLogUrl;
	}

	public String getSendPlayTimeUrl() {
		return sendPlayTimeUrl;
	}

	public String getGsmMessage() {
		return gsmMessage;
	}

	public int getGsmMessageLength() {
		return gsmMessageLength;
	}
}
