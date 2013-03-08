package com.busan.cw.clsp20120924.view;

import utils.LogUtils.l;
import utils.StringUtils;
import view.CCView;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.busan.cw.clsp20120924.interfaces.SideMenuViewCallback;
import com.busan.cw.clsp20120924_beta.R;
import com.fedorvlasov.lazylist.ImageLoader;

public class SideMenuView extends CCView implements OnClickListener {

	private TextView searchBtn;

	private LinearLayout profileLayout;

	private ImageView profileImage;
	private TextView userNameText;
	private TextView pointText;
	private TextView bonusText;
	private ImageView ticketImage;
	private TextView ticketText;
	private TextView ticketPeriodText;

	private TableRow continueBtn;
	private TableRow zzimListBtn;
	private TableRow viewListBtn;
	private TableRow useInfoBtn;
	private TableRow billingBtn;
	private TableRow eventBtn;
	private TableRow userCenterBtn;
	private TableRow versionBtn;
	private TextView versionText;
	private ImageView versionFlag;

	private SideMenuViewCallback callback;

	public void setCallback(SideMenuViewCallback callback) {
		this.callback = callback;
	}

	public void setNickName(String userName) {
		// TODO Auto-generated method stub
		userNameText.setText(userName + " "
				+ getContext().getString(R.string.sla));
	}

	public void setProfileUrl(String url) {
		ImageLoader loader = new ImageLoader(getContext());
		loader.DisplayImage(url, profileImage);
	}

	public void setVersionName(String name) {
		versionText.setText(name);
	}

	public void setUpdateFlag(boolean flag) {
		versionFlag.setVisibility(flag ? VISIBLE : GONE);
	}

	public void setSafeMode(boolean flag) {
		profileLayout.setBackgroundColor(flag ? Color.parseColor("#7fae3b")
				: Color.parseColor("#3c3c3c"));
	}

	public void setDebugMode(boolean flag) {
		profileLayout.setBackgroundColor(flag ? Color.parseColor("#ff0000")
				: Color.parseColor("#3c3c3c"));
	}

	public void setPoint(int point) {
		pointText.setText(StringUtils.comma(point));
	}

	public void setBonus(int bonus) {
		bonusText.setText(StringUtils.comma(bonus));
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
			l.d("ticket period : " + period);
			ticketPeriodText.setText(period + " 까지 사용가능");
			ticketPeriodText.setVisibility(VISIBLE);
		} else {
			ticketPeriodText.setVisibility(GONE);
		}
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		searchBtn = (TextView) findViewById(R.id.btn_sidemenu_search);
		searchBtn.setOnClickListener(this);

		profileLayout = (LinearLayout) findViewById(R.id.layout_sidemenu_profile);

		profileImage = (ImageView) findViewById(R.id.img_sidemenu_profile);
		profileImage.setOnClickListener(this);
		userNameText = (TextView) findViewById(R.id.text_sidemenu_username);
		pointText = (TextView) findViewById(R.id.text_sidemenu_point);
		bonusText = (TextView) findViewById(R.id.text_sidemenu_bonus);
		ticketImage = (ImageView) findViewById(R.id.img_sidemenu_ticket);
		ticketText = (TextView) findViewById(R.id.text_sidemenu_ticket);
		ticketPeriodText = (TextView) findViewById(R.id.text_sidemenu_period);

		continueBtn = (TableRow) findViewById(R.id.row_sidemenu_continue);
		continueBtn.setOnClickListener(this);
		zzimListBtn = (TableRow) findViewById(R.id.row_sidemenu_zzim);
		zzimListBtn.setOnClickListener(this);
		viewListBtn = (TableRow) findViewById(R.id.row_sidemenu_viewlist);
		viewListBtn.setOnClickListener(this);
		useInfoBtn = (TableRow) findViewById(R.id.row_sidemenu_useinfo);
		useInfoBtn.setOnClickListener(this);
		billingBtn = (TableRow) findViewById(R.id.row_sidemenu_billing);
		billingBtn.setOnClickListener(this);
		eventBtn = (TableRow) findViewById(R.id.row_sidemenu_event);
		eventBtn.setOnClickListener(this);
		userCenterBtn = (TableRow) findViewById(R.id.row_sidemenu_usercenter);
		userCenterBtn.setOnClickListener(this);
		versionBtn = (TableRow) findViewById(R.id.row_sidemenu_versioninfo);
		versionBtn.setOnClickListener(this);

		versionText = (TextView) findViewById(R.id.text_sidemenu_version);
		versionFlag = (ImageView) findViewById(R.id.img_sidemenu_version);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(searchBtn))
			callback.onSearchClick(this);
		else if (v.equals(profileImage))
			callback.onProfileClick(this);
		else if (v.equals(continueBtn))
			callback.onContinueClick(this);
		else if (v.equals(zzimListBtn))
			callback.onZzimListClick(this);
		else if (v.equals(viewListBtn))
			callback.onViewListClick(this);
		else if (v.equals(useInfoBtn))
			callback.onUseInfoClick(this);
		else if (v.equals(billingBtn))
			callback.onBillingMenuClick(this);
		else if (v.equals(eventBtn))
			callback.onEventClick(this);
		else if (v.equals(userCenterBtn))
			callback.onUserCenterClick(this);
		else if (v.equals(versionBtn))
			callback.onVersionInfoClick(this);
	}

	public SideMenuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SideMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SideMenuView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_sidemenu;
	}

}
