package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.AppConfigData;
import com.busan.cw.clsp20120924.model.PlayerConfigData;
import com.busan.cw.clsp20120924.model.UserConfigData;

public interface AppModelCallback {
	public void onAppConfigChanged(AppConfigData config);
	public void onPlayerConfigChanged(PlayerConfigData config);
	public void onUserConfigChanged(UserConfigData config);
}
