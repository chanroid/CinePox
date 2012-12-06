/**
 * 0. Project  : CinePox_MVC
 *
 * 1. FileName : MovieDataModel.java
 * 2. Package : com.busan.cw.clsp20120924.model
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 31. 오후 6:00:25
 * 6. 변경이력 : 
 *		2012. 10. 31. 오후 6:00:25 : 신규
 *
 */
package com.busan.cw.clsp20120924.model;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.co.chan.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.busan.cw.clsp20120924.base.Domain;
import com.busan.cw.clsp20120924.base.Model;
import com.busan.cw.clsp20120924.structs.BestItemData;
import com.busan.cw.clsp20120924.structs.CategoryItemData;
import com.busan.cw.clsp20120924.structs.SearchItemData;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : MovieDataModel.java
 * 3. Package  : com.busan.cw.clsp20120924.model
 * 4. Comment  : 검색, 메인 영화목록 관리
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 6:00:25
 * </PRE>
 */
public class MovieDataModel extends Model {
	private static MovieDataModel instance;

	private ArrayList<BestItemData> mBestDataList;
	private ArrayList<SearchItemData> mSearchList;
	private CategoryItemData mCurrentCategory;

	public static MovieDataModel getInstance(Context ctx) {
		if (instance == null)
			instance = new MovieDataModel(ctx);
		return instance;
	}

	public static void clearInstance() {
		instance = null;
	}

	private MovieDataModel(Context ctx) {
	}

	public synchronized void loadSearchResult(String query)
			throws ClientProtocolException, JSONException, IOException {
		mSearchList = new ArrayList<SearchItemData>();
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair(KEY_SETTING, "response_type:json"));
		param.add(new BasicNameValuePair(KEY_SET_DEVICE, "android(APP)"));
		param.add(new BasicNameValuePair(KEY_QUERY, URLEncoder.encode(query,
				HTTP.UTF_8)));
		JSONArray a = new JSONArray(Util.Stream.stringFromURLbyPOST(
				Domain.ACCESS_DOMAIN + SEARCH_PATH, param));
		for (int i = 0; i < a.length(); i++) {
			JSONObject jitem = a.getJSONObject(i);
			SearchItemData data = new SearchItemData();
			data.koreanName = jitem.getString("title");
			data.englishName = jitem.getString("title_english");
			if ("".equalsIgnoreCase(data.englishName)) {
				data.fullName = data.koreanName;
			} else {
				data.fullName = data.koreanName + " ( " + data.englishName
						+ " )";
			}
			mSearchList.add(data);
		}
	}
	
	public ArrayList<SearchItemData> getSearchResult() {
		// TODO Auto-generated method stub
		return mSearchList;
	}

	public synchronized void loadMovieList(CategoryItemData data)
			throws ClientProtocolException, IOException, JSONException {
		mBestDataList = new ArrayList<BestItemData>();
		JSONObject o = Util.Stream.jsonFromURL(data.url);
		JSONArray list = o.getJSONArray(KEY_LIST);
		for (int i = 0; i < list.length(); i++) {
			BestItemData item = new BestItemData();
			JSONObject movieInfo = list.getJSONObject(i);
			JSONObject postimage = movieInfo.getJSONObject("postimage");
			item.targetURL = Domain.WEB_DOMAIN
					+ "vod/detail.html?SET_DEVICE=android(APP)&movieProduct_seq="
					+ movieInfo.getString(KEY_MOVIE_NUM);
			item.isAdult = "Y".equals(postimage.getString(KEY_ISADULT));
			item.imageURL = postimage.getString(KEY_THUMB_URL);
			// item.title = o.getString(KEY_TITLE);
			mBestDataList.add(item);
		}
	}

	public ArrayList<BestItemData> getMovieList() {
		return mBestDataList;
	}
	
	public void setCurrentCategory(CategoryItemData data) {
		mCurrentCategory = data;
	}
	
	public CategoryItemData getCurrentCategory() {
		return mCurrentCategory;
	}
}
