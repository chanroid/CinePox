/**
 * 0. Project  : CinePox
 *
 * 1. FileName : DownRestartActivity.java
 * 2. Package : com.kr.busan.cw.cinepox.downloader
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 28. 오후 3:46:15
 * 6. 변경이력 : 
 *		2012. 10. 28. 오후 3:46:15 : 신규
 *
 */
package com.kr.busan.cw.cinepox.downloader;

import com.busan.cw.clsp20120924.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : DownRestartActivity.java
 * 3. Package  : com.kr.busan.cw.cinepox.downloader
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 28. 오후 3:46:15
 * </PRE>
 */
public class DownRestartActivity extends Activity {
	DownManager mng;
	Downloader down;
	int mId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mId = getIntent().getIntExtra("num", -1);
		if (mId < 0) {
			finish();
			return;
		}

		mng = DownManager.getInstance(this);
		down = mng.get(mId);
		if (down == null) {
			finish();
			return;
		}

		String title = down.getTitle();
		if (title != null)
			title = "[" + title + "]\n";
		else {
			finish();
			return;
		}

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setCancelable(false);
		dialog.setTitle(R.string.download_cancel);
		dialog.setMessage(title + getString(R.string.download_restart));
		dialog.setPositiveButton(R.string.cancel, cancelListener);
		dialog.setNegativeButton(R.string.continue_, continueListener);
		dialog.show();

	}

	DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			mng.remove(down);
			finish();
		}
	};

	DialogInterface.OnClickListener continueListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			mng.queue(down);
			finish();
		}
	};

}
