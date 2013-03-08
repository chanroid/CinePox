package com.busan.cw.clsp20120924.view;

import java.util.ArrayList;

import utils.DisplayUtils;
import view.CCView;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busan.cw.clsp20120924.interfaces.MovieListViewCallback;
import com.busan.cw.clsp20120924.model.MovieItemData;
import com.busan.cw.clsp20120924.model.SimplePagingData;
import com.busan.cw.clsp20120924.model.SortMenuItemData;
import com.busan.cw.clsp20120924_beta.R;

import extend.SegmentLayout;
import extend.SegmentLayout.OnSegmentChangedListener;
import extend.SegmentLayout.Segment;

/**
 * 영화목록 페이지 뷰
 * 
 * 카테고리 목록 다이얼로그 띄우는 부분, 페이저별로 리스트뷰 세팅하는 부분 추가로 작업 필요
 * 
 * @author CINEPOX
 * 
 */
public class MovieListView extends CCView implements OnSegmentChangedListener,
		OnItemClickListener, OnClickListener,
		android.content.DialogInterface.OnClickListener, AnimationListener {

	private LinearLayout menuContainer;
	private LinearLayout menuLayout;

	private ImageButton menuBtn;

	private Animation menuShowAni;
	private Animation menuHideAni;

	private ListView movielistView;
	private SegmentLayout movielistFooterPageSegment;

	private MovieListAdapter lastAdapter;

	private RelativeLayout pagerNaviLayout;
	private ImageButton pagerPrevBtn;
	private ImageButton pagerNextBtn;
	private TextView pagerIndexText;
	private ProgressBar progress;

	private MovieListViewCallback callback;

	private boolean autoloadPage = true;

	public void setCallback(MovieListViewCallback callback) {
		this.callback = callback;
	}

	public void setMovieList(ArrayList<MovieItemData> array) {
		MovieListAdapter adapter = new MovieListAdapter(getContext(),
				R.layout.listitem_movielist, array);
		movielistView.setAdapter(adapter);
		adapter.notifyDataSetInvalidated();
		if (lastAdapter != null)
			lastAdapter.getImageLoader().clearMemory();
		lastAdapter = adapter;
	}

	public void setPageNumInfo(int current, int last) {
		pagerIndexText.setText(current + " / " + last);
		// 첫페이지, 마지막페이지 여부에 따라 버튼 보임/숨김 처리
		pagerPrevBtn.setVisibility(current == 1 ? GONE : VISIBLE);
		pagerNextBtn.setVisibility(current == last ? GONE : VISIBLE);
	}

	public void setPagingInfo(ArrayList<SimplePagingData> pagingInfo,
			boolean last) {
		autoloadPage = true;
		movielistFooterPageSegment.removeAllViewsInLayout();
		ArrayList<Segment> segments = new ArrayList<SegmentLayout.Segment>();

		if (pagingInfo.get(0).getPageNum() > 5) {
			Segment s = Segment.newSegment(getPagingArrowButtonView("<"),
					"prev");
			segments.add(s);
		}

		for (SimplePagingData data : pagingInfo) {
			Segment s = Segment.newSegment(getPagingButtonView(data),
					String.valueOf(data.getPageNum()));
			segments.add(s);
		}

		if (!last) {
			Segment s = Segment.newSegment(getPagingArrowButtonView(">"),
					"next");
			segments.add(s);
		}

		movielistFooterPageSegment.setSegments(segments);
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		menuShowAni = AnimationUtils.loadAnimation(getContext(),
				com.fedorvlasov.lazylist.R.anim.thumb_fade_in);
		menuShowAni.setAnimationListener(this);
		menuHideAni = AnimationUtils.loadAnimation(getContext(),
				com.fedorvlasov.lazylist.R.anim.thumb_fade_out);
		menuHideAni.setAnimationListener(this);

		menuContainer = (LinearLayout) findViewById(R.id.layout_movielist_menu_container);
		menuLayout = (LinearLayout) findViewById(R.id.layout_movielist_menu_layout);

		menuBtn = (ImageButton) findViewById(R.id.btn_movielist_plusmenu);
		menuBtn.setOnClickListener(this);

		movielistView = (ListView) findViewById(R.id.pager_movielist_listpage);
		movielistView.setOnItemClickListener(this);

		movielistFooterPageSegment = new SegmentLayout(getContext());
		int padding = (int) DisplayUtils.applyDimension(20, getContext());
		movielistFooterPageSegment.setPadding(padding / 2, padding / 2,
				padding, padding);
		movielistFooterPageSegment.setGravity(Gravity.CENTER);
		movielistFooterPageSegment.setSegmentsMargin(5);
		movielistFooterPageSegment.setOnSegmentChangedListener(this);

		pagerNaviLayout = (RelativeLayout) inflate(getContext(),
				R.layout.listitem_movielist_footer_page_navigation, null);

		pagerPrevBtn = (ImageButton) pagerNaviLayout
				.findViewById(R.id.btn_movielist_pager_prev);
		pagerPrevBtn.setOnClickListener(this);
		pagerNextBtn = (ImageButton) pagerNaviLayout
				.findViewById(R.id.btn_movielist_pager_next);
		pagerNextBtn.setOnClickListener(this);
		pagerIndexText = (TextView) pagerNaviLayout
				.findViewById(R.id.text_movielist_pager_num);

		progress = (ProgressBar) findViewById(R.id.progress_movielist_loading);

		movielistView.addFooterView(movielistFooterPageSegment);
		movielistView.addFooterView(pagerNaviLayout);
	}

	public void showLoading() {
		movielistView.setVisibility(INVISIBLE);
		progress.setVisibility(VISIBLE);
	}

	public void hideLoading() {
		movielistView.setVisibility(VISIBLE);
		progress.setVisibility(INVISIBLE);
	}

	public void showMenu() {
		menuLayout.setVisibility(INVISIBLE);
		menuLayout.startAnimation(menuShowAni);
	}

	public void hideMenu() {
		menuLayout.startAnimation(menuHideAni);
	}

	public void toggleMenu() {
		if (menuLayout.getVisibility() == VISIBLE)
			hideMenu();
		else
			showMenu();
	}

	private CheckedTextView getPagingButtonView(SimplePagingData data) {
		CheckedTextView btn = new CheckedTextView(getContext());
		int padding = (int) DisplayUtils.applyDimension(10, getContext());
		btn.setPadding(padding, padding / 2, padding, padding / 2);
		btn.setText(String.valueOf(data.getPageNum()));
		if (data.isSelected()) {
			btn.setBackgroundResource(R.drawable.bt_movielist_paging_on);
			btn.setTextColor(Color.WHITE);
		} else {
			btn.setBackgroundResource(R.drawable.bt_movielist_paging_off);
			btn.setTextColor(Color.parseColor("#bbbbbb"));
		}
		return btn;
	}

	private CheckedTextView getPagingArrowButtonView(String arrow) {
		CheckedTextView btn = new CheckedTextView(getContext());
		int padding = (int) DisplayUtils.applyDimension(10, getContext());
		btn.setPadding(padding, padding / 2, padding, padding / 2);
		btn.setText(arrow);
		btn.setBackgroundResource(R.drawable.bt_movielist_paging_off);
		btn.setTextColor(Color.parseColor("#bbbbbb"));
		return btn;
	}

	public void addMenu(final SortMenuItemData... param) {
		for (final SortMenuItemData data : param) {
			SortMenuItemView view = new SortMenuItemView(getContext());
			view.setData(data);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (callback == null)
						return;

					callback.onSortItemClick(MovieListView.this, data.tag);
				}
			});
			menuContainer.addView(view);

			ImageView line = new ImageView(getContext());
			line.setImageResource(R.drawable.line_movielist_menu);
			menuContainer.addView(line, -1, -1);
		}
	}

	public void clearMenu() {
		menuContainer.removeAllViewsInLayout();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		callback.onMovieListItemClick(this, arg2);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		dialog.dismiss();
		if (callback == null)
			return;

		// 어떤 다이얼로그인지 분기해서 콜백 구현해 주어야 함
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(pagerPrevBtn)) {
			callback.onPrevPageClick(this);
		} else if (v.equals(pagerNextBtn)) {
			callback.onNextPageClick(this);
		} else if (v.equals(menuBtn))
			toggleMenu();
	}

	@Override
	public void onSegmentChanged(SegmentLayout segment, String tag) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (segment.equals(movielistFooterPageSegment)) {
			// 태그가 정수형이어야 함
			if (!autoloadPage) {
				if ("prev".equals(tag))
					callback.onPrevPageGroupClick(this);
				else if ("next".equals(tag))
					callback.onNextPageGroupClick(this);
				else
					callback.onPageClick(this, Integer.parseInt(tag));
			} else
				autoloadPage = false;
		}
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_movielist;
	}

	public MovieListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MovieListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MovieListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if (animation.equals(menuShowAni))
			menuLayout.setVisibility(VISIBLE);
		else if (animation.equals(menuHideAni))
			menuLayout.setVisibility(GONE);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

}
