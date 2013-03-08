package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.SNSLoginData;
import com.busan.cw.clsp20120924.model.TwitterModel;

public interface TwitterModelUserInfoCallback {

	public void onTwitterUserInfoStart(TwitterModel model);

	public void onTwitterUserInfoError(TwitterModel model, String message);

	public void onTwitterUserInfoComplete(TwitterModel model, SNSLoginData data);
}
