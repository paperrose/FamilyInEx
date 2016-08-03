package com.artfonapps.familyinex.network;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import org.json.JSONArray;

/**
 * Created by paperrose on 31.07.2016.
 * Запускается раз в 5 минут
 * Отсылает все запросы, которые не отправились ранее
 */
public class SenderService extends IntentService{
    public static final String TAG = "SenderService";


    public SenderService() {
        super(TAG);
    }

    @Override
    public void onCreate() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Communicator.reSendPush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
