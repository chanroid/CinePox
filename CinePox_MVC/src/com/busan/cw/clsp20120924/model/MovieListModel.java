package com.busan.cw.clsp20120924.model;

import static com.busan.cw.clsp20120924.model.MovieListRequestData.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.LogUtils.l;
import utils.StringUtils;
import android.content.Context;
import android.os.AsyncTask;

import com.busan.cw.clsp20120924.base.BaseModel;
import com.busan.cw.clsp20120924.base.DomainManager;
import com.busan.cw.clsp20120924.interfaces.MovieListModelCallback;

public class MovieListModel extends BaseModel {

	private int listNum;
	private int listItemCount;
	private int currentPageNum;
	private int totalItemCount;

	private ArrayList<SimplePagingData> pagingInfoList = new ArrayList<SimplePagingData>();

	private int prevPageGroupLastPageNum;
	private int nextPageGroupFrontPageNum;

	private int totalPageCount;

	private ArrayList<MovieItemData> movieItemList = new ArrayList<MovieItemData>();

	private MovieListModelCallback callback;

	public void setCallback(MovieListModelCallback callback) {
		// TODO Auto-generated method stub
		this.callback = callback;
	}

	public MovieListModel(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	public void loadMovieList(MovieListRequestData data) {
		new MovieListSync().execute(data);
	}

	private class MovieListSync extends
			AsyncTask<MovieListRequestData, Integer, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (callback != null)
				callback.onMovieListLoadStart(MovieListModel.this);
		}

		@Override
		protected String doInBackground(MovieListRequestData... params) {
			// TODO Auto-generated method stub
			MovieListRequestData data = params[0];
			String listUrl = DomainManager.getAppDomain(getContext())
					+ app().getAppConfig().getMovieListUrl();
			StringBuilder paramBuilder = new StringBuilder();
			for (NameValuePair p : buildParams(data)) {
				if (!"".equals(p.getValue()))
					paramBuilder.append(p.getName() + "=" + p.getValue() + "&");
			}
			listUrl += "&" + paramBuilder.toString();
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(listUrl)
						.openConnection();
				DomainManager.signHeader(conn);
				// StreamUtils.addPostParams(conn, buildParams(data));
				l.d("movielist url : " + listUrl);

				String listJsonString = StringUtils.stringFromStream(conn
						.getInputStream());
				l.d("movielist json : " + listJsonString);
				JSONObject listJson = new JSONObject(listJsonString);
				if (!isY(listJson.getString(EXTRA_RESULT)))
					return listJson.getString(EXTRA_MSG);
				JSONObject dataJson = listJson.getJSONObject(EXTRA_DATA);
				return parse(dataJson);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result == null) {
				if (callback != null)
					callback.onMovieListLoadComplete(MovieListModel.this);
			} else {
				if (callback != null)
					callback.onMovieListLoadError(MovieListModel.this, result);
			}
		}

	}

	private String parse(JSONObject ob) {
		try {
			JSONObject o = ob.getJSONObject(RES_PAGING);
			listNum = o.getInt(RES_LIST_NUM);
			try {
				currentPageNum = o.getInt(RES_PAGE_NUM);
			} catch (JSONException e) {
			}
			totalItemCount = o.getInt(RES_QUERY_COUNT);

			JSONObject pagingList = o.getJSONObject(RES_PAGE_LIST);
			// lastPageNum = pagingList.getInt(RES_LAST_PAGE);
			// nextPageNum = pagingList.getInt(RES_NEXT_LOAD_PAGE);
			totalPageCount = pagingList.getInt(RES_TOTAL_LOAD_PAGE);

			JSONArray pageAvg = pagingList.getJSONArray(RES_PAGE_AVG);

			if (pageAvg.length() > 0)
				pagingInfoList.clear();

			for (int i = 0; i < pageAvg.length(); i++) {
				JSONObject page = pageAvg.getJSONObject(i);
				try {
					SimplePagingData info = new SimplePagingData(page);
					pagingInfoList.add(info);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			JSONArray movieList = ob.getJSONArray(RES_LIST_DATA);

			if (movieList.length() > 0)
				movieItemList.clear();

			for (int i = 0; i < movieList.length(); i++) {
				JSONObject movie = movieList.getJSONObject(i);
				try {
					MovieItemData item = new MovieItemData(movie);
					movieItemList.add(item);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return e1.getMessage();
		}
		return null;
	}

	private ArrayList<NameValuePair> buildParams(MovieListRequestData data) {
		ArrayList<NameValuePair> result = new ArrayList<NameValuePair>();
		result.add(new BasicNameValuePair(EXTRA_LIST_COUNT, String
				.valueOf(data.sc_list_count)));
		result.add(new BasicNameValuePair(EXTRA_PAGE, String.valueOf(data.page)));
		result.add(new BasicNameValuePair(EXTRA_SEARCH_TYPE, data.sc_type));
		result.add(new BasicNameValuePair(EXTRA_SEARCH_IS_MONTH,
				isY(data.sc_is_month)));
		result.add(new BasicNameValuePair(EXTRA_PAGE_COUNT, String
				.valueOf(data.page_count)));
		result.add(new BasicNameValuePair(EXTRA_SEARCH_CATEGORY,
				data.sc_category));
		result.add(new BasicNameValuePair(EXTRA_SEARCH_ORDER, data.sc_order));
		result.add(new BasicNameValuePair(EXTRA_SEARCH_PRICE, String
				.valueOf(data.sc_set_price)));
		result.add(new BasicNameValuePair(EXTRA_SEARCH_COUNTRY, data.sc_country));
		// 2013-02-28 필수값이 아니므로 일단 리스트 작업 끝나고 수정
		// result.add(new BasicNameValuePair(EXTRA_DISPLAY_CLASS, String
		// .valueOf(data.sc_max_disp_class)));
		// result.add(new BasicNameValuePair(EXTRA_SEARCH_DISPLAY,
		// isY(data.sc_display)));
		// try {
		// result.add(new BasicNameValuePair(EXTRA_SEARCH_QUERY, URLEncoder
		// .encode(data.search_query, HTTP.UTF_8)));
		// } catch (UnsupportedEncodingException e) {
		// }
		return result;
	}

	/**
	 * 현재 리스트의 영화 총 갯수
	 * 
	 * @return
	 */
	public int getListNum() {
		return listNum;
	}

	/**
	 * 현재 페이지의 영화 총 갯수
	 * 
	 * @return
	 */
	public int getListItemCount() {
		return listItemCount;
	}

	/**
	 * 현재 페이지 번호
	 * 
	 * @return
	 */
	public int getCurrentPageNum() {
		return currentPageNum;
	}

	/**
	 * 현재 리스트의 영화 총 갯수
	 * 
	 * @see getListNum()
	 * @return
	 */
	public int getTotalItemCount() {
		return totalItemCount;
	}

	/**
	 * 이전 페이지 묶음의 마지막 페이지 번호
	 * 
	 * @return
	 */
	public int getPrevPageGroupLastPageNum() {
		return prevPageGroupLastPageNum;
	}

	/**
	 * 다음 페이지 묶음의 첫번째 페이지 번호
	 * 
	 * @return
	 */
	public int getNextPageGroupFrontPageNum() {
		return nextPageGroupFrontPageNum;
	}

	/**
	 * 현재 리스트의 총 페이지 갯수
	 * 
	 * @return
	 */
	public int getTotalPageCount() {
		return totalPageCount;
	}

	/**
	 * 페이지 인덱스 구성을 위한 페이지 정보 목록
	 * 
	 * @return
	 */
	public ArrayList<SimplePagingData> getPagingInfoList() {
		return pagingInfoList;
	}

	/**
	 * 리스트 아이템 구성을 위한 영화정보 목록
	 * 
	 * @return
	 */
	public ArrayList<MovieItemData> getMovieItemList() {
		return movieItemList;
	}

}
