package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.MovieListView;

public interface MovieListViewCallback {
	public void onSortItemClick(MovieListView view, String tag);
	
	public void onPageClick(MovieListView view, int page);

	public void onMovieListItemClick(MovieListView view, int index);

	public void onPrevPageClick(MovieListView view);

	public void onNextPageClick(MovieListView view);

	public void onPrevPageGroupClick(MovieListView view);

	public void onNextPageGroupClick(MovieListView view);
}
