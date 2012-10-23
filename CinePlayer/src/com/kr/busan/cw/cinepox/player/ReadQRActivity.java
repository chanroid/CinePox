package com.kr.busan.cw.cinepox.player;

import com.kr.busan.cw.cinepox.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ReadQRActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.readqr);
	}

	private void goQRPlay() {
		// TODO Auto-generated method stub
		Intent intent = new Intent("com.kr.cinepox.player.QRPLAY");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data
					.getStringExtra("SCAN_RESULT"))));
			setResult(RESULT_OK);
			finish();
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		goQRPlay();
	}
}
