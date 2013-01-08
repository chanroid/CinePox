package com.busan.cw.clsp20120924.downloader;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.NotificationManager;
import android.content.Context;

public class DownManager2 {
	private static DownManager2 instance;
	public static int NOTIFICATION_COUNT = Integer.MAX_VALUE;
	private NotificationManager mNotiManager;
	private BlockingQueue<Downloader2> mQueue;
	private Context mContext;

	public static DownManager2 getInstance(Context ctx) {
		if (instance == null)
			instance = new DownManager2(ctx);
		return instance;
	}

	public static void clearInstance() {
		instance = null;
	}

	private DownManager2(Context ctx) {
		mQueue = new LinkedBlockingQueue<Downloader2>();
		mContext = ctx;
		mNotiManager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public Downloader2 getQueue(int num) {
		for (Downloader2 data : mQueue) {
			if (data.getNum() == num)
				return data;
		}
		return null;
	}

	public void restart(int num) throws InterruptedException {
		Downloader2 executor = getQueue(num);
		if (executor != null) {
			executor.cancel();
			mQueue.remove(executor);
			DownloadData data = executor.getData();
			queue(data);
		}
	}

	public void cancel(int num, boolean delete) {
		Downloader2 executor = getQueue(num);
		if (executor != null) {
			executor.cancel();
			if (delete)
				new File(executor.getData().filePath).delete();
			mQueue.remove(executor);
		}
	}

	public void queue(DownloadData data) throws InterruptedException {
		Downloader2 downloader = new Downloader2(mContext, mQueue,
				mNotiManager, data);
		Thread worker = new Thread(downloader);
		worker.start();
		if (NOTIFICATION_COUNT == Integer.MIN_VALUE)
			NOTIFICATION_COUNT = Integer.MAX_VALUE;
		NOTIFICATION_COUNT -= 1;
	}

	public void cancelAllDownload() {
		for (Downloader2 data : mQueue) {
			data.cancel();
		}
		mQueue.clear();
	}

}
