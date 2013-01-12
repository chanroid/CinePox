package com.busan.cw.clsp20120924.movie;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.StringUtils;
import android.annotation.TargetApi;
import android.os.AsyncTask;

public class SearchParser extends AsyncTask<String, Integer, String> {

	public static final String AUTOCOMPLETE_URL = Domain.ACCESS_DOMAIN
			+ "smartSearch/momentSearch/";

	ArrayList<String> result = new ArrayList<String>();
	ArrayList<String> query = new ArrayList<String>();
	ParserCallback callback;
	String mQuery;

	public interface ParserCallback {
		public void onStart(SearchParser parser);

		public void onComplete(SearchParser parser);

		public void onError(SearchParser parser, String message);

		public void onCancel(SearchParser parser);
	}

	public SearchParser(String query) {
		// TODO Auto-generated constructor stub
		mQuery = query;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		callback.onStart(this);
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			result = parse();
		} catch (Exception e) {
			// e.printStackTrace();
			return "error";
		}
		return null;
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		callback.onCancel(this);
	}

	@TargetApi(11)
	@Override
	protected void onCancelled(String result) {
		// TODO Auto-generated method stub
		super.onCancelled(result);
		callback.onError(this, result);
	}

	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result != null)
			callback.onError(this, result);
		else {
			callback.onComplete(this);
		}
	};

	ArrayList<String> parse() throws ClientProtocolException, IOException,
			JSONException {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("setting", "response_type:json"));
		param.add(new BasicNameValuePair("SET_DEVICE", "android(APP)"));
		param.add(new BasicNameValuePair("sc_query", URLEncoder.encode(mQuery,
				HTTP.UTF_8)));
		JSONArray a = new JSONArray(StringUtils.stringFromURL(
				AUTOCOMPLETE_URL, param));
		for (int i = 0; i < a.length(); i++) {
			JSONObject jitem = a.getJSONObject(i);
			String title_ko = jitem.getString("title");
			String title_en = jitem.getString("title_english");
			String title;
			if ("".equalsIgnoreCase(title_en)) {
				title = title_ko;
			} else {
				title = title_ko + " ( " + title_en + " )";
			}
			list.add(title);
			query.add(title_ko);
		}
		return list;
	}

}
