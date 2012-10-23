package com.kr.busan.cw.cinepox.movie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ServiceActivateActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
		super.onCreate(savedInstanceState);
		Intent i = new Intent(this, CinepoxService.class);
		startService(i);
		finish();
	}
}
