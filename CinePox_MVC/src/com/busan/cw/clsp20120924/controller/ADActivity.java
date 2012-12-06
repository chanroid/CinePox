/**
 * 0. Project  : CinePox_MVC
 *
 * 1. FileName : ADActivity.java
 * 2. Package : com.busan.cw.clsp20120924.controller
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 31. 오후 5:40:58
 * 6. 변경이력 : 
 *		2012. 10. 31. 오후 5:40:58 : 신규
 *
 */
package com.busan.cw.clsp20120924.controller;

import java.io.IOException;

import kr.co.chan.util.Util;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.CompoundButton;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.interfaces.ADViewCallback;
import com.busan.cw.clsp20120924.model.ConfigModel;
import com.busan.cw.clsp20120924.view.ADView;

import controller.CCActivity;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : ADActivity.java
 * 3. Package  : com.busan.cw.clsp20120924.controller
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 5:40:58
 * </PRE>
 */
public class ADActivity extends CCActivity implements ADViewCallback, Constants {

	private ADView mAdView;
	private ConfigModel mConfigModel;
	private Thread mTimer = new Thread() {
		public void run() {
			for (int i = 0; i < 10; i++) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					return;
				}
			}
			finish();
		}
	};
	private class ImgSync extends AsyncTask<Void, Void, Bitmap> {

		String url;

		public ImgSync(String url) {
			super();
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
			if (result == null) {
				finish();
			} else {
				mTimer.interrupt();
				mAdView.setADImage(result);
				setContentView(mAdView);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mConfigModel = (ConfigModel) loadModel(ConfigModel.class);
		mAdView = (ADView) loadView(ADView.class);
		mAdView.setCallback(this);
		Intent i = getIntent();
		String webUrl = i.getStringExtra(KEY_WEB_URL);
		String imgUrl = i.getStringExtra(KEY_IMG_URL);
		if (imgUrl != null) {
			new ImgSync(imgUrl).execute();
		} else if (webUrl != null) {
			mAdView.setADUrl(webUrl);
		} else {
			finish();
		}
		mTimer.start();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		super.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
		if (v.getId() == R.id.ad_imageview
				&& getIntent().getStringExtra(KEY_WEB_URL) != null)
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent()
					.getStringExtra(KEY_WEB_URL))));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked) {
			mConfigModel.addReadMessage(getIntent().getStringExtra(KEY_AD_NUM));
			finish();
		} else
			mConfigModel.removeReadMessage(getIntent().getStringExtra(KEY_AD_NUM));
	}

	@Override
	public WebChromeClient setWebClient() {
		// TODO Auto-generated method stub
		return new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				setProgress(newProgress * 100);
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
					mTimer.interrupt();
					setContentView(mAdView);
				}
			}
		};
	}

}
