package com.busan.cw.clsp20120924.view;

import iface.OnItemClickListener;
import view.CCView;
import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.busan.cw.clsp20120924_beta.R;
import com.busan.cw.clsp20120924.interfaces.MovieSynopsisViewCallback;

import extend.ExpandedListView;

public class MovieSynopsisView extends CCView implements OnClickListener,
		OnItemClickListener {

	private TextView synopsisText;
	private TextView synopsisExpandBtn;
	private ExpandedListView actorListView;
	private TextView actorListExpandBtn;

	private MovieSynopsisViewCallback callback;

	private boolean isSynopsisExpanded;

	public void setCallback(MovieSynopsisViewCallback callback) {
		this.callback = callback;
	}

	public void setSynopsisText(String cs) {
		synopsisText.setText(Html.fromHtml(cs));
	}

	// 모델 작성 후 구현
	// 배우 목록

	public MovieSynopsisView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MovieSynopsisView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MovieSynopsisView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		synopsisText = (TextView) findViewById(R.id.text_synopsis_desc);
		synopsisExpandBtn = (TextView) findViewById(R.id.text_synopsis_desc_expand);
		synopsisExpandBtn.setOnClickListener(this);
		actorListView = (ExpandedListView) findViewById(R.id.list_synopsis_actors);
		actorListView.setOnItemClickListener(this);
		actorListExpandBtn = (TextView) findViewById(R.id.text_synopsis_actors_expand);
		synopsisExpandBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (arg0.equals(synopsisExpandBtn)) {
			callback.onSynopsisExpandClick(this);
			// 텍스트 더보기/줄이기
			toggleExpand();
		} else if (arg0.equals(actorListExpandBtn)) {
			callback.onActorListExpandClick(this);
			// 배우목록 더보기/줄이기
			if (actorListView.isExpanded())
				actorListView.collapse(4);
			else
				actorListView.expand();
		}
	}
	
	public void setExpand(boolean expand) {
		if (expand) {
			synopsisText.setMaxLines(4);
			isSynopsisExpanded = false;
		} else {
			synopsisText.setMaxLines(100);
			isSynopsisExpanded = true;
		}
	}
	
	private void toggleExpand() {
		setExpand(isSynopsisExpanded);
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_moviedetail_synopsis;
	}

	@Override
	public void OnItemClick(ExpandedListView view, View viewitem, int position) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (view.equals(actorListView))
			callback.onActorListItemClick(this, position);

	}

}
