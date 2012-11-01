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
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : BestAdapter.java
 * 3. Package  : com.busan.cw.clsp20120924.view
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 5:56:54
 * </PRE>
 */
public class MainBestAdapter extends SimpleAdapter {

	/**
	 * <PRE>
	 * 1. MethodName : BestAdapter
	 * 2. ClassName  : BestAdapter
	 * 3. Comment   : 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 31. 오후 5:57:19
	 * </PRE>
	 *   @param context
	 *   @param data
	 *   @param resource
	 *   @param from
	 *   @param to
	 */ 
	public MainBestAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
