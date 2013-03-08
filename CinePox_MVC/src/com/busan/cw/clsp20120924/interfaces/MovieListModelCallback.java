package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.MovieListModel;

public interface MovieListModelCallback {
	public void onMovieListLoadStart(MovieListModel model);
	public void onMovieListLoadError(MovieListModel model, String message);
	public void onMovieListLoadComplete(MovieListModel model);
}
