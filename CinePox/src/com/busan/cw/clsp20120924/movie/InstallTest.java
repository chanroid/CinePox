package com.busan.cw.clsp20120924.movie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class InstallTest extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent i = new Intent(this, CinepoxService.class);
		startService(i);
		finish();
	}
}
