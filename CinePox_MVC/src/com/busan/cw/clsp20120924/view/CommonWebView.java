package com.busan.cw.clsp20120924.view;

import utils.LogUtils.l;
import view.CCView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.busan.cw.clsp20120924.base.DomainManager;
import com.busan.cw.clsp20120924.interfaces.CommonWebViewCallback;
import com.busan.cw.clsp20120924_beta.R;

public class CommonWebView extends CCView implements OnClickListener {

	private ImageButton prevBtn;
	private ImageButton nextBtn;
	private ImageButton closeBtn;
	private ImageButton refreshBtn;
	private WebView webView;
	private ProgressBar progress;
	private ProgressDialog progressDialog;

	private CommonWebViewCallback callback;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		prevBtn = (ImageButton) findViewById(R.id.btn_webview_back);
		prevBtn.setOnClickListener(this);
		nextBtn = (ImageButton) findViewById(R.id.btn_webview_ff);
		nextBtn.setOnClickListener(this);
		refreshBtn = (ImageButton) findViewById(R.id.btn_webview_refresh);
		refreshBtn.setOnClickListener(this);
		closeBtn = (ImageButton) findViewById(R.id.btn_webview_close);
		closeBtn.setOnClickListener(this);
		webView = (WebView) findViewById(R.id.webview_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setSupportMultipleWindows(true);
		webView.setWebChromeClient(new CommonWebViewChromeClient());
		webView.setWebViewClient(new CommonWebViewClient());
		progress = (ProgressBar) findViewById(R.id.progress_webview);
		progressDialog = new ProgressDialog(getContext());
		progressDialog.setCancelable(false);
	}

	private class CommonWebViewChromeClient extends WebChromeClient {
		public boolean onJsAlert(android.webkit.WebView view, String url,
				String message, final android.webkit.JsResult result) {
			showJSAlert(message, result);
			return true;
		};

		public boolean onJsConfirm(android.webkit.WebView view, String url,
				String message, final android.webkit.JsResult result) {
			showJSConfirm(message, result);
			return true;
		};

		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			if (newProgress < 100)
				showLoading();
			else
				hideLoading();
			super.onProgressChanged(view, newProgress);
		}

		public boolean onCreateWindow(WebView view, boolean isDialog,
				boolean isUserGesture, android.os.Message resultMsg) {
			return super.onCreateWindow(view, false, true, resultMsg);
		};
	};

	private class CommonWebViewClient extends WebViewClient {
		public void onPageStarted(android.webkit.WebView v, String url,
				android.graphics.Bitmap favicon) {
			super.onPageStarted(v, url, favicon);
			l.d("webview url : " + url);
			showLoading();
		};

		public void onPageFinished(android.webkit.WebView v, String url) {
			super.onPageFinished(v, url);
			hideLoading();
			setGoBack(v.canGoBack());
			setGoForward(v.canGoForward());
		};

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url, DomainManager.getWebViewHttpHeader());
			return true;
		};

	};

	private void showJSConfirm(String message, final JsResult result) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
		dialog.setTitle(R.string.alert);
		dialog.setMessage(message);
		dialog.setPositiveButton(R.string.done,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						result.confirm();
					}
				});
		dialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						result.cancel();
					}
				});
		dialog.show();
	}

	private void showJSAlert(String message, final JsResult result) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
		dialog.setTitle(R.string.alert);
		dialog.setMessage(message);
		dialog.setPositiveButton(R.string.done,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						result.confirm();
					}
				});
		dialog.show();
	}

	public void setRefreshable(boolean flag) {
		refreshBtn.setVisibility(flag ? VISIBLE : GONE);
	}

	public void refresh() {
		webView.reload();
	}

	public void setCallback(CommonWebViewCallback callback) {
		this.callback = callback;
	}

	public void setGoBack(boolean flag) {
		prevBtn.setEnabled(flag);
	}

	public void setGoForward(boolean flag) {
		nextBtn.setEnabled(flag);
	}

	public void showLoading() {
		progress.setVisibility(VISIBLE);
	}

	public void hideLoading() {
		progress.setVisibility(GONE);
	}

	public void showLoadingDialog(String message) {
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	public void hideLoadingDialog() {
		progressDialog.dismiss();
	}

	public void loadUrl(String url) {
		webView.loadUrl(url, DomainManager.getWebViewHttpHeader());
	}

	public void loadData(String data, String mimetype, String encoding) {
		// TODO Auto-generated method stub
		webView.loadData(data, mimetype, encoding);
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

	public void setWebViewClient(WebViewClient client) {
		webView.setWebViewClient(client);
	}

	public void setWebChromeClient(WebChromeClient client) {
		webView.setWebChromeClient(client);
	}

	public void addJavaScriptInterface(Object iface, String className) {
		webView.addJavascriptInterface(iface, className);
	}

	public void removeJavaScriptInterface(String name) {
		webView.removeJavascriptInterface(name);
	}

	public CommonWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_join_webview;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(closeBtn))
			callback.onCloseClick(this);
		else if (v.equals(nextBtn))
			callback.onNextClick(this);
		else if (v.equals(prevBtn))
			callback.onPrevClick(this);
		else if (v.equals(refreshBtn))
			callback.onRefreshClick(this);
	}

}
