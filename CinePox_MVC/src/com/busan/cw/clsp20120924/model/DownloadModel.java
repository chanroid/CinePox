/**
 * 0. Project  : CinePox_MVC
 *
 * 1. FileName : DownloadModel.java
 * 2. Package : com.busan.cw.clsp20120924.model
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 31. 오후 5:43:47
 * 6. 변경이력 : 
 *		2012. 10. 31. 오후 5:43:47 : 신규
 *
 */
package com.busan.cw.clsp20120924.model;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.base.Model;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : DownloadModel.java
 * 3. Package  : com.busan.cw.clsp20120924.model
 * 4. Comment  : 다운로드 동작 관리
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 5:43:47
 * </PRE>
 */
public class DownloadModel extends Model {
	private static DownloadModel instance;

	private ArrayList<Downloader> mStack;
	private Downloader.Callback mDownloadCallback;

	public static DownloadModel getInstance(Context ctx) {
		if (instance == null)
			instance = new DownloadModel();
		return instance;
	}

	public static void clearInstance() {
		instance = null;
	}

	private DownloadModel() {
		initDownloadPath();
		mStack = new ArrayList<Downloader>();
	}
	
	public void setDownloadCallback(Downloader.Callback callback) {
		mDownloadCallback = callback;
	}
	
	public void addDownload(String parseUrl) {
		
	}

	public boolean add(String url, String title) {
		Downloader.Builder builder = new Downloader.Builder();
		builder.setURL(url);
		builder.setId(mStack.size());
		builder.setTitle(title);
		builder.setCallback(mDownloadCallback);
		builder.setIconResources(R.drawable.ic_launcher);
		Downloader d = builder.build();
		if (mStack.add(d)) {
			d.start();
			return true;
		} else
			return false;
	}
	
	public void remove(int id) {
		mStack.remove(id);
	}

	public void initDownloadPath() {
		File dir = new File(Environment.getExternalStorageDirectory()
				+ "/cinepox");
		if (!dir.exists())
			dir.mkdirs();
	}

}
