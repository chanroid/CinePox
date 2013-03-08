package com.busan.cw.clsp20120924.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.busan.cw.clsp20120924.base.JSONStruct;

/**
 * 페이징이 필요한 경우 각 페이지 식별에 필요한 객체
 * 
 * @author CINEPOX
 *
 */
public final class SimplePagingData extends JSONStruct {

	private boolean selected;
	private int pageNum;

	public SimplePagingData(JSONObject page) throws JSONException {
		// TODO Auto-generated constructor stub
		super(page);
	}

	/**
	 * 현재 페이지인지에 대한 여부
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * 해당 페이지 번호
	 * 
	 * @return
	 */
	public int getPageNum() {
		return Integer.valueOf(pageNum);
	}

	@Override
	public void buildResult(JSONObject o) throws JSONException {
		// TODO Auto-generated method stub
		pageNum = o.getInt(RES_NUM);
		selected = o.getBoolean(RES_SELECTED);
	}

}
