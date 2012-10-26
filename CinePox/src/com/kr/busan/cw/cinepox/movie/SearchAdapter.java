package com.kr.busan.cw.cinepox.movie;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.busan.cw.clsp20120924.R;

public class SearchAdapter extends ArrayAdapter<String> {

	public SearchAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.search_item, null);
		}

		TextView tv = (TextView) convertView.findViewById(R.id.text1);
		tv.setText(getItem(position));

		return convertView;
	}

}
