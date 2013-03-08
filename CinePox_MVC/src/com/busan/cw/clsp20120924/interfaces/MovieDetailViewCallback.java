package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.MovieDetailView;

public interface MovieDetailViewCallback extends SeriseListViewCallback,
		MovieSynopsisViewCallback, MovieSNSReplyViewCallback {
	public static final String SEGMENT_TAG_SYNOPSIS = "moviedetail_segment_synopsis";
	public static final String SEGMENT_TAG_STILLCUT = "moviedetail_segment_stillcut";
	public static final String SEGMENT_TAG_SNSREPLY = "moviedetail_segment_snsreply";
	
	public void onPreviewClick(MovieDetailView view);

	public void onRatingClick(MovieDetailView view);

	public void onShareClick(MovieDetailView view);

	public void onStreamingClick(MovieDetailView view);

	public void onDownloadClick(MovieDetailView view);

	public void onZzimClick(MovieDetailView view);

	public void onResolutionDetailClick(MovieDetailView view);

	public void onFileinfoDetailClick(MovieDetailView view);

	public void onRelativeMovieItemClick(MovieDetailView view, int position);

	public void onHelpClick(MovieDetailView view);
	
	public void onStillcutItemClick(MovieDetailView view, int position);
	
	// 영화정보 Segment는 하위뷰 Visiblity에 관한 변경만 하고 콜백 하지 않음
}
