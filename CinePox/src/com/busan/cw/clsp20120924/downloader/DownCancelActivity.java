package com.busan.cw.clsp20120924.downloader;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.busan.cw.clsp20120924.R;

public class DownCancelActivity extends Activity {

	DownManager mng;
	int mId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mId = getIntent().getIntExtra("num", -1);
		if (mId < 0) {
			finish();
			return;
		}

		mng = DownManager.getInstance(this);
		Downloader down = mng.get(mId);
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
		dialog.setTitle(R.string.download_cancel);
		dialog.setCancelable(false);
		dialog.setMessage(title + getString(R.string.noti_download_cancel));
		dialog.setPositiveButton(R.string.cancel, cancelListener);
		dialog.setNeutralButton(R.string.delete, deleteListener);
		dialog.setNegativeButton(R.string.continue_, continueListener);
		dialog.show();
	}

	DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			mng.remove(mId);
			finish();
		}
	};

	DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			cancelListener.onClick(dialog, which);
			if (mng.get(mId) != null)
				new File(mng.get(mId).getPath()).delete();
			finish();
		}
	};

	DialogInterface.OnClickListener continueListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			finish();
		}
	};

}
