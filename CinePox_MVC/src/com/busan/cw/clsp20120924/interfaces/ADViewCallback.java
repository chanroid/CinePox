package com.busan.cw.clsp20120924.interfaces;

import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.widget.CompoundButton.OnCheckedChangeListener;

public interface ADViewCallback extends OnClickListener,
		OnCheckedChangeListener {
	public WebChromeClient setWebClient();
}
