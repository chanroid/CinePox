package com.busan.cw.clsp20120924.gcm;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMReceiver extends GCMBroadcastReceiver {
	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		// TODO Auto-generated method stub
		return GCMService.class.getName();
	}
}
