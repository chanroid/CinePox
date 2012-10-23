package com.kr.busan.cw.cinepox.movie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.chan.util.Util;
import kr.co.chan.util.Interfaces.Destroyable;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.fedorvlasov.lazylist.ImageLoader;
import com.kr.busan.cw.cinepox.R;

public class BestAdapter extends SimpleAdapter implements Destroyable {

	private Map<Integer, Bitmap> bmArray = new HashMap<Integer, Bitmap>();
	private List<Map<String, String>> mDataArray;
	private Context mContext;
	private ImageLoader mImageLoader;

	int w;
	int h;
	FrameLayout.LayoutParams ivparam;

	public BestAdapter(Context context, List<Map<String, String>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		mDataArray = data;
		mContext = context;
		w = Util.Display.getWindowSize(mContext)[0] / 4;
		h = w * 3 / 2;
		ivparam = new FrameLayout.LayoutParams(w, h);
		mImageLoader = new ImageLoader(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataArray.size();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.movie_item, null);
		}

		ImageView iv = (ImageView) view.findViewById(R.id.iv_movieitem_thumb);
		iv.setVisibility(View.INVISIBLE);
		iv.setLayoutParams(ivparam);

		boolean isAdult = "Y".equalsIgnoreCase(mDataArray.get(position).get(
				"is_adult"));
		if (isAdult) {
			iv.setImageResource(R.drawable.eximg1);
			iv.setVisibility(View.VISIBLE);
		} else
			mImageLoader.DisplayImage(mDataArray.get(position).get("sn_url"),
					iv);

		return view;
	}

	@Override
	public synchronized void destroy() {
		try {
			for (int i = 0; i < bmArray.size(); i++)
				bmArray.get(i).recycle();
			bmArray.clear();
		} catch (Exception e) {
		}
	}

}
