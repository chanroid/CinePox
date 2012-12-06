/**
 * 0. Project  : CinePox_MVC
 *
 * 1. FileName : SearchActivity.java
 * 2. Package : com.busan.cw.clsp20120924.controller
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 31. 오후 5:40:30
 * 6. 변경이력 : 
 *		2012. 10. 31. 오후 5:40:30 : 신규
 *
 */
package com.busan.cw.clsp20120924.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.base.Domain;
import com.busan.cw.clsp20120924.interfaces.SearchViewCallback;
import com.busan.cw.clsp20120924.model.MovieDataModel;
import com.busan.cw.clsp20120924.structs.SearchItemData;
import com.busan.cw.clsp20120924.view.SmartSearchAdapter;
import com.busan.cw.clsp20120924.view.SmartSearchView;

import controller.CCActivity;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : SearchActivity.java
 * 3. Package  : com.busan.cw.clsp20120924.controller
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 5:40:30
 * </PRE>
 */
public class SearchActivity extends CCActivity implements SearchViewCallback {

	private MovieDataModel mDataModel;
	private SearchListLoader mDataLoader;
	private SmartSearchView mSearchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mDataModel = (MovieDataModel) loadModel(MovieDataModel.class);
		mSearchView = (SmartSearchView) loadView(SmartSearchView.class);
		mSearchView.setCallback(this);
		setContentView(mSearchView);
	}

	private class SearchListLoader extends
			AsyncTask<String, Integer, ArrayList<SearchItemData>> {

		@Override
		protected ArrayList<SearchItemData> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				mDataModel.loadSearchResult(arg0[0]);
				return mDataModel.getSearchResult();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<SearchItemData> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			refreshList(result);
		}
	}

	private void refreshList(ArrayList<SearchItemData> data) {
		if (data == null)
			return;
		SmartSearchAdapter adapter = new SmartSearchAdapter(this,
				R.layout.search_item, data);
		mSearchView.setAdapter(adapter);
	}

	private void search(String query) {
		if (query != null)
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String
						.format(Domain.WEB_DOMAIN
								+ "smart/smart_search.html?q=%s",
								URLEncoder.encode(query, HTTP.UTF_8)))));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if (mDataLoader != null)
			mDataLoader.cancel(true);
		mDataLoader = new SearchListLoader();
		mDataLoader.execute(s.toString());
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		mSearchView.setText(mDataModel.getSearchResult().get(arg2).fullName);
		search(mSearchView.getText().toString());
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
							mSearchView.setText(matches.get(which));
							search(matches.get(which));
						}
					});
			dialog.show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_search_commit:
			search(mSearchView.getText().toString());
			break;
		case R.id.btn_search_voice:
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			startActivityForResult(intent, 1);
			break;
		case R.id.btn_search_back:
			finish();
			break;
		}
	}

	@Override
	// 검색창 콜백
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_SEARCH)
			search(mSearchView.getText().toString());
		return false;
	}

}
