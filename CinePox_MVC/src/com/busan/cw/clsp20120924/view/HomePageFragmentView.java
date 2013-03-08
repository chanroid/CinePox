package com.busan.cw.clsp20120924.view;

import static com.busan.cw.clsp20120924.interfaces.HomePageFragmentViewCallback.SEGMENT_TAG_EVENT;
import static com.busan.cw.clsp20120924.interfaces.HomePageFragmentViewCallback.SEGMENT_TAG_RECOMMEND;
import static com.busan.cw.clsp20120924.interfaces.HomePageFragmentViewCallback.SEGMENT_TAG_SNSREVIEW;

import java.util.ArrayList;

import utils.DisplayUtils;
import view.CCView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.busan.cw.clsp20120924_beta.R;
import com.busan.cw.clsp20120924.interfaces.HomePageFragmentViewCallback;
import com.busan.cw.clsp20120924.model.Top100Data;

import extend.SegmentLayout;
import extend.SegmentLayout.OnSegmentChangedListener;
import extend.SegmentLayout.Segment;

@SuppressWarnings("deprecation")
public class HomePageFragmentView extends CCView implements
		OnItemClickListener, OnSegmentChangedListener {

	private TextView top100num;
	private TextView top100title;
	private ImageView top100arrow;
	private TextView top100changed;

	private TextView gallery1title;
	private Gallery gallery1;
	private TextView gallery2title;
	private Gallery gallery2;

	private SegmentLayout segment;

	private SNSReviewView snsreviewView;
	private Gallery eventGallery;
	private Gallery relatedGallery;

	private HomePageFragmentViewCallback callback;

	public HomePageFragmentView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public HomePageFragmentView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public HomePageFragmentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setTop100Data(Top100Data data) {
		top100num.setText(data.getNum());
		top100title.setText(data.getTitle());
		top100arrow.setImageResource(data.getChanged() > 0 ? R.drawable.img_up
				: R.drawable.img_down);
		if (data.getChanged() == 0)
			top100arrow.setImageDrawable(null);
		top100changed.setText(data.getChanged() + "");
	}

	public void setGallery1Title(String title) {
		gallery1title.setText(title);
	}

	public void setGallery1Adapter(BaseAdapter adapter) {
		gallery1.setAdapter(adapter);
	}

	public void setGallery2Title(String title) {
		gallery2title.setText(title);
	}

	public void setGallery2Adapter(BaseAdapter adapter) {
		gallery2.setAdapter(adapter);
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_homepage;
	}

	public void setCallback(HomePageFragmentViewCallback callback) {
		this.callback = callback;
		snsreviewView.setCallback(callback);
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		top100num = (TextView) findViewById(R.id.text_home_top100_num);
		top100title = (TextView) findViewById(R.id.text_home_top100_title);
		top100arrow = (ImageView) findViewById(R.id.img_home_top100_arrow);
		top100changed = (TextView) findViewById(R.id.text_home_top100_changed);

		gallery1title = (TextView) findViewById(R.id.text_home_list1_title);
		gallery1 = (Gallery) findViewById(R.id.gallery_home_list1);
		gallery1.setOnItemClickListener(this);
		gallery2title = (TextView) findViewById(R.id.text_home_list2_title);
		gallery2 = (Gallery) findViewById(R.id.gallery_home_list2);
		gallery2.setOnItemClickListener(this);

		snsreviewView = (SNSReviewView) findViewById(R.id.view_home_snsreview);
		eventGallery = (Gallery) findViewById(R.id.gallery_home_event);
		eventGallery.setOnItemClickListener(this);
		relatedGallery = (Gallery) findViewById(R.id.gallery_home_related);
		relatedGallery.setOnItemClickListener(this);

		segment = (SegmentLayout) findViewById(R.id.segment_home);
		segment.setOnSegmentChangedListener(this);
		segment.setSegments(createSegments());
		segment.setSegmentsMargin(10);

	}

	private ArrayList<Segment> createSegments() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
		params.weight = 1.f;
		Segment snsSegment = Segment.newSegment(
				getSegmentView(R.string.home_snsreview), SEGMENT_TAG_SNSREVIEW);
		snsSegment.setLayoutParams(params);
		
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(-2, -2);
		params1.weight = 1.f;
		Segment eventSegment = Segment.newSegment(
				getSegmentView(R.string.home_event), SEGMENT_TAG_EVENT);
		eventSegment.setLayoutParams(params1);
		
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(-2, -2);
		params2.weight = 1.f;
		Segment recommendSegment = Segment.newSegment(
				getSegmentView(R.string.home_recommend), SEGMENT_TAG_RECOMMEND);
		recommendSegment.setLayoutParams(params2);

		ArrayList<Segment> result = new ArrayList<Segment>();
		result.add(snsSegment);
		result.add(eventSegment);
		result.add(recommendSegment);

		return result;
	}

	private CheckedTextView getSegmentView(int textid) {
		// TextView tab = new TextView(getContext());
		// tab.setText(textid);
		// tab.setBackgroundResource(R.drawable.img_tab);
		// tab.setGravity(Gravity.CENTER);
		// return tab;
		CheckedTextView view = new CheckedTextView(getContext());
		view.setText(textid);
		view.setBackgroundResource(R.drawable.img_tab);
		view.setTextSize(DisplayUtils.applyScale(7, getContext()));
		view.setGravity(Gravity.CENTER);
		int padding = (int) DisplayUtils.applyDimension(7, getContext());
		view.setPadding(0, padding, 0, padding);
		return view;
	}

	@Override
	public void onSegmentChanged(SegmentLayout segment, String tag) {
		snsreviewView.setVisibility(SEGMENT_TAG_SNSREVIEW.equals(tag) ? VISIBLE
				: GONE);
		eventGallery.setVisibility(SEGMENT_TAG_EVENT.equals(tag) ? VISIBLE
				: GONE);
		relatedGallery
				.setVisibility(SEGMENT_TAG_RECOMMEND.equals(tag) ? VISIBLE
						: GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		if (arg0.equals(gallery1))
			callback.onGallery1ItemClick(this, arg2);
		else if (arg0.equals(gallery2))
			callback.onGallery2ItemClick(this, arg2);
		else if (arg0.equals(eventGallery))
			callback.onEventItemClick(this, arg2);
		else if (arg0.equals(relatedGallery))
			callback.onRelatedItemClick(this, arg2);
	}
}
