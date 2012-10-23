package com.kr.busan.cw.cinepox.player;

public class QualityData {
	private String Name;
	private String Type;
	private String Url;
	private String Key;
	
	public String getKey() {
		return Key;
	}
	
	public void setKey(String key) {
		Key = key;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public QualityData(String name, String type, String url, String key) {
		super();
		Name = name;
		Type = type;
		Url = url;
		Key = key;
	}

	public QualityData() {
		super();
		// TODO Auto-generated constructor stub
	}
}
