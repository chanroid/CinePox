/**
 * 0. Project  : CinePox_MVC
 *
 * 1. FileName : CinePoxWidgetProvider.java
 * 2. Package : com.busan.cw.clsp20120924.controller
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 31. 오후 6:47:58
 * 6. 변경이력 : 
 *		2012. 10. 31. 오후 6:47:58 : 신규
 *
 */
package com.busan.cw.clsp20120924.controller;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.busan.cw.clsp20120924.R;
import com.busan.cw.clsp20120924.model.ConfigModel;
import com.busan.cw.clsp20120924.structs.WidgetUpdateData;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : CinePoxWidgetProvider.java
 * 3. Package  : com.busan.cw.clsp20120924.controller
 * 4. Comment  : 위젯 프로바이더 ㅇㅇ
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 6:47:58
 * </PRE>
 */
public class CinePoxWidgetProvider extends AppWidgetProvider {

	public final static String ACTION_QRPLAY = "QRPLAY";
	private long mInterval = 30000l;
	private PendingIntent mIntentSender;
	private AlarmManager mAlarmManager;
	private ConfigModel mConfigModel;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		String action = intent.getAction();
		if ("android.appwidget.action.APPWIDGET_UPDATE"
				.equalsIgnoreCase(action)) {
			removePreviousAlarm();
			registerAlarm(context, intent);
		} else if ("android.appwidget.action.APPWIDGET_DISABLED"
				.equalsIgnoreCase(action)) {
			removePreviousAlarm();
		}
	}

	void setInterval(long interval) {
		if (mInterval >= 10000l)
			mInterval = interval;
	}

	private void registerAlarm(Context context, Intent intent) {
		long firstTime = System.currentTimeMillis() + mInterval;
		mIntentSender = PendingIntent.getBroadcast(context, 0, intent, 0);
		mAlarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		mAlarmManager.set(AlarmManager.RTC, firstTime, mIntentSender);
	}

	private void removePreviousAlarm() {
		// TODO Auto-generated method stub
		if (mAlarmManager != null && mIntentSender != null) {
			mIntentSender.cancel();
			mAlarmManager.cancel(mIntentSender);
		}
	}

	private void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int widgetId) {
		ArrayList<WidgetUpdateData> dataList = mConfigModel.getWidgetData();

		if (dataList == null || dataList.size() < 1)
			return;

		WidgetUpdateData result = dataList.get((int) (Math.random() * (dataList
				.size() - 1)));

		if (result == null)
			return;

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget_provider);

		initViewContent(context, result, views);

		appWidgetManager.updateAppWidget(widgetId, views);
	}

	private void initViewContent(Context ctx, WidgetUpdateData data,
			RemoteViews views) {
		Intent qrplayIntent = new Intent(ctx, IntroActivity.class);
		qrplayIntent.setAction(ACTION_QRPLAY);
		PendingIntent qrpi = PendingIntent.getActivity(ctx, 0, qrplayIntent, 0);
		views.setOnClickPendingIntent(R.id.btn_widget_qrplay, qrpi);

		Intent searchIntent = new Intent(ctx, SearchActivity.class);
		PendingIntent searchpi = PendingIntent.getActivity(ctx, 0,
				searchIntent, 0);
		views.setOnClickPendingIntent(R.id.btn_widget_search, searchpi);

		Intent movieIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.url));
		PendingIntent moviepi = PendingIntent.getActivity(ctx, 0, movieIntent,
				0);
		views.setOnClickPendingIntent(R.id.tv_widget_contents, moviepi);
		views.setTextViewText(R.id.tv_widget_title, data.title);
		views.setTextViewText(R.id.tv_widget_contents, data.desc);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		for (int i : appWidgetIds)
			updateAppWidget(context, appWidgetManager, i);
	}

}
