package com.busan.cw.clsp20120924.model;

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
import utils.StreamUtils;
import utils.StringUtils;
import android.content.Context;
import android.os.AsyncTask;

import com.busan.cw.clsp20120924.base.BaseModel;
import com.busan.cw.clsp20120924.base.DomainManager;
import com.busan.cw.clsp20120924.interfaces.MovieDetailModelCallback;

public class MovieDetailModel extends BaseModel {

	private boolean sellable;

	private String postImageUrl;
	private int viewClass;
	private String title;
	private String director;
	private String actor;
	private String supportActor;
	private String extra;
	private String genre;
	private String previewVideo;
	private int downloadPrice;
	private int streamPrice;
	private int rating;
	private int showTime;
	private String country;
	private String discussion;
	private String content;
	private String award;
	private int movieNum;
	private boolean adult;

	private ArrayList<ImageData> stillImages;

	private boolean hd;
	private boolean tvlive;
	private boolean newMovie;
	private boolean online;
	private boolean kome;
	private boolean cinema;
	private boolean threed;
	private boolean dubbing;
	private boolean month;

	private MovieDetailModelCallback callback;

	public void setCallback(MovieDetailModelCallback callback) {
		this.callback = callback;
	}

	public MovieDetailModel(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	public void loadMovieDetail(int movieNum) {
		new MovieDetailSync().execute(movieNum);
	}

	private class MovieDetailSync extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (callback != null)
				callback.onMovieDetailLoadStart(MovieDetailModel.this);
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub

			String url = DomainManager.getAppDomain(getContext())
					+ app().getAppConfig().getMovieDetailUrl();
			ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(RES_MOVIE_NUM, String
					.valueOf(params[0])));
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(url)
						.openConnection();
				DomainManager.signHeader(conn);
				StreamUtils.addPostParams(conn, postParams);
				JSONObject movieJson = new JSONObject(
						StringUtils.stringFromStream(conn.getInputStream()));
				l.d("movieDetail data : " + movieJson.toString());
				if (!isY(movieJson.getString(EXTRA_RESULT)))
					return movieJson.getString(EXTRA_MSG);

				JSONObject dataJson = movieJson.getJSONObject(EXTRA_DATA);
				return parse(dataJson);
			} catch (IOException e) {
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
			if (callback != null)
				if (result == null)
					callback.onMovieDetailLoadComplete(MovieDetailModel.this);
				else
					callback.onMovieDetailLoadError(MovieDetailModel.this,
							result);
		}

	}

	private String parse(JSONObject o) {
		try {
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
			supportActor = o.getString(RES_ASSIST_ACTOR);
			extra = o.getString(RES_EXTRA_ACTOR);
			content = o.getString(RES_DESC);
			award = o.getString(RES_AWARD);
			country = o.getString(RES_VIEW_COUNTRY);
			movieNum = o.getInt(RES_MOVIE_NUM);

			try {
				showTime = o.getInt(RES_RUNNING_TIME);
			} catch (JSONException e) {
			}

			try {
				JSONArray previewArray = o.getJSONArray(RES_PREVIEW);
				for (int i = 0; i < previewArray.length();) {
					JSONObject previewJson = previewArray.getJSONObject(i);
					previewVideo = previewJson.getString(RES_DESC);
					break;
				}
			} catch (JSONException e) {
			}

			try {
				JSONObject ratingJson = o.getJSONObject(RES_NETIZEN);
				rating = ratingJson.getInt(RES_NET_RATING);
			} catch (JSONException e) {
			}

			try {
				JSONArray stillImageArray = o.getJSONArray(RES_STILLIMAGE);
				stillImages = new ArrayList<ImageData>();
				for (int i = 0; i < stillImageArray.length(); i++) {
					ImageData data = new ImageData(
							stillImageArray.getJSONObject(i));
					stillImages.add(data);
				}
			} catch (JSONException e) {
			}

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
		} catch (JSONException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return null;
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

	public String getSupportActor() {
		return supportActor;
	}

	public String getExtra() {
		return extra;
	}

	public String getPreviewVideo() {
		return previewVideo;
	}

	public String getContent() {
		return content;
	}

	public String getAward() {
		return award;
	}

	public ArrayList<ImageData> getStillImages() {
		return stillImages;
	}
}
