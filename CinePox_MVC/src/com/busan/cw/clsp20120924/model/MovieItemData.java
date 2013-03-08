package com.busan.cw.clsp20120924.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.busan.cw.clsp20120924.base.JSONStruct;

/**
 * 영화 목록 아이템 데이터 객체
 * 
 * @author CINEPOX
 * 
 */
public class MovieItemData extends JSONStruct {

	private boolean sellable;

	private String postImageUrl;
	private int viewClass;
	private String title;
	private String director;
	private String actor;
	private String genre;
	private int downloadPrice;
	private int streamPrice;
	private int rating;
	private int showTime;
	private String country;
	private String discussion;
	private int movieNum;
	private boolean adult;

	private boolean hd;
	private boolean tvlive;
	private boolean newMovie;
	private boolean online;
	private boolean kome;
	private boolean cinema;
	private boolean threed;
	private boolean dubbing;
	private boolean month;

	public MovieItemData(JSONObject o) throws JSONException {
		super(o);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void buildResult(JSONObject o) throws JSONException {
		// TODO Auto-generated method stub
		JSONObject postImageJson = o.getJSONObject(RES_POSTIMAGE);
		postImageUrl = postImageJson.getString(RES_THUMB_URL);
		adult = isY(postImageJson.getString(RES_IS_ADULT));
		// postImageUrl = postImageJson.getString(RES_URL);

		viewClass = o.getInt(RES_VIEW_CLASS);
		title = o.getString(RES_TITLE);
		director = o.getString(RES_DIRECTOR);
		actor = o.getString(RES_LEADER_ACTOR);
		genre = o.getString(RES_VIEW_GENRE);
		downloadPrice = o.getInt(RES_DOWNLOAD_PRICE);
		streamPrice = o.getInt(RES_STREAM_PRICE);
		discussion = o.getString(RES_DISCUSSION);

		try {
			showTime = o.getInt(RES_RUNNING_TIME);
		} catch (JSONException e) {
		}
		
		country = o.getString(RES_VIEW_COUNTRY);

		try {
			JSONObject ratingJson = o.getJSONObject(RES_NETIZEN);
			rating = ratingJson.getInt(RES_NET_RATING);
		} catch (JSONException e) {
		}

		movieNum = o.getInt(RES_MOVIE_NUM);

		hd = isY(o.getString(RES_IS_HD));
		tvlive = isY(o.getString(RES_IS_TVLIVE));
		newMovie = isY(o.getString(RES_IS_NEW));
		online = isY(o.getString(RES_IS_ONLINE));
		kome = isY(o.getString(RES_IS_KOME));
		cinema = isY(o.getString(RES_IS_CINEMA));
		threed = isY(o.getString(RES_IS_3D));
		dubbing = isY(o.getString(RES_IS_DUBBING));
		month = isY(o.getString(RES_IS_MONTH));

		sellable = isY(o.getString(RES_IS_SELL));
	}

	public boolean isSellable() {
		return sellable;
	}

	public String getPostImageUrl() {
		return postImageUrl;
	}

	public int getViewClass() {
		return viewClass;
	}

	public String getTitle() {
		return title;
	}

	public String getDirector() {
		return director;
	}

	public String getActor() {
		return actor;
	}

	public String getGenre() {
		return genre;
	}

	public int getDownloadPrice() {
		return downloadPrice;
	}

	public int getStreamPrice() {
		return streamPrice;
	}

	public int getRating() {
		return rating;
	}

	public int getMovieNum() {
		return movieNum;
	}

	public boolean isHd() {
		return hd;
	}

	public boolean isTvlive() {
		return tvlive;
	}

	public boolean isNewMovie() {
		return newMovie;
	}

	public boolean isOnline() {
		return online;
	}

	public boolean isKome() {
		return kome;
	}

	public String getDiscussion() {
		return discussion;
	}

	public String getCountry() {
		return country;
	}

	public int getShowTime() {
		return showTime;
	}

	public boolean isCinema() {
		return cinema;
	}

	public boolean is3d() {
		return threed;
	}

	public boolean isMonth() {
		return month;
	}

	public boolean isDubbing() {
		return dubbing;
	}

	public boolean isAdult() {
		return adult;
	}
}
