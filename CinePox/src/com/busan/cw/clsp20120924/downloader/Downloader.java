package com.busan.cw.clsp20120924.downloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

import utils.MathUtils;
import utils.StorageUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.busan.cw.clsp20120924.R;
import com.kr.busan.cw.cinepox.player.controller.PlayerActivity;

public class Downloader extends Handler implements Runnable {

	private static final int MESSAGE_NOTIFY_PROGRESS = -1;
	private static final int MESSAGE_NOTIFY_START = 0;
	private static final int MESSAGE_NOTIFY_EXISTS = 1;
	private static final int MESSAGE_NOTIFY_CANCELED = 2;
	private static final int MESSAGE_NOTIFY_ERROR = 3;
	private static final int MESSAGE_NOTIFY_COMPLETED = 4;
	private static final int MESSAGE_NOTIFY_NOT_ENOUGH_SIZE = 5;

	private DownloadData mData;
	private BlockingQueue<Downloader> mQueue;
	private NotificationManager mNotiManager;
	private PendingIntent mEmptyPendingIntent;
	private Notification mNoti;
	private Context mContext;
	private int mNotiId;
	private int mTimeout = 60000;
	private int mProgress = -1;
	private int mIcon = R.drawable.ic_launcher;
	private boolean isCanceled = false;

	public Downloader(Context ctx, BlockingQueue<Downloader> queue,
			NotificationManager notiManager, DownloadData data) {
		// TODO Auto-generated constructor stub

		mQueue = queue;
		mData = data;
		mContext = ctx;
		mNotiManager = notiManager;

		// 스택에 있는 애들이면 아예 추가 안함
		if (checkDuplecate(this))
			return;

		try {
			mQueue.put(this);
			mNotiId = DownManager.NOTIFICATION_COUNT;
			mEmptyPendingIntent = PendingIntent.getActivity(mContext, 0,
					new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
		} catch (InterruptedException e) {
			mQueue.remove(this);
		}
	}

	private boolean checkDuplecate(Downloader data) {
		for (Downloader queue : mQueue) {
			boolean equalName = data.mData.filePath
					.equals(queue.mData.filePath);
			boolean equalUrl = data.mData.fileUrl.equals(queue.mData.fileUrl);
			if (equalName || equalUrl)
				return true;
		}
		return false;
	}

	public int getNum() {
		return mNotiId;
	}

	public DownloadData getData() {
		return mData;
	}

	public void cancel() {
		isCanceled = true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		Intent playerIntent = new Intent(mContext, PlayerActivity.class);
		playerIntent.setData(Uri.fromFile(new File(mData.filePath)));
		PendingIntent playerPi = PendingIntent.getActivity(mContext, 0,
				playerIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		Intent cancelIntent = new Intent(mContext, DownCancelActivity.class);
		cancelIntent.putExtra("num", mNotiId);
		PendingIntent cancelPi = PendingIntent.getActivity(mContext, 0,
				cancelIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		Intent restartIntent = new Intent(mContext, DownRestartActivity.class);
		restartIntent.putExtra("num", mNotiId);
		PendingIntent restartPi = PendingIntent.getActivity(mContext, 0,
				restartIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		if (Build.VERSION.SDK_INT > 13) {
			Notification.Builder mBuilder = new Notification.Builder(mContext);
			switch (msg.what) {
			case MESSAGE_NOTIFY_START:
				mBuilder.setContentTitle(mData.title);
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
				break;
			case MESSAGE_NOTIFY_EXISTS:
				mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
				mBuilder.setContentTitle(mData.title);
				mBuilder.setAutoCancel(true);
				mBuilder.setSmallIcon(mIcon);
				mBuilder.setContentText(mContext
						.getString(R.string.download_already_completed));
				mBuilder.setTicker(mContext
						.getString(R.string.download_already_completed));
				mBuilder.setContentIntent(playerPi);
				mBuilder.setDeleteIntent(mEmptyPendingIntent);
				if (Build.VERSION.SDK_INT >= 16)
					mNoti = mBuilder.build();
				else
					mNoti = mBuilder.getNotification();
				break;
			case MESSAGE_NOTIFY_CANCELED:
				mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
				mBuilder.setLights(0xffff0000, 500, 500);
				mBuilder.setContentTitle(mData.title);
				mBuilder.setSmallIcon(mIcon);
				mBuilder.setAutoCancel(true);
				mBuilder.setContentIntent(mEmptyPendingIntent);
				mBuilder.setDeleteIntent(mEmptyPendingIntent);
				mBuilder.setContentText(mContext
						.getString(R.string.download_canceled));
				mBuilder.setTicker(mContext
						.getString(R.string.download_canceled));
				mNoti = mBuilder.getNotification();
				break;
			case MESSAGE_NOTIFY_ERROR:
				mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
				mBuilder.setLights(0xffff0000, 500, 500);
				mBuilder.setContentTitle(mData.title);
				mBuilder.setSmallIcon(mIcon);
				mBuilder.setAutoCancel(true);
				mBuilder.setContentIntent(restartPi);
				mBuilder.setDeleteIntent(mEmptyPendingIntent);
				mBuilder.setContentText(mContext
						.getString(R.string.download_error)
						+ ((Exception) msg.obj).getMessage());
				mBuilder.setTicker(mContext.getString(R.string.download_error)
						+ ((Exception) msg.obj).getMessage());
				if (Build.VERSION.SDK_INT >= 16) {
					mNoti = mBuilder.build();
				} else
					mNoti = mBuilder.getNotification();
				break;
			case MESSAGE_NOTIFY_COMPLETED:
				mBuilder.setVibrate(new long[] { 0, 1000 });
				mBuilder.setLights(0xff00ff00, 500, 500);
				mBuilder.setContentTitle(mData.title);
				mBuilder.setSmallIcon(mIcon);
				mBuilder.setContentText(mContext
						.getString(R.string.download_completed));
				mBuilder.setTicker(mContext
						.getString(R.string.download_completed));
				mBuilder.setAutoCancel(true);
				mBuilder.setContentIntent(playerPi);
				mBuilder.setDeleteIntent(mEmptyPendingIntent);
				if (Build.VERSION.SDK_INT >= 16) {
					mNoti = mBuilder.build();
				} else
					mNoti = mBuilder.getNotification();
				mContext.sendBroadcast(new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
								.fromFile(new File(mData.filePath))));
				break;
			case MESSAGE_NOTIFY_PROGRESS:
				mBuilder.setProgress(100, mProgress, false);
				mBuilder.setContentTitle(mData.title);
				mBuilder.setContentIntent(cancelPi);
				mBuilder.setOngoing(true);
				mBuilder.setSmallIcon(mIcon);
				if (Build.VERSION.SDK_INT >= 16)
					mNoti = mBuilder.build();
				else
					mNoti = mBuilder.getNotification();
				break;
			case MESSAGE_NOTIFY_NOT_ENOUGH_SIZE:
				mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
				mBuilder.setLights(0xff00ff00, 500, 500);
				mBuilder.setContentTitle(mData.title);
				mBuilder.setSmallIcon(mIcon);
				mBuilder.setContentText(mContext
						.getString(R.string.download_notenouchsize));
				mBuilder.setTicker(mContext
						.getString(R.string.download_notenouchsize));
				mBuilder.setAutoCancel(true);
				mBuilder.setContentIntent(mEmptyPendingIntent);
				mBuilder.setDeleteIntent(mEmptyPendingIntent);
				if (Build.VERSION.SDK_INT >= 16) {
					mNoti = mBuilder.build();
				} else
					mNoti = mBuilder.getNotification();
				break;
			default:
				break;
			}
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					mContext);
			switch (msg.what) {
			case MESSAGE_NOTIFY_START:
				mBuilder.setContentTitle(mData.title);
				mBuilder.setSmallIcon(mIcon);
				mBuilder.setContentText(mContext
						.getString(R.string.download_preparing));
				mBuilder.setTicker(mContext
						.getString(R.string.download_preparing));
				mBuilder.setContentIntent(mEmptyPendingIntent);
				mNoti = mBuilder.getNotification();
				break;
			case MESSAGE_NOTIFY_EXISTS:
				mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
				mBuilder.setContentTitle(mData.title);
				mBuilder.setAutoCancel(true);
				mBuilder.setSmallIcon(mIcon);
				mBuilder.setContentText(mContext
						.getString(R.string.download_already_completed));
				mBuilder.setTicker(mContext
						.getString(R.string.download_already_completed));
				mBuilder.setContentIntent(playerPi);
				mBuilder.setDeleteIntent(mEmptyPendingIntent);
				mNoti = mBuilder.getNotification();
				break;
			case MESSAGE_NOTIFY_CANCELED:
				mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
				mBuilder.setLights(0xffff0000, 500, 500);
				mBuilder.setContentTitle(mData.title);
				mBuilder.setSmallIcon(mIcon);
				mBuilder.setAutoCancel(true);
				mBuilder.setDeleteIntent(mEmptyPendingIntent);
				mBuilder.setContentIntent(mEmptyPendingIntent);
				mBuilder.setContentText(mContext
						.getString(R.string.download_canceled));
				mBuilder.setTicker(mContext
						.getString(R.string.download_canceled));
				mNoti = mBuilder.getNotification();
				break;
			case MESSAGE_NOTIFY_ERROR:
				mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
				mBuilder.setLights(0xffff0000, 500, 500);
				mBuilder.setContentTitle(mData.title);
				mBuilder.setSmallIcon(mIcon);
				mBuilder.setAutoCancel(true);
				mBuilder.setContentIntent(restartPi);
				mBuilder.setDeleteIntent(mEmptyPendingIntent);
				mBuilder.setContentText(mContext
						.getString(R.string.download_error)
						+ ((Exception) msg.obj).getMessage());
				mBuilder.setTicker(mContext.getString(R.string.download_error)
						+ ((Exception) msg.obj).getMessage());
				mNoti = mBuilder.getNotification();
				break;
			case MESSAGE_NOTIFY_COMPLETED:
				mBuilder.setVibrate(new long[] { 0, 1000 });
				mBuilder.setLights(0xff00ff00, 500, 500);
				mBuilder.setContentTitle(mData.title);
				mBuilder.setSmallIcon(mIcon);
				mBuilder.setContentText(mContext
						.getString(R.string.download_completed));
				mBuilder.setTicker(mContext
						.getString(R.string.download_completed));
				mBuilder.setAutoCancel(true);
				mBuilder.setContentIntent(playerPi);
				mBuilder.setDeleteIntent(mEmptyPendingIntent);
				mNoti = mBuilder.getNotification();
				mContext.sendBroadcast(new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
								.fromFile(new File(mData.filePath))));
				break;
			case MESSAGE_NOTIFY_PROGRESS:
				mBuilder.setContentTitle(mData.title);
				mBuilder.setContentIntent(cancelPi);
				mBuilder.setContentText(mProgress
						+ mContext.getString(R.string.download_progresstext));
				mBuilder.setOngoing(true);
				mBuilder.setSmallIcon(mIcon);
				mNoti = mBuilder.getNotification();
				break;
			case MESSAGE_NOTIFY_NOT_ENOUGH_SIZE:
				mBuilder.setVibrate(new long[] { 0, 1000 });
				mBuilder.setLights(0xff00ff00, 500, 500);
				mBuilder.setContentTitle(mData.title);
				mBuilder.setSmallIcon(mIcon);
				mBuilder.setContentText(mContext
						.getString(R.string.download_notenouchsize));
				mBuilder.setTicker(mContext
						.getString(R.string.download_notenouchsize));
				mBuilder.setAutoCancel(true);
				mBuilder.setContentIntent(mEmptyPendingIntent);
				mBuilder.setDeleteIntent(mEmptyPendingIntent);
				mNoti = mBuilder.getNotification();
				break;
			default:
				break;
			}
		}
		mNotiManager.notify(mNotiId, mNoti);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		sendEmptyMessage(MESSAGE_NOTIFY_START);

		BufferedInputStream input = null;
		RandomAccessFile output = null;
		HttpURLConnection conn = null;
		long fileSize, remains, lenghtOfFile = 0;

		try {

			File file = new File(mData.filePath);
			if (!file.exists())
				file.createNewFile();

			output = new RandomAccessFile(file.getAbsolutePath(), "rw");
			output.seek(fileSize = output.length());

			conn = (HttpURLConnection) new URL(mData.fileUrl).openConnection();
			conn.addRequestProperty("Range",
					"bytes=" + String.valueOf(fileSize) + '-');
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

			// 2013-01-07 외장메모리 용량검사
			long externalRemain = StorageUtils.getAvailableExternalMemorySize();
			if (externalRemain < remains) {
				sendEmptyMessage(MESSAGE_NOTIFY_NOT_ENOUGH_SIZE);
				output.close();
				conn.disconnect();
				return;
			}

			lenghtOfFile = remains + fileSize;

			if (remains <= 0) {
				sendEmptyMessage(MESSAGE_NOTIFY_EXISTS);
				output.close();
				conn.disconnect();
				return;
			}

			input = new BufferedInputStream(conn.getInputStream());

			if (fileSize < lenghtOfFile) {
				int readBytes = 0;
				byte data[] = new byte[input.available()];
				while ((readBytes = input.read(data)) != -1) {
					if (isCanceled) {
						sendEmptyMessage(MESSAGE_NOTIFY_CANCELED);
						break;
					}
					output.write(data, 0, readBytes);
					fileSize += readBytes;
					if (mProgress != MathUtils.getPercent(fileSize,
							lenghtOfFile)) {
						Message msg = new Message();
						msg.what = MESSAGE_NOTIFY_PROGRESS;
						mProgress = MathUtils
								.getPercent(fileSize, lenghtOfFile);
						msg.arg1 = mProgress;
						sendMessage(msg);
					}
				}
			}

			if (!isCanceled)
				sendEmptyMessage(MESSAGE_NOTIFY_COMPLETED);
		} catch (IOException e) {
			e.printStackTrace();
			Message msg = new Message();
			msg.what = MESSAGE_NOTIFY_ERROR;
			msg.obj = e;
			sendMessage(msg);
		} finally {
			try {
				if (input != null)
					input.close();
				if (output != null)
					output.close();
				if (conn != null)
					conn.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
