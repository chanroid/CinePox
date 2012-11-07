package com.kr.busan.cw.cinepox.movie;

import com.busan.cw.clsp20120924.R;

import kr.co.chan.util.l;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class LoginActivity extends Activity {

	boolean isInFlag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (!handleLoginIntent())
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse(Config.Domain
							+ "member/login.html?SET_DEVICE=android(APP)")));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isInFlag) {
			isInFlag = false;
		} else {
			if (getIntent().getData() == null)
				finish();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		setIntent(intent);
		handleLoginIntent();
		finish();
	}

	boolean handleLoginIntent() {
		Uri data = getIntent().getData();
		if (data == null) {
			return false;
		}

		String id = data.getQueryParameter("id");
		String pw = data.getQueryParameter("pw");
		String auto = data.getQueryParameter("auto");
		if (id == null || pw == null || auto == null)
			return false;
		l.i(data.toString());
		l.i(id + ", " + pw + ", " + auto);
		loginConfirm(id, pw);
		getConfig().setAutoLogin("1".equalsIgnoreCase(auto) ? true : false);
		Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
		isInFlag = true;
		finish();
		sendBroadcast(new Intent("login"));
		return true;
	}

	void loginConfirm(String id, String pw) {
		getConfig().setAccount(id, pw);
		getConfig().setLogined(true);
	}

	Config getConfig() {
		return Config.getInstance(this);
	}

}
