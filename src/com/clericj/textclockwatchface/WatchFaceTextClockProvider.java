package com.clericj.textclockwatchface;

import java.util.Calendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.RemoteViews;


/*
 * Виджет играет роль циферблата для умных часов от SmartQ ZWatch
 * Особенностью виджета является текстовый вывод текущего времени
 * более естественный для запоминания человеком.
 * Часы показывают время так, как если бы вам ответил человек, спроси
 * вы его - который сейчас час
 */

public class WatchFaceTextClockProvider extends AppWidgetProvider {
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		registerReceivers(context);
		updateTime(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		String action = intent.getAction();
		if (Intent.ACTION_TIME_TICK.equals(action)
				|| Intent.ACTION_TIME_CHANGED.equals(action)) {
			updateTime(context);
		}
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		registerReceivers(context);

		Intent serviceIntent = new Intent(context, TextClockService.class);
		context.startService(serviceIntent);
		updateTime(context);
	}

	private void registerReceivers(Context context) {
		/* Регистрация отслеживаемых событий
		 */
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIME_TICK);
		context.getApplicationContext().registerReceiver(this, filter);
	}
	

	private void updateTime(Context context) {
	/* Задание актуального времени
	*/
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.textclock_widget);

		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);

		views.setTextViewText(R.id.time, getTimeText(hour, minute));
		manager.updateAppWidget(new ComponentName(context,
			WatchFaceTextClockProvider.class), views);

	}

	private int incrementHour(int hour) {
	/* Сдвиг часа на одно деление вперёд в 12 часовом формате
	 * т.е. если у нас 12 часов, то следующим будет 1
	 * hour - значение часа
	 */
    	if(hour == 12) {
    		hour = 1;
    	} else {
    		hour += 1;
    		if(hour == 12) {
    			hour = 0;
    		}
    	}
    	return hour;
	}

	String getHourText(int hour, int variant) {
	/* Получение текстового представления часа
	 * hour - значение часа
	 * variant - вариант написания часа
	*/
		String[][] hour_text_variants = {
				{"двенадцать",     "двенадцать часов",    "двенадцатого"},
				{"час",            "один час",            "первого"},
				{"два",            "два часа",            "второго"},
				{"три",            "три часа",            "третьего"},
				{"четыре",         "четыре часа",         "четвёртого"},
				{"пять",           "пять часов",          "пятого"},
				{"шесть",          "шесть часов",         "шестого"},
				{"семь",           "семь часов",          "седьмого"},
				{"восемь",         "восемь часов",        "восьмого"},
				{"девять",         "девять часов",        "девятого"},
				{"десять",         "десять часов",        "десятого"},
				{"одиннадцать",    "одиннадцать часов",   "одиннадцатого"}
		};
		return hour_text_variants[hour][variant];
	}

	String getTimeText(int h, int m) {
	/* Получение текстового представления времени
	 * m - значение минут
	 * h - значение часа
	 */
		//System.out.println(String.format("getTimeText(%d, %d)", hour, minute));
		String r = "";
		if(h >= 12) {
			h -= 12;
		}
        if(m == 58 || m == 59) { r = String.format("почти %s", getHourText(incrementHour(h), 0));
        } else if((m >= 0)&&(m <  3)) {r=String.format("%s", getHourText(h, 1));
        } else if((m >  2)&&(m <  8)) {r=String.format("%s пять минут", getHourText(h, 1));
        } else if((m >  7)&&(m < 13)) {r=String.format("%s десять минут", getHourText(h, 1));
        } else if((m > 12)&&(m < 18)) {r=String.format("четверть %s", getHourText(incrementHour(h), 2));
        } else if((m > 17)&&(m < 23)) {r=String.format("%s двадцать минут", getHourText(h, 1));
        } else if((m > 22)&&(m < 28)) {r=String.format("%s двадцать пять минут", getHourText(h, 1));
        } else if((m > 27)&&(m < 33)) {r=String.format("половина %s", getHourText(incrementHour(h), 2));
        } else if((m > 32)&&(m < 38)) {r=String.format("%s тридцать пять минут", getHourText(h, 1));
        } else if((m > 37)&&(m < 43)) {r=String.format("без двадцати %s", getHourText(incrementHour(h), 0));
        } else if((m > 42)&&(m < 48)) {r=String.format("без четверти %s", getHourText(incrementHour(h), 0));
        } else if((m > 47)&&(m < 53)) {r=String.format("без десяти %s", getHourText(incrementHour(h), 0));
        } else if((m > 52)&&(m < 58)) {r=String.format("без пяти %s", getHourText(incrementHour(h), 0));
        }
        System.out.println(r);
		return r;
	}
}
