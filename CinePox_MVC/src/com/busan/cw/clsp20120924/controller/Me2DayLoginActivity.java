package com.busan.cw.clsp20120924.controller;

import utils.ThemeUtils;
import utils.LogUtils.l;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.model.Me2DayModel;
import com.busan.cw.clsp20120924.view.ProgressWebView;

public class Me2DayLoginActivity extends Activity implements Constants {

	private ProgressWebView webView;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		ThemeUtils.darkNoTitle(this);
		super.onCreate(savedInstanceState);
		webView = new ProgressWebView(this);
		webView.setWebViewClient(me2LoginClient);
		webView.loadUrl(getIntent().getStringExtra(EXTRA_URL));
		setContentView(webView);
	};

	private WebViewClient me2LoginClient = new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			if (url.startsWith(Me2DayModel.ME2_AUTH_SUBMIT_POPUP_URL_TRUE)) {
				setResult(Activity.RESULT_OK);
				finish();
			} else if (url
					.startsWith(Me2DayModel.ME2_AUTH_SUBMIT_POPUP_URL_FALSE)) {
				setResult(Activity.RESULT_CANCELED);
				finish();
			} else {
				view.loadUrl(url);
			}
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			l.d("me2Login page url : " + url);
			webView.showLoading();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			webView.hideLoading();
			if (url.startsWith(Me2DayModel.ME2_AUTH_SUBMIT_POPUP_URL_TRUE)) {
				setResult(Activity.RESULT_OK);
				finish();
			} else if (url
					.startsWith(Me2DayModel.ME2_AUTH_SUBMIT_POPUP_URL_FALSE)) {
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
		};

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			setResult(Activity.RESULT_CANCELED);
			finish();
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			super.onReceivedSslError(view, handler, error);
			setResult(Activity.RESULT_CANCELED);
			finish();
		};
	};
}
