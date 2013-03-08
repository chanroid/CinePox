package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.view.MovieSNSReplyView;

public interface MovieSNSReplyViewCallback {
	public void onReplyWriteClick(MovieSNSReplyView view);
	public void onReplyExpandClick(MovieSNSReplyView view);
	public void onReplyProfileClick(MovieSNSReplyView view, int index);
}
