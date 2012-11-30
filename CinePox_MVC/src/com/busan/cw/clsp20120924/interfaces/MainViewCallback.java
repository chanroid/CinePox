package com.busan.cw.clsp20120924.interfaces;

import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import com.busan.cw.clsp20120924.structs.CategoryItemData;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public interface MainViewCallback extends OnClickListener, OnRefreshListener,
		OnItemClickListener {
	public void onCategoryChanged(CategoryItemData name);
}
