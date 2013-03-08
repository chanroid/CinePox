package com.busan.cw.clsp20120924.interfaces;


public interface CommonWebViewJSInterface {

	public void confirm(String message, String confirmMethod,
			String cancelMethod);

	public void alert(String message, String confirmMethod);

	public void close(String message);

	public void close();

	public void loading(String message);

	public void loading();

	public void loadingClose();
}
