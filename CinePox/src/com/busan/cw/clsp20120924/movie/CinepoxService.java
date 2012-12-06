package com.busan.cw.clsp20120924.movie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.chan.util.Util;
import kr.co.chan.util.l;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.aidl.ICinepoxService;
import com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback;
import com.busan.cw.clsp20120924.downloader.DownManager;
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

	private DownManager mDownManager;
	// private BluetoothChatService mBTChat;

	// private LocationManager mLocManager;
	// private Location mLocation;

	// private ShakeListener mShaker;
	// private SensorManager sensorManager;
	// private Sensor accelerormeterSensor;

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

	public final String GCM_SENDER_ID = "614996383425";
	public String SMS_KEY = "";
	private int mNotiId;
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
					// Automatically registers application on startup.
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
				l.i("sendshowtime : " + url);
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
					String name = o.getString("save_name");
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
		int result = mDownManager.queue(url, DownManager.DOWNLOAD_PATH + "/"
				+ filename, R.drawable.ic_launcher, filename);
		if (result == -1)
			Toast.makeText(this, R.string.download_exist, Toast.LENGTH_SHORT)
					.show();
		else if (result == -2)
			Toast.makeText(this, R.string.download_error, Toast.LENGTH_SHORT)
					.show();
	}

	protected void addDownload(String stringExtra) {
		// TODO Auto-generated method stub
		new DownloadInfoParser().execute(stringExtra);
	}

	void refresh() {
		// new PushSync().execute();
	}

	class PushSync extends AsyncTask<String, Integer, JSONArray> {

		@Override
		protected JSONArray doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String url = Domain.ACCESS_DOMAIN
						+ "cinepoxAPP/getPush?setting=response_type:json";
				return new JSONArray(Util.Stream.stringFromURL(url));
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				for (int i = 0; i < result.length(); i++) {
					try {
						JSONObject jsonData = result.getJSONObject(i);
						if (!getConfig().isReadPush(jsonData.getString("num"))) {
							notifyPush(jsonData);
							getConfig().addReadPush(jsonData.getString("num"));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	void notifyPush(JSONObject o) throws JSONException {
		String title = o.getString("title");
		String message = o.getString("message");
		String url = Domain.ACCESS_DOMAIN + o.getString("url");
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(Intent.ACTION_VIEW, Uri.parse(url)), 0);
		Notification notif = new Notification(R.drawable.ic_notify,
				"새로운 영화가 업데이트 되었습니다!", System.currentTimeMillis());
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notif.flags |= Notification.FLAG_SHOW_LIGHTS;
		notif.ledARGB = Color.BLUE;
		notif.ledOnMS = 1000;
		notif.ledOffMS = 2000;
		notif.setLatestEventInfo(this, title, message, contentIntent);
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.notify((mNotiId += 1), notif);
	}

	void initService() {
		mConfigModel = PlayerConfigModel.getInstance(this);
		mDownManager = DownManager.getInstance(this);
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
		// mBTChat = BluetoothChatService.getService(this, mHandler);
		// mBTChat.start();
		// mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// Criteria criteria = new Criteria();
		// String provider = mLocManager.getBestProvider(criteria, true);
		// if (provider == null) {
		// provider = LocationManager.NETWORK_PROVIDER;
		// }
		// mLocation = mLocManager.getLastKnownLocation(provider);
		// mLocManager.requestLocationUpdates(provider, 10000, 100, this);
		// mShaker = new ShakeListener(this);
		// mShaker.setOnShakeListener(this);
		// sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// accelerormeterSensor = sensorManager
		// .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// if (accelerormeterSensor != null)
		// sensorManager.registerListener(this, accelerormeterSensor,
		// SensorManager.SENSOR_DELAY_FASTEST);
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
		// GCMRegistrar.checkDevice(this);
		// GCMRegistrar.checkManifest(this);
		// registerReceiver(mHandleMessageReceiver,
		// new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
		// final String regId = GCMRegistrar.getRegistrationId(this);
		// if (regId.equals("")) {
		// GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		// } else {
		// if (GCMRegistrar.isRegisteredOnServer(this)) {
		//
		// } else {
		// final Context context = this;
		// new Thread() {
		//
		// @Override
		// public void run() {
		// boolean registered =
		// ServerUtilities.register(context, regId);
		// if (!registered) {
		// GCMRegistrar.unregister(context);
		// }
		// }
		//
		// }.start();
		// }
		// }
	}

	State getState() {
		return (State) getApplication();
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
		// unregisterReceiver(mHandleMessageReceiver);
		// GCMRegistrar.onDestroy(this);
		registerRestrartAlarm();
		// if (sensorManager != null)
		// sensorManager.unregisterListener(this);
		// mBTChat.stop();
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
