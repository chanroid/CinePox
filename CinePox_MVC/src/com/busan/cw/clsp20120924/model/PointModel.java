package com.busan.cw.clsp20120924.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import utils.StreamUtils;
import utils.StringUtils;
import utils.LogUtils.l;

import android.content.Context;
import android.os.AsyncTask;

import com.busan.cw.clsp20120924.base.BaseModel;
import com.busan.cw.clsp20120924.base.DomainManager;
import com.busan.cw.clsp20120924.interfaces.PointModelCallback;

public class PointModel extends BaseModel {

	private PointModelCallback callback;

	public void setCallback(PointModelCallback callback) {
		this.callback = callback;
	}

	public PointModel(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	public void loadPoint(int memberNum) {
		new PointLoader().execute(memberNum);
	}

	private class PointLoader extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (callback != null)
				callback.onPointLoadStart(PointModel.this);
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub

			try {
				String pointUrl = DomainManager.getAppDomain(getContext())
						+ app().getAppConfig().getPointInfoUrl();
				HttpURLConnection conn = (HttpURLConnection) new URL(pointUrl)
						.openConnection();
				DomainManager.signHeader(conn);
				ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
				postParams.add(new BasicNameValuePair(EXTRA_MEMBER_NUM, String
						.valueOf(params[0])));
				StreamUtils.addPostParams(conn, postParams);
				String jsonString = StringUtils.stringFromStream(conn
						.getInputStream());

				l.d("point result : " + jsonString);
				JSONObject pointJson = new JSONObject(jsonString);
				if (!isY(pointJson.getString(EXTRA_RESULT)))
					return pointJson.getString(EXTRA_MSG);

				int point = pointJson.getJSONObject(EXTRA_DATA)
						.getJSONObject(EXTRA_POINT_CINEPOX).getInt(EXTRA_POINT);
				int bonus = pointJson.getJSONObject(EXTRA_DATA)
						.getJSONObject(EXTRA_POINT_BONUS).getInt(EXTRA_POINT);
				// 나머지는 통합될 포인트라 앱에서는 관리하지 않음

				UserConfigData data = app().getUserConfig();
				data.point = point;
				data.bonus = bonus;
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
					callback.onPointLoadComplete(PointModel.this);
				else
					callback.onPointLoadError(PointModel.this, result);
		}
	}
}
