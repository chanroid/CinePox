/**
 * 0. Project  : CinePox_MVC
 *
 * 1. FileName : MainActivity.java
 * 2. Package : com.busan.cw.clsp20120924.controller
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 31. 오후 5:40:15
 * 6. 변경이력 : 
 *		2012. 10. 31. 오후 5:40:15 : 신규
 *
 */
package com.busan.cw.clsp20120924.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.base.Domain;
import com.busan.cw.clsp20120924.interfaces.MainViewCallback;
import com.busan.cw.clsp20120924.model.ConfigModel;
import com.busan.cw.clsp20120924.model.MovieDataModel;
import com.busan.cw.clsp20120924.structs.CategoryItemData;
import com.busan.cw.clsp20120924.view.MainView;

import controller.CCActivity;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : MainActivity.java
 * 3. Package  : com.busan.cw.clsp20120924.controller
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 5:40:15
 * </PRE>
 */
public class MainActivity extends CCActivity implements Constants,
		MainViewCallback {

	private MainView mMainView;
	private ConfigModel mConfigModel;
	private MovieDataModel mDataModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mMainView = (MainView) loadView(MainView.class);
		mConfigModel = (ConfigModel) loadModel(ConfigModel.class);
		mDataModel = (MovieDataModel) loadModel(MovieDataModel.class);
		mMainView.setCallback(this);
		mMainView.setBanner(mConfigModel.getMainBannerData());
		setContentView(mMainView);
	}

	void goQRPlay() {
		Intent i = new Intent(this, QRPlayActivity.class);
		startActivity(i);
	}

	void goMovie() {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Domain.WEB_DOMAIN
				+ "vod/movie/movieList.html" + "?" + KEY_SET_DEVICE + "="
				+ DEVICE_ANDROID_APP));
		startActivity(i);
	}

	void goVideo() {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Domain.WEB_DOMAIN
				+ "vod/tv/tvList.html" + "?" + KEY_SET_DEVICE + "="
				+ DEVICE_ANDROID_APP));
		startActivity(i);
	}

	void goSearch() {
		Intent i = new Intent(this, SearchActivity.class);
		startActivity(i);
	}

	void goMyPage() {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Domain.WEB_DOMAIN
				+ "mypage/wish_list.html" + "?" + KEY_SET_DEVICE + "="
				+ DEVICE_ANDROID_APP));
		startActivity(i);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_main_best:
			// 별거 없음
			break;
		case R.id.btn_main_movie:
			goMovie();
			break;
		case R.id.btn_main_mylist:
			goMyPage();
			break;
		case R.id.btn_main_premium:
			if (mConfigModel.getMainBannerData() != null)
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(mConfigModel.getMainBannerData().url)));
			break;
		case R.id.btn_main_qrplay:
			goQRPlay();
			break;
		case R.id.btn_main_search:
			goSearch();
			break;
		case R.id.btn_main_video:
			goVideo();
			break;
		default:
			break;
		}
	}

	/**
	 * 현재 선택된 카테고리의 리스트 새로고침
	 */
	private void refreshList() {

	}

	/**
	 * 그리드뷰 새로고침 이벤트
	 */
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		refreshList();
	}

	/**
	 * 그리드뷰 아이템 선택 이벤트
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		try {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mDataModel
					.getBestList().get(arg2).targetURL)));
		} catch (Exception e) {
			refreshList();
		}
	}

	/**
	 * 카테고리 변경 이벤트
	 */
	@Override
	public void onCategoryChanged(CategoryItemData name) {
		// TODO Auto-generated method stub

	}

}
