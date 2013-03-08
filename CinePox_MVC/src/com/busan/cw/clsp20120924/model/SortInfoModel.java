package com.busan.cw.clsp20120924.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.LogUtils.l;
import utils.StreamUtils;
import utils.StringUtils;
import android.content.Context;
import android.os.AsyncTask;

import com.busan.cw.clsp20120924.base.BaseModel;
import com.busan.cw.clsp20120924.base.DomainManager;
import com.busan.cw.clsp20120924.interfaces.SortInfoModelCallback;

public class SortInfoModel extends BaseModel {

	private ArrayList<SortInfoItemData> dataArray;

	private SortInfoModelCallback callback;

	public SortInfoModel(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	public void loadSortInfo(String url, String type,
			SortInfoModelCallback callback) {
		if (type == null)
			type = "";
		this.callback = callback;
		new CategoryLoadSync().execute(url, type);
	}

	private class CategoryLoadSync extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (callback != null)
				callback.onSortInfoLoadStart(SortInfoModel.this);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			dataArray = new ArrayList<SortInfoItemData>();

			String url = DomainManager.getAppDomain(getContext()) + params[0];
			String type = params[1];

			ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(EXTRA_TYPE, type));
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(url)
						.openConnection();
				DomainManager.signHeader(conn);
				StreamUtils.addPostParams(conn, postParams);
				JSONObject categoryJson = new JSONObject(
						StringUtils.stringFromStream(conn.getInputStream()));
				l.i("category list : " + categoryJson.toString());
				if (!isY(categoryJson.getString(EXTRA_RESULT)))
					return categoryJson.getString(EXTRA_MSG);

				JSONArray categoryItems = categoryJson.getJSONArray(EXTRA_DATA);
				for (int i = 0; i < categoryItems.length(); i++) {
					SortInfoItemData data = new SortInfoItemData(
							categoryItems.getJSONObject(i));
					dataArray.add(data);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			} catch (JSONException e) {
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
			if (callback != null)
				if (result == null)
					callback.onSortInfoLoadComplete(SortInfoModel.this);
				else
					callback.onSortInfoLoadError(SortInfoModel.this, result);
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<SortInfoItemData> getCategoryArray(String defaultValue) {
		ArrayList<SortInfoItemData> result = (ArrayList<SortInfoItemData>) dataArray
				.clone();
		if (defaultValue != null)
			try {
				result.add(0, new SortInfoItemData("", defaultValue));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return result;
	}

	/**
	 * 
	 * @param defaultName
	 *            아무것도 선택하지 않을때의 기본 이름
	 * @return
	 */
	public String[] getDataNames(String defaultName) {
		int defaultCount = 0;
		String[] result;
		if (defaultName != null) {
			defaultCount = 1;
			result = new String[dataArray.size() + defaultCount];
			result[0] = defaultName;
		} else {
			result = new String[dataArray.size()];
		}

		for (int i = 0; i < dataArray.size(); i++) {
			l.d("show sort category : " + dataArray.get(i).getName());
			result[i + defaultCount] = dataArray.get(i).getName();
		}
		return result;
	}

	public ArrayList<String> getDataCodes(boolean defaultValue) {
		ArrayList<String> result = new ArrayList<String>();

		if (defaultValue)
			result.add("");

		for (SortInfoItemData data : dataArray) {
			result.add(data.getCode());
		}
		return result;
	}
}
