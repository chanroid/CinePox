package com.busan.cw.clsp20120924.controller;

import utils.LogUtils.l;
import utils.ThemeUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.busan.cw.clsp20120924.interfaces.CommonWebViewCallback;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.view.CommonWebView;
import com.busan.cw.clsp20120924_beta.R;

public class CommonWebViewActivity extends Activity implements
		CommonWebViewCallback {

	public CommonWebView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ThemeUtils.darkNoTitle(this);
		super.onCreate(savedInstanceState);
		view = new CommonWebView(this);
		view.setCallback(this);
		setContentView(view);
	}

	public void showJSConfirm(String message, final String confirmMethod,
			final String cancelMethod) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				CommonWebViewActivity.this);
		dialog.setTitle(R.string.alert);
		dialog.setMessage(message);
		dialog.setPositiveButton(R.string.done,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if (confirmMethod != null)
							callJS(confirmMethod);
					}
				});
		if (cancelMethod != null)
			dialog.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							callJS(cancelMethod);
						}
					});
		dialog.show();
	}

	public void showJSAlert(String message, final String confirmMethod) {
		showJSConfirm(message, confirmMethod, null);
	}

	public void callJS(final String method) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				l.d("javascript call " + method);
				view.loadUrl("javascript:" + method);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (!view.canGoBack()) {
			setResult(RESULT_CANCELED);
			finish();
		} else
			view.goBack();
	}

	public CinepoxAppModel app() {
		return (CinepoxAppModel) getApplication();
	}

	@Override
	public void onPrevClick(CommonWebView view) {
		// TODO Auto-generated method stub
		view.goBack();
	}

	@Override
	public void onNextClick(CommonWebView view) {
		// TODO Auto-generated method stub
		view.goForward();
	}

	@Override
	public void onCloseClick(CommonWebView view) {
		// TODO Auto-generated method stub
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	public void onRefreshClick(CommonWebView view) {
		// TODO Auto-generated method stub
		view.refresh();
	}
}
