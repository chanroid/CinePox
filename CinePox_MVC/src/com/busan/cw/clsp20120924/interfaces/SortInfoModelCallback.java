package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.SortInfoModel;

public interface SortInfoModelCallback {
	public void onSortInfoLoadStart(SortInfoModel model);

	public void onSortInfoLoadError(SortInfoModel model, String message);

	public void onSortInfoLoadComplete(SortInfoModel model);
}
