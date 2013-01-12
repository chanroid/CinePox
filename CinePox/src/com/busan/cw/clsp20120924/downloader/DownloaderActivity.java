package com.busan.cw.clsp20120924.downloader;

import java.net.URLEncoder;

import org.apache.http.protocol.HTTP;

import utils.StorageUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.busan.cw.clsp20120924.movie.CinepoxService;
import com.busan.cw.clsp20120924.movie.Config;
import com.busan.cw.clsp20120924.movie.Domain;

public class DownloaderActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (!StorageUtils.isExternalMemoryAvailable()) {
			Toast.makeText(this, "외장메모리가 없는 단말기에서는 다운로드 서비스를 이용하실 수 없습니다.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		try {
			startService(new Intent(this, CinepoxService.class));
			Uri uri = getIntent().getData();
			if (uri == null)
				return;
			String order_code = uri.getQueryParameter("order_code");
			String product_num = uri.getQueryParameter("productInfo_seq");
			String ctype = URLEncoder.encode(uri.getQueryParameter("ctype"),
					HTTP.UTF_8);
			String mem_num = uri.getQueryParameter("member_seq");
			String setting = "response_type:json";
			String url = Domain.ACCESS_DOMAIN
					+ "download/appDownloadUrl/?order_code=%s&productInfo_seq=%s&ctype=%s&member_seq=%s&setting=%s&SET_DEVICE=android(APP)";
			url = String.format(url, order_code, product_num, ctype, mem_num,
					setting);
			Intent i = new Intent(CinepoxService.ACTION_START_DOWNLOAD);
			i.putExtra("url", url);
			sendBroadcast(i);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Config getConfig() {
		// TODO Auto-generated method stub
		return Config.getInstance(this);
	}
}
