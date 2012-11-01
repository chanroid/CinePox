package com.kr.busan.cw.cinepox.player.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

import com.kr.busan.cw.cinepox.player.structs.QualityData;

import kr.co.chan.util.Util;
import android.content.Context;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : VideoModel.java
 * 3. Package  : com.kr.busan.cw.cinepox.player.model
 * 4. Comment  : 플레이어 제어 기능에 관련된 처리 및 자료구조
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 15. 오후 1:51:18
 * </PRE>
 */
public class VideoModel extends PlayerModel {

	private static VideoModel instance;

	public static VideoModel getInstance(Context ctx) {
		if (instance == null)
			instance = new VideoModel(ctx);
		return instance;
	}

	private Context mContext;
	private Calendar mCalendar;

	private ArrayList<QualityData> mQualityArray;


	private VideoModel(Context ctx) {
		mContext = ctx;
		mCalendar = Calendar.getInstance();
	}
	
	public ArrayList<QualityData> getQualityArray() {
		return mQualityArray;
	}

	public void setQualityArray(ArrayList<QualityData> data) {
		mQualityArray = data;
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : sendShownTime
	 * 2. ClassName  : VideoModel
	 * 3. Comment   : 현재 재생정보를 서버로 전송
	 * 				재생중인 영화, 총 재생시간, 재생중인 구간
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 3:59:22
	 * </PRE>
	 * 
	 * @return void
	 */
	public void sendShownTime() {

	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : getTimeText
	 * 2. ClassName  : VideoModel
	 * 3. Comment   : 현재 시간을 00:00 형태의 String으로 리턴
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 4:44:06
	 * </PRE>
	 * 
	 * @return String
	 */
	public String getTimeText() {
		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int min = mCalendar.get(Calendar.MINUTE);
		StringBuilder mFormatBuilder = new StringBuilder();
		Formatter mFormatter = new Formatter(mFormatBuilder,
				Locale.getDefault());
		return mFormatter.format("%02d:%02d", hour, min).toString();
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : getBattText
	 * 2. ClassName  : VideoModel
	 * 3. Comment   : 현재 배터리 수준을 표시할 String 리턴
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 4:46:23
	 * </PRE>
	 * 
	 * @return String
	 */
	public String getBattText() {
		return Util.Phone.getBattStat(mContext) + "%";
	}

}
