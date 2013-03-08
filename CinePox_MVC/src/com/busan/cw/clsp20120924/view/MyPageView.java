package com.busan.cw.clsp20120924.view;

import utils.StringUtils;
import view.CCView;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.busan.cw.clsp20120924.interfaces.MyPageViewCallback;
import com.busan.cw.clsp20120924_beta.R;
import com.fedorvlasov.lazylist.ImageLoader;

import extend.ClearableEditText;

public class MyPageView extends CCView implements OnClickListener, TextWatcher,
		OnCheckedChangeListener {

	private ImageButton backBtn;

	private ImageView profileImage;
	private TextView profileImageChangeBtn;

	private TextView emailText;
	private ClearableEditText nicknameEdit;
	private ImageView ticketImage;
	private TextView userGradeText;
	private TextView ticketText;
	private TextView ticketPeriodText;
	private TextView ticketBtn;
	private TextView pointText;
	private TextView bonusText;
	private TextView pointLogBtn;
	private TextView bonusLogBtn;
	// 2012-02-22 팝콘과 보너스가 통합됨에 따라 해당 기능 제거
	// private TextView popcornText;
	// private TextView popcornTransBtn;
	private TextView messageBtn;
	private TextView autologinText;
	private CheckBox autologinCheck;
	private TextView profileEditBtn;
	private TextView passwordEditBtn;
	private TextView leaveBtn;

	private TableRow profileEditRow;
	private TableRow passwordEditRow;

	private TextView logoutBtn;

	private MyPageViewCallback callback;

	public void setCallback(MyPageViewCallback callback) {
		this.callback = callback;
	}

	public void setAutologinState(boolean state) {
		autologinCheck.setChecked(state);
		autologinText.setText(state ? R.string.login_autologining
				: R.string.login_nonautologin);
	}

	public void setAccountName(String name) {
		if (name == null)
			name = "";
		boolean snsflag = name.contains(":");
		profileImageChangeBtn.setVisibility(snsflag ? GONE : VISIBLE);
		profileEditRow.setVisibility(snsflag ? GONE : VISIBLE);
		passwordEditRow.setVisibility(snsflag ? GONE : VISIBLE);
		nicknameEdit.setEnabled(!snsflag);
		if (snsflag) {
			Resources res = getContext().getResources();
			Drawable leftIcon = null;
			if (name.contains("facebook:")) {
				name = name.replace("facebook:", "");
				leftIcon = res.getDrawable(R.drawable.img_facebook);
			} else if (name.contains("twitter:")) {
				name = name.replace("twitter:", "");
				leftIcon = res.getDrawable(R.drawable.img_twitter);
			} else if (name.contains("me2day:")) {
				name = name.replace("me2day:", "");
				leftIcon = res.getDrawable(R.drawable.img_me2day);
			} else if (name.contains("yozm:")) {
				name = name.replace("yozm:", "");
				leftIcon = res.getDrawable(R.drawable.img_yozm);
			}
			emailText.setCompoundDrawablesWithIntrinsicBounds(leftIcon, null,
					null, null);
		}
		emailText.setText(name);
	}

	public void setProfileUrl(String url) {
		ImageLoader loader = new ImageLoader(getContext());
		loader.DisplayImage(url, profileImage);
	}

	public void setNickName(String name) {
		nicknameEdit.setText(name);
	}

	public void setPoint(int point) {
		pointText.setText(StringUtils.comma(point));
	}

	public void setBonus(int bonus) {
		bonusText.setText(StringUtils.comma(bonus));
	}

	public void setUserLevelName(String levelName) {
		userGradeText.setText(levelName);
	}

	public void setPremium(boolean premium, boolean isAuto) {
		if (premium) {
			ticketImage.setVisibility(VISIBLE);
			if (isAuto)
				ticketText.setText(R.string.ticket_premium_auto);
			else
				ticketText.setText(R.string.ticket_premium);
		} else {
			ticketImage.setVisibility(GONE);
			ticketText.setText(R.string.ticket_none);
		}
	}

	public void setPremiumPeriod(String period) {
		if (period != null) {
			ticketPeriodText.setText(period + " 까지 사용가능");
			ticketPeriodText.setVisibility(VISIBLE);
		} else {
			ticketPeriodText.setVisibility(GONE);
		}
	}

	public void showLogout() {
		Builder dialog = new AlertDialog.Builder(getContext());
		dialog.setCancelable(false);
		dialog.setTitle(R.string.alert);
		dialog.setMessage(R.string.noti_logout);
		dialog.setPositiveButton(R.string.done,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();

						if (callback != null)
							callback.onLogoutConfirm(MyPageView.this);
					}
				});
		dialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();

						if (callback != null)
							callback.onLogoutCancel(MyPageView.this);
					}
				});
		dialog.create().show();
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		backBtn = (ImageButton) findViewById(R.id.btn_mypage_back);
		backBtn.setOnClickListener(this);

		profileImage = (ImageView) findViewById(R.id.img_login_profile);
		profileImage.setOnClickListener(this);
		profileImageChangeBtn = (TextView) findViewById(R.id.btn_login_profile_change);
		profileImageChangeBtn.setOnClickListener(this);

		emailText = (TextView) findViewById(R.id.text_mypage_email);
		nicknameEdit = (ClearableEditText) findViewById(R.id.edit_mypage_nick);
		nicknameEdit.addTextChangedListener(this);
		nicknameEdit.setSingleLine();
		nicknameEdit.setClearIcon(R.drawable.bt_del_idpw);
		userGradeText = (TextView) findViewById(R.id.text_mypage_usergrade);
		ticketImage = (ImageView) findViewById(R.id.img_mypage_ticket);
		ticketText = (TextView) findViewById(R.id.text_mypage_ticket);
		ticketPeriodText = (TextView) findViewById(R.id.text_mypage_ticket_period);
		ticketBtn = (TextView) findViewById(R.id.btn_mypage_ticket_detail);
		ticketBtn.setOnClickListener(this);
		pointText = (TextView) findViewById(R.id.text_mypage_point);
		bonusText = (TextView) findViewById(R.id.text_mypage_popcorn);
		pointLogBtn = (TextView) findViewById(R.id.btn_mypage_point_detail);
		pointLogBtn.setOnClickListener(this);
		bonusLogBtn = (TextView) findViewById(R.id.btn_mypage_popcorn_detail);
		bonusLogBtn.setOnClickListener(this);
		// popcornText = (TextView) findViewById(R.id.text_mypage_popcorn);
		// popcornTransBtn = (TextView)
		// findViewById(R.id.btn_mypage_popcorn_trans);
		// popcornTransBtn.setOnClickListener(this);
		messageBtn = (TextView) findViewById(R.id.btn_mypage_message_detail);
		messageBtn.setOnClickListener(this);
		autologinText = (TextView) findViewById(R.id.text_mypage_autologin);
		autologinCheck = (CheckBox) findViewById(R.id.check_mypage_autologin);
		autologinCheck.setOnCheckedChangeListener(this);
		profileEditBtn = (TextView) findViewById(R.id.btn_mypage_userinfoedit_detail);
		profileEditBtn.setOnClickListener(this);
		passwordEditBtn = (TextView) findViewById(R.id.btn_mypage_passwordedit_detail);
		passwordEditBtn.setOnClickListener(this);
		leaveBtn = (TextView) findViewById(R.id.btn_mypage_leave);
		leaveBtn.setOnClickListener(this);

		profileEditRow = (TableRow) findViewById(R.id.layout_mypage_profileedit);
		passwordEditRow = (TableRow) findViewById(R.id.layout_mypage_passwordedit);

		logoutBtn = (TextView) findViewById(R.id.btn_mypage_bottom);
		logoutBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(backBtn))
			callback.onBackClick(this);
		else if (v.equals(profileImage))
			callback.onProfileImageClick(this);
		else if (v.equals(profileImageChangeBtn))
			callback.onProfileImageChangeClick(this);
		else if (v.equals(ticketBtn))
			callback.onTicketButtonClick(this);
		else if (v.equals(pointLogBtn))
			callback.onPointDetailClick(this);
		else if (v.equals(bonusLogBtn))
			callback.onBonusDetailClick(this);
		else if (v.equals(messageBtn))
			callback.onMessageDetailClick(this);
		// else if (v.equals(popcornTransBtn))
		// callback.onPopcornTransClick(this);
		else if (v.equals(profileEditBtn))
			callback.onProfileEditClick(this);
		else if (v.equals(passwordEditBtn))
			callback.onPasswordEditClick(this);
		else if (v.equals(leaveBtn))
			callback.onLeaveClick(this);
		else if (v.equals(logoutBtn))
			callback.onLogoutClick(this);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		callback.onNicknameEdited(this, s);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (buttonView.equals(autologinCheck))
			callback.onAutologinCheck(this, isChecked);

	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_mypage;
	}

	public MyPageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyPageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// do nothing
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// do nothing
	}

}
