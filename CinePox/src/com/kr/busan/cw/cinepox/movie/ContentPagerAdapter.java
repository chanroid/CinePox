package com.kr.busan.cw.cinepox.movie;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ContentPagerAdapter extends FragmentStatePagerAdapter {

	ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

	public ContentPagerAdapter(FragmentManager fragmentManager) {
		// TODO Auto-generated constructor stub
		super(fragmentManager);
		BestFragment best = new BestFragment();
//		MyPageFragment my = new MyPageFragment();
		mFragments.add(best);
//		mFragments.add(my);
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return mFragments.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFragments.size();
	}

}
