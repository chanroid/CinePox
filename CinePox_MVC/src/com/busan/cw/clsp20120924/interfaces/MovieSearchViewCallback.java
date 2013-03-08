package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.MovieSearchView;

public interface MovieSearchViewCallback {
	public void onSearchQueryChanged(MovieSearchView view, CharSequence query);

	public void onSearchClick(MovieSearchView view);

	public void onVoiceClick(MovieSearchView view);

	public void onSearchResultItemClick(MovieSearchView view, int position);
	
	public void onVoiceResultItemClick(MovieSearchView view, String selectedItem);
}
