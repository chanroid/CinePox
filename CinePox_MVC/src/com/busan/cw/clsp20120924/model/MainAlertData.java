package com.busan.cw.clsp20120924.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.busan.cw.clsp20120924.base.JSONStruct;

public class MainAlertData extends JSONStruct {
	public MainAlertData(JSONObject o) throws JSONException {
		super(o);
		// TODO Auto-generated constructor stub
	}

	private String num;
	private String what;
	private String type;
	private String message;
	private String image;
	private String url;

	public String getNum() {
		return num;
	}

	public String getWhat() {
		return what;
	}

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public String getImage() {
		return image;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public void buildResult(JSONObject o) throws JSONException {
		// TODO Auto-generated method stub
		num = o.getString(EXTRA_CODE);
		what = o.getString(EXTRA_WHAT);
		type = o.getString(EXTRA_TYPE);
		message = o.getString(EXTRA_MSG);
		image = o.getString(EXTRA_IMAGE);
		url = o.getString(EXTRA_URL);
	}

}
