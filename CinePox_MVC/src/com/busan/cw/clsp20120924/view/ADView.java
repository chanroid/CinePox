package com.busan.cw.clsp20120924.view;

import view.CCView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.interfaces.ADViewCallback;

@SuppressLint("SetJavaScriptEnabled")
public class ADView extends CCView {

	private WebView mWebAd;
	private ImageView mImgAd;
	private CheckBox mCheck;
	private ImageButton mCloseBtn;
	
	public ADView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.ad_webview;
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		mWebAd = (WebView) findViewById(R.id.ad_webview);
		mWebAd.getSettings().setJavaScriptEnabled(true);
		mWebAd.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mImgAd = (ImageView) findViewById(R.id.ad_imageview);
		mCheck = (CheckBox) findViewById(R.id.check_ad_noshow);
		mCloseBtn = (ImageButton) findViewById(R.id.btn_ad_close);
	}
	
	public void setADImage(Bitmap image) {
		mImgAd.setVisibility(View.VISIBLE);
		mImgAd.setImageBitmap(image);
	}
	
	public void setADUrl(String url) {
		mWebAd.setVisibility(View.VISIBLE);
		mWebAd.loadUrl(url);
	}
	
	public void setCallback(ADViewCallback cb) {
		mCheck.setOnCheckedChangeListener(cb);
		mCloseBtn.setOnClickListener(cb);
		mWebAd.setWebChromeClient(cb.setWebClient());
	}

}
