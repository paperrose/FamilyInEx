package com.artfonapps.familyinex.network;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.artfonapps.familyinex.ApplicationParameters;

/**
 * Created by paperrose on 08.07.2016.
 * Пока не используется
 * Нужен для запуска службы повторной пересылки
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
   /*     if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent alarmIntent = new Intent(context, GetDeviceIdService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ApplicationParameters.timerInterval, pendingIntent);
        }*/
    }
}
