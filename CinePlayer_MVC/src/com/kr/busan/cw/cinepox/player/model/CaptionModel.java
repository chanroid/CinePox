/**
 * 0. Project  : CinePlayer_MVC
 *
 * 1. FileName : CaptionModel.java
 * 2. Package : com.kr.busan.cw.cinepox.player.model
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 16. 오후 4:25:59
 * 6. 변경이력 : 
 *		2012. 10. 16. 오후 4:25:59 : 신규
 *
 */
package com.kr.busan.cw.cinepox.player.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.chan.util.Util;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import com.kr.busan.cw.cinepox.player.model.PlayerModel.Const;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : CaptionModel.java
 * 3. Package  : com.kr.busan.cw.cinepox.player.model
 * 4. Comment  : 자막 관련기능 모델
 * 		반드시 생성 후 loadCaption이 끝난 뒤 사용할것
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 16. 오후 4:25:59
 * </PRE>
 */

public class CaptionModel {

	private static CaptionModel instance;

	public static CaptionModel getInstance() {
		if (instance == null)
			instance = new CaptionModel();
		return instance;
	}


	private Map<String, ArrayList<CaptionData>> mCaptionArray;
	private ArrayList<String> mLangNames;
	private ArrayList<String> mLangKeys;

	private String mCurrentCC = CaptionData.LANG_KR;
	private int mCurrentCCIndex = 0;
	private boolean isEnabled = false;

	public Map<String, ArrayList<CaptionData>> getCaptionArray() {
		return mCaptionArray;
	}

	public ArrayList<String> getLangNames() {
		return mLangNames;
	}

	public ArrayList<String> getLangKeys() {
		return mLangKeys;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : loadCaption
	 * 2. ClassName  : CaptionModel
	 * 3. Comment   : 지정된 경로에서 자막 파일을 가져와서
	 * 		데이터를 파싱
	 * 		(주의 : 반드시 네트워크 스레드에서 호출할 것) 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 16. 오후 4:40:09
	 * </PRE>
	 * 
	 * @return boolean (주의 : 이 값이 false가 되면 다시 수행하기 전까지는 아무 메서드도 호출하지 말것)
	 * @param path
	 *            로컬 경로 또는 네트워크 절대경로
	 */
	public synchronized boolean loadCaption(String path) {
		if (path == null || "".equalsIgnoreCase(path))
			return (isEnabled = false);
		InputStream in;
		try {
			if (path.contains("http://") || path.contains("https://")) {
				URL url = new URL(path);
				in = url.openStream();
			} else {
				File file = new File(path);
				in = new FileInputStream(file);
			}

			mCaptionArray = new HashMap<String, ArrayList<CaptionData>>();
			mLangNames = new ArrayList<String>();
			mLangKeys = new ArrayList<String>();

			String ext = Util.File.getExtension(path);
			if (ext.equalsIgnoreCase(Const.CAPTION_EXT_SMI)
					|| ext.equalsIgnoreCase(Const.CAPTION_EXT_SRT))
				parseSMICaption(in);
			else if (ext.equalsIgnoreCase(Const.CAPTION_EXT_SRT))
				parseSRTCaption(in);

			if (mCaptionArray.size() == 0 || mLangKeys.size() == 0
					|| mLangNames.size() == 0)
				return (isEnabled = false);

			mLangNames.add("자막 숨김");
			mLangKeys.add("");
			return (isEnabled = true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return (isEnabled = false);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return (isEnabled = false);
		} catch (IOException e) {
			e.printStackTrace();
			return (isEnabled = false);
		}
	}

	private void parseSMICaption(InputStream stream)
			throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		String buffer = "";
		long time = -1;
		String tag = null;
		String lang = null;
		String text = null;
		boolean smistart = false;

		while ((buffer = reader.readLine()) != null) {
			if (buffer.contains("{") && buffer.contains("CC")) {
				// 자막 언어구분정보 파싱
				String[] info = buffer.split(" ");
				for (String s : info) {
					String name, classname;
					if (s.contains("Name")) {
						name = s.replace("Name:", "").trim().replace(";", "")
								.replace("{", "");
						mLangNames.add(name);
						// 자막 구분 이름
					} else if (s.contains(".")) {
						classname = s.replace(".", "").trim();
						mLangKeys.add(classname);
						mCurrentCC = mLangKeys.get(0);
						// 자막 구분 클래스 이름
					}
				}
			}

			if (buffer.contains("<SYNC")) {
				smistart = true;
				if (time != -1) {
					Source s = new Source(tag);
					Element p = s.getFirstElement(HTMLElementName.P);
					if (p != null) {
						lang = p.getAttributeValue("Class");
						text = p.toString();
					} else {
						lang = "KRCC";
						text = tag;
					}

					for (String key : mLangKeys) {
						if (!mCaptionArray.containsKey(key)) {
							ArrayList<CaptionData> map = new ArrayList<CaptionData>();
							mCaptionArray.put(key, map);
						}
					}

					if (mCaptionArray.containsKey(lang)) {
						CaptionData data = new CaptionData();
						data.TIME = time;
						data.TEXT = text;
						mCaptionArray.get(lang).add(data);
					}
				}
				time = Integer.parseInt(buffer.substring(
						buffer.indexOf("=") + 1, buffer.indexOf(">")));
				tag = buffer
						.substring(buffer.indexOf(">") + 1, buffer.length());
			} else {
				// 시작태그가 아닌 경우 이전 태그의 뒤에 덧붙임
				if (smistart == true) {
					tag += buffer;
				}
			}
		}
	}

	private void parseSRTCaption(InputStream stream)
			throws NumberFormatException, IOException {

	}

	/**
	 * <PRE>
	 * 1. MethodName : setCCLang
	 * 2. ClassName  : CaptionModel
	 * 3. Comment   : 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 26. 오후 3:01:58
	 * </PRE>
	 * 
	 * @return void
	 * @param which
	 */
	public void setCCLang(int which) {
		mCurrentCC = mLangKeys.get(which);
		mCurrentCCIndex = which;
	}

	public int getCCLangIndex() {
		return mCurrentCCIndex;
	}

	public synchronized String getCaption(long playTime) {
		if (!mCaptionArray.containsKey(mCurrentCC))
			return "";
		int l = 0, m, h = mCaptionArray.get(mCurrentCC).size() - 1;
		while (l <= h) {
			m = (l + h) / 2;
			if (mCaptionArray.get(mCurrentCC).get(m).TIME <= playTime
					&& playTime < mCaptionArray.get(mCurrentCC).get(m + 1).TIME) {
				return mCaptionArray.get(mCurrentCC).get(m).TEXT;
			}
			if (playTime > mCaptionArray.get(mCurrentCC).get(m + 1).TIME) {
				l = m + 1;
			} else {
				h = m - 1;
			}
		}
		return mCaptionArray.get(mCurrentCC).get(0).TEXT.trim();
	}

	/**
	 * <PRE>
	 * 1. ClassName : CaptionData
	 * 2. FileName  : VideoModel.java
	 * 3. Package  : com.kr.busan.cw.cinepox.player.model
	 * 4. Comment  : 자막 정보를 가지고 있는 구조체
	 * 5. 작성자   : 박찬우
	 * 6. 작성일   : 2012. 10. 15. 오후 3:56:43
	 * </PRE>
	 */
	public class CaptionData {
		public static final String LANG_KR = "KRCC";
		public static final String LANG_EN = "ENCC";
		public static final String LANG_JP = "JPCC";
		public static final String LANG_CN = "CNCC";

		public String TEXT;
		public long TIME;
	}

}
