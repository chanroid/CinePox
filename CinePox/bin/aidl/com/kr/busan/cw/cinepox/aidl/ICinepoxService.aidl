package com.kr.busan.cw.cinepox.aidl;

import com.kr.busan.cw.cinepox.aidl.ICinepoxServiceCallback;

interface ICinepoxService {
	void doService(int what, String data);
	void registerCallback(ICinepoxServiceCallback cb);
	void unregisterCallback(ICinepoxServiceCallback cb);
}