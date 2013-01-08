package com.kr.busan.cw.cinepox.player.model;

import android.content.Context;

public class BookmarkModel extends Model {
	private static BookmarkModel instance;
	private Context mContext;

	private String mBookmarkDeleteUrl;
	private String mBookmarkListUrl;
	private String mBookmarkInsertUrl;
	
	public static BookmarkModel getInstance(Context ctx) {
		if (instance == null)
			instance = new BookmarkModel(ctx);
		return instance;
	}

	public static void clearInstance() {
		instance = null;
	}

	private BookmarkModel(Context ctx) {
		mContext = ctx;

	}
	
	public void setInsertUrl(String url) {
		mBookmarkInsertUrl = url;
	}
	
	public void setDeleteUrl(String url) {
		mBookmarkDeleteUrl = url;
	}
	
	public void setListUrl(String url) {
		mBookmarkListUrl = url;
	}
}
