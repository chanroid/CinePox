package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.ConfigModel;

public interface ConfigModelCallback {
	public void onLoadConfigStart(ConfigModel model);

	public void onLoadConfigError(ConfigModel model, String error);

	public void onLoadConfigComplete(ConfigModel model);
}
