package com.busan.cw.clsp20120924.base;

import model.CCModel;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JSONStruct extends CCModel implements Constants {
	public JSONStruct(JSONObject o) throws JSONException {
		buildResult(o);
	}
	
	public abstract void buildResult(JSONObject o) throws JSONException;
}
