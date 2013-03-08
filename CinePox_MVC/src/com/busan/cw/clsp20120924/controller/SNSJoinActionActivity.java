package com.busan.cw.clsp20120924.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import utils.EncryptUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.busan.cw.clsp20120924.base.DomainManager;
import com.busan.cw.clsp20120924.interfaces.CommonWebViewCallback;
import com.busan.cw.clsp20120924.interfaces.CommonWebViewJSInterface;
import com.busan.cw.clsp20120924.model.SNSLoginData;
import com.busan.cw.clsp20120924_beta.R;

public class SNSJoinActionActivity extends CommonWebViewActivity implements
		CommonWebViewCallback {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		try {
			view.addJavaScriptInterface(joinActionJSInterface, "android");
			view.loadUrl(DomainManager.getWebViewDomain(this)
					+ app().getAppConfig().getSnsJoinUrl() + buildParams());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private String buildParams() throws UnsupportedEncodingException {
		SNSLoginData data = app().getUserConfig().loginData;
		Object[] params = { data.sns, data.userId,
				URLEncoder.encode(data.nickName, "utf-8"),
				URLEncoder.encode(data.snsHome, "utf-8"),
				URLEncoder.encode(data.profileImage, "utf-8"),
				EncryptUtils.encodeMD5("dummy") };
		return String
				.format("&sns_name=%s&user_id=%s&nickname=%s&home=%s&image=%s&user_key=%s",
						params);
	}

	private CommonWebViewJSInterface joinActionJSInterface = new CommonWebViewJSInterface() {
		public void confirm(String message, String confirmMethod,
				String cancelMethod) {
			showJSConfirm(message, confirmMethod, cancelMethod);
		}

		public void alert(String message, String confirmMethod) {
			showJSAlert(message, confirmMethod);
		}

		public void close(String message) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					SNSJoinActionActivity.this);
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

		public void close() {
			setResult(RESULT_OK);
			finish();
		}

		public void loading(String message) {
			view.showLoadingDialog(message);
		}

		public void loading() {
			view.showLoadingDialog(null);
		}

		public void loadingClose() {
			view.hideLoadingDialog();
		}

		@SuppressWarnings("unused")
		public void joinSuccess() {
			close();
		}

	};

}
