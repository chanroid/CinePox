package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.TwitterModel;

public interface TwitterModelPostCallback {

	public void onTwitterPostStart(TwitterModel model);

	public void onTwitterPostError(TwitterModel model, String message);

	public void onTwitterPostComplete(TwitterModel model);
}
