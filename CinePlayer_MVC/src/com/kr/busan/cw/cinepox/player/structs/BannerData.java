package com.kr.busan.cw.cinepox.player.structs;

import android.os.Parcel;
import android.os.Parcelable;

public class BannerData implements Parcelable {

	public static final String EXTRA_KEY = "bannerdata";

	public static final int TYPE_TEXT = 1;
	public static final int TYPE_VIDEO = 2;
	public static final int TYPE_IMAGE = 3;

	public int SEQ;
	public String TITLE;
	public String CONTENTS;
	public boolean isShowed = false;
	public int TYPE;
	public int SHOWTIME; // 이미지일 경우에만 적용 SHOWTIME만큼만 표시

	public BannerData() {
	}

	public BannerData(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void readFromParcel(Parcel in) {
		// TODO Auto-generated method stub
		SEQ = in.readInt();
		TITLE = in.readString();
		CONTENTS = in.readString();
		isShowed = (Boolean) in.readValue(Boolean.class.getClassLoader());
		TYPE = in.readInt();
		SHOWTIME = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(SEQ);
		dest.writeString(TITLE);
		dest.writeString(CONTENTS);
		dest.writeValue(isShowed);
		dest.writeInt(TYPE);
		dest.writeInt(SHOWTIME);
	}

	public static final Parcelable.Creator<BannerData> CREATOR = new Parcelable.Creator<BannerData>() {
		public BannerData createFromParcel(Parcel in) {
			return new BannerData(in);
		}

		public BannerData[] newArray(int size) {
			return new BannerData[size];
		}
	};
}
