package com.kr.busan.cw.cinepox.downloader;

import java.util.ArrayList;

import kr.co.chan.util.l;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.busan.cw.clsp20120924.R;

/**
 * 이슈사항 정리
 * 
 * 공통 - 다운로드 알림이 뜨지 않는 경우 - 앱을 재실행 하거나 - 다운로드를 다시 시도하거나 - 휴대폰을 재부팅 하면 해결됨
 * 
 * 갤럭시노트(LGT) - 다운로드 진행이 되지 않는 경우 - 휴대폰을 재부팅 하면 해결됨
 * 
 * @author CINEPOX
 * 
 */

public class DownManager implements
		com.kr.busan.cw.cinepox.downloader.Downloader.OnProgressUpdateListener {

	private static DownManager instance;
	public static String DOWNLOAD_PATH = Environment
			.getExternalStorageDirectory() + "/cinepox";

	private Context mContext;

	private DownManager(Context ctx) {
		mQueueStack = new ArrayList<Downloader>();
		mContext = ctx.getApplicationContext();
	}

	public static DownManager getInstance(Context ctx) {
		if (instance == null)
			instance = new DownManager(ctx);
		return instance;
	}

	public interface OnProgressUpdateListener {
		/**
		 * @param id
		 *            Downloader Object's Unique id.
		 * @param progress
		 *            Download progress.
		 */
		public void OnProgressUpdate(int id, int progress);
	}

	private ArrayList<Downloader> mQueueStack;
	private OnProgressUpdateListener listen;

	public void setCallBack(OnProgressUpdateListener l) {
		listen = l;
	}

	boolean checkDuplicateUrl(String url) {
		for (Downloader d : mQueueStack) {
			String path = Uri.parse(d.getUrl()).getPath();
			String urlpath = Uri.parse(url).getPath();
			if (path.equals(urlpath))
				return true;
		}
		return false;
	}

	public int queue(String url, String path, int icon, String title) {
		try {
			final int id = mQueueStack.size();
			if (checkDuplicateUrl(url)) {
				l.i("this file is already downloading. ignore request");
				return -1;
			}
			Downloader down = new Downloader(mContext, url, path, id, icon,
					title);
			down.setOnProgressUpdateListener(this);
			mQueueStack.add(down);
			down.execute();
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			l.i("download request error : " + e.getMessage());
			return -2;
		}
	}

	public int queue(String url) {
		String filename = url.split("/")[url.split("/").length - 1];
		return queue(url, DOWNLOAD_PATH + "/" + filename,
				R.drawable.ic_launcher, filename);
	}

	public int queue(Downloader param) {
		mQueueStack.remove(param);
		return queue(param.getUrl(), param.getPath(),
				com.kr.busan.cw.cinepox.R.drawable.ic_launcher,
				param.getTitle());
	}

	public void remove(Downloader d) {
		d.performCancel();
		mQueueStack.remove(d);
	}

	public void remove(int id) {
		get(id).performCancel();
		mQueueStack.remove(id);
	}

	public Downloader get(int id) {
		for (Downloader d : mQueueStack) {
			if (d.getId() == id)
				return d;
		}
		return null;
	}

	@Override
	public void OnProgressUpdate(Downloader d, int progress) {
		// TODO Auto-generated method stub
		if (listen != null)
			listen.OnProgressUpdate(d.getId(), progress);
		if (progress == 100)
			mQueueStack.remove(d);
	}

}
