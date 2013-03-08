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

import utils.LogUtils.l;
import utils.StreamUtils;
import utils.StringUtils;
import android.content.Context;
import android.os.AsyncTask;

import com.busan.cw.clsp20120924.base.BaseModel;
import com.busan.cw.clsp20120924.base.DomainManager;
import com.busan.cw.clsp20120924.interfaces.TicketModelCallback;

public class TicketModel extends BaseModel {

	private TicketModelCallback callback;

	public void setCallback(TicketModelCallback callback) {
		this.callback = callback;
	}

	public TicketModel(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	public void loadTicket(int memberNum) {
		new TicketLoader().execute(memberNum);
	}

	private class TicketLoader extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (callback != null)
				callback.onTicketLoadStart(TicketModel.this);
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub

			try {
				String ticketUrl = DomainManager.getAppDomain(getContext())
						+ app().getAppConfig().getPeriodInfoUrl();
				HttpURLConnection conn = (HttpURLConnection) new URL(ticketUrl)
						.openConnection();
				DomainManager.signHeader(conn);
				ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
				postParams.add(new BasicNameValuePair(EXTRA_MEMBER_NUM, String
						.valueOf(params[0])));
				StreamUtils.addPostParams(conn, postParams);
				String jsonString = StringUtils.stringFromStream(conn
						.getInputStream());

				l.d("ticket result : " + jsonString);
				JSONObject ticketJson = new JSONObject(jsonString);
				if (!isY(ticketJson.getString(EXTRA_RESULT)))
					return ticketJson.getString(EXTRA_MSG);

				int ticketEndDate = 0;
				String ticketEndDateView = null;
				boolean isPremium = false;

				try {
					ticketEndDate = ticketJson.getJSONObject(EXTRA_DATA)
							.getJSONObject(EXTRA_MEMBERSHIP)
							.getInt(EXTRA_END_DATE);
					ticketEndDateView = ticketJson.getJSONObject(EXTRA_DATA)
							.getJSONObject(EXTRA_MEMBERSHIP)
							.getString(EXTRA_END_DATE_VIEW);
					isPremium = true;
				} catch (JSONException e) {
				}
				boolean isAuto = isY(ticketJson.getJSONObject(EXTRA_DATA)
						.getString(EXTRA_IS_AUTO));

				UserConfigData data = app().getUserConfig();
				data.ticketEndDate = ticketEndDate;
				data.ticketEndDateView = ticketEndDateView;
				data.ticketIsAuto = isAuto;
				data.premium = isPremium;
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
					callback.onTicketLoadComplete(TicketModel.this);
				else
					callback.onTicketLoadError(TicketModel.this, result);
		}
	}
}
