package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.MovieSynopsisView;

public interface MovieSynopsisViewCallback {
	public void onSynopsisExpandClick(MovieSynopsisView view);
	public void onActorListExpandClick(MovieSynopsisView view);
	public void onActorListItemClick(MovieSynopsisView view, int index);
}
