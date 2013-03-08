package com.busan.cw.clsp20120924.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.busan.cw.clsp20120924.base.JSONStruct;

public class Top100Data extends JSONStruct {
	
	private int num;
	private String title;
	private int changed;

	public Top100Data(JSONObject o) throws JSONException {
		super(o);
		// TODO Auto-generated constructor stub
	}

	public int getNum() {
		return num;
	}

	public String getTitle() {
		return title;
	}

	public int getChanged() {
		return changed;
	}

	@Override
	public void buildResult(JSONObject o) throws JSONException {
		// TODO Auto-generated method stub
		
	}

}
