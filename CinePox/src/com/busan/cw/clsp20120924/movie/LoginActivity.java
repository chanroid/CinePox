package com.busan.cw.clsp20120924.movie;

import kr.co.chan.util.l;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gcm.GCMRegistrar;

/**
 * 기존 모든 기능 배제하고 GCM Key 등록 용도로만 사용. 차후 필요할시 파라미터와 기능 추가.
 * 
 * @author CINEPOX
 * 
 */
public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		l.i("Cinepox Login : " + getIntent().getDataString());
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		String memberId = getIntent().getData().getQueryParameter(
				Constants.KEY_MEMBER_NUM);
		Config.getInstance(this).setMemnum(memberId);
		if (!"".equals(memberId) && memberId != null)
			sendBroadcast(new Intent(CinepoxService.ACTION_REGISTER_GCM));
		finish();
	}

}
