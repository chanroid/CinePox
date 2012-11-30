package com.busan.cw.clsp20120924.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class QRPlayActivity extends Activity {

	boolean isInFlag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = new Intent("com.kr.cinepox.player.QRPLAY");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data
						.getStringExtra("SCAN_RESULT"))));
				finish();
				break;
			default:
				break;
			}
		}
	}
}
