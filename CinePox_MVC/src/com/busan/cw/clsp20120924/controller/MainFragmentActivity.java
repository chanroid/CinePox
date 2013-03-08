package com.busan.cw.clsp20120924.controller;

import utils.DisplayUtils;
import utils.ManifestUtils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.interfaces.ConfigModelCallback;
import com.busan.cw.clsp20120924.interfaces.LoginModelCallback;
import com.busan.cw.clsp20120924.interfaces.MainViewCallback;
import com.busan.cw.clsp20120924.interfaces.PointModelCallback;
import com.busan.cw.clsp20120924.interfaces.SideMenuViewCallback;
import com.busan.cw.clsp20120924.interfaces.TicketModelCallback;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.model.ConfigModel;
import com.busan.cw.clsp20120924.model.LoginModel;
import com.busan.cw.clsp20120924.model.PointModel;
import com.busan.cw.clsp20120924.model.TicketModel;
import com.busan.cw.clsp20120924.view.MainView;
import com.busan.cw.clsp20120924.view.SideMenuView;
import com.busan.cw.clsp20120924_beta.R;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

@SuppressLint("HandlerLeak")
public class MainFragmentActivity extends SlidingFragmentActivity implements
		MainViewCallback, SideMenuViewCallback, PointModelCallback,
		TicketModelCallback, Constants, LoginModelCallback, ConfigModelCallback {

	private static final int BACK_CODE = 0;

	private boolean isDetailShowing = false;
	private boolean isBack = false;

	private MainView mainView;
	private SideMenuView menuView;
	private SlidingMenu menu;

	private CinepoxAppModel appModel;
	private PointModel pointModel;
	private TicketModel ticketModel;
	private LoginModel loginModel;
	private ConfigModel configModel;

	private Fragment lastMenuFragment;

	private HomeFragment homeFragment;
	private MovieListFragment movieFragment;
	private MovieListFragment tvFragment;
	private MovieListFragment aniFragment;
	private MovieListFragment premiumFragment;
	private MovieListFragment adultFragment;
	private UpdateFragment updateFragment;
	private MovieDetailFragment detailFragment;

	@Override
	public void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		setTheme(R.style.Theme_Sherlock_NoActionBar);
		super.onCreate(arg0);

		mainView = new MainView(this);
		mainView.setCallback(this);

		menuView = new SideMenuView(this);
		menuView.setCallback(this);

		menu = getSlidingMenu();
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setBehindOffset(DisplayUtils.getWindowSize(this)[0] / 7);
		menu.setFadeDegree(1.f);
		menu.setShadowDrawable(R.drawable.sidemenu_shadow);
		menu.setBehindScrollScale(0.f);
		menu.setShadowWidth((int) ((float) menu.getWidth() * (75.f / 1000.f)));
		menu.requestLayout();
		menu.invalidate();

		setContentView(mainView);
		setBehindContentView(menuView);

		mainView.initSegment();

		pointModel = new PointModel(this);
		pointModel.setCallback(this);

		ticketModel = new TicketModel(this);
		ticketModel.setCallback(this);

		loginModel = new LoginModel(this);
		loginModel.setCallback(this);

		configModel = new ConfigModel(this);
		configModel.setCallback(this);

		checkAd();
		restoreFragments();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (isDetailShowing)
			changeFragment(lastMenuFragment);
		else {
			if (isBack)
				super.onBackPressed();
			else {
				isBack = true;
				Toast.makeText(this, R.string.backtoexit, Toast.LENGTH_SHORT)
						.show();
				mTimehandler.sendEmptyMessageDelayed(0, 2000);
			}
		}
	}

	public void showLoading() {
		mainView.showLoading();
	}

	public void hideLoading() {
		mainView.hideLoading();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshMenu();
	}

	private void loadPoint() {
		pointModel.loadPoint(appModel.getUserConfig().getMemberSeq());
	}

	private void loadTicket() {
		ticketModel.loadTicket(appModel.getUserConfig().getMemberSeq());
	}

	private void loadUserInfo() {
		if (appModel.getUserConfig() == null) {
			// 다시 로그인 하도록 처리.
		} else {
			menuView.setNickName(appModel.getUserConfig().getNickName());
			menuView.setProfileUrl(appModel.getUserConfig()
					.getProfileImageUrl());
			menuView.setSafeMode(appModel.getUserConfig().isSafeMode());
			mainView.setMenuVisiblity(appModel.getUserConfig().getNotSeeMenus());
			loadPoint();
			loadTicket();
			if (appModel.isDebugMode())
				mainView.showDebugMode();
		}
	}

	private void loadConfig() {
		if (appModel.getAppConfig() == null)
			configModel.loadConfig();
		else {
			if (appModel.getAppConfig().getRecentVerCode() > ManifestUtils
					.getVersionCode(this))
				menuView.setUpdateFlag(true);
			else
				menuView.setUpdateFlag(false);
			if (appModel.isDebugMode())
				menuView.setDebugMode(true);
			if (appModel.isDebugMode())
				mainView.showDebugMode();
		}
	}

	private void checkAd() {
		String adimage = ((CinepoxAppModel) getApplication()).getAppConfig()
				.getAdImageUrl();
		if (!"".equals(adimage))
			startActivityForResult(new Intent(this, AppADActivity.class), 0);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == 4444)
			finish();
	}

	private void refreshMenu() {
		appModel = (CinepoxAppModel) getApplication();

		menuView.setVersionName(ManifestUtils.getVersionNameText(this)
				.toString());

		loadUserInfo();
		loadConfig();

	}

	@Override
	public void onSearchClick(SideMenuView view) {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, MovieSearchActivity.class));
	}

	@Override
	public void onProfileClick(SideMenuView view) {
		startActivityForResult(new Intent(this, MyPageActivity.class), 0);
	}

	@Override
	public void onContinueClick(SideMenuView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onZzimListClick(SideMenuView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onViewListClick(SideMenuView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUseInfoClick(SideMenuView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBillingMenuClick(SideMenuView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEventClick(SideMenuView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserCenterClick(SideMenuView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVersionInfoClick(SideMenuView view) {
		// TODO Auto-generated method stub
		if (isDetailShowing)
			onBackPressed();
		changeFragment(updateFragment);
	}

	@Override
	public void onSidemenuClick(MainView view) {
		// TODO Auto-generated method stub
		menu.showBehind(true);
	}

	@Override
	public void onSearchClick(MainView view) {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, MovieSearchActivity.class));
	}

	protected void changeFragment(Fragment fragment) {
		FragmentTransaction trans = getSupportFragmentManager()
				.beginTransaction();
		if (lastMenuFragment != null)
			trans.hide(lastMenuFragment);

		if (fragment != null) {
			isDetailShowing = fragment.equals(detailFragment)
					|| fragment.equals(updateFragment);
			trans.show(fragment);
			if (!isDetailShowing) {
				trans.hide(detailFragment);
				trans.hide(updateFragment);
				lastMenuFragment = fragment;
			}
		}

		trans.commit();
		getSupportFragmentManager().executePendingTransactions();
		menu.showAbove();
	}

	protected void showMovieDetailFragment(int movieNum) {
		changeFragment(detailFragment);
		detailFragment.setMovieNum(movieNum);
	}

	private void restoreFragments() {
		// TODO Auto-generated method stub
		FragmentManager manager = getSupportFragmentManager();
		homeFragment = (HomeFragment) manager
				.findFragmentByTag(EXTRA_MENU_HOME);
		movieFragment = (MovieListFragment) manager
				.findFragmentByTag(EXTRA_MENU_MOVIE);
		tvFragment = (MovieListFragment) manager
				.findFragmentByTag(EXTRA_MENU_TV);
		aniFragment = (MovieListFragment) manager
				.findFragmentByTag(EXTRA_MENU_KIDS);
		premiumFragment = (MovieListFragment) manager
				.findFragmentByTag(EXTRA_MENU_MONTH);
		adultFragment = (MovieListFragment) manager
				.findFragmentByTag(EXTRA_MENU_ADULT);
		updateFragment = (UpdateFragment) manager.findFragmentByTag("update");
		detailFragment = (MovieDetailFragment) manager
				.findFragmentByTag("moviedetail");

		FragmentTransaction trans = getSupportFragmentManager()
				.beginTransaction();

		if (homeFragment == null) {
			homeFragment = new HomeFragment();
			trans.add(mainView.getFragmentContainerId(), homeFragment,
					EXTRA_MENU_HOME);
		}

		if (movieFragment == null) {
			movieFragment = new MovieListFragment();
			Bundle b = new Bundle();
			b.putInt(MovieListFragment.EXTRA_LIST_NUM,
					MovieListFragment.LIST_MOVIE);
			movieFragment.setArguments(b);
			trans.add(mainView.getFragmentContainerId(), movieFragment,
					EXTRA_MENU_MOVIE);
		}

		if (tvFragment == null) {
			tvFragment = new MovieListFragment();
			Bundle b1 = new Bundle();
			b1.putInt(MovieListFragment.EXTRA_LIST_NUM,
					MovieListFragment.LIST_TV);
			tvFragment.setArguments(b1);
			trans.add(mainView.getFragmentContainerId(), tvFragment,
					EXTRA_MENU_TV);

		}

		if (aniFragment == null) {
			aniFragment = new MovieListFragment();
			Bundle b2 = new Bundle();
			b2.putInt(MovieListFragment.EXTRA_LIST_NUM,
					MovieListFragment.LIST_ANI);
			aniFragment.setArguments(b2);
			trans.add(mainView.getFragmentContainerId(), aniFragment,
					EXTRA_MENU_KIDS);
		}

		if (premiumFragment == null) {
			premiumFragment = new MovieListFragment();
			Bundle b3 = new Bundle();
			b3.putInt(MovieListFragment.EXTRA_LIST_NUM,
					MovieListFragment.LIST_PREMIUM);
			premiumFragment.setArguments(b3);
			trans.add(mainView.getFragmentContainerId(), premiumFragment,
					EXTRA_MENU_MONTH);
		}

		if (adultFragment == null) {
			adultFragment = new MovieListFragment();
			Bundle b4 = new Bundle();
			b4.putInt(MovieListFragment.EXTRA_LIST_NUM,
					MovieListFragment.LIST_ADULT);
			adultFragment.setArguments(b4);
			trans.add(mainView.getFragmentContainerId(), adultFragment,
					EXTRA_MENU_ADULT);
		}

		if (updateFragment == null) {
			updateFragment = new UpdateFragment();
			trans.add(mainView.getFragmentContainerId(), updateFragment,
					"update");
		}

		if (detailFragment == null) {
			detailFragment = new MovieDetailFragment();
			trans.add(mainView.getFragmentContainerId(), detailFragment,
					"moviedetail");
		}

		trans.hide(adultFragment);
		trans.hide(aniFragment);
		trans.hide(detailFragment);
		trans.hide(homeFragment);
		trans.hide(movieFragment);
		trans.hide(premiumFragment);
		trans.hide(tvFragment);
		trans.hide(updateFragment);

		trans.commit();
		changeFragment(homeFragment);
	}

	@Override
	public void onHomeSegmentClick(MainView view) {
		// TODO Auto-generated method stub
		changeFragment(homeFragment);
	}

	@Override
	public void onMovieSegmentClick(MainView view) {
		// TODO Auto-generated method stub
		changeFragment(movieFragment);
	}

	@Override
	public void onTVSegmentClick(MainView view) {
		// TODO Auto-generated method stub
		changeFragment(tvFragment);
	}

	@Override
	public void onAniSegmentClick(MainView view) {
		// TODO Auto-generated method stub
		changeFragment(aniFragment);
	}

	@Override
	public void onPremiumSegmentClick(MainView view) {
		// TODO Auto-generated method stub
		changeFragment(premiumFragment);
	}

	@Override
	public void onAdultSegmentClick(MainView view) {
		// TODO Auto-generated method stub
		changeFragment(adultFragment);
	}

	@Override
	public void onPointLoadStart(PointModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPointLoadComplete(PointModel model) {
		// TODO Auto-generated method stub
		menuView.setPoint(appModel.getUserConfig().getPoint());
		menuView.setBonus(appModel.getUserConfig().getBonus());
	}

	@Override
	public void onPointLoadError(PointModel model, String message) {
		// TODO Auto-generated method stub
		menuView.setPoint(0);
		menuView.setBonus(0);
	}

	@Override
	public void onTicketLoadStart(TicketModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTicketLoadComplete(TicketModel model) {
		// TODO Auto-generated method stub
		menuView.setPremium(appModel.getUserConfig().isPremium(), appModel
				.getUserConfig().isTicketIsAuto());
		menuView.setPremiumPeriod(appModel.getUserConfig()
				.getTicketEndDateView());
	}

	@Override
	public void onTicketLoadError(TicketModel model, String message) {
		// TODO Auto-generated method stub
		menuView.setPremium(false, false);
		menuView.setPremiumPeriod(null);
	}

	@Override
	public void onLoginSuccess(LoginModel model) {
		// TODO Auto-generated method stub
		loadUserInfo();
	}

	@Override
	public void onLoginStart(LoginModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoginError(LoginModel model, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadConfigStart(ConfigModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadConfigError(ConfigModel model, String error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadConfigComplete(ConfigModel model) {
		// TODO Auto-generated method stub
		loadConfig();
	}

	private Handler mTimehandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BACK_CODE:
				isBack = false;
				break;
			default:
				break;
			}
		}
	};

}
