package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.HomePageFragmentView;

public interface HomePageFragmentViewCallback extends SNSReviewViewCallback {
	public static final String SEGMENT_TAG_SNSREVIEW = "home_segment_snsreview";
	public static final String SEGMENT_TAG_EVENT = "home_semgent_event";
	public static final String SEGMENT_TAG_RECOMMEND = "home_segment_related";

	public void onGallery1ItemClick(HomePageFragmentView view, int position);

	public void onGallery2ItemClick(HomePageFragmentView view, int position);

	public void onEventItemClick(HomePageFragmentView view, int position);

	public void onRelatedItemClick(HomePageFragmentView view, int position);
}
