package com.busan.cw.clsp20120924.view;

import java.util.List;

import view.CCAdapter;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.model.ImageData;
import com.busan.cw.clsp20120924_beta.R;
import com.fedorvlasov.lazylist.AbsImageLoader;
import com.fedorvlasov.lazylist.ImageLoader;

public class MovieDetailStillcutAdapter extends CCAdapter<ImageData> {

	private ImageLoader imageLoader;

	public MovieDetailStillcutAdapter(Context context, int resource,
			List<ImageData> data) {
		super(context, resource, data);
		imageLoader = new ImageLoader(getContext());
		// TODO Auto-generated constructor stub
	}

	@Override
	public View initItem(ImageData data, View convertView) {
		// TODO Auto-generated method stub
		if (data.isAdult() && !app().getUserConfig().isShowAdult())
			((ImageView) convertView).setImageResource(R.drawable.bg_poster_19);
		else
			imageLoader.DisplayImage(data.getImageUrl(),
					(ImageView) convertView);
		return convertView;
	}

	public AbsImageLoader getImageLoader() {
		// TODO Auto-generated method stub
		return imageLoader;
	}

	private CinepoxAppModel app() {
		return (CinepoxAppModel) getContext().getApplicationContext();
	}

}
