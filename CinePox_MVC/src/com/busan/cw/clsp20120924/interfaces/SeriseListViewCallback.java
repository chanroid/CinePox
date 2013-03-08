package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.SeriseListView;

public interface SeriseListViewCallback {
	public void onShowAllClick(SeriseListView view);

	public void onOrderClick(SeriseListView view);

	/** (주의: 페이지는 1부터 시작) **/
	public void onSerisePageChanged(SeriseListView view, int position);

	/** (주의: 페이지는 1부터 시작) **/
	public void onSeriseItemClick(SeriseListView view, int page, int position);

	public void onNextClick(SeriseListView view);

	public void onPrevClick(SeriseListView view);
}
