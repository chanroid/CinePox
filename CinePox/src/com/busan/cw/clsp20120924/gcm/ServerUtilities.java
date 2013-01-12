/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.busan.cw.clsp20120924.gcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import utils.LogUtils.l;
import utils.StreamUtils;
import android.content.Context;

import com.google.android.gcm.GCMRegistrar;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	/**
	 * Register this account/device pair within the server.
	 * 
	 */
	public static void register(final Context context, final String regId,
			String memberId) {
		l.i("registering device (regId = " + regId + ")");
		final String serverUrl = CommonUtilities.SERVER_URL + "/setKey";
		final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("registration_id", regId));
		params.add(new BasicNameValuePair("member_seq", memberId));
		// Once GCM returns a registration id, we need to register it in the
		// demo server. As the server might be down, we will retry it a couple
		// times.
		new Thread() {
			public void run() {
				long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
				for (int i = 1; i <= MAX_ATTEMPTS; i++) {
					l.i("Attempt #" + i + " to register");
					try {
						StreamUtils.inStreamFromURL(serverUrl, params);
						GCMRegistrar.setRegisteredOnServer(context, true);
						return;
					} catch (IOException e) {
						// Here we are simplifying and retrying on any error; in
						// a real
						// application, it should retry only on unrecoverable
						// errors
						// (like HTTP error code 503).
						l.i("Failed to register on attempt " + i + ":" + e);
						if (i == MAX_ATTEMPTS) {
							break;
						}
						try {
							l.i("Sleeping for " + backoff + " ms before retry");
							Thread.sleep(backoff);
						} catch (InterruptedException e1) {
							// Activity finished before we complete - exit.
							l.i("Thread interrupted: abort remaining retries!");
							Thread.currentThread().interrupt();
							return;
						}
						// increase backoff exponentially
						backoff *= 2;
					}
				}
			}
		}.start();
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	static void unregister(final Context context, final String regId) {
		l.i("unregistering device (regId = " + regId + ")");
		// String serverUrl = SERVER_URL + "/deleteKey";
		// Map<String, String> params = new HashMap<String, String>();
		// params.put("regId", regId);
		// try {
		// post(serverUrl, params);
		GCMRegistrar.setRegisteredOnServer(context, false);
		// } catch (IOException e) {
		// At this point the device is unregistered from GCM, but still
		// registered in the server.
		// We could try to unregister again, but it is not necessary:
		// if the server tries to send a message to the device, it will get
		// a "NotRegistered" error message and should unregister the device.
		// }
	}

}
