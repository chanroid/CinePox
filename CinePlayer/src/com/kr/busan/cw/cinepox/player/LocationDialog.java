package com.kr.busan.cw.cinepox.player;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;

public class LocationDialog extends Activity implements OnCancelListener,
		OnDismissListener {

	AlertDialog.Builder mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		finish();
	}

}
