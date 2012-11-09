package com.kr.busan.cw.cinepox.movie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setTheme(android.R.style.Theme_NoDisplay);
		super.onCreate(savedInstanceState);
		String sms_key = getIntent().getData().getQueryParameter("sms_key");
		Intent i = new Intent(CinepoxService.ACTION_SEND_PAYCODE);
		i.putExtra("sms_key", sms_key);
		sendBroadcast(i);
		finish();
	}
	

}
