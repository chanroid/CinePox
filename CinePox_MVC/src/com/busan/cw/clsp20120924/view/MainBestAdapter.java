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

import view.CCBaseAdapter;
import android.content.Context;
import android.view.View;

import com.busan.cw.clsp20120924.structs.BestItemData;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : BestAdapter.java
 * 3. Package  : com.busan.cw.clsp20120924.view
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 5:56:54
 * </PRE>
 * @param <BestItemData>
 */
public class MainBestAdapter extends CCBaseAdapter<BestItemData> {

	public MainBestAdapter(Context context, int resource,
			List<BestItemData> data) {
		super(context, resource, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View initItem(BestItemData data, View convertView) {
		// TODO Auto-generated method stub
		return null;
	}

}
