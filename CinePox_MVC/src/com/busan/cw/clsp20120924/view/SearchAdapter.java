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

import view.CCBaseAdapter;
import android.content.Context;
import android.view.View;

import com.busan.cw.clsp20120924.structs.SearchItemData;

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
public class SearchAdapter extends CCBaseAdapter<SearchItemData> {

	public SearchAdapter(Context context, int resource,
			List<SearchItemData> data) {
		super(context, resource, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View initItem(SearchItemData data, View convertView) {
		// TODO Auto-generated method stub
		return null;
	}

}
