package com.busan.cw.clsp20120924.movie;

import java.io.IOException;

import kr.co.chan.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.busan.cw.clsp20120924.R;

public class ADActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	static final String EXTRA_WEB_URL = "web_url";
	static final String EXTRA_IMG_URL = "img_url";
	static final String EXTRA_AD_NUM = "num";

	WebView mWebAd;
	ImageView mImgAd;
	CheckBox mCheck;
	ImageButton mCloseBtn;
	RelativeLayout mRoot;
	CountSync mTimer;

	String mAdNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (Util.Display.isTablet(this))
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		super.onCreate(savedInstanceState);
		mTimer = new CountSync();
		mRoot = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.ad_webview, null);
		mWebAd = (WebView) mRoot.findViewById(R.id.ad_webview);
		mWebAd.getSettings().setJavaScriptEnabled(true);
		mWebAd.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mWebAd.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				setProgress(newProgress * 100);
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
					mTimer.interrupt();
					setContentView(mRoot);
				}
			}
		});
		mImgAd = (ImageView) mRoot.findViewById(R.id.ad_imageview);
		mCheck = (CheckBox) mRoot.findViewById(R.id.check_ad_noshow);
		mCloseBtn = (ImageButton) mRoot.findViewById(R.id.btn_ad_close);
		mCheck.setOnCheckedChangeListener(this);
		mCloseBtn.setOnClickListener(this);

		Intent i = getIntent();
		mAdNum = i.getStringExtra(EXTRA_AD_NUM);
		String webUrl = i.getStringExtra(EXTRA_WEB_URL);
		String imgUrl = i.getStringExtra(EXTRA_IMG_URL);
		if (imgUrl != null) {
			mImgAd.setClickable(true);
			mImgAd.setOnClickListener(this);
			new ImgSync(mImgAd, imgUrl).execute();
		} else if (webUrl != null) {
			mWebAd.loadUrl(webUrl);
			mWebAd.setVisibility(View.VISIBLE);
		}
		mTimer.start();
	}

	Config getConfig() {
		// TODO Auto-generated method stub
		return Config.getInstance(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked) {
			getConfig().addReadMessage(mAdNum);
			finish();
		} else
			getConfig().removeReadMessage(mAdNum);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
		if (v.getId() == R.id.ad_imageview
				&& getIntent().getStringExtra(EXTRA_WEB_URL) != null)
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent()
					.getStringExtra(EXTRA_WEB_URL))));
	}

	public class CountSync extends Thread {
		public void run() {
			for (int i = 0; i < 10; i++) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					return;
				}
			}
			finish();
		};
	}

	public class ImgSync extends AsyncTask<Void, Void, Bitmap> {

		ImageView iv;
		String url;

		public ImgSync(ImageView iv, String url) {
			super();
			this.iv = iv;
			this.url = url;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Bitmap b;
			try {
				b = Util.Stream.bitmapFromURL(url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return b;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Bitmap b = result;
			if (b == null) {
				finish();
				return;
			}
			mTimer.interrupt();
			iv.setVisibility(View.VISIBLE);
			iv.setImageBitmap(b);
			setContentView(mRoot);
		}

	}
}
