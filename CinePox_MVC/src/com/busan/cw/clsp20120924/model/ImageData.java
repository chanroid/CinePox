package com.busan.cw.clsp20120924.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.busan.cw.clsp20120924.base.JSONStruct;

/**
 * 영화목록에서 썸네일, 스틸 이미지를 표시하는데 필요한 객체
 * 
 * @author CINEPOX
 *
 */
public class ImageData extends JSONStruct {
	
	public ImageData(JSONObject o) throws JSONException {
		super(o);
	}
	
	private boolean adult;
	private String imageUrl;
	private String thumbUrl;

	/**
	 * 성인이미지 여부
	 * 
	 * @return
	 */
	public boolean isAdult() {
		return adult;
	}

	/**
	 * 원본 이미지 url
	 * 
	 * @return
	 */
	public String getImageUrl() {
		return imageUrl;
	}
	
	/**
	 * 썸네일 이미지 url
	 * 
	 * @return
	 */
	public String getThumbUrl() {
		return thumbUrl;
	}

	@Override
	public void buildResult(JSONObject o) throws JSONException {
		// TODO Auto-generated method stub
		adult = isY(o.getString(RES_IS_ADULT));
		thumbUrl = o.getString(RES_THUMB_URL);
		imageUrl = o.getString(RES_URL);
	}

}
