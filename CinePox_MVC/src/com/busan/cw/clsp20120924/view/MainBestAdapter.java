/**
 * 0. Project  : CinePox_MVC
 *
 * 1. FileName : BestAdapter.java
 * 2. Package : com.busan.cw.clsp20120924.view
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 31. 오후 5:56:54
 * 6. 변경이력 : 
 *		2012. 10. 31. 오후 5:56:54 : 신규
 *
 */
package com.busan.cw.clsp20120924.view;

import java.util.List;

import kr.co.chan.util.Util;

import view.CCAdapter;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.structs.BestItemData;
import com.fedorvlasov.lazylist.ImageLoader;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : BestAdapter.java
 * 3. Package  : com.busan.cw.clsp20120924.view
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 5:56:54
 * </PRE>
 * 
 * @param <BestItemData>
 */
public class MainBestAdapter extends CCAdapter<BestItemData> {

	private int itemWidth;
	private int itemHeight;
	private FrameLayout.LayoutParams itemParams;
	private ImageLoader mImageLoader;

	public MainBestAdapter(Context context, int resource,
			List<BestItemData> data) {
		super(context, resource, data);
		itemWidth = Util.Display.getWindowSize(getContext())[0] / 4;
		itemHeight = itemWidth * 3 / 2;
		itemParams = new FrameLayout.LayoutParams(itemWidth, itemHeight);
		mImageLoader = new ImageLoader(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View initItem(BestItemData data, View view) {
		// TODO Auto-generated method stub
		ImageView iv = (ImageView) view.findViewById(R.id.iv_movieitem_thumb);
		iv.setScaleType(ScaleType.FIT_XY);
		iv.setVisibility(View.INVISIBLE);
		iv.setLayoutParams(itemParams);

		if (data.isAdult) {
			iv.setImageResource(R.drawable.eximg1);
			iv.setVisibility(View.VISIBLE);
		} else
			mImageLoader.DisplayImage(data.imageURL, iv);

		return view;
	}

}
