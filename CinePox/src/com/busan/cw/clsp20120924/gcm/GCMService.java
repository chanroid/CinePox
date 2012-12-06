package com.busan.cw.clsp20120924.gcm;

import kr.co.chan.util.l;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.busan.cw.clsp20120924.movie.Config;
import com.busan.cw.clsp20120924.movie.Constants;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMService extends GCMBaseIntentService {
	
	public GCMService() {
		// TODO Auto-generated constructor stub
		super(CommonUtilities.SENDER_ID);
		l.i("consturct gcm service");
	}
	
	@Override
	protected void onRegistered(Context context, String registrationId) {
		// TODO Auto-generated method stub
		l.i("onRegistered : " + registrationId);
		ServerUtilities.register(context, registrationId,
				Config.getInstance(context).getMemnum());
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try {
			Bundle b = intent.getExtras();
			l.i("onMessage : " + b.getString(Constants.KEY_MSG));
			JSONObject msg = new JSONObject(b.getString(Constants.KEY_MSG));
			GCMMessageData data = new GCMMessageData();
			data.title = msg.getString(Constants.KEY_TITLE);
			data.message = msg.getString("until");
			data.url = msg.getString(Constants.KEY_URL);
			data.action = msg.getInt("action");
			CommonUtilities.generateNotification(context, data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		l.i("gcm service error : " + arg1);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
}
