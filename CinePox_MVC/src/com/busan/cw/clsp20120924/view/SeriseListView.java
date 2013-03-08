package com.busan.cw.clsp20120924.view;

import view.CCView;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.busan.cw.clsp20120924.interfaces.SeriseListViewCallback;
import com.busan.cw.clsp20120924_beta.R;

/**
 * 영화상세정보에서 사용하는 시리즈 목록 뷰
 * 
 * @author CINEPOX
 * 
 */
public class SeriseListView extends CCView implements OnClickListener,
		OnPageChangeListener, OnItemClickListener {

	private TextView showAllBtn;
	private TextView orderBtn;

	private ViewPager seriseListPager;

	private ImageButton nextPageBtn;
	private ImageButton prevPageBtn;
	private TextView pageNumText;

	private SeriseListViewCallback callback;

	private int lastPageIndex;

	public void setCallback(SeriseListViewCallback callback) {
		this.callback = callback;
	}

	// 모델 완성되면 구현

	public void setPageNumInfo(int current, int last) {
		lastPageIndex = last;
		pageNumText.setText(current + " / " + last);
		// 첫페이지, 마지막페이지 여부에 따라 버튼 보임/숨김 처리
		prevPageBtn.setVisibility(current == 1 ? GONE : VISIBLE);
		nextPageBtn.setVisibility(current == last ? GONE : VISIBLE);
	}

	public SeriseListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SeriseListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SeriseListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_seriselist;
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		showAllBtn = (TextView) findViewById(R.id.btn_moviedetail_serise_all);
		showAllBtn.setOnClickListener(this);
		orderBtn = (TextView) findViewById(R.id.btn_moviedetail_serise_order);
		orderBtn.setOnClickListener(this);

		seriseListPager = (ViewPager) findViewById(R.id.pager_moviedetail_serises);
		seriseListPager.setOnPageChangeListener(this);

		nextPageBtn = (ImageButton) findViewById(R.id.btn_moviedetail_pager_next);
		nextPageBtn.setOnClickListener(this);
		prevPageBtn = (ImageButton) findViewById(R.id.btn_moviedetail_pager_prev);
		prevPageBtn.setOnClickListener(this);
		pageNumText = (TextView) findViewById(R.id.text_moviedetail_pager_num);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		// 데이터로 갖고있는 번호와 프로그램에서 사용하는 번호가 다름
		callback.onSeriseItemClick(this, seriseListPager.getCurrentItem() + 1,
				arg2);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// do nothing
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// do nothing
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		setPageNumInfo(arg0 + 1, lastPageIndex);
		callback.onSerisePageChanged(this, arg0 + 1);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(showAllBtn))
			callback.onShowAllClick(this);
		else if (v.equals(orderBtn))
			callback.onOrderClick(this);
		else if (v.equals(nextPageBtn))
			callback.onNextClick(this);
		else if (v.equals(prevPageBtn))
			callback.onPrevClick(this);

	}

}
