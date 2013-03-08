package com.busan.cw.clsp20120924.view;

import view.CCView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.busan.cw.clsp20120924_beta.R;
import com.busan.cw.clsp20120924.interfaces.MovieSearchViewCallback;

import extend.ClearableEditText;

public class MovieSearchView extends CCView implements TextWatcher,
		OnClickListener, OnItemClickListener {

	private ClearableEditText searchQueryEdit;
	private ImageButton searchBtn;
	private ImageButton voiceBtn;
	private ListView searchResultList;

	private MovieSearchViewCallback callback;

	public void setCallback(MovieSearchViewCallback callback) {
		this.callback = callback;
	}

	public String getSearchQuery() {
		return searchQueryEdit.getText().toString();
	}

	public void setSearchQuery(CharSequence query) {
		searchQueryEdit.setText(query);
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		searchQueryEdit = (ClearableEditText) findViewById(R.id.edit_search);
		searchQueryEdit.addTextChangedListener(this);
		searchQueryEdit.setClearIcon(R.drawable.bt_del_idpw);
		searchQueryEdit.setSingleLine();
		searchBtn = (ImageButton) findViewById(R.id.btn_search);
		searchBtn.setOnClickListener(this);
		voiceBtn = (ImageButton) findViewById(R.id.btn_search_voice);
		voiceBtn.setOnClickListener(this);
		searchResultList = (ListView) findViewById(R.id.list_search_result);
		searchResultList.setOnItemClickListener(this);
	}

	public void showVoiceResult(final String[] result) {
		AlertDialog ad = new AlertDialog.Builder(getContext()).setItems(result,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if (callback != null)
							callback.onVoiceResultItemClick(
									MovieSearchView.this, result[which]);
					}
				}).create();
		ad.show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (arg0.equals(searchResultList))
			callback.onSearchResultItemClick(this, arg2);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(searchBtn))
			callback.onSearchClick(this);
		else if (v.equals(voiceBtn))
			callback.onVoiceClick(this);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		callback.onSearchQueryChanged(this, s);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// do nothing

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// do nothing

	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_search;
	}

	public MovieSearchView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MovieSearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MovieSearchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

}
