package com.kr.busan.cw.cinepox.movie;

import static com.kr.busan.cw.cinepox.movie.Constants.KEY_LIST;
import static com.kr.busan.cw.cinepox.movie.Constants.KEY_MOVIE_NUM;
import static com.kr.busan.cw.cinepox.movie.Constants.KEY_TITLE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.chan.util.Util;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;

public class BestParser extends AsyncTask<String, Integer, String> {

	Activity mContext;
	String mUrl;
	List<Map<String, String>> mResult = new ArrayList<Map<String, String>>();
	ParserCallback callback;

	public BestParser(Activity ctx, String url) {
		super();
		mUrl = url;
		mContext = ctx;
		// TODO Auto-generated constructor stub
	}

	public List<Map<String, String>> getResult() {
		return mResult;
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			parseCategory(mUrl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (result == null) {
			if (callback != null)
				callback.onComplete(this);
		} else {
			if (callback != null)
				callback.onError(this, result);
		}
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		if (callback != null)
			callback.onCancel(this);
	}

	@TargetApi(11)
	@Override
	protected void onCancelled(String result) {
		// TODO Auto-generated method stub
		super.onCancelled(result);
		if (callback != null)
			callback.onError(this, "canceled");
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (callback != null)
			callback.onStart(this);
	}

	Config getConfig() {
		return Config.getInstance(mContext);
	}

	State getState() {
		return (State) mContext.getApplication();
	}

	void parseCategory(String url) throws ClientProtocolException, IOException,
			JSONException {
		JSONObject o = Util.Stream.jsonFromURL(url);
		JSONArray list = o.getJSONArray(KEY_LIST);
		mResult = new ArrayList<Map<String, String>>();
		for (int i = 0; i < list.length(); i++) {
			HashMap<String, String> item = parseItem(list.getJSONObject(i));
			mResult.add(item);
		}
	}

	HashMap<String, String> parseItem(JSONObject o) throws JSONException {
		HashMap<String, String> item = new HashMap<String, String>();
		JSONObject ai = o.getJSONObject("postimage");
		item.put(KEY_MOVIE_NUM, o.getString("movieProduct_seq"));
		item.put(KEY_TITLE, o.getString(KEY_TITLE));
		item.put("is_adult", ai.getString("is_adult"));
		item.put("sn_url", ai.getString("sn_url"));
		return item;
	}

	public interface ParserCallback {
		public void onStart(BestParser parser);

		public void onComplete(BestParser parser);

		public void onError(BestParser parser, String message);

		public void onCancel(BestParser parser);
	}
}
