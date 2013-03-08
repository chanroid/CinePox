package com.busan.cw.clsp20120924.view;

import view.CCView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.busan.cw.clsp20120924_beta.R;

@SuppressLint("SetJavaScriptEnabled")
public class ProgressWebView extends CCView {

	private WebView webView;
	private ProgressBar progress;

	// 아직 콜백은 필요없음

	public void setWebViewClient(WebViewClient client) {
		webView.setWebViewClient(client);
	}

	public void loadUrl(String url) {
		webView.loadUrl(url);
	}

	public boolean canGoBack() {
		return webView.canGoBack();
	}

	public boolean canGoForward() {
		return webView.canGoForward();
	}

	public void goBack() {
		webView.goBack();
	}

	public void goForward() {
		webView.goForward();
	}

	public void showLoading() {
		progress.setVisibility(VISIBLE);
	}

	public void hideLoading() {
		progress.setVisibility(GONE);
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		webView = (WebView) findViewById(R.id.webview_webview);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient());
		progress = (ProgressBar) findViewById(R.id.progress_webview);
	}

	public ProgressWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ProgressWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_webview;
	}

}
