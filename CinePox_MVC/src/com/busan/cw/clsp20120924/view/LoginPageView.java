package com.busan.cw.clsp20120924.view;

import utils.StringUtils;
import view.CCView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.busan.cw.clsp20120924.interfaces.LoginPageViewCallback;
import com.busan.cw.clsp20120924_beta.R;

import extend.ClearableEditText;

public class LoginPageView extends CCView implements OnClickListener,
		OnCheckedChangeListener {

	private ImageButton backBtn;

	private TextView specialBtn;
	private TextView hangulBtn;
	private ImageView specialImage;
	private ImageView hangulImage;

	private ClearableEditText idEdit;
	private ClearableEditText pwEdit;

	private CheckBox autologinCheck;
	private Button loginBtn;

	private TextView missedBtn;

	private ImageButton joinBtn;

	private ProgressDialog progress;

	private LoginPageViewCallback callback;

	public void setCallback(LoginPageViewCallback callback) {
		this.callback = callback;
	}
	
	public String getIdText() {
		return idEdit.getText().toString();
	}

	public String getPwText() {
		return pwEdit.getText().toString();
	}

	public int getIdTextLength() {
		return idEdit.getText().toString().length();
	}

	public int getPwTextLength() {
		return pwEdit.getText().toString().length();
	}

	public void setIdText(String id) {
		idEdit.setText(id);
	}

	public void setPwText(String pw) {
		pwEdit.setText(pw);
	}

	public boolean isAutologinChecked() {
		return autologinCheck.isChecked();
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		backBtn = (ImageButton) findViewById(R.id.btn_login_back);
		backBtn.setOnClickListener(this);

		specialBtn = (TextView) findViewById(R.id.text_login_special_character);
		specialBtn.setOnClickListener(this);
		hangulBtn = (TextView) findViewById(R.id.text_login_hangul);
		hangulBtn.setOnClickListener(this);
		specialImage = (ImageView) findViewById(R.id.img_keyboard_special);
		hangulImage = (ImageView) findViewById(R.id.img_keyboard_hangul);

		InputFilter[] textFilters = new InputFilter[] {
				StringUtils.getEditTextFilter("^[a-zA-Z0-9]+$"),
				new InputFilter.LengthFilter(12) };

		idEdit = (ClearableEditText) findViewById(R.id.edit_login_id);
		idEdit.setMaxEms(12);
		idEdit.setSingleLine();
		idEdit.setFilters(textFilters);
		idEdit.setClearIcon(R.drawable.bt_del_idpw);
		idEdit.requestFocus();
		pwEdit = (ClearableEditText) findViewById(R.id.edit_login_pw);
		pwEdit.setMaxEms(12);
		pwEdit.setSingleLine();
		pwEdit.setFilters(textFilters);
		pwEdit.setPassword(true);
		pwEdit.setClearIcon(R.drawable.bt_del_idpw);

		autologinCheck = (CheckBox) findViewById(R.id.check_login_autologin);
		autologinCheck.setOnCheckedChangeListener(this);
		loginBtn = (Button) findViewById(R.id.btn_login_commit);
		loginBtn.setOnClickListener(this);

		missedBtn = (TextView) findViewById(R.id.text_login_missed_account);
		missedBtn.setOnClickListener(this);

		joinBtn = (ImageButton) findViewById(R.id.btn_login_join);
		joinBtn.setOnClickListener(this);

		progress = new ProgressDialog(getContext());
		progress.setMessage(getContext().getString(R.string.readytoresponse));
		progress.setCancelable(false);
	}

	public void showLoading() {
		if (!progress.isShowing())
			progress.show();
	}

	public void dismissLoaing() {
		if (progress.isShowing())
			progress.dismiss();
	}

	public void showError(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.error);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.done, OnErrorDialogClickListener);
		builder.setCancelable(false);
		builder.show();
	}

	private void showFindAccount() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.find_account);
		builder.setItems(new String[] {
				getContext().getString(R.string.find_id),
				getContext().getString(R.string.find_pw) },
				OnFindAccountClickListener);
		builder.show();
	}

	private DialogInterface.OnClickListener OnErrorDialogClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.dismiss();
		}

	};

	private DialogInterface.OnClickListener OnFindAccountClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			if (callback == null)
				return;
			switch (which) {
			case 0:
				callback.onMissedIdClick(LoginPageView.this);
				break;
			case 1:
				callback.onMissedPwClick(LoginPageView.this);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (buttonView.equals(autologinCheck))
			callback.onAutoLoginCheck(this, isChecked);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(backBtn))
			callback.onBackClick(this);
		else if (v.equals(specialBtn)) {
			// 키보드 이미지 보임/숨김 처리
			hangulImage.setVisibility(GONE);
			specialImage
					.setVisibility(specialImage.getVisibility() == GONE ? VISIBLE
							: GONE);
		} else if (v.equals(hangulBtn)) {
			// 키보드 이미지 보임/숨김 처리
			specialImage.setVisibility(GONE);
			hangulImage
					.setVisibility(hangulImage.getVisibility() == GONE ? VISIBLE
							: GONE);
		} else if (v.equals(loginBtn))
			callback.onLoginClick(this);
		else if (v.equals(missedBtn))
			showFindAccount();
		else if (v.equals(joinBtn))
			callback.onJoinClick(this);
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_cinepox_login;
	}

	public LoginPageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public LoginPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public LoginPageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

}
