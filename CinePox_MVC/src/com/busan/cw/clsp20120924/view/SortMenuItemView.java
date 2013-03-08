package com.busan.cw.clsp20120924.view;

import view.CCView;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.busan.cw.clsp20120924.model.SortMenuItemData;
import com.busan.cw.clsp20120924_beta.R;

public class SortMenuItemView extends CCView {
	
	private ImageView icon;
	private TextView text;

	public SortMenuItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.listitem_movielist_menu;
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		icon = (ImageView) findViewById(R.id.img_movielist_menu_icon);
		text = (TextView) findViewById(R.id.text_movielist_menu_text);
	}
	
	public void setData(SortMenuItemData data) {
		icon.setImageResource(data.iconRes);
		text.setText(data.title);
	}

}
