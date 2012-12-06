/**
 * 0. Project  : CinePox_MVC
 *
 * 1. FileName : CinePoxService.java
 * 2. Package : com.busan.cw.clsp20120924.controller
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 31. 오후 6:17:52
 * 6. 변경이력 : 
 *		2012. 10. 31. 오후 6:17:52 : 신규
 *
 */
package com.busan.cw.clsp20120924.controller;

import java.io.File;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.model.ConfigModel;
import com.busan.cw.clsp20120924.model.DownloadModel;
import com.busan.cw.clsp20120924.model.Downloader;
import com.kr.busan.cw.cinepox.player.controller.PlayerActivity;

import controller.CCService;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : CinePoxService.java
 * 3. Package  : com.busan.cw.clsp20120924.controller
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 6:17:52
 * </PRE>
 */
public class CinePoxService extends CCService implements Constants, Downloader.Callback {

	private NotificationManager mNotiManager;
	private PendingIntent mEmptyPendingIntent;
	private PendingIntent mDataIntentSender;
	private DownloadModel mDownloadModel;
	private ConfigModel mConfigModel;
	private AlarmManager mAlarmManager;

	public static final int WHAT_CHANGED_ALARM = 2011;
	public static final int WHAT_CHANGED_INTERVAL = 2012;
	
	public static final int WIDGET_DATA_INTERVAL = 86400000;
	
	public static final String ACTION_RESTART_SERVICE = "com.busan.cw.clsp20120924.service.restart";
	public static final String ACTION_START_DOWNLOAD = "com.busan.cw.clsp20120924.service.download";
	public static final String ACTION_SEND_PAYCODE = "com.busan.cw.clsp20120924.service.paycode";
	public static final String ACTION_SEND_ERRORLOG = "com.busan.cw.clsp20120924.service.errorlog";
	public static final String ACTION_LOAD_WIDGET_DATA = "com.busan.cw.clsp20120924.service.widgetdata";
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (ACTION_RESTART_SERVICE.equalsIgnoreCase(intent
					.getAction())) {
				context.startService(new Intent(context, CinePoxService.class));
			} else if (ACTION_START_DOWNLOAD.equalsIgnoreCase(intent
					.getAction())) {
				mDownloadModel.addDownload(intent.getStringExtra("url"));
			} else if (ACTION_SEND_PAYCODE.equalsIgnoreCase(intent.getAction())) {
				registerCodeReceiver(intent.getStringExtra("sms_key"));
			} else if (ACTION_SEND_ERRORLOG
					.equalsIgnoreCase(intent.getAction())) {
				mConfigModel.sendErrorLog(intent);
			} else if (ACTION_LOAD_WIDGET_DATA.equalsIgnoreCase(intent
					.getAction())) {
				unregisterWidgetDataAlarm();
				mConfigModel.loadWidgetData();
				registerWidgetDataAlarm();
			}
		}
	};

	class CodeReceiver extends BroadcastReceiver {
		
		private String mSMSKey;
		
		public CodeReceiver(String key) {
			mSMSKey = key;
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if ("android.provider.Telephony.SMS_RECEIVED"
					.equalsIgnoreCase(intent.getAction())) {
				mConfigModel.sendSMSCode(mSMSKey, intent);
				unregisterReceiver(this);
			}
		}
	}

	void registerCodeReceiver(String key) {
		registerReceiver(new CodeReceiver(key), new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED"));
	}

	void registerWidgetDataAlarm() {
		Intent i = new Intent(ACTION_LOAD_WIDGET_DATA);
		mDataIntentSender = PendingIntent.getBroadcast(this, 0, i, 0);
		mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis()
				+ WIDGET_DATA_INTERVAL, mDataIntentSender);
	}

	void unregisterWidgetDataAlarm() {
		if (mAlarmManager != null && mDataIntentSender != null) {
			mDataIntentSender.cancel();
			mAlarmManager.cancel(mDataIntentSender);
		}
	}

	void registerRestrartAlarm() {
		Intent i = new Intent(ACTION_RESTART_SERVICE);
		PendingIntent mIntentSender = PendingIntent.getBroadcast(this, 0, i, 0);
		mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 5000,
				mIntentSender);
	}

	@Override
	public void handleStart(Intent intent) {
		mNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mEmptyPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
		mDownloadModel = (DownloadModel) loadModel(DownloadModel.class);
		mDownloadModel.setDownloadCallback(this);
		mConfigModel = (ConfigModel) loadModel(ConfigModel.class);
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_RESTART_SERVICE);
		filter.addAction(ACTION_START_DOWNLOAD);
		filter.addAction(ACTION_SEND_ERRORLOG);
		filter.addAction(ACTION_SEND_PAYCODE);
		filter.addAction(ACTION_LOAD_WIDGET_DATA);
		registerReceiver(mReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Downloader d) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT >= 14) {
			Notification.Builder mBuilder = new Notification.Builder(this);
			mBuilder.setContentTitle(d.getTitle());
			mBuilder.setOngoing(true);
			mBuilder.setSmallIcon(d.getIcon());
			mBuilder.setContentText(this
					.getString(R.string.download_preparing));
			mBuilder.setTicker(getString(R.string.download_preparing));
			mBuilder.setContentIntent(mEmptyPendingIntent);
			if (Build.VERSION.SDK_INT >= 16)
				mNotiManager.notify(d.getId(), mBuilder.build());
			else
				mNotiManager.notify(d.getId(), mBuilder.getNotification());
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this);
			mBuilder.setContentTitle(d.getTitle());
			mBuilder.setOngoing(true);
			mBuilder.setSmallIcon(d.getIcon());
			mBuilder.setContentText(this
					.getString(R.string.download_preparing));
			mBuilder.setTicker(getString(R.string.download_preparing));
			mBuilder.setContentIntent(mEmptyPendingIntent);
			mNotiManager.notify(d.getId(), mBuilder.getNotification());
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCanceled(Downloader d) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT >= 14) {
			Notification.Builder mBuilder = new Notification.Builder(this);
			mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
			mBuilder.setLights(0xffff0000, 500, 500);
			mBuilder.setContentTitle(d.getTitle());
			mBuilder.setSmallIcon(d.getIcon());
			mBuilder.setAutoCancel(true);
			mBuilder.setDeleteIntent(mEmptyPendingIntent);
			mBuilder.setContentText(this
					.getString(R.string.download_canceled));
			mBuilder.setTicker(getString(R.string.download_canceled));
			if (Build.VERSION.SDK_INT >= 16)
				mNotiManager.notify(d.getId(), mBuilder.build());
			else
				mNotiManager.notify(d.getId(), mBuilder.getNotification());
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this);
			mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
			mBuilder.setLights(0xffff0000, 500, 500);
			mBuilder.setContentTitle(d.getTitle());
			mBuilder.setSmallIcon(d.getIcon());
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(mEmptyPendingIntent);
			mBuilder.setContentText(this
					.getString(R.string.download_canceled));
			mBuilder.setTicker(getString(R.string.download_canceled));
			mNotiManager.notify(d.getId(), mBuilder.getNotification());
		}
	}

	@Override
	public void onPrepared(Downloader d) {
		// TODO Auto-generated method stub
		onProgress(d, 0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onError(Downloader d, int what) {
		// TODO Auto-generated method stub
		String message = null;
		Intent cancelIntent;
		switch (what) {
		case Downloader.CALLBACK_ERROR:
			message = getString(R.string.download_error);
			cancelIntent = new Intent(this, DownloadRestartActivity.class);
			cancelIntent.putExtra(KEY_NUM, d.getId());
			break;
		case Downloader.CALLBACK_ERROR_DOWNLOADED:
			message = getString(R.string.download_already_completed);
			cancelIntent = new Intent();
			break;
		default:
			return;
		}
		PendingIntent pi = PendingIntent.getActivity(this, 0, cancelIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if (Build.VERSION.SDK_INT >= 14) {
			Notification.Builder mBuilder = new Notification.Builder(this);
			mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
			mBuilder.setLights(0xffff0000, 500, 500);
			mBuilder.setContentTitle(d.getTitle());
			mBuilder.setSmallIcon(d.getIcon());
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(pi);
			mBuilder.setContentText(message);
			mBuilder.setTicker(message);
			if (Build.VERSION.SDK_INT >= 16)
				mNotiManager.notify(d.getId(), mBuilder.build());
			else
				mNotiManager.notify(d.getId(), mBuilder.getNotification());
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this);
			mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
			mBuilder.setLights(0xffff0000, 500, 500);
			mBuilder.setContentTitle(d.getTitle());
			mBuilder.setSmallIcon(d.getIcon());
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(pi);
			mBuilder.setContentText(message);
			mBuilder.setTicker(message);
			mNotiManager.notify(d.getId(), mBuilder.getNotification());
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onProgress(Downloader d, int progress) {
		// TODO Auto-generated method stub
		Intent cancelIntent = new Intent(this, DownloadCancelActivity.class);
		cancelIntent.putExtra(KEY_NUM, d.getId());
		PendingIntent pi = PendingIntent.getActivity(this, 0, cancelIntent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT >= 14) {
			Notification.Builder mBuilder = new Notification.Builder(this);
			mBuilder.setProgress(100, progress, false);
			mBuilder.setContentTitle(d.getTitle());
			mBuilder.setContentIntent(pi);
			mBuilder.setOngoing(true);
			mBuilder.setSmallIcon(d.getIcon());
			if (Build.VERSION.SDK_INT >= 16)
				mNotiManager.notify(d.getId(), mBuilder.build());
			else
				mNotiManager.notify(d.getId(), mBuilder.getNotification());
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this);
			mBuilder.setContentTitle(d.getTitle());
			mBuilder.setContentIntent(pi);
			mBuilder.setContentText(progress
					+ getString(R.string.download_progresstext));
			mBuilder.setOngoing(true);
			mBuilder.setSmallIcon(d.getIcon());
			mNotiManager.notify(d.getId(), mBuilder.getNotification());
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCompleted(Downloader d) {
		// TODO Auto-generated method stub
		Intent playerIntent = new Intent(this, PlayerActivity.class);
		playerIntent.setData(Uri.fromFile(new File(d.getFilePath())));
		PendingIntent intent = PendingIntent.getActivity(this, 0,
				playerIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		if (Build.VERSION.SDK_INT > 13) {
			Notification.Builder mBuilder = new Notification.Builder(this);
			mBuilder.setVibrate(new long[] { 0, 1000 });
			mBuilder.setLights(0xff00ff00, 500, 500);
			mBuilder.setContentTitle(d.getTitle());
			mBuilder.setSmallIcon(d.getIcon());
			mBuilder.setContentText(this
					.getString(R.string.download_completed));
			mBuilder.setTicker(getString(R.string.download_completed));
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(intent);
			mBuilder.setDeleteIntent(mEmptyPendingIntent);
			if (Build.VERSION.SDK_INT >= 16)
				mNotiManager.notify(d.getId(), mBuilder.build());
			else
				mNotiManager.notify(d.getId(), mBuilder.getNotification());
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this);
			mBuilder.setVibrate(new long[] { 0, 1000 });
			mBuilder.setLights(0xff00ff00, 500, 500);
			mBuilder.setContentTitle(d.getTitle());
			mBuilder.setSmallIcon(d.getIcon());
			mBuilder.setContentText(this
					.getString(R.string.download_completed));
			mBuilder.setTicker(getString(R.string.download_completed));
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(intent);
			mNotiManager.notify(d.getId(), mBuilder.getNotification());
		}

		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(d
						.getFilePath()))));
	}

}
