package com.busan.cw.clsp20120924.base;

import model.CCModel;
import android.content.Context;

import com.busan.cw.clsp20120924_beta.R;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;

public abstract class BaseModel extends CCModel implements Constants {

	private Context context;

	public BaseModel(Context ctx) {
		context = ctx;
	}

	public CinepoxAppModel app() {
		return (CinepoxAppModel) context.getApplicationContext();
	}
	
	public Context getContext() {
		return context;
	}

	public String connectError() {
		return context.getString(R.string.error_connect);
	}

	public String deviceError() {
		return context.getString(R.string.error_device);
	}

	public String dataError() {
		return context.getString(R.string.error_server_data);
	}

}
