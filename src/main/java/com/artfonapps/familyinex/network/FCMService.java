package com.artfonapps.familyinex.network;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.artfonapps.familyinex.constants.Comparables;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.constants.DbTables;
import com.artfonapps.familyinex.constants.PushUtils;
import com.artfonapps.familyinex.db.models.Expense;
import com.artfonapps.familyinex.db.models.ExpenseType;
import com.artfonapps.familyinex.db.models.Income;
import com.artfonapps.familyinex.db.models.PayType;
import com.artfonapps.familyinex.db.models.PlannedExpenses;
import com.artfonapps.familyinex.db.models.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by paperrose on 19.07.2016.
 */
public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";
    public static final String REFRESH_COMMAND = "refresh";
    private Handler handler;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }

    private void insertIncome(final Map<String, String> messageBody) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(messageBody.get(PushUtils.DESC));
            User user = new Select()
                    .from(User.class)
                    .where(DbFields.MAIL + Comparables.E, obj.getJSONObject(PushUtils.USER).getString(PushUtils.MAIL))
                    .executeSingle();
            long time = Long.parseLong(obj.getString(PushUtils.TIME));
            long remoteId = time;
            float sum = Float.parseFloat(obj.getString(PushUtils.SUM));
            PayType payType = obj.getJSONObject(PushUtils.TYPE).getLong(PushUtils.REMOTE_ID) == 1 ?
                    PayType.getCardType() : PayType.getCashType();
            Income income = new Income(remoteId, user, sum, time, payType);
            income.save();
            Intent intent = new Intent(REFRESH_COMMAND);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertPlannedExpense(final Map<String, String> messageBody) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(messageBody.get(PushUtils.DESC));
            long time = Long.parseLong(obj.getString(PushUtils.TIME));
            long remoteId = time;
            float sum = Float.parseFloat(obj.getString(PushUtils.SUM));
            PlannedExpenses plannedExpense = new PlannedExpenses();
            plannedExpense.expenseType = Long.parseLong(obj.getString(PushUtils.EXPENSE_TYPE));;
            plannedExpense.sum = sum;
            plannedExpense.time = time;
            plannedExpense.remoteId = remoteId;
            plannedExpense.save();
            Intent intent = new Intent(REFRESH_COMMAND);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void insertExpense(final Map<String, String> messageBody) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(messageBody.get(PushUtils.DESC));
            String comment = obj.getString(PushUtils.COMMENT);
            User user = new Select()
                    .from(User.class)
                    .where(DbFields.MAIL + Comparables.E, obj.getJSONObject(PushUtils.USER).getString(PushUtils.MAIL))
                    .executeSingle();
            long expenseType = Long.parseLong(obj.getString(PushUtils.EXPENSE_TYPE));
            long time = Long.parseLong(obj.getString(PushUtils.TIME));
            long remoteId = time;
            float sum = Float.parseFloat(obj.getString(PushUtils.SUM));
            PayType payType = obj.getJSONObject(PushUtils.PAY_TYPE).getLong(PushUtils.REMOTE_ID) == 1 ?
                    PayType.getCardType() : PayType.getCashType();
            Expense expense = new Expense(remoteId, sum, expenseType, payType, user, time, comment);
            expense.save();
            Intent intent = new Intent(REFRESH_COMMAND);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeObject(final Map<String, String> messageBody, final Class c) {
        handler.post(new Runnable() {
            public void run() {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(messageBody.get(PushUtils.DESC));
                    long remoteId = Long.parseLong(obj.getString(PushUtils.REMOTE_ID));
                    new Delete().from(c).where(DbFields.REMOTE_ID + Comparables.E, remoteId).execute();
                    Intent intent = new Intent(REFRESH_COMMAND);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage.getData());
    }

    private void sendNotification(final Map<String, String> messageBody) {
        switch (messageBody.get(PushUtils.TYPE)) {
            case "-1": //remove
                switch (messageBody.get(PushUtils.TITLE)) {
                    case DbTables.EXPENSE:
                        removeObject(messageBody, Expense.class);
                        break;
                    case DbTables.INCOME:
                        removeObject(messageBody, Income.class);
                        break;

                    default:
                        break;
                }
                break;
            case "0": //insert
                switch (messageBody.get(PushUtils.TITLE)) {
                    case DbTables.EXPENSE:
                        handler.post(new Runnable() {
                            public void run() {
                                insertExpense(messageBody);
                            }
                        });

                        break;
                    case DbTables.INCOME:
                        handler.post(new Runnable() {
                            public void run() {
                                insertIncome(messageBody);
                            }
                        });
                    case DbTables.PLANNED_EXPENSES:
                        handler.post(new Runnable() {
                            public void run() {
                                insertPlannedExpense(messageBody);
                            }
                        });
                        break;
                    default:
                        break;
                }
                break;
            case "1": //update
                switch (messageBody.get(PushUtils.TITLE)) {
                    case DbTables.EXPENSE:
                        handler.post(new Runnable() {
                            public void run() {

                            }
                        });

                        break;
                    default:
                        break;
                }
                break;
        }

    }

}
