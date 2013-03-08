package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.MainView;

public interface MainViewCallback {
	
	public static final String SEGMENT_TAG_HOME = "main_segment_home";
	public static final String SEGMENT_TAG_MOVIE = "main_segment_movie";
	public static final String SEGMENT_TAG_TV = "main_segment_tv";
	public static final String SEGMENT_TAG_ANI = "main_segment_ani";
	public static final String SEGMENT_TAG_PREMIUM = "main_segment_premium";
	public static final String SEGMENT_TAG_ADULT = "main_segment_adult";
	
	public void onSidemenuClick(MainView view);

	public void onSearchClick(MainView view);

	public void onHomeSegmentClick(MainView view);

	public void onMovieSegmentClick(MainView view);

	public void onTVSegmentClick(MainView view);

	public void onAniSegmentClick(MainView view);

	public void onPremiumSegmentClick(MainView view);

	public void onAdultSegmentClick(MainView view);

}
