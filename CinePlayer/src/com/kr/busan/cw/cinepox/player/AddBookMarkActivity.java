package com.kr.busan.cw.cinepox.player;

import kr.co.chan.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.kr.busan.cw.cinepox.R;

public class AddBookMarkActivity extends Activity {
	
	private TextView mTimeText;
	private String mTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bookmark);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mTime = getIntent().getStringExtra(Intent.EXTRA_TEXT);
		mTimeText = (TextView) findViewById(R.id.textview_bookmark_time);
		mTimeText.setText(Util.Time.stringForTime(mTime));
	}

}
