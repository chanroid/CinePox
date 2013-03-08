package com.busan.cw.clsp20120924.view;

import view.CCView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.Toast;

import com.busan.cw.clsp20120924_beta.R;
import com.busan.cw.clsp20120924.interfaces.IntroViewCallback;

public class IntroView extends CCView {

	public IntroViewCallback callback;

	public void setCallback(IntroViewCallback callback) {
		this.callback = callback;
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.intro;
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub

	}

	public void showAgree() {
		Toast.makeText(getContext(), R.string.join_agree, Toast.LENGTH_SHORT)
				.show();
	}

	public void showError(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.error);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.done, OnErrorDialogClickListener);
		builder.setCancelable(false);
		builder.show();
	}

	public void showUpdate(boolean cancelable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.update);
		builder.setMessage(R.string.version_update);
		builder.setPositiveButton(R.string.done, OnUpdateDialogConfirmListener);
		if (cancelable)
			builder.setNegativeButton(R.string.cancel,
					OnUpdateDialogCancelListener);
		builder.setCancelable(false);
		builder.show();
	}

	private DialogInterface.OnClickListener OnErrorDialogClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			((Activity) getContext()).finish();
		}

	};

	private DialogInterface.OnClickListener OnUpdateDialogConfirmListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			if (callback != null)
				callback.onUpdateConfirm(IntroView.this);
		}
	};

	private DialogInterface.OnClickListener OnUpdateDialogCancelListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			if (callback != null)
				callback.onUpdateCancel(IntroView.this);
		}
	};

	public IntroView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public IntroView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public IntroView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

}
