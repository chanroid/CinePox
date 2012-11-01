/**
 * 0. Project  : CinePox_MVC
 *
 * 1. FileName : SearchAdapter.java
 * 2. Package : com.busan.cw.clsp20120924.view
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 31. 오후 6:49:37
 * 6. 변경이력 : 
 *		2012. 10. 31. 오후 6:49:37 : 신규
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
 * 2. FileName  : SearchAdapter.java
 * 3. Package  : com.busan.cw.clsp20120924.view
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 6:49:37
 * </PRE>
 */
public class SearchAdapter extends SimpleAdapter {

	/**
	 * <PRE>
	 * 1. MethodName : SearchAdapter
	 * 2. ClassName  : SearchAdapter
	 * 3. Comment   : 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 31. 오후 6:49:48
	 * </PRE>
	 *   @param context
	 *   @param data
	 *   @param resource
	 *   @param from
	 *   @param to
	 */ 
	public SearchAdapter(Context context, List<? extends Map<String, ?>> data,
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
