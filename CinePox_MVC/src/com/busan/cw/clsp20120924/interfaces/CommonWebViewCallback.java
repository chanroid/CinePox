package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.CommonWebView;

public interface CommonWebViewCallback {
	public void onPrevClick(CommonWebView view);
	public void onNextClick(CommonWebView view);
	public void onCloseClick(CommonWebView view);
	public void onRefreshClick(CommonWebView commonWebView);
}
