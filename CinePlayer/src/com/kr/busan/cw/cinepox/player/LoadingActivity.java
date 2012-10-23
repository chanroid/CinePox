package com.kr.busan.cw.cinepox.player;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.kr.busan.cw.cinepox.R;

public class LoadingActivity extends Activity implements ServiceConnection,
		OnCancelListener {

	ProgressDialog mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mProgress = new ProgressDialog(this);
		mProgress.setMessage(getString(R.string.readytoresponse));
		mProgress.setOnCancelListener(this);
		mProgress.show();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		
	}

}
