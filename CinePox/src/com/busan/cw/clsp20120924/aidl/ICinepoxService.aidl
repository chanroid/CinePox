package com.busan.cw.clsp20120924.aidl;

import com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback;

interface ICinepoxService {
	void doService(int what, String data);
	void registerCallback(ICinepoxServiceCallback cb);
	void unregisterCallback(ICinepoxServiceCallback cb);
}