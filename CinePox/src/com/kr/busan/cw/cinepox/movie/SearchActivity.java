package com.kr.busan.cw.cinepox.movie;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.co.chan.util.Classes.ClearableEditText;
import kr.co.chan.util.Interfaces.Refreshable;

import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.kr.busan.cw.cinepox.R;
import com.kr.busan.cw.cinepox.movie.SearchParser.ParserCallback;

public class SearchActivity extends Activity implements OnClickListener,
		OnKeyListener, TextWatcher, OnItemClickListener, ParserCallback,
		Refreshable {

	ImageButton mSearchBtn;
	ImageButton mVoiceBtn;
	ClearableEditText mSearchEdit;
	ListView mListView;

	static final String SEARCH_URL = Config.WebDomain
			+ "smart/smart_search.html?q=%s";

	SearchAdapter mAdapter;
	ArrayList<String> mData;
	SearchParser mParser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT < 11)
			setTheme(android.R.style.Theme_Black_NoTitleBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		mListView = (ListView) findViewById(R.id.list_search);
		mListView.setOnItemClickListener(this);
		mSearchBtn = (ImageButton) findViewById(R.id.btn_search_commit);
		mVoiceBtn = (ImageButton) findViewById(R.id.btn_search_voice);
		mSearchEdit = (ClearableEditText) findViewById(R.id.edit_search_form);
		mSearchEdit.textBox.addTextChangedListener(this);
		mSearchEdit.textBox.setOnKeyListener(this);
		mSearchEdit.textBox.setHint("검색어");
		mSearchEdit.textBox.setTextColor(Color.BLACK);
		mSearchEdit.textBox
				.setBackgroundResource(R.drawable.bg_form);
		mSearchEdit.textBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		mSearchEdit.textBox.setSingleLine();
		mSearchBtn.setOnClickListener(this);
		mVoiceBtn.setOnClickListener(this);
		refreshList();

		if (getIntent() != null && getIntent().getData() != null) {
			if ("voice".equalsIgnoreCase(getIntent().getData()
					.getQueryParameter("action"))) {
				startVoiceRecognitionActivity();
			} else if ("search".equalsIgnoreCase(getIntent().getData()
					.getQueryParameter("action"))) {

			}
		}

	}

	void refreshList() {
		if (mParser != null)
			mParser.cancel(true);
		mParser = new SearchParser(mSearchEdit.textBox.getText().toString());
		mParser.callback = this;
		mParser.execute();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_search_commit:
			search();
			break;
		case R.id.btn_search_voice:
			startVoiceRecognitionActivity();
			break;
		case R.id.btn_search_back:
			finish();
			break;
		}
	}

	private void search() {
		if (mSearchEdit.textBox.getText().toString() != null)
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String
						.format(SEARCH_URL, URLEncoder.encode(
								mSearchEdit.textBox.getText().toString(),
								HTTP.UTF_8)))));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			final ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setAdapter(new ArrayAdapter<String>(this,
					R.layout.search_item, R.id.text1, matches),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							mSearchEdit.textBox.setText(matches.get(which));
							search();
						}
					});
			dialog.show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_SEARCH)
			search();
		return false;
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		refreshList();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		mSearchEdit.textBox.setText(mParser.query.get(arg2));
		search();
	}

	@Override
	public void onStart(SearchParser parser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onComplete(SearchParser parser) {
		// TODO Auto-generated method stub
		mData = parser.result;
		refresh(0, 0);
	}

	@Override
	public void onError(SearchParser parser, String message) {
		// TODO Auto-generated method stub
		mData = parser.result;
		refresh(0, -1);
	}

	@Override
	public void onCancel(SearchParser parser) {
		// TODO Auto-generated method stub
		mData = parser.result;
		refresh(0, -1);
	}

	@Override
	public void refresh(int what, int extra) {
		// TODO Auto-generated method stub
		mAdapter = new SearchAdapter(this, R.layout.search_item, mData);
		mListView.setAdapter(mAdapter);
	}

}
