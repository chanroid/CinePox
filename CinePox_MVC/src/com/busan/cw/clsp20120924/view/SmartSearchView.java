package com.busan.cw.clsp20120924.view;

import kr.co.chan.util.Classes.ClearableEditText;
import view.CCView;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ListView;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.interfaces.SearchViewCallback;

public class SmartSearchView extends CCView {
	
	private ImageButton mSearchBtn;
	private ImageButton mVoiceBtn;
	private ClearableEditText mSearchEdit;
	private ListView mListView;
	
	public SmartSearchView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.search;
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		mListView = (ListView) findViewById(R.id.list_search);
		mSearchBtn = (ImageButton) findViewById(R.id.btn_search_commit);
		mVoiceBtn = (ImageButton) findViewById(R.id.btn_search_voice);
		mSearchEdit = (ClearableEditText) findViewById(R.id.edit_search_form);
		mSearchEdit.textBox.setHint("검색어");
		mSearchEdit.textBox.setTextColor(Color.BLACK);
		mSearchEdit.textBox.setBackgroundResource(R.drawable.bg_form);
		mSearchEdit.textBox.setSingleLine();
		mSearchEdit.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI
				| EditorInfo.IME_ACTION_SEARCH);
		mSearchEdit.setPrivateImeOptions("defaultInputmode=korea;"); // 잘 안먹힘 ㅠㅜ
	}

	public void setCallback(SearchViewCallback cb) {
		mListView.setOnItemClickListener(cb);
		mSearchEdit.textBox.addTextChangedListener(cb);
		mSearchEdit.textBox.setOnKeyListener(cb);
		mSearchBtn.setOnClickListener(cb);
		mVoiceBtn.setOnClickListener(cb);
	}

	public void setAdapter(SmartSearchAdapter adapter) {
		mListView.setAdapter(adapter);
	}
	
	public void setText(String text) {
		mSearchEdit.setText(text);
	}
	
	public Editable getText() {
		return mSearchEdit.getText();
	}
}
