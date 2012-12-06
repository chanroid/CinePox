package com.busan.cw.clsp20120924.downloader;

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
import android.support.v4.app.NotificationCompat;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.movie.CinepoxService;
import com.kr.busan.cw.cinepox.player.controller.PlayerActivity;

@SuppressLint({ "HandlerLeak", "NewApi" })
@SuppressWarnings("deprecation")
public class Downloader extends AsyncTask<String, Integer, Integer> implements
		com.kr.busan.cw.cinepox.player.base.Constants {

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
	private PendingIntent mEmptyPendingIntent;
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
		mEmptyPendingIntent = PendingIntent.getActivity(mContext, 0,
				new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
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

		int result = 0;
		if (fileSize < lenghtOfFile) {
			int readBytes = 0;
			byte data[] = new byte[input.available()];
			while ((readBytes = input.read(data)) != -1) {
				if (isCanceled) {
					result = 1;
					break;
				}
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
		return result;
	}

	public void removeNoti() {
		mNotimanager.cancelAll();
	}

	class ServiceHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Intent playerIntent = new Intent(mContext, PlayerActivity.class);
				PendingIntent i = PendingIntent.getActivity(mContext, 0,
						playerIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
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
					mBuilder.setDeleteIntent(mEmptyPendingIntent);
					if (Build.VERSION.SDK_INT >= 16) {
						mBuilder.addAction(
								android.R.drawable.ic_menu_slideshow, "열기", i);
						mNoti = mBuilder.build();
					} else
						mNoti = mBuilder.getNotification();
				} else {
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
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
					mBuilder.setDeleteIntent(mEmptyPendingIntent);
					mNoti = mBuilder.getNotification();
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
					mBuilder.setContentIntent(mEmptyPendingIntent);
					if (Build.VERSION.SDK_INT >= 16)
						mNoti = mBuilder.build();
					else
						mNoti = mBuilder.getNotification();
				} else {
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							mContext);
					mBuilder.setContentTitle(mTitle);
					mBuilder.setSmallIcon(mIcon);
					mBuilder.setContentText(mContext
							.getString(R.string.download_preparing));
					mBuilder.setTicker(mContext
							.getString(R.string.download_preparing));
					mBuilder.setContentIntent(mEmptyPendingIntent);
					mNoti = mBuilder.getNotification();
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

	private void notifyDownloadStart() {
		// TODO Auto-generated method stub
		h.sendEmptyMessage(1);
	}

	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			return start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendError(Throwable t) {
		Intent i = new Intent(CinepoxService.ACTION_SEND_ERRORLOG);
		i.putExtra(KEY_EXCEPTION, t);
		i.putExtra(KEY_MOVIE_URL, mUrl);
		mContext.sendBroadcast(i);
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
				Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT >= 14) {
			Notification.Builder mBuilder = new Notification.Builder(mContext);
			mBuilder.setProgress(100, mProgress, false);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setContentIntent(pi);
			mBuilder.setOngoing(true);
			mBuilder.setSmallIcon(mIcon);
			if (Build.VERSION.SDK_INT >= 16)
				mNoti = mBuilder.build();
			else
				mNoti = mBuilder.getNotification();
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					mContext);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setContentIntent(pi);
			mBuilder.setContentText(progress
					+ mContext.getString(R.string.download_progresstext));
			mBuilder.setOngoing(true);
			mBuilder.setSmallIcon(mIcon);
			mNoti = mBuilder.getNotification();
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
			mBuilder.setContentIntent(mEmptyPendingIntent);
			mBuilder.setDeleteIntent(mEmptyPendingIntent);
			mBuilder.setContentText(mContext
					.getString(R.string.download_canceled));
			mBuilder.setTicker(mContext.getString(R.string.download_canceled));
			mNoti = mBuilder.getNotification();
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					mContext);
			mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
			mBuilder.setLights(0xffff0000, 500, 500);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setSmallIcon(mIcon);
			mBuilder.setAutoCancel(true);
			mBuilder.setDeleteIntent(mEmptyPendingIntent);
			mBuilder.setContentIntent(mEmptyPendingIntent);
			mBuilder.setContentText(mContext
					.getString(R.string.download_canceled));
			mBuilder.setTicker(mContext.getString(R.string.download_canceled));
			mNoti = mBuilder.getNotification();
		}
		DownManager.getInstance(mContext).remove(this);
		mNotimanager.notify(mId, mNoti);
	}

	private void showError() {
		// TODO Auto-generated method stub
		showError("");
	}

	private void showError(String e) {
		Intent cancelIntent = new Intent(mContext, DownRestartActivity.class);
		cancelIntent.putExtra("num", mId);
		PendingIntent pi = PendingIntent.getActivity(mContext, 0, cancelIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if (Build.VERSION.SDK_INT > 13) {
			Notification.Builder mBuilder = new Notification.Builder(mContext);
			mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
			mBuilder.setLights(0xffff0000, 500, 500);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setSmallIcon(mIcon);
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(pi);
			mBuilder.setDeleteIntent(mEmptyPendingIntent);
			mBuilder.setContentText(mContext.getString(R.string.download_error)
					+ e);
			mBuilder.setTicker(mContext.getString(R.string.download_error) + e);
			if (Build.VERSION.SDK_INT >= 16) {
				mNoti = mBuilder.build();
			} else
				mNoti = mBuilder.getNotification();
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					mContext);
			mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
			mBuilder.setLights(0xffff0000, 500, 500);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setSmallIcon(mIcon);
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(pi);
			mBuilder.setDeleteIntent(mEmptyPendingIntent);
			mBuilder.setContentText(mContext.getString(R.string.download_error)
					+ e);
			mBuilder.setTicker(mContext.getString(R.string.download_error) + e);
			mNoti = mBuilder.getNotification();
		}
		mNotimanager.notify(mId, mNoti);
	}

	private void showComplete() {
		// TODO Auto-generated method stub
		Intent playerIntent = new Intent(mContext, PlayerActivity.class);
		playerIntent.setData(Uri.fromFile(new File(mPath)));
		PendingIntent intent = PendingIntent.getActivity(mContext, 0,
				playerIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

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
			mBuilder.setDeleteIntent(mEmptyPendingIntent);
			if (Build.VERSION.SDK_INT >= 16) {
				mBuilder.addAction(android.R.drawable.ic_menu_slideshow, "열기",
						intent);
				mNoti = mBuilder.build();
			} else
				mNoti = mBuilder.getNotification();
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					mContext);
			mBuilder.setVibrate(new long[] { 0, 1000 });
			mBuilder.setLights(0xff00ff00, 500, 500);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setSmallIcon(mIcon);
			mBuilder.setContentText(mContext
					.getString(R.string.download_completed));
			mBuilder.setTicker(mContext.getString(R.string.download_completed));
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(intent);
			mBuilder.setDeleteIntent(mEmptyPendingIntent);
			mNoti = mBuilder.getNotification();
		}

		mContext.sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(
						mPath))));
		mNotimanager.notify(mId, mNoti);
	}

}
