package com.busan.cw.clsp20120924.view;

import view.CCView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.busan.cw.clsp20120924.interfaces.MovieSNSReplyViewCallback;
import com.busan.cw.clsp20120924_beta.R;

import extend.ExpandedListView;

public class MovieSNSReplyView extends CCView implements OnClickListener,
		iface.OnItemClickListener {

	private TextView writeReplyBtn;
	private ExpandedListView replyList;
	private TextView replyExpandBtn;

	private MovieSNSReplyViewCallback callback;

	public void setCallback(MovieSNSReplyViewCallback callback) {
		this.callback = callback;
	}
	
	// 모델 작성 후 구현
	// 댓글 목록 어댑터 설정

	public MovieSNSReplyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MovieSNSReplyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MovieSNSReplyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		writeReplyBtn = (TextView) findViewById(R.id.btn_moviedetail_snsreply);
		writeReplyBtn.setOnClickListener(this);
		replyList = (ExpandedListView) findViewById(R.id.list_moviedetail_snsreply);
		replyList.setOnItemClickListener(this);
		replyExpandBtn = (TextView) findViewById(R.id.btn_moviedetail_snsreply_expand);
		replyExpandBtn.setOnClickListener(this);
	}

	@Override
	public void OnItemClick(ExpandedListView view, View viewitem, int position) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (view.equals(replyList))
			callback.onReplyProfileClick(this, position);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(writeReplyBtn))
			callback.onReplyWriteClick(this);
		else if (v.equals(replyExpandBtn))
			callback.onReplyExpandClick(this);
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_snsreply;
	}

}
