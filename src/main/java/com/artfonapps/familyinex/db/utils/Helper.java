package com.artfonapps.familyinex.db.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.artfonapps.familyinex.db.models.Expense;
import com.artfonapps.familyinex.db.models.Income;
import com.artfonapps.familyinex.db.models.JsonModel;

import java.sql.Date;
import java.text.ParseException;
import java.util.List;

/**
 * Created by paperrose on 05.07.2016.
 */
public class Helper {
    private Context context;
    public static final String COMMAND = "refresh_push_count";

    public void setReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(messageReceiver, new IntentFilter(COMMAND));
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            try {
                JsonModel jModel = new JsonModel();
            } catch (Exception e) {

            }
        }
    };
}
