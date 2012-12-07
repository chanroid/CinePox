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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

	/**
	 * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
	 */
	// static final String SERVER_URL = "http://chanroid.appspot.com";
	public static final String SERVER_URL = "http://access.cinepox.com/androidPushMsg";

	/**
	 * Google API project id registered to use GCM.
	 */
	public static final String SENDER_ID = "223311663856";

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	public static void generateNotification(Context context, GCMMessageData data) {
		String message = data.message;
		String url = data.url;
		int lightColor = 0xffff0000;
		long[] vibrate = new long[] { 0, 200, 300, 200 };

		NotificationManager mNotiManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent contentIntent;
		if (data.url == null || "".equals(data.url))
			contentIntent = PendingIntent.getActivity(context, 0, new Intent(),
					0);
		else
			contentIntent = PendingIntent.getActivity(context, 0, new Intent(
					Intent.ACTION_VIEW, Uri.parse(url)), 0);

		if (Build.VERSION.SDK_INT >= 14) {
			Notification.Builder mBuilder = new Notification.Builder(context);
			mBuilder.setVibrate(vibrate);
			mBuilder.setLights(lightColor, 500, 500);
			mBuilder.setContentTitle(data.title);
			mBuilder.setSmallIcon(com.busan.cw.clsp20120924.R.drawable.ic_launcher);
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(contentIntent);
			mBuilder.setContentText(message);
			mBuilder.setTicker(message);
			if (Build.VERSION.SDK_INT >= 16)
				mNotiManager.notify(data.action, mBuilder.build());
			else
				mNotiManager.notify(data.action, mBuilder.getNotification());
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context);
			mBuilder.setVibrate(vibrate);
			mBuilder.setLights(lightColor, 500, 500);
			mBuilder.setContentTitle(data.title);
			mBuilder.setSmallIcon(com.busan.cw.clsp20120924.R.drawable.ic_launcher);
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(contentIntent);
			mBuilder.setContentText(data.message);
			mBuilder.setTicker(data.message);
			mNotiManager.notify(data.action, mBuilder.getNotification());
		}

	}

}
