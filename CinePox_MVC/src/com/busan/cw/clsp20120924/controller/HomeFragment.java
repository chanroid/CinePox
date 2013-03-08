package com.busan.cw.clsp20120924.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.busan.cw.clsp20120924.interfaces.HomePageFragmentViewCallback;
import com.busan.cw.clsp20120924.view.HomePageFragmentView;
import com.busan.cw.clsp20120924.view.SNSReviewView;

public class HomeFragment extends Fragment implements
		HomePageFragmentViewCallback {

	private HomePageFragmentView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		view = new HomePageFragmentView(getActivity());
		view.setCallback(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return view;
	}

	@Override
	public void onSNSReviewClick(SNSReviewView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGallery1ItemClick(HomePageFragmentView view, int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGallery2ItemClick(HomePageFragmentView view, int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEventItemClick(HomePageFragmentView view, int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRelatedItemClick(HomePageFragmentView view, int position) {
		// TODO Auto-generated method stub

	}
}
