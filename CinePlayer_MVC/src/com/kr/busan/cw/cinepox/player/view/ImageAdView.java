package com.kr.busan.cw.cinepox.player.view;

import view.CCView;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.kr.busan.cw.cinepox.R;

public class ImageAdView extends CCView {
	
	private ImageView adImage;

	public ImageAdView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		// TODO Auto-generated method stub
		adImage.setClickable(true);
		adImage.setOnClickListener(l);
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.imageadview;
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		adImage = (ImageView) findViewById(R.id.adView);
	}
	
	public void setAdImage(Bitmap b) {
		adImage.setImageBitmap(b);
	}

}
