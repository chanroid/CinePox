package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.PointModel;

public interface PointModelCallback {
	public void onPointLoadStart(PointModel model);
	public void onPointLoadComplete(PointModel model);
	public void onPointLoadError(PointModel model, String message);
}
