package com.busan.cw.clsp20120924.model;

public class UserConfigData {

	// 로그인 계정 정보 변수
	protected int adult;
	protected boolean admin; // 관리자
	protected boolean testId; // 테스트용 아이디
	protected int memberSeq;
	protected String accountType; // 계정 타입
	protected String accountName; // 아이디
	protected int siteSeq;
	protected String userName; // 사용자 이름
	protected int groupSeq; // 사용자 그룹 구분 번호
	protected String groupName; // 사용자 그룹 구분 문자열
	protected int levelSeq; // 사용자 등급 구분 번호
	protected String levelName; // 사용자 등급 구분 문자열
	protected int activityQuantity; // 사용자 활동지수
	protected boolean snsLogin;
	protected String profileImageUrl;
	protected String nickName;

	// 안심아이디 관련 변수
	protected boolean safeId;
	protected String[] notseeMenus; // 표시안할 메뉴
	protected String[] notseeCategorys; // 표시안할 카테고리
	protected int viewClass; // 연령대

	// 포인트 관련 변수
	protected int point;
	protected int bonus;

	// 월정액 관련 변수
	protected boolean premium;
	protected int ticketEndDate;
	protected String ticketEndDateView;
	protected boolean ticketIsAuto;

	public SNSLoginData loginData = new SNSLoginData();

	public boolean isSafeMode() {
		return safeId;
	}

	public String[] getNotSeeMenus() {
		return notseeMenus;
	}

	public String[] getNotSeeCategorys() {
		return notseeCategorys;
	}

	public int getViewClass() {
		return viewClass;
	}

	/**
	 * @return -1 : 인증 필요, 0 : 미성년자, 1 : 성인
	 * 
	 */
	public int isAdult() {
		return adult;
	}
	
	public boolean isShowAdult() {
		return adult == 1;
	}

	public boolean isAdmin() {
		return admin;
	}

	public boolean isTestId() {
		return testId;
	}

	public int getMemberSeq() {
		return memberSeq;
	}

	public String getAccountType() {
		return accountType;
	}

	public String getAccountName() {
		return accountName;
	}

	public int getSiteSeq() {
		return siteSeq;
	}

	public String getUserName() {
		return userName;
	}

	public int getGroupSeq() {
		return groupSeq;
	}

	public String getGroupName() {
		return groupName;
	}

	public int getLevelSeq() {
		return levelSeq;
	}

	public String getLevelName() {
		return levelName;
	}

	public int getActivityQuantity() {
		return activityQuantity;
	}

	public boolean isSnsLogin() {
		return snsLogin;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public String getNickName() {
		if ("".equals(nickName))
			return getUserName();
		else
			return nickName;
	}

	public int getPoint() {
		return point;
	}

	public int getBonus() {
		return bonus;
	}

	public int getTicketEndDate() {
		return ticketEndDate;
	}

	public String getTicketEndDateView() {
		return ticketEndDateView;
	}

	public boolean isTicketIsAuto() {
		return ticketIsAuto;
	}

	public boolean isPremium() {
		return premium;
	}

}
