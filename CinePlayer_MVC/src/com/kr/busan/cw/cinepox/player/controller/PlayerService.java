/**
 * 0. Project  : CinePlayer_MVC
 *
 * 1. FileName : PlayerService.java
 * 2. Package : com.kr.busan.cw.cinepox.player.controller
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 30. 오후 4:34:32
 * 6. 변경이력 : 
 *		2012. 10. 30. 오후 4:34:32 : 신규
 *
 */
package com.kr.busan.cw.cinepox.player.controller;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.kr.busan.cw.cinepox.player.model.PlayerConfigModel;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : PlayerService.java
 * 3. Package  : com.kr.busan.cw.cinepox.player.controller
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 30. 오후 4:34:32
 * </PRE>
 */
@SuppressWarnings("deprecation")
public class PlayerService extends Service {

	public static final String ACTION_SEND_TIME = "cinepox.player.service.ACTION_SEND_TIME";
	public static final String ACTION_SEND_ERROR = "cinepox.player.service.ACTION_SEND_ERROR";

	private PlayerConfigModel mConfigModel;

	private BroadcastReceiver mPlayerServiceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_SEND_TIME.equals(intent.getAction()))
				sendTime(intent);
			else if (ACTION_SEND_ERROR.equals(intent.getAction()))
				sendError(intent);
		}
	};

	private class SendErrorThread extends Thread {

		private Intent intent;
 
		private SendErrorThread(Intent intent) {
			this.intent = intent;
		}

		public void run() {
			try {
				mConfigModel.sendErrorLog(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class SendTimeThread extends Thread {

		private Intent intent;

		private SendTimeThread(Intent intent) {
			this.intent = intent;
		}

		public void run() {
			try {
				mConfigModel.sendPlayTime(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void sendError(Intent intent) {
		new SendErrorThread(intent).start();
	}

	private void sendTime(Intent intent) {
		new SendTimeThread(intent).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		handleStart();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		handleStart();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		handleDestroy();
		super.onDestroy();
	}

	private void handleStart() {
		registerReceiver();
		mConfigModel = PlayerConfigModel.getInstance(this);
	}

	private void handleDestroy() {
		unregisterReceiver(mPlayerServiceReceiver);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SEND_ERROR);
		filter.addAction(ACTION_SEND_TIME);
		registerReceiver(mPlayerServiceReceiver, filter);
	}

}
