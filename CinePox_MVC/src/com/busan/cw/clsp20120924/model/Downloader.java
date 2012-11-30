package com.busan.cw.clsp20120924.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.co.chan.util.Util;
import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class Downloader {
	public interface Callback {
		public void onStart(Downloader d);

		public void onCanceled(Downloader d);

		public void onPrepared(Downloader d);

		public void onError(Downloader d, int what);

		public void onProgress(Downloader d, int progress);

		public void onCompleted(Downloader d);
	}

	private Handler callbackHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mCallback == null)
				return;
			switch (msg.what) {
			case CALLBACK_START:
				mCallback.onStart(Downloader.this);
				break;
			case CALLBACK_ERROR:
			case CALLBACK_ERROR_DOWNLOADED:
				mCallback.onError(Downloader.this, msg.what);
				break;
			case CALLBACK_CANCELED:
				mCallback.onCanceled(Downloader.this);
				break;
			case CALLBACK_PROGRESS:
				mCallback.onProgress(Downloader.this, msg.arg1);
				break;
			case CALLBACK_COMPLETED:
				mCallback.onCompleted(Downloader.this);
			}
		};
	};

	private class DownloadThread extends Thread {
		public void run() {
			RandomAccessFile output = null;
			BufferedInputStream input = null;
			HttpURLConnection conn = null;

			callbackHandler.sendEmptyMessage(CALLBACK_START);

			try {
				long fileSize, remains, lenghtOfFile = 0;
				File file = new File(mDownPath);
				if (!file.exists())
					file.createNewFile();

				output = new RandomAccessFile(file.getAbsolutePath(), "rw");
				output.seek(fileSize = output.length());

				conn = (HttpURLConnection) new URL(mUrl).openConnection();
				conn.addRequestProperty("Range",
						"bytes=" + String.valueOf(fileSize) + '-');
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(10000);
				conn.setAllowUserInteraction(true);
				conn.setDoInput(true);
				conn.setDoOutput(false);
				if (conn.getResponseCode() == -1) {
					conn.disconnect();
				}
				remains = conn.getContentLength();

				lenghtOfFile = remains + fileSize;

				if (remains <= 0) {
					callbackHandler.sendEmptyMessage(CALLBACK_ERROR_DOWNLOADED);
					output.close();
					conn.disconnect();
					return;
				}

				input = new BufferedInputStream(conn.getInputStream());

				Message msg = new Message();
				msg.what = CALLBACK_PROGRESS;
				msg.arg1 = 0;
				callbackHandler.sendMessage(msg);

				if (fileSize < lenghtOfFile) {
					int readBytes = 0;
					byte data[] = new byte[input.available()];
					while ((readBytes = input.read(data)) != -1) {
						if (isCanceled)
							break;
						output.write(data, 0, readBytes);
						fileSize += readBytes;
						if (mProgress != Util.Math.getPercent(fileSize,
								lenghtOfFile)) {
							msg.arg1 = Util.Math.getPercent(fileSize,
									lenghtOfFile);
							callbackHandler.sendMessage(msg);
							mProgress = msg.arg1;
						}
					}
				}
				callbackHandler.sendEmptyMessage(CALLBACK_COMPLETED);
			} catch (Exception e) {
				callbackHandler.sendEmptyMessage(CALLBACK_ERROR);
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

	private Thread downThread;
	private boolean isCanceled = false;
	private String mUrl;
	private int mId;
	private int mIcon;
	private int mProgress = -1;
	private String mTitle;
	private String mDownPath;
	private Callback mCallback;

	public static final int CALLBACK_START = 0;
	public static final int CALLBACK_ERROR_DOWNLOADED = 1;
	public static final int CALLBACK_ERROR = 2;
	public static final int CALLBACK_PROGRESS = 3;
	public static final int CALLBACK_COMPLETED = 4;
	public static final int CALLBACK_CANCELED = 5;

	private static String DOWNLOAD_DIR = Environment
			.getExternalStorageDirectory() + "/cinepox";

	public static class Builder {
		private String mUrl;
		private int mId;
		private int mIcon;
		private String mTitle;
		private Callback mCallback;

		public Builder() {
		}

		public void setURL(String url) {
			mUrl = url;
		}

		public void setId(int id) {
			mId = id;
		}

		public void setIconResources(int resid) {
			mIcon = resid;
		}

		public void setTitle(String title) {
			mTitle = title;
		}

		public void setCallback(Callback callback) {
			mCallback = callback;
		}

		public Downloader build() {
			if (mUrl == null || mIcon == 0 || mTitle == null)
				throw new IllegalArgumentException("not enough parameters");
			String ext = Util.File.getExtension(mUrl);
			return new Downloader(mUrl, mId, mIcon, mTitle, DOWNLOAD_DIR + "/"
					+ mTitle + "." + ext, mCallback);
		}
	}

	private Downloader(String url, int id, int icon, String title,
			String fileName, Callback callback) {
		mUrl = url;
		mId = id;
		mIcon = icon;
		mTitle = title;
		mDownPath = fileName;
	}

	public void start() {
		downThread = new DownloadThread();
		downThread.start();
	}

	public void cancel() {
		isCanceled = true;
	}

	public int getId() {
		// TODO Auto-generated method stub
		return mId;
	}

	public int getIcon() {
		// TODO Auto-generated method stub
		return mIcon;
	}

	public String getTitle() {
		return mTitle;
	}
	
	public String getFilePath() {
		return mDownPath;
	}

}
