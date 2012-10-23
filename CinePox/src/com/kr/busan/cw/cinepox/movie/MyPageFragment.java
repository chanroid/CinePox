package com.kr.busan.cw.cinepox.movie;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kr.busan.cw.cinepox.R;

public class MyPageFragment extends Fragment {

	LinearLayout mRoot;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT > 11) {
			getActivity().setTheme(android.R.style.Theme_Holo);
		}
		super.onCreate(savedInstanceState);
		mRoot = (LinearLayout) getLayoutInflater(getArguments()).inflate(
				R.layout.main_mypage, null);
		initList();
	}

	Config getConfig() {
		return Config.getInstance(getActivity());
	}

	State getState() {
		return (State) getActivity().getApplication();
	}

	void initList() {
		refreshList();
	}

	void refreshList() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return mRoot;
	}
}
