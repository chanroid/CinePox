package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.MovieDetailModel;

public interface MovieDetailModelCallback {
	public void onMovieDetailLoadStart(MovieDetailModel model);

	public void onMovieDetailLoadError(MovieDetailModel model, String messgae);

	public void onMovieDetailLoadComplete(MovieDetailModel model);
}
