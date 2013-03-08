package com.busan.cw.clsp20120924.controller;

import utils.ThemeUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.busan.cw.clsp20120924.base.DomainManager;
import com.busan.cw.clsp20120924.interfaces.CommonWebViewJSInterface;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.model.PreferenceModel;
import com.busan.cw.clsp20120924_beta.R;

public class MemberOutActivity extends CommonWebViewActivity {
	private CinepoxAppModel appModel;
	private PreferenceModel prefModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ThemeUtils.darkNoTitle(this);
		super.onCreate(savedInstanceState);

		appModel = (CinepoxAppModel) getApplication();
		prefModel = new PreferenceModel(this);
		view.addJavaScriptInterface(joinActionJSInterface, "android");
		view.loadUrl(DomainManager.getWebViewDomain(this)
				+ appModel.getAppConfig().getMemberoutUrl() + buildParams());
	}

	private String buildParams() {
		String data = app().getUserConfig().getAccountName();
		return String.format("?id=%s&", data);
	}

	private CommonWebViewJSInterface joinActionJSInterface = new CommonWebViewJSInterface() {
		@SuppressWarnings("unused")
		public void mebOutCallback() {
			prefModel.setAutoLogin(false);
			prefModel.setAutoLoginAction(null);
			appModel.processLogout();
			setResult(RESULT_OK);
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
					MemberOutActivity.this);
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
			setResult(RESULT_OK);
			finish();
			// 뭔가 결과값을 받아서 자동로그인을 하도록 해야 함
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

}
