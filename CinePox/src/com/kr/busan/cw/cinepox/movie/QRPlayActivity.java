package com.kr.busan.cw.cinepox.movie;

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
		// Intent intent = new Intent(this, CaptureActivity.class);
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
//				Toast.makeText(this, data.getStringExtra("SCAN_RESULT"),
//						Toast.LENGTH_LONG).show();
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
