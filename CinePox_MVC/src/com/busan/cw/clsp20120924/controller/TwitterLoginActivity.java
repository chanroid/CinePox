package com.busan.cw.clsp20120924.controller;

import utils.ThemeUtils;
import utils.LogUtils.l;

import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.view.ProgressWebView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TwitterLoginActivity extends Activity implements Constants {

	private ProgressWebView webView;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		ThemeUtils.darkNoTitle(this);
		super.onCreate(savedInstanceState);
		webView = new ProgressWebView(this);
		webView.setWebViewClient(twitterLoginClient);
		webView.loadUrl(getIntent().getStringExtra(EXTRA_URL));
		setContentView(webView);
	};

	private WebViewClient twitterLoginClient = new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			webView.showLoading();
			l.d("twitter Login page url : " + url);
			if (url.startsWith("http://m.cinepox.com")) {
				Intent i = new Intent();
				i.putExtra(EXTRA_URL, url);
				setResult(Activity.RESULT_OK, i);
				finish();
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			webView.hideLoading();
		};

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Intent i = new Intent();
			i.putExtra(EXTRA_MSG, description);
			setResult(Activity.RESULT_CANCELED, i);
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
