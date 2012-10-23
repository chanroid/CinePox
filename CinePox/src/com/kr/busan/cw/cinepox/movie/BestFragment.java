package com.kr.busan.cw.cinepox.movie;

import java.util.List;
import java.util.Map;

import kr.co.chan.util.R;
import kr.co.chan.util.Util;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.kr.busan.cw.cinepox.movie.BestParser.ParserCallback;

public class BestFragment extends Fragment implements OnItemClickListener,
		OnClickListener, ParserCallback, OnRefreshListener {

	LinearLayout mRoot;
	LinearLayout mContainer;
	// ProgressBar mProgress;
	PullToRefreshGridView mGridBase;
	GridView mGrid;
	TextView prevText;

	List<Map<String, String>> mDataArray;

	BestParser mParser;
	BestAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT > 11) {
			getActivity().setTheme(android.R.style.Theme_Holo_NoActionBar);
		}
		super.onCreate(savedInstanceState);
		mRoot = (LinearLayout) getLayoutInflater(getArguments()).inflate(
				R.layout.main_best, null);
		mContainer = (LinearLayout) mRoot
				.findViewById(com.kr.busan.cw.cinepox.R.id.layout_best_btncontainer);
		// mProgress = (ProgressBar) mRoot.findViewById(
		// com.kr.busan.cw.cinepox.R.id.layout_best_gridcontainer)
		// .findViewById(com.kr.busan.cw.cinepox.R.id.progress_main_best);
		mGridBase = (PullToRefreshGridView) mRoot.findViewById(
				com.kr.busan.cw.cinepox.R.id.layout_best_gridcontainer)
				.findViewById(com.kr.busan.cw.cinepox.R.id.grid_main_best);
		// Util.Views.setClickListeners(mFooter, this);
		mGridBase.setOnRefreshListener(this);
		mGridBase.setMode(Mode.PULL_DOWN_TO_REFRESH);
		mGridBase.setLoadingDrawable(getResources().getDrawable(
				com.kr.busan.cw.cinepox.R.drawable.img_loading));
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setGravity(Gravity.CENTER);
		ImageView bar = new ImageView(getActivity());
		bar.setImageDrawable(getResources().getDrawable(
				com.kr.busan.cw.cinepox.R.drawable.img_loading));
		final Interpolator interpolator = new LinearInterpolator();
		RotateAnimation mRotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setInterpolator(interpolator);
		mRotateAnimation.setDuration(600);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		mRotateAnimation.setRepeatMode(Animation.RESTART);
		bar.startAnimation(mRotateAnimation);
		layout.addView(bar);
		mGridBase.setEmptyView(layout);
		mGrid = mGridBase.getRefreshableView();
		mGrid.setPadding(0, 0, 0,
				(int) Util.Views.applyDimension(20, getActivity()));
		mGrid.setOnItemClickListener(this);
		initList();
	}

	@Override
	public void onDestroy() {
		if (mParser.getStatus() == AsyncTask.Status.RUNNING)
			mParser.cancel(true);
		if (mAdapter != null)
			mAdapter.destroy();
		super.onDestroy();
	};

	Config getConfig() {
		return Config.getInstance(getActivity());
	}

	@TargetApi(16)
	void initList() {
		mContainer.removeAllViews();
		getConfig().setCurrentCategory(getConfig().getCategoryUrl(0));
		TextView btn = null;
		// Button btn = null;
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(-2, -1);
		param.weight = 1.0f;
		int resid = com.kr.busan.cw.cinepox.R.drawable.bg_tab_left;
		int color = Color.YELLOW;
		for (String s : getConfig().getCategoryNames()) {
			// s = s.substring(0, 8);
			btn = new TextView(getActivity());
			if (prevText == null)
				prevText = btn;
			if (Build.VERSION.SDK_INT >= 16)
				btn.setBackground(getResources().getDrawable(resid));
			else
				btn.setBackgroundResource(resid);
			btn.setGravity(Gravity.CENTER);
			btn.setText(s);
			btn.setLines(1);
			btn.setEms(7);
			btn.setTextColor(color);
			btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			btn.setClickable(true);
			btn.setOnClickListener(this);
			resid = com.kr.busan.cw.cinepox.R.drawable.bg_tab_1px;
			mContainer.addView(btn, param);
			color = Color.WHITE;
		}
		if (btn != null)
			btn.setBackgroundResource(com.kr.busan.cw.cinepox.R.drawable.bg_tab_right);
		refreshList();
	}

	void refreshList() {
		mParser = new BestParser(getActivity(), getConfig()
				.getCurrentCategory());
		mParser.callback = this;
		mParser.execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return mRoot;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (prevText != null)
			prevText.setTextColor(Color.WHITE);
		TextView btn = (TextView) v;
		btn.setTextColor(Color.YELLOW);
		prevText = btn;
		// Button btn = (Button) v;
		if (mDataArray != null)
			mDataArray.clear();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
			mAdapter.destroy();
		}
		getConfig().setCurrentCategory(
				getConfig().getCategoryUrl(
						getConfig().getCategoryNames().indexOf(
								btn.getText().toString())));
		refreshList();
	}

	@Override
	public void onStart(BestParser parser) {
		// TODO Auto-generated method stub
		// mProgress.setVisibility(View.VISIBLE);
	}

	@Override
	public void onComplete(BestParser parser) {
		// TODO Auto-generated method stub
		// mProgress.setVisibility(View.GONE);
		mDataArray = parser.getResult();
		if (mDataArray == null || mDataArray.size() <= 0)
			refreshList();
		else {
			mAdapter = new BestAdapter(getActivity(), mDataArray,
					com.kr.busan.cw.cinepox.R.layout.movie_item, null, null);
			mGridBase.onRefreshComplete();
			mGrid.setAdapter(mAdapter);
		}
	}

	@Override
	public void onError(BestParser parser, String message) {
		// TODO Auto-generated method stub
		// mProgress.setVisibility(View.GONE);
		mGridBase.onRefreshComplete();
	}

	@Override
	public void onCancel(BestParser parser) {
		// TODO Auto-generated method stub
		// mProgress.setVisibility(View.GONE);
		mGridBase.onRefreshComplete();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (mDataArray == null || mDataArray.size() <= 0)
			refreshList();
		else
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse(String
							.format(Config.WebDomain
									+ "vod/detail.html?SET_DEVICE=android(APP)&movieProduct_seq=%s",
									mDataArray.get(arg2).get(
											Parser.KEY_MOVIE_NUM)))));
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		refreshList();
	}

}
