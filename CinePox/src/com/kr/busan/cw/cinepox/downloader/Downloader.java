package com.kr.busan.cw.cinepox.downloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.co.chan.util.Util;
import kr.co.chan.util.l;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.kr.busan.cw.cinepox.R;

@SuppressLint("HandlerLeak")
@SuppressWarnings("deprecation")
public class Downloader extends AsyncTask<String, Integer, Integer> {

	private int mTimeout = 10000;
	// private int mBufferSize = 1024;
	private int mId = Integer.MAX_VALUE;
	private int mProgress = -1;
	private int mIcon;
	private boolean isCanceled = false;

	private String mPath;
	private String mUrl;
	private String mTitle;
	private Context mContext;

	private OnProgressUpdateListener listen;
	private NotificationManager mNotimanager;
	private Notification mNoti;
	private Handler h;

	/**
	 * DownManager를 통해서만 생성할 수 있음.
	 */
	protected Downloader(Context ctx, String url, String path, int id,
			int icon, String title) {
		this();
		h = new ServiceHandler();
		mContext = ctx.getApplicationContext();
		mPath = path;
		mUrl = url;
		mId = id;
		mIcon = icon;
		mTitle = title;
		mNotimanager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	/**
	 * 기본생성자 사용불가
	 */
	private Downloader() {
		super();
	}

	protected interface OnProgressUpdateListener {
		public void OnProgressUpdate(Downloader d, int progress);
	}

	public void setId(int id) {
		mId = id;
	}

	public int getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setOnProgressUpdateListener(OnProgressUpdateListener l) {
		listen = l;
	}

	private int start() throws IOException {
		notifyDownloadStart();
		long fileSize, remains, lenghtOfFile = 0;
		File file = new File(mPath);
		if (!file.exists())
			file.createNewFile();

		RandomAccessFile output = new RandomAccessFile(file.getAbsolutePath(),
				"rw");
		output.seek(fileSize = output.length());

		HttpURLConnection conn = (HttpURLConnection) new URL(mUrl)
				.openConnection();
		conn.addRequestProperty("Range", "bytes=" + String.valueOf(fileSize)
				+ '-');
		conn.setReadTimeout(mTimeout);
		conn.setConnectTimeout(mTimeout);
		conn.setAllowUserInteraction(true);
		conn.setDoInput(true);
		conn.setDoOutput(false);
		// conn.connect();
		if (conn.getResponseCode() == -1) {
			conn.disconnect();
		}
		remains = conn.getContentLength();

		lenghtOfFile = remains + fileSize;

		if (remains <= 0) {
			notifyDownloaded();
			output.close();
			conn.disconnect();
			return 2;
		}

		BufferedInputStream input = new BufferedInputStream(
				conn.getInputStream());

		publishProgress(0);

		if (fileSize < lenghtOfFile) {
			int readBytes = 0;
			byte data[] = new byte[input.available()];
			while ((readBytes = input.read(data)) != -1) {
				if (isCanceled)
					return 1;
				output.write(data, 0, readBytes);
				fileSize += readBytes;
				if (mProgress != Util.Math.getPercent(fileSize, lenghtOfFile))
					publishProgress(Util.Math
							.getPercent(fileSize, lenghtOfFile));
			}
		}

		input.close();
		output.close();
		conn.disconnect();
		return 0;
	}

	private void notifyDownloadStart() {
		// TODO Auto-generated method stub
		h.sendEmptyMessage(1);
	}

	class ServiceHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent playerIntent;
			if (Util.App
					.isInstalled(mContext, "com.kr.busan.cw.cinepox.player")) {
				playerIntent = new Intent("com.kr.busan.cw.cinepox.player.PLAY");
			} else {
				playerIntent = new Intent(Intent.ACTION_VIEW);
			}
			PendingIntent i = PendingIntent.getActivity(mContext, 0,
					playerIntent, 0);
			switch (msg.what) {
			case 0:
				playerIntent.setData(Uri.fromFile(new File(mPath)));
				if (Build.VERSION.SDK_INT > 13) {
					Notification.Builder mBuilder = new Notification.Builder(
							mContext);
					mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
					mBuilder.setContentTitle(mTitle);
					mBuilder.setAutoCancel(true);
					mBuilder.setSmallIcon(mIcon);
					mBuilder.setContentText(mContext
							.getString(R.string.download_already_completed));
					mBuilder.setTicker(mContext
							.getString(R.string.download_already_completed));
					mBuilder.setContentIntent(i);
					mNoti = mBuilder.getNotification();
				} else {
					mNoti = new Notification(
							mIcon,
							mContext.getString(R.string.download_already_completed),
							System.currentTimeMillis());
					mNoti.vibrate = new long[] { 0, 200, 300, 200 };
					mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
					mNoti.setLatestEventInfo(mContext, mTitle, mContext
							.getString(R.string.download_already_completed), i);
					mNoti.contentIntent = i;
				}

				mNotimanager.notify(mId, mNoti);
				break;
			case 1:
				if (Build.VERSION.SDK_INT > 13) {
					Notification.Builder mBuilder = new Notification.Builder(
							mContext);
					mBuilder.setContentTitle(mTitle);
					mBuilder.setSmallIcon(mIcon);
					mBuilder.setContentText(mContext
							.getString(R.string.download_preparing));
					mBuilder.setTicker(mContext
							.getString(R.string.download_preparing));
					mBuilder.setContentIntent(i);
					mNoti = mBuilder.getNotification();
				} else {
					mNoti = new Notification(mIcon,
							mContext.getString(R.string.download_preparing),
							System.currentTimeMillis());
					mNoti.vibrate = new long[] { 0, 200, 300, 200 };
					mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
					mNoti.contentIntent = i;
					mNoti.setLatestEventInfo(mContext,
							mContext.getString(R.string.download_preparing),
							mContext.getString(R.string.download_preparing),
							null);
				}

				mNotimanager.notify(mId, mNoti);
				break;
			}
		}
	};

	protected String getUrl() {
		return mUrl;
	}

	public String getPath() {
		return mPath;
	}

	public void performCancel() {
		isCanceled = true;
	}

	private void notifyDownloaded() {
		// TODO Auto-generated method stub
		h.sendEmptyMessage(0);
	}

	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			return start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cancel(true);
			return -1;
		}
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		mNotimanager.cancel(mId);
		showError();
	}

	@Override
	protected void onCancelled(Integer result) {
		// TODO Auto-generated method stub
		super.onCancelled(result);
		mNotimanager.cancel(mId);
		showError();
	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		switch (result) {
		case -1:
			showError();
			break;
		case 0:
			showComplete();
			break;
		case 1:
			showCancel();
			break;
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		mProgress = values[0];
		if (listen != null)
			listen.OnProgressUpdate(this, mProgress);
		if (mProgress <= 100 && mProgress >= 0) {
			l.i("progress : " + mProgress);
			showProgress(mProgress);
		} else if (mProgress < 0) {
			mNotimanager.cancel(mId);
			showError(mContext.getString(R.string.download_exist));
		}
	}

	private void showProgress(int progress) {
		Intent cancelIntent = new Intent(mContext, DownCancelActivity.class);
		cancelIntent.putExtra("num", mId);
		PendingIntent pi = PendingIntent.getActivity(mContext, 0, cancelIntent,
				0);
		if (Build.VERSION.SDK_INT >= 14) {
			Notification.Builder mBuilder = new Notification.Builder(mContext);
			mBuilder.setProgress(100, mProgress, false);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setContentIntent(pi);
			mBuilder.setOngoing(true);
			mBuilder.setSmallIcon(mIcon);
			mNoti = mBuilder.getNotification();
		} else {
			mNoti = new Notification();
			mNoti.setLatestEventInfo(
					mContext,
					mTitle,
					progress
							+ mContext
									.getString(R.string.download_progresstext),
					PendingIntent.getActivity(mContext, 0, new Intent(
							Intent.ACTION_VIEW, Uri.parse(mPath)),
							PendingIntent.FLAG_UPDATE_CURRENT));
			mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
			mNoti.icon = mIcon;
			mNoti.contentIntent = pi;
			mNoti.when = System.currentTimeMillis();
		}

		mNotimanager.notify(mId, mNoti);
	}

	private void showCancel() {
		if (Build.VERSION.SDK_INT > 13) {
			Notification.Builder mBuilder = new Notification.Builder(mContext);
			mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
			mBuilder.setLights(0xffff0000, 500, 500);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setSmallIcon(mIcon);
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(PendingIntent.getActivity(mContext, 0,
					new Intent(Intent.ACTION_VIEW, Uri.parse(mPath)),
					PendingIntent.FLAG_UPDATE_CURRENT));
			mBuilder.setContentText(mContext
					.getString(R.string.download_canceled));
			mBuilder.setTicker(mContext.getString(R.string.download_canceled));
			mNoti = mBuilder.getNotification();
		} else {
			mNoti = new Notification(mIcon,
					mContext.getString(R.string.download_canceled),
					System.currentTimeMillis());
			mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
			mNoti.flags |= Notification.FLAG_SHOW_LIGHTS;
			mNoti.ledARGB = 0xffff0000;
			mNoti.ledOffMS = 500;
			mNoti.ledOnMS = 500;
			mNoti.tickerText = mContext.getString(R.string.download_canceled);
			mNoti.setLatestEventInfo(mContext, mTitle, mContext
					.getString(R.string.download_canceled), PendingIntent
					.getActivity(mContext, 0, new Intent(Intent.ACTION_VIEW,
							Uri.parse(mPath)),
							PendingIntent.FLAG_UPDATE_CURRENT));
		}
		DownManager.getInstance(mContext).remove(this);
		mNotimanager.notify(mId, mNoti);
	}

	private void showError() {
		// TODO Auto-generated method stub
		showError("");
	}

	private void showError(String e) {
		if (Build.VERSION.SDK_INT > 13) {
			Notification.Builder mBuilder = new Notification.Builder(mContext);
			mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
			mBuilder.setLights(0xffff0000, 500, 500);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setSmallIcon(mIcon);
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(PendingIntent.getActivity(mContext, 0,
					new Intent(Intent.ACTION_VIEW, Uri.parse(mPath)),
					PendingIntent.FLAG_UPDATE_CURRENT));
			mBuilder.setContentText(mContext.getString(R.string.download_error)
					+ e);
			mBuilder.setTicker(mContext.getString(R.string.download_error) + e);
			mNoti = mBuilder.getNotification();
		} else {
			mNoti = new Notification(mIcon,
					mContext.getString(R.string.download_error) + e,
					System.currentTimeMillis());
			mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
			mNoti.flags |= Notification.FLAG_SHOW_LIGHTS;
			mNoti.ledARGB = 0xffff0000;
			mNoti.ledOffMS = 500;
			mNoti.ledOnMS = 500;
			mNoti.tickerText = mContext.getString(R.string.download_error) + e;
			mNoti.setLatestEventInfo(mContext, mTitle,
					mContext.getString(R.string.download_error) + e,
					PendingIntent.getActivity(mContext, 0, new Intent(
							Intent.ACTION_VIEW, Uri.parse(mPath)),
							PendingIntent.FLAG_UPDATE_CURRENT));
		}
		DownManager.getInstance(mContext).remove(this);
		mNotimanager.notify(mId, mNoti);
	}

	private void showComplete() {
		// TODO Auto-generated method stub
		Intent playerIntent;
		if (Util.App.isInstalled(mContext, "com.kr.busan.cw.cinepox.player")) {
			playerIntent = new Intent("com.kr.busan.cw.cinepox.player.PLAY");
		} else {
			playerIntent = new Intent(Intent.ACTION_VIEW);
		}
		playerIntent.setData(Uri.fromFile(new File(mPath)));
		PendingIntent intent = PendingIntent.getActivity(mContext, 0,
				playerIntent, 0);

		if (Build.VERSION.SDK_INT > 13) {
			Notification.Builder mBuilder = new Notification.Builder(mContext);
			mBuilder.setVibrate(new long[] { 0, 1000 });
			mBuilder.setLights(0xff00ff00, 500, 500);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setSmallIcon(mIcon);
			mBuilder.setContentText(mContext
					.getString(R.string.download_completed));
			mBuilder.setTicker(mContext.getString(R.string.download_completed));
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(intent);
			mNoti = mBuilder.getNotification();
		} else {
			mNoti = new Notification(mIcon,
					mContext.getString(R.string.download_completed),
					System.currentTimeMillis());
			mNoti.vibrate = new long[] { 0, 1000 };
			mNoti.ledARGB = 0xff00ff00;
			mNoti.ledOffMS = 500;
			mNoti.ledOnMS = 500;
			mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
			mNoti.setLatestEventInfo(mContext, mTitle,
					mContext.getString(R.string.download_completed), intent);
			mNoti.contentIntent = intent;
		}

		mContext.sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(
						mPath))));
		mNotimanager.notify(mId, mNoti);
	}

}
