package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.WidgetModel;

public interface WidgetModelCallback {
	public void onWidgetDataLoadStart(WidgetModel model);
	public void onWidgetDataLoadError(WidgetModel model, String message);
	public void onWidgetDataLoadComplete(WidgetModel model);
}
