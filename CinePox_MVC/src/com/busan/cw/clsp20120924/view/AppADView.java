package com.busan.cw.clsp20120924.view;

import view.CCView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.busan.cw.clsp20120924_beta.R;
import com.busan.cw.clsp20120924.interfaces.AppADViewCallback;
import com.fedorvlasov.lazylist.AbsImageLoader;
import com.fedorvlasov.lazylist.ImageLoader;
import com.fedorvlasov.lazylist.ImageLoaderCallback;

@SuppressLint("SetJavaScriptEnabled")
public class AppADView extends CCView implements OnClickListener,
		OnCheckedChangeListener, ImageLoaderCallback {

	private CheckBox notseeCheck;
	private ImageButton skipBtn;

	private ImageView adImage;

	private AppADViewCallback callback;

	public void setCallback(AppADViewCallback callback) {
		this.callback = callback;
	}

	public void setAdDuring(int during) {
		notseeCheck
				.setText(during + getContext().getString(R.string.notseeday));
	}

	public void setADImageUrl(String url) {
		// 사실 이건 Model이지만 API 사용성 차원에서 시도해 보는것. 사용성이 구리면 다시 Model로 이동
		// CinepoxImageLoader loader = new CinepoxImageLoader(getContext());
		ImageLoader loader = new ImageLoader(getContext());
		loader.setCallback(this);
		loader.DisplayImage(url, adImage);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (buttonView.equals(notseeCheck))
			callback.onNotseeCheck(this, isChecked);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(skipBtn))
			callback.onSkipClick(this);
		else if (v.equals(adImage))
			callback.onADImageClick(this);
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_adview;
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		notseeCheck = (CheckBox) findViewById(R.id.check_ad_noshow);
		skipBtn = (ImageButton) findViewById(R.id.btn_ad_close);
		adImage = (ImageView) findViewById(R.id.ad_imageview);
	}

	public AppADView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public AppADView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public AppADView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onLoad(AbsImageLoader loader) {
		// TODO Auto-generated method stub
		notseeCheck.setOnCheckedChangeListener(this);
		skipBtn.setOnClickListener(this);
		adImage.setOnClickListener(this);
	}

	@Override
	public void onStart(AbsImageLoader loader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(AbsImageLoader loader, String message) {
		// TODO Auto-generated method stub
		if (callback != null)
			callback.onADLoadError(this);
	}

}
