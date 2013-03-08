package com.busan.cw.clsp20120924.view;

import view.CCView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.busan.cw.clsp20120924_beta.R;
import com.busan.cw.clsp20120924.interfaces.UpdateViewCallback;

public class UpdateView extends CCView implements OnClickListener {

	private TextView currentText;
	private TextView recentText;
	private TextView updateMessageText;
	private Button updateButton;

	private UpdateViewCallback callback;

	public void setCallback(UpdateViewCallback callback) {
		this.callback = callback;
	}

	public void setVersionInfo(String current, String recent) {
		currentText.setText(current);
		recentText.setText(recent);
	}

	public void setUpdate(boolean flag) {
		updateButton.setVisibility(flag ? VISIBLE : GONE);
		String message = flag ? getContext().getString(R.string.version_update)
				: getContext().getString(R.string.version_recent);
		updateMessageText.setText(message);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(updateButton))
			callback.onUpdateClick(this);
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_update;
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		currentText = (TextView) findViewById(R.id.text_update_current);
		recentText = (TextView) findViewById(R.id.text_update_recent);
		updateMessageText = (TextView) findViewById(R.id.text_update_message);
		updateButton = (Button) findViewById(R.id.btn_update);
		updateButton.setOnClickListener(this);
	}

	public UpdateView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public UpdateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public UpdateView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

}
