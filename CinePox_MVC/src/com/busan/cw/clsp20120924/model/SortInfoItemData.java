package com.busan.cw.clsp20120924.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.busan.cw.clsp20120924.base.JSONStruct;

public class SortInfoItemData extends JSONStruct {

	private String name;
	private String code;

	public SortInfoItemData(JSONObject o) throws JSONException {
		super(o);
		// TODO Auto-generated constructor stub
	}
	
	public SortInfoItemData(String code, String name) throws JSONException {
		super(null);
		this.code = code;
		this.name = name;
	}

	@Override
	public void buildResult(JSONObject o) throws JSONException {
		// TODO Auto-generated method stub
		if (o == null)
			return;
		
		code = o.getString(EXTRA_CODE);
		name = o.getString(EXTRA_NAME);
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
