package com.busan.cw.clsp20120924.view;

import static com.busan.cw.clsp20120924.interfaces.MainViewCallback.SEGMENT_TAG_ADULT;
import static com.busan.cw.clsp20120924.interfaces.MainViewCallback.SEGMENT_TAG_ANI;
import static com.busan.cw.clsp20120924.interfaces.MainViewCallback.SEGMENT_TAG_HOME;
import static com.busan.cw.clsp20120924.interfaces.MainViewCallback.SEGMENT_TAG_MOVIE;
import static com.busan.cw.clsp20120924.interfaces.MainViewCallback.SEGMENT_TAG_PREMIUM;
import static com.busan.cw.clsp20120924.interfaces.MainViewCallback.SEGMENT_TAG_TV;

import java.util.ArrayList;

import view.CCView;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.interfaces.MainViewCallback;
import com.busan.cw.clsp20120924_beta.R;

import extend.SegmentLayout;
import extend.SegmentLayout.OnSegmentChangedListener;
import extend.SegmentLayout.Segment;

/**
 * 메인 메뉴, 카테고리 버튼들을 가지고 있는 뷰
 * 
 * @author CINEPOX
 * 
 */
public class MainView extends CCView implements OnClickListener,
		OnSegmentChangedListener, Constants {

	private ImageButton sidemenuBtn;
	private ImageButton searchBtn;

	private SegmentLayout mainSegment;
	private FrameLayout fragmentContainer;

	private Segment homeSegment;
	private Segment movieSegment;
	private Segment tvSegment;
	private Segment aniSegment;
	private Segment premiumSegment;
	private Segment adultSegment;
	
	private ProgressDialog loadingDialog;

	private MainViewCallback callback;

	public void setCallback(MainViewCallback callback) {
		this.callback = callback;
	}

	public int getFragmentContainerId() {
		return fragmentContainer.getId();
	}

	public void initSegment() {
		mainSegment.setCurrentSegment(0);
	}

	public void setMenuVisiblity(String[] hideMenus) {
		aniSegment.show();
		adultSegment.show();
		premiumSegment.show();
		movieSegment.show();
		tvSegment.show();

		if (hideMenus == null)
			return;

		for (String s : hideMenus) {
			if (EXTRA_MENU_ADULT.equals(s)) {
				adultSegment.hide();
			} else if (EXTRA_MENU_KIDS.equals(s)) {
				aniSegment.hide();
			} else if (EXTRA_MENU_MONTH.equals(s)) {
				premiumSegment.hide();
			} else if (EXTRA_MENU_MOVIE.equals(s)) {
				movieSegment.hide();
			} else if (EXTRA_MENU_TV.equals(s)) {
				tvSegment.hide();
			}
		}
	}
	
	public void setAdultVisiblity(boolean adult) {
		if (adult)
			adultSegment.show();
		else
			adultSegment.hide();
	}
	
	public void showLoading() {
		if (!loadingDialog.isShowing())
			loadingDialog.show();
	}
	
	public void hideLoading() {
		if (loadingDialog.isShowing())
			loadingDialog.dismiss();
	}

	public void showDebugMode() {
		Toast.makeText(getContext(),
				"테스트 모드로 접속하셨습니다. 실제 서비스 되는 앱과 다르게 동작할 수 있습니다.",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		sidemenuBtn = (ImageButton) findViewById(R.id.btn_home_sidemenu);
		sidemenuBtn.setOnClickListener(this);
		searchBtn = (ImageButton) findViewById(R.id.btn_home_search);
		searchBtn.setOnClickListener(this);

		mainSegment = (SegmentLayout) findViewById(R.id.segment_home);
		mainSegment.setOnSegmentChangedListener(this);
		mainSegment.setSegments(createSegments());
		mainSegment.setSegmentsMargin(7);

		fragmentContainer = (FrameLayout) findViewById(R.id.layout_home_fragmentcontainer);
		
		loadingDialog = new ProgressDialog(getContext());
		loadingDialog.setCancelable(false);
		loadingDialog.setMessage(getContext().getString(R.string.loading));
	}

	private ArrayList<Segment> createSegments() {
		homeSegment = Segment.newSegment(
				getSegmentView(R.drawable.bt_main_home), SEGMENT_TAG_HOME);
		movieSegment = Segment.newSegment(
				getSegmentView(R.drawable.bt_main_movie), SEGMENT_TAG_MOVIE);
		tvSegment = Segment.newSegment(getSegmentView(R.drawable.bt_main_tv),
				SEGMENT_TAG_TV);
		aniSegment = Segment.newSegment(getSegmentView(R.drawable.bt_main_ani),
				SEGMENT_TAG_ANI);
		premiumSegment = Segment
				.newSegment(getSegmentView(R.drawable.bt_main_premium),
						SEGMENT_TAG_PREMIUM);
		adultSegment = Segment.newSegment(
				getSegmentView(R.drawable.bt_main_adult), SEGMENT_TAG_ADULT);

		ArrayList<Segment> result = new ArrayList<Segment>();
		result.add(homeSegment);
		result.add(movieSegment);
		result.add(tvSegment);
		result.add(aniSegment);
		result.add(premiumSegment);
		result.add(adultSegment);

		return result;
	}

	private CheckedTextView getSegmentView(int resId) {
		CheckedTextView ch = new CheckedTextView(getContext());
		ch.setBackgroundResource(resId);
		return ch;
	}

	@Override
	public void onSegmentChanged(SegmentLayout segment, String tag) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (tag.equals(SEGMENT_TAG_ADULT))
			callback.onAdultSegmentClick(this);
		else if (tag.equals(SEGMENT_TAG_ANI))
			callback.onAniSegmentClick(this);
		else if (tag.equals(SEGMENT_TAG_HOME))
			callback.onHomeSegmentClick(this);
		else if (tag.equals(SEGMENT_TAG_MOVIE))
			callback.onMovieSegmentClick(this);
		else if (tag.equals(SEGMENT_TAG_PREMIUM))
			callback.onPremiumSegmentClick(this);
		else if (tag.equals(SEGMENT_TAG_TV))
			callback.onTVSegmentClick(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(sidemenuBtn))
			callback.onSidemenuClick(this);
		else if (v.equals(searchBtn))
			callback.onSearchClick(this);
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_home;
	}

	public MainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MainView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

}
