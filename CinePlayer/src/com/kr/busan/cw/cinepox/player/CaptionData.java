package com.kr.busan.cw.cinepox.player;


public class CaptionData  {
	private String text;
	private long time;
	public CaptionData(long time, String text) {
		this.time = time;
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public long getTime() {
		return time;
	}
}
