package com.busan.cw.clsp20120924.view;

import view.CCView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.busan.cw.clsp20120924_beta.R;
import com.busan.cw.clsp20120924.interfaces.SelectLoginMethodViewCallback;

public class SelectLoginMethodView extends CCView implements OnClickListener {

	private ImageButton normalLoginBtn;
	private ImageButton fbLoginBtn;
	private ImageButton twLoginBtn;
	private ImageButton me2LoginBtn;
	private ImageButton yzLoginBtn;

	private SelectLoginMethodViewCallback callback;

	private ProgressDialog loadingDialog;

	public void setCallback(SelectLoginMethodViewCallback callback) {
		this.callback = callback;
	}

	public void showLoading() {
		if (!loadingDialog.isShowing())
			loadingDialog.show();
	}

	public void dismissLoading() {
		if (loadingDialog.isShowing())
			loadingDialog.dismiss();
	}

	public void showError(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.error);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.done, null);
		builder.setCancelable(false);
		builder.show();
	}

	public void showAgree() {
		Toast.makeText(getContext(), R.string.join_agree, Toast.LENGTH_SHORT)
				.show();
	}

	public SelectLoginMethodView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_select_login_method;
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		normalLoginBtn = (ImageButton) findViewById(R.id.bt_login_cinepox);
		fbLoginBtn = (ImageButton) findViewById(R.id.bt_login_facebook);
		twLoginBtn = (ImageButton) findViewById(R.id.bt_login_twitter);
		me2LoginBtn = (ImageButton) findViewById(R.id.bt_login_me2day);
		yzLoginBtn = (ImageButton) findViewById(R.id.bt_login_yozm);

		normalLoginBtn.setOnClickListener(this);
		fbLoginBtn.setOnClickListener(this);
		twLoginBtn.setOnClickListener(this);
		me2LoginBtn.setOnClickListener(this);
		yzLoginBtn.setOnClickListener(this);

		loadingDialog = new ProgressDialog(getContext());
		loadingDialog.setCancelable(false);
		loadingDialog.setMessage(getContext().getString(R.string.loading));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(normalLoginBtn)) {
			callback.onClickCinepoxLogin(this);
		} else if (v.equals(fbLoginBtn)) {
			callback.onClickFacebookLogin(this);
		} else if (v.equals(twLoginBtn)) {
			callback.onClickTwitterLogin(this);
		} else if (v.equals(me2LoginBtn)) {
			callback.onClickMe2dayLogin(this);
		} else if (v.equals(yzLoginBtn)) {
			callback.onClickYozmLogin(this);
		}
	}

}
