package com.busan.cw.clsp20120924.movie;

import java.util.ArrayList;

import utils.DisplayUtils;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.busan.cw.clsp20120924.R;

public class WidgetProvider extends AppWidgetProvider {

	public final static String ACTION_QRPLAY = "QRPLAY";
	private long mInterval = 30000l;
	private PendingIntent mIntentSender;
	private AlarmManager mAlarmManager;

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
		ArrayList<UpdateData> dataList = Config.getInstance(context)
				.getWidgetData();

		if (dataList == null || dataList.size() < 1)
			return;

		UpdateData result = dataList.get((int) (Math.random() * (dataList
				.size() - 1)));
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget_provider);

		Intent qrplayIntent = new Intent(context, IntroActivity.class);
		qrplayIntent.setAction(ACTION_QRPLAY);
		PendingIntent qrpi = PendingIntent.getActivity(context, 0,
				qrplayIntent, 0);
		views.setOnClickPendingIntent(R.id.btn_widget_qrplay, qrpi);
		Intent searchIntent = new Intent(context, SearchActivity.class);
		PendingIntent searchpi = PendingIntent.getActivity(context, 0,
				searchIntent, 0);
		views.setOnClickPendingIntent(R.id.btn_widget_search, searchpi);

		if (result != null) {
			Intent movieIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(result.url));
			PendingIntent moviepi = PendingIntent.getActivity(context, 0,
					movieIntent, 0);
			views.setOnClickPendingIntent(R.id.tv_widget_contents, moviepi);
			views.setTextViewText(R.id.tv_widget_title, result.title);
			views.setTextViewText(R.id.tv_widget_contents, result.desc);
		}

		appWidgetManager.updateAppWidget(widgetId, views);
	}

	public static class UpdateData {
		public String title;
		public String desc;
		public String url;
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		for (int i : appWidgetIds)
			updateAppWidget(context, appWidgetManager, i);
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		if (DisplayUtils.isTablet(context))
			Toast.makeText(context, "태블릿에서는 사용하실 수 없습니다. 빠른 시일 내에 업데이트 하겠습니다.",
					Toast.LENGTH_SHORT).show();
		else
			super.onEnabled(context);
	}

}
