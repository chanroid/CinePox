package com.busan.cw.clsp20120924.controller;

import java.util.ArrayList;

import utils.ThemeUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import com.busan.cw.clsp20120924.interfaces.MovieSearchViewCallback;
import com.busan.cw.clsp20120924.view.MovieSearchView;

public class MovieSearchActivity extends Activity implements
		MovieSearchViewCallback {

	private MovieSearchView view;

	private static final int REQUEST_RECOGNIZER = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ThemeUtils.transParent(this);
		super.onCreate(savedInstanceState);
		view = new MovieSearchView(this);
		view.setCallback(this);
		setContentView(view);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_RECOGNIZER) {
				ArrayList<String> resultArray = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				String[] result = new String[resultArray.size()];
				resultArray.toArray(result);
				view.showVoiceResult(result);
			}
		}
	}

	@Override
	public void onSearchQueryChanged(MovieSearchView view, CharSequence query) {
		// 스마트검색 실행

	}

	@Override
	public void onSearchClick(MovieSearchView view) {
		// 검색결과 리스트 보여주기

	}

	@Override
	public void onVoiceClick(MovieSearchView view) {
		// TODO Auto-generated method stub
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
		i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
		i.putExtra(RecognizerIntent.EXTRA_PROMPT, "말을 하세요.");
		startActivityForResult(i, REQUEST_RECOGNIZER);
	}

	@Override
	public void onSearchResultItemClick(MovieSearchView view, int position) {
		// 어댑터에서 결과 가져와서 검색어로 적용
		// model 작성 완료되면 작업
		
	}

	@Override
	public void onVoiceResultItemClick(MovieSearchView view, String selectedItem) {
		// TODO Auto-generated method stub
		view.setSearchQuery(selectedItem);
	}
}
