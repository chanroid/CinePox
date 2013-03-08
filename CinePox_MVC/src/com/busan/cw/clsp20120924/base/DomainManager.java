package com.busan.cw.clsp20120924.base;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.busan.cw.clsp20120924.model.CinepoxAppModel;

public class DomainManager {
	private static CinepoxAppModel app(Context ctx) {
		return (CinepoxAppModel) ctx.getApplicationContext();
	}
	
	public static void signHeader(HttpURLConnection conn) {
		conn.addRequestProperty("site", "cinepoxApp");
	}
	
	public static Map<String, String> getWebViewHttpHeader() {
		Map<String, String> additionalHttpHeaders = new HashMap<String, String>();
		additionalHttpHeaders.put("site", "cinepoxApp");
		return additionalHttpHeaders;
	}

	public static String getAppDomain(Context ctx) {
		if (app(ctx).isDebugMode())
			return app(ctx).getAppConfig().getDebugDomain();
		else
			return app(ctx).getAppConfig().getDomain();
	}

	public static String getWebDomain(Context ctx) {
		if (app(ctx).isDebugMode())
			return app(ctx).getAppConfig().getmWebDebugDomain();
		else
			return app(ctx).getAppConfig().getmWebDomain();
	}

	public static String getWebViewDomain(Context ctx) {
		if (app(ctx).isDebugMode())
			return app(ctx).getAppConfig().getWebViewDebugDomain();
		else
			return app(ctx).getAppConfig().getWebViewDomain();
	}
	
	public static String getPlayerDomain(Context ctx) {
		if (app(ctx).isDebugMode())
			return app(ctx).getPlayerConfig().getPlayerDebugDomain();
		else
			return app(ctx).getPlayerConfig().getPlayerDomain();
	}
}
