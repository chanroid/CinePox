package com.busan.cw.clsp20120924.view;

import java.util.ArrayList;

import view.CCView;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.interfaces.MainViewCallback;
import com.busan.cw.clsp20120924.structs.BestItemData;
import com.busan.cw.clsp20120924.structs.CategoryItemData;
import com.busan.cw.clsp20120924.structs.MainBannerData;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

public class MainView extends CCView {

	private PullToRefreshGridView mPullGrid;
	private GridView mMovieGrid;
	private LinearLayout mCategoryBtnContainer;

	private ImageButton mBestBtn;
	private ImageButton mMovieBtn;
	private ImageButton mTVBtn;
	private ImageButton mMyListBtn;
	private ImageButton mQRPlayBtn;
	private ImageButton mSearchBtn;
	private ImageButton mBannerBtn;
	private TextView prevCategoryBtn;

	private MainViewCallback mCallback;

	public MainView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.main;
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		mPullGrid = (PullToRefreshGridView) findViewById(R.id.grid_main_best);
		mPullGrid.setLoadingDrawable(getResources().getDrawable(R.drawable.img_loading));
		mPullGrid.setEmptyView(getEmptyView());
		mMovieGrid = mPullGrid.getRefreshableView();
		mCategoryBtnContainer = (LinearLayout) findViewById(R.id.layout_best_btncontainer);
		mBestBtn = (ImageButton) findViewById(R.id.btn_main_best);
		mMovieBtn = (ImageButton) findViewById(R.id.btn_main_movie);
		mTVBtn = (ImageButton) findViewById(R.id.btn_main_video);
		mMyListBtn = (ImageButton) findViewById(R.id.btn_main_mylist);
		mQRPlayBtn = (ImageButton) findViewById(R.id.btn_main_qrplay);
		mSearchBtn = (ImageButton) findViewById(R.id.btn_main_search);
		mBannerBtn = (ImageButton) findViewById(R.id.btn_main_premium);
	}
	
	private View getEmptyView() {
		LinearLayout layout = new LinearLayout(getContext());
		layout.setGravity(Gravity.CENTER);
		ImageView bar = new ImageView(getContext());
		bar.setImageDrawable(getResources().getDrawable(R.drawable.img_loading));
		Interpolator interpolator = new LinearInterpolator();
		RotateAnimation mRotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setInterpolator(interpolator);
		mRotateAnimation.setDuration(600);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		mRotateAnimation.setRepeatMode(Animation.RESTART);
		bar.startAnimation(mRotateAnimation);
		layout.addView(bar);
		return layout;
	}

	public void setCallback(MainViewCallback cb) {
		mCallback = cb;
		mPullGrid.setOnRefreshListener(cb);
		mMovieGrid.setOnItemClickListener(cb);
		mBestBtn.setOnClickListener(cb);
		mMovieBtn.setOnClickListener(cb);
		mTVBtn.setOnClickListener(cb);
		mMyListBtn.setOnClickListener(cb);
		mQRPlayBtn.setOnClickListener(cb);
		mSearchBtn.setOnClickListener(cb);
		mBannerBtn.setOnClickListener(cb);
	}

	public void setBanner(MainBannerData data) {
		mBannerBtn.setImageBitmap(data.banner);
	}
	
	public void onRefreshComplete() {
		mPullGrid.onRefreshComplete();
	}

	public void setMovieList(ArrayList<BestItemData> array) {
		if (array == null)
			return;
		MainBestAdapter adapter = new MainBestAdapter(getContext(),
				R.layout.movie_item, array);
		mMovieGrid.setAdapter(adapter);
	}

	public void setCategory(ArrayList<CategoryItemData> array) {
		mCategoryBtnContainer.removeAllViews();
		LinearLayout.LayoutParams categoryParam = new LinearLayout.LayoutParams(
				-2, -1);
		categoryParam.weight = 1.0f;
		int resid = R.drawable.bg_tab_left;
		int color = Color.YELLOW;
		TextView categoryBtn = null;
		for (final CategoryItemData s : array) {
			categoryBtn = new TextView(getContext());
			if (prevCategoryBtn == null)
				prevCategoryBtn = categoryBtn;
			if (Build.VERSION.SDK_INT >= 16)
				categoryBtn.setBackground(getResources().getDrawable(resid));
			else
				categoryBtn.setBackgroundResource(resid);
			categoryBtn.setGravity(Gravity.CENTER);
			categoryBtn.setText(s.title);
			categoryBtn.setLines(1);
			categoryBtn.setEms(7);
			categoryBtn.setTextColor(color);
			categoryBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			categoryBtn.setClickable(true);
			categoryBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (prevCategoryBtn != null)
						prevCategoryBtn.setTextColor(Color.WHITE);
					((TextView) v).setTextColor(Color.YELLOW);
					prevCategoryBtn = (TextView) v;
					mCallback.onCategoryChanged(s);
				}
			});
			resid = R.drawable.bg_tab_1px;
			mCategoryBtnContainer.addView(categoryBtn, categoryParam);
			color = Color.WHITE;
		}
		if (categoryBtn != null)
			categoryBtn.setBackgroundResource(R.drawable.bg_tab_right);
	}

}
