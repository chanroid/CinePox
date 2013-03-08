package com.busan.cw.clsp20120924.controller;

import utils.ThemeUtils;
import utils.LogUtils.l;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.base.DomainManager;
import com.busan.cw.clsp20120924.interfaces.CommonWebViewJSInterface;
import com.busan.cw.clsp20120924.interfaces.LoginModelCallback;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.model.LoginModel;
import com.busan.cw.clsp20120924_beta.R;

public class CinepoxJoinActivity extends CommonWebViewActivity implements
		LoginModelCallback, Constants {

	private CinepoxAppModel appModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ThemeUtils.darkNoTitle(this);
		super.onCreate(savedInstanceState);

		appModel = (CinepoxAppModel) getApplication();
		view.addJavaScriptInterface(joinActionJSInterface, "android");
		view.loadUrl(DomainManager.getWebViewDomain(this)
				+ appModel.getAppConfig().getJoinUrl());
	}

	private CommonWebViewJSInterface joinActionJSInterface = new CommonWebViewJSInterface() {
		@SuppressWarnings("unused")
		public void joinBasicSuccess(String id, String pw) {
			l.d("join success : " + id + ", " + pw);
			Intent i = new Intent();
			i.putExtra(EXTRA_ID, id);
			i.putExtra(EXTRA_PASSWORD, pw);
			setResult(Activity.RESULT_OK, i);
			finish();
		}

		@Override
		public void confirm(String message, String confirmMethod,
				String cancelMethod) {
			showJSConfirm(message, confirmMethod, cancelMethod);
		}

		@Override
		public void alert(String message, String confirmMethod) {
			showJSAlert(message, confirmMethod);
		}

		@Override
		public void close(String message) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					CinepoxJoinActivity.this);
			dialog.setTitle(R.string.alert);
			dialog.setMessage(message);
			dialog.setPositiveButton(R.string.done,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							setResult(RESULT_CANCELED);
							finish();
						}
					});
			dialog.show();
		}

		@Override
		public void close() {
			setResult(RESULT_CANCELED);
			finish();
		}

		@Override
		public void loading(String message) {
			view.showLoadingDialog(message);
		}

		@Override
		public void loading() {
			view.showLoadingDialog(null);
		}

		@Override
		public void loadingClose() {
			view.hideLoadingDialog();
		}
	};

	@Override
	public void onLoginSuccess(LoginModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoginStart(LoginModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoginError(LoginModel model, String message) {
		// TODO Auto-generated method stub

	}

}
