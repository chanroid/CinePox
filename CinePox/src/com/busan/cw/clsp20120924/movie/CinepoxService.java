package com.busan.cw.clsp20120924.movie;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.chan.util.Util;
import kr.co.chan.util.l;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.aidl.ICinepoxService;
import com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback;
import com.busan.cw.clsp20120924.downloader.DownManager2;
import com.busan.cw.clsp20120924.downloader.DownloadData;
import com.busan.cw.clsp20120924.gcm.CommonUtilities;
import com.busan.cw.clsp20120924.gcm.ServerUtilities;
import com.busan.cw.clsp20120924.movie.WidgetProvider.UpdateData;
import com.google.android.gcm.GCMRegistrar;
import com.kr.busan.cw.cinepox.player.model.PlayerConfigModel;

@SuppressLint("HandlerLeak")
public class CinepoxService extends Service {

	private RemoteCallbackList<ICinepoxServiceCallback> mCallbacks = new RemoteCallbackList<ICinepoxServiceCallback>();
	private PendingIntent mIntentSender;
	private PendingIntent mDataIntentSender;
	private AlarmManager mAlarmManager;
	private Config mConfig;

	private DownManager2 mDownManager;
	private PlayerConfigModel mConfigModel;

	public static final int WHAT_CHANGED_ALARM = 2011;
	public static final int WHAT_CHANGED_INTERVAL = 2012;
	public static final int REQ_REFRESH = 2013;
	public static final String ACTION_REGISTER_GCM = "com.kr.busan.cw.cinepox.service.register";
	public static final String ACTION_REFRESH_SERVICE = "com.kr.busan.cw.cinepox.service.refresh";
	public static final String ACTION_RESTART_SERVICE = "com.kr.busan.cw.cinepox.service.restart";
	public static final String ACTION_START_DOWNLOAD = "com.kr.busan.cw.cinepox.service.download";
	public static final String ACTION_SEND_PAYCODE = "com.kr.busan.cw.cinepox.service.paycode";
	public static final String ACTION_SEND_ERRORLOG = "com.kr.busan.cw.cinepox.service.errorlog";
	public static final String ACTION_LOAD_WIDGET_DATA = "com.kr.busan.cw.cinepox.service.widgetdata";

	public String SMS_KEY = "";
	private int mDataInterval = 86400000;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (ACTION_REFRESH_SERVICE.equalsIgnoreCase(intent.getAction())) {
				unregisterAlarm();
				refresh();
				registerAlarm();
			} else if (ACTION_RESTART_SERVICE.equalsIgnoreCase(intent
					.getAction())) {
				context.startService(new Intent(context, CinepoxService.class));
			} else if (ACTION_START_DOWNLOAD.equalsIgnoreCase(intent
					.getAction())) {
				addDownload(intent.getStringExtra("url"));
			} else if (ACTION_SEND_PAYCODE.equalsIgnoreCase(intent.getAction())) {
				SMS_KEY = intent.getStringExtra("sms_key");
				registerReceiver(new CodeReceiver(), new IntentFilter(
						"android.provider.Telephony.SMS_RECEIVED"));
			} else if (ACTION_SEND_ERRORLOG
					.equalsIgnoreCase(intent.getAction())) {
				new SendErrorThread(intent).start();
			} else if (ACTION_LOAD_WIDGET_DATA.equalsIgnoreCase(intent
					.getAction())) {
				unregisterWidgetDataAlarm();
				new LoadWidgetDataSync().start();
				registerWidgetDataAlarm();
			} else if (ACTION_REGISTER_GCM.equals(intent.getAction())) {
				l.i("broadcast gcm service to cinepoxservice");
				final String regId = GCMRegistrar
						.getRegistrationId(CinepoxService.this);
				if (regId.equals("")) {
					GCMRegistrar.register(CinepoxService.this,
							CommonUtilities.SENDER_ID);
				} else {
					l.i("regId = " + regId);
					ServerUtilities.register(CinepoxService.this, regId,
							mConfig.getMemnum());
				}
			}
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

	class CodeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if ("android.provider.Telephony.SMS_RECEIVED"
					.equalsIgnoreCase(intent.getAction())) {
				Bundle bundle = intent.getExtras();
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] msgs = new SmsMessage[pdus.length];
				for (int i = 0; i < msgs.length; i++) {
					msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
					String message = msgs[i].getMessageBody();
					if (message.contains("danalpay") || message.contains("다날")
							|| message.contains("cinepox")) {
						Pattern pattern = Pattern.compile("[0-9]{4,8}");
						Matcher matcher = pattern.matcher(message);
						if (matcher.find()) {
							String code = matcher.group();
							String key = SMS_KEY;
							new CodeSendSync(key, code).start();
						}
						unregisterReceiver(this);
					}
				}
			}
		}
	}

	// private Handler mHandler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// switch (msg.what) {
	// case MESSAGE_STATE_CHANGE:
	// break;
	// case MESSAGE_WRITE:
	// Toast.makeText(getApplicationContext(), "전송이 완료되었습니다.",
	// Toast.LENGTH_SHORT).show();
	// break;
	// case MESSAGE_READ:
	// byte[] readBuf = (byte[]) msg.obj;
	// String readMessage = new String(readBuf, 0, msg.arg1);
	// Intent i = new Intent(Intent.ACTION_VIEW,
	// Uri.parse(readMessage));
	// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// startActivity(i);
	// break;
	// case MESSAGE_DEVICE_NAME:
	// break;
	// case MESSAGE_TOAST:
	// Toast.makeText(getApplicationContext(),
	// msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
	// .show();
	// break;
	// }
	// };
	// };

	class LoadWidgetDataSync extends Thread {
		@Override
		public void run() {
			JSONArray jsonData = null;
			try {
				if (getConfig().getWidgetUrl() != null) {
					l.i(getConfig().getWidgetUrl());
					jsonData = new JSONArray(
							Util.Stream.stringFromURL(getConfig()
									.getWidgetUrl()
									+ "?setting=response_type:json"));
				} else
					jsonData = new JSONArray(
							Util.Stream
									.stringFromURL(Domain.ACCESS_DOMAIN
											+ "cinepoxAPP/getAdWidget?setting=response_type:json"));
				l.i(jsonData.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (jsonData != null) {
				try {
					ArrayList<UpdateData> list = new ArrayList<UpdateData>();
					for (int i = 0; i < jsonData.length(); i++) {
						UpdateData data = new UpdateData();
						JSONObject dataObject = jsonData.getJSONObject(i);
						String title = dataObject.getString("title");
						String desc = dataObject.getString("desc");
						String url = Domain.WEB_DOMAIN
								+ dataObject.getString("url");
						data.title = title;
						data.desc = desc;
						data.url = url;
						list.add(data);
					}
					getConfig().setWidgetData(list);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class CodeSendSync extends Thread {

		String order;
		String commit;

		public CodeSendSync(String order, String commit) {
			super();
			this.order = order;
			this.commit = commit;
		}

		public void run() {
			// response가 있든 말든 걍 처리.
			try {
				String url = Domain.ACCESS_DOMAIN + "cinepoxAPP/setPaySms/";
				ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
				param.add(new BasicNameValuePair("sms_key", order));
				param.add(new BasicNameValuePair("sms_num", commit));
				Util.Stream.inStreamFromURLbyPOST(url, param);
				SMS_KEY = "";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class SendTimeThread extends Thread {

		String url;

		public SendTimeThread(String url) {
			this.url = url;
		}

		public void run() {
			// response가 있든 말든 걍 처리.
			try {
				Util.Stream.inStreamFromURL(url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class DownloadInfoParser extends AsyncTask<String, Integer, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				return Util.Stream.jsonFromURL(params[0]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			if (result == null)
				return;
			try {
				JSONArray list = result.getJSONArray("list");
				for (int i = 0; i < list.length(); i++) {
					JSONObject o = list.getJSONObject(i);
					String url = o.getString("url");
					String name = Util.File.clearName(o.getString("save_name"));
					addDownloadQueue(url, name);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(CinepoxService.this,
						"다운로드 시도에 실패했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

	protected void removeDownloadQueue(String stringExtra) {
		// TODO Auto-generated method stub
	}

	protected void addDownloadQueue(String url, String filename) {
		DownloadData data = new DownloadData();
		data.filePath = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
				+ "/" + filename;
		data.fileUrl = url;
		data.title = filename;
		try {
			mDownManager.queue(data);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, R.string.download_error, Toast.LENGTH_SHORT)
					.show();
		}
	}

	protected void addDownload(String stringExtra) {
		// TODO Auto-generated method stub
		new DownloadInfoParser().execute(stringExtra);
	}

	void refresh() {
		// new PushSync().execute();
	}

	void initService() {
		mConfigModel = PlayerConfigModel.getInstance(this);
		mDownManager = DownManager2.getInstance(this);
		mConfig = Config.getInstance(this);
		mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_REFRESH_SERVICE);
		filter.addAction(ACTION_RESTART_SERVICE);
		filter.addAction(ACTION_START_DOWNLOAD);
		filter.addAction(ACTION_SEND_ERRORLOG);
		filter.addAction(ACTION_SEND_PAYCODE);
		filter.addAction(ACTION_LOAD_WIDGET_DATA);
		filter.addAction(ACTION_REGISTER_GCM);
		registerReceiver(mReceiver, filter);
		new LoadWidgetDataSync().start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		handleStart(intent, startId);
		return START_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		handleStart(intent, startId);
	}

	void handleStart(Intent intent, int startId) {
		initService();
		unregisterAlarm();
		unregisterWidgetDataAlarm();
		refresh();
		registerWidgetDataAlarm();
		registerAlarm();
	}

	Config getConfig() {
		return Config.getInstance(this);
	}

	void registerRestrartAlarm() {
		Intent i = new Intent(ACTION_RESTART_SERVICE);
		mIntentSender = PendingIntent.getBroadcast(this, REQ_REFRESH, i, 0);
		mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 5000,
				mIntentSender);
	}

	void registerAlarm() {
		registerAlarm(getConfig().getAlarmInterval());
	}

	void registerAlarm(long interval) {
		Intent i = new Intent(ACTION_REFRESH_SERVICE);
		mIntentSender = PendingIntent.getBroadcast(this, REQ_REFRESH, i, 0);
		mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis()
				+ interval, mIntentSender);
	}

	void unregisterAlarm() {
		if (mAlarmManager != null && mIntentSender != null) {
			mIntentSender.cancel();
			mAlarmManager.cancel(mIntentSender);
		}
	}

	void registerWidgetDataAlarm() {
		Intent i = new Intent(ACTION_LOAD_WIDGET_DATA);
		mDataIntentSender = PendingIntent.getBroadcast(this, REQ_REFRESH, i, 0);
		mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis()
				+ mDataInterval, mDataIntentSender);
	}

	void unregisterWidgetDataAlarm() {
		if (mAlarmManager != null && mDataIntentSender != null) {
			mIntentSender.cancel();
			mAlarmManager.cancel(mDataIntentSender);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	void doActivity(int what, String data) throws RemoteException {
		int i = mCallbacks.beginBroadcast();
		while (i > 0) {
			i--;
			try {
				mCallbacks.getBroadcastItem(i).doActivity(what, data);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		mCallbacks.finishBroadcast();
	}

	private void cancelAllDownload() {
		// TODO Auto-generated method stub
		mDownManager.cancelAllDownload();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		unregisterAlarm();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		cancelAllDownload();
		unregisterReceiver(mReceiver);
		unregisterAlarm();
		registerRestrartAlarm();
		super.onDestroy();
	};

	private ICinepoxService.Stub mBinder = new ICinepoxService.Stub() {

		@Override
		public void doService(int what, String data) throws RemoteException {
		}

		@Override
		public void registerCallback(ICinepoxServiceCallback c)
				throws RemoteException {
			mCallbacks.register(c);
		}

		@Override
		public void unregisterCallback(ICinepoxServiceCallback c)
				throws RemoteException {
			mCallbacks.unregister(c);
		}

	};

}
