package com.busan.cw.clsp20120924.controller;

import utils.LogUtils.l;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.interfaces.MovieListModelCallback;
import com.busan.cw.clsp20120924.interfaces.MovieListViewCallback;
import com.busan.cw.clsp20120924.interfaces.SortInfoModelCallback;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.model.MovieListModel;
import com.busan.cw.clsp20120924.model.MovieListRequestData;
import com.busan.cw.clsp20120924.model.SortInfoModel;
import com.busan.cw.clsp20120924.model.SortMenuItemData;
import com.busan.cw.clsp20120924.view.MovieListView;
import com.busan.cw.clsp20120924_beta.R;

public class MovieListFragment extends Fragment implements
		MovieListViewCallback, MovieListModelCallback, Constants {

	public static final String EXTRA_LIST_NUM = "list";

	public static final int LIST_MOVIE = 0;
	public static final int LIST_TV = 1;
	public static final int LIST_ANI = 2;
	public static final int LIST_PREMIUM = 3;
	public static final int LIST_ADULT = 4;

	private String menuName;
	private int menu;

	private MovieListView view;
	private MovieListModel movieModel;
	private SortInfoModel sortinfoModel;

	private MovieListRequestData requestData;

	private SortMenuItemData sortItem;
	private SortMenuItemData countryItem;
	private SortMenuItemData pointItem;
	private SortMenuItemData categoryItem;
	private SortMenuItemData isAdultItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		view = new MovieListView(getActivity());
		view.setCallback(this);

		menu = getArguments().getInt(EXTRA_LIST_NUM);

		sortItem = new SortMenuItemData(EXTRA_SORT_METHOD);
		sortItem.title = getString(R.string.movielist_sort_method);
		sortItem.iconRes = R.drawable.ic_movielist_update;

		countryItem = new SortMenuItemData(EXTRA_SORT_COUNTRY);
		countryItem.title = getString(R.string.movielist_sort_country);
		countryItem.iconRes = R.drawable.ic_movielist_nara;

		pointItem = new SortMenuItemData(EXTRA_SORT_POINT);
		pointItem.title = getString(R.string.movielist_sort_point);
		pointItem.iconRes = R.drawable.ic_movielist_price;

		categoryItem = new SortMenuItemData(EXTRA_SORT_CATEGORY);
		categoryItem.title = getString(R.string.movielist_sort_category);
		categoryItem.iconRes = R.drawable.ic_movielist_category;

		isAdultItem = new SortMenuItemData(EXTRA_SORT_ADULT);
		isAdultItem.title = getString(R.string.movielist_sort_movie);
		isAdultItem.iconRes = R.drawable.ic_movielist_new;

		refreshMenu();

		requestData = new MovieListRequestData();
		movieModel = new MovieListModel(getActivity());
		movieModel.setCallback(this);
		sortinfoModel = new SortInfoModel(getActivity());
		loadDefaultMovieList();
	}

	public CinepoxAppModel app() {
		return (CinepoxAppModel) getParent().getApplication();
	}

	private MainFragmentActivity getParent() {
		return (MainFragmentActivity) getActivity();
	}

	public void showLoading() {
		// getParent().showLoading();
		view.showLoading();
	}

	public void hideLoading() {
		// getParent().hideLoading();
		view.hideLoading();
	}

	private void loadDefaultMovieList() {
		requestData.page = 1;
		requestData.sc_category = "";
		if (menu == LIST_PREMIUM) {
			requestData.sc_type = EXTRA_MENU_MOVIE;
			requestData.sc_is_month = true;
		} else {
			requestData.sc_type = menuName;
			requestData.sc_is_month = false;
		}
		loadMovieList(requestData);
	}

	private void loadMovieList(MovieListRequestData data) {
		movieModel.loadMovieList(data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return view;
	}

	@Override
	public void onPrevPageClick(MovieListView view) {
		// TODO Auto-generated method stub
		requestData.page--;
		loadMovieList(requestData);
	}

	@Override
	public void onNextPageClick(MovieListView view) {
		// TODO Auto-generated method stub
		requestData.page++;
		loadMovieList(requestData);
	}

	@Override
	public void onMovieListItemClick(MovieListView view, int index) {
		// TODO Auto-generated method stub
		if (index >= movieModel.getMovieItemList().size())
			return;

		getParent().showMovieDetailFragment(
				movieModel.getMovieItemList().get(index).getMovieNum());
	}

	@Override
	public void onMovieListLoadStart(MovieListModel model) {
		// TODO Auto-generated method stub
		showLoading();
	}

	@Override
	public void onMovieListLoadError(MovieListModel model, String message) {
		// TODO Auto-generated method stub
		hideLoading();
	}

	@Override
	public void onMovieListLoadComplete(MovieListModel model) {
		// TODO Auto-generated method stub
		hideLoading();
		view.setMovieList(model.getMovieItemList());

		view.setPageNumInfo(model.getCurrentPageNum(),
				model.getTotalPageCount());

		int lastPage = model.getPagingInfoList()
				.get(model.getPagingInfoList().size() - 1).getPageNum();

		boolean isLast = lastPage >= model.getTotalPageCount();

		view.setPagingInfo(model.getPagingInfoList(), isLast);
	}

	@Override
	public void onPageClick(MovieListView view, int page) {
		// TODO Auto-generated method stub
		if (page < 1 || page > movieModel.getTotalPageCount())
			page = 1;

		requestData.page = page;
		loadMovieList(requestData);
	}

	@Override
	public void onPrevPageGroupClick(MovieListView view) {
		// TODO Auto-generated method stub
		if (requestData.page < 6)
			return;

		requestData.page -= 5;
		loadMovieList(requestData);
	}

	@Override
	public void onNextPageGroupClick(MovieListView view) {
		// TODO Auto-generated method stub
		if (requestData.page > movieModel.getTotalPageCount() - 5)
			return;

		requestData.page += 5;
		loadMovieList(requestData);
	}

	@Override
	public void onSortItemClick(MovieListView view, String tag) {
		// TODO Auto-generated method stub
		// 콜백은 작성 후 적용
		view.hideMenu();
		l.i("sortitem click : " + tag);
		if (tag == null || "".equals(tag))
			return;

		SortInfoCallback callback = new SortInfoCallback(tag);
		if (EXTRA_SORT_ADULT.equals(tag)) {
			String[] showArray = new String[] {
					getString(R.string.movielist_sort_movie),
					getString(R.string.movielist_sort_adult) };
			showSortChangeDialog(tag, showArray);
		} else if (EXTRA_SORT_CATEGORY.equals(tag)) {
			sortinfoModel.loadSortInfo(app().getAppConfig()
					.getCategoryListUrl(), menuName, callback);
		} else if (EXTRA_SORT_COUNTRY.equals(tag)) {
			sortinfoModel.loadSortInfo(
					app().getAppConfig().getCountryListUrl(), null, callback);
		} else if (EXTRA_SORT_METHOD.equals(tag)) {
			sortinfoModel.loadSortInfo(app().getAppConfig().getSortListUrl(),
					menuName, callback);
		} else if (EXTRA_SORT_POINT.equals(tag)) {
			sortinfoModel.loadSortInfo(app().getAppConfig()
					.getPointsortListUrl(), null, callback);
		}
	}

	private void refreshMenu() {
		view.clearMenu();
		switch (menu) {
		case LIST_MOVIE:
			menuName = EXTRA_MENU_MOVIE;
			// 정렬방식, 국가, 포인트, 카테고리
			view.addMenu(countryItem, pointItem, categoryItem, sortItem);
			break;
		case LIST_TV:
			menuName = EXTRA_MENU_TV;
			// 정렬방식, 국가, 포인트, 카테고리
			view.addMenu(countryItem, pointItem, categoryItem, sortItem);
			break;
		case LIST_ANI:
			menuName = EXTRA_MENU_KIDS;
			view.addMenu(pointItem, sortItem);
			// 정렬방식, 포인트
			break;
		case LIST_PREMIUM:
			// 일반에선 장르, 성인에서는 카테고리
			if (isAdultItem.title
					.equals(getString(R.string.movielist_sort_movie)))
				view.addMenu(isAdultItem, countryItem, categoryItem, sortItem);
			else if (isAdultItem.title
					.equals(getString(R.string.movielist_sort_adult)))
				view.addMenu(isAdultItem, categoryItem);
			break;
		case LIST_ADULT:
			menuName = EXTRA_MENU_ADULT;
			// 장르, 포인트
			view.addMenu(pointItem, categoryItem);
			break;
		default:
			menuName = EXTRA_MENU_MOVIE;
			view.addMenu(countryItem, pointItem, categoryItem, sortItem);
			break;
		}

	}

	public void showSortChangeDialog(final String sort, String[] showArray) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setItems(showArray, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (EXTRA_SORT_ADULT.equals(sort)) {
					changeSort(sort, which);
				} else
					changeSort(sort, which);
			}
		});
		dialog.show();
	}

	private void changeSort(String sort, int which) {
		// 여기서 메뉴이름 바꾸고 메뉴 새로고침
		if (EXTRA_SORT_ADULT.equals(sort)) {
			requestData.sc_type = which == 0 ? EXTRA_MENU_MOVIE
					: EXTRA_MENU_ADULT;
			isAdultItem.title = which == 0 ? getString(R.string.movielist_sort_movie)
					: getString(R.string.movielist_sort_adult);
			menuName = which == 0 ? EXTRA_MENU_MOVIE : EXTRA_MENU_ADULT;
		} else if (EXTRA_SORT_CATEGORY.equals(sort)) {
			requestData.sc_category = sortinfoModel.getDataCodes(true).get(
					which);
			categoryItem.title = sortinfoModel
					.getCategoryArray(
							getString(R.string.movielist_sort_category))
					.get(which).getName();
		} else if (EXTRA_SORT_COUNTRY.equals(sort)) {
			requestData.sc_country = sortinfoModel.getDataCodes(true)
					.get(which);
			countryItem.title = sortinfoModel
					.getCategoryArray(
							getString(R.string.movielist_sort_country))
					.get(which).getName();
		} else if (EXTRA_SORT_METHOD.equals(sort)) {
			requestData.sc_order = sortinfoModel.getDataCodes(false).get(which);
			sortItem.title = sortinfoModel.getCategoryArray(null).get(which)
					.getName();
		} else if (EXTRA_SORT_POINT.equals(sort)) {
			if ("".equals(sortinfoModel.getDataCodes(true).get(which)))
				requestData.sc_set_price = 0;
			else
				requestData.sc_set_price = Integer.parseInt(sortinfoModel
						.getDataCodes(true).get(which));
			pointItem.title = sortinfoModel
					.getCategoryArray(getString(R.string.movielist_sort_point))
					.get(which).getName();
		}
		refreshMenu();
		loadMovieList(requestData);
	}

	private class SortInfoCallback implements SortInfoModelCallback {

		private String tag;

		public SortInfoCallback(String tag) {
			this.tag = tag;
		}

		@Override
		public void onSortInfoLoadStart(SortInfoModel model) {
			// TODO Auto-generated method stub
			getParent().showLoading();
		}

		@Override
		public void onSortInfoLoadError(SortInfoModel model, String message) {
			// TODO Auto-generated method stub
			getParent().hideLoading();
		}

		@Override
		public void onSortInfoLoadComplete(SortInfoModel model) {
			// TODO Auto-generated method stub
			getParent().hideLoading();
			String defaultName = null;
			String[] showArray = null;
			// 성인은 스레드 호출 안하고 바로 넘어간다.
			// 정렬방식에선 따로 추가할건 없다.
			// 여기서 다이얼로그 array만들고 넘어감
			if (EXTRA_SORT_CATEGORY.equals(tag)) {
				defaultName = getString(R.string.movielist_sort_category);
			} else if (EXTRA_SORT_COUNTRY.equals(tag)) {
				defaultName = getString(R.string.movielist_sort_country);
			} else if (EXTRA_SORT_POINT.equals(tag)) {
				defaultName = getString(R.string.movielist_sort_point);
			}
			showArray = model.getDataNames(defaultName);
			showSortChangeDialog(tag, showArray);
		}

	}
}
