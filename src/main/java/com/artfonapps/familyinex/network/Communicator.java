package com.artfonapps.familyinex.network;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.activeandroid.query.Select;
import com.activeandroid.util.Log;
import com.artfonapps.familyinex.ApplicationParameters;
import com.artfonapps.familyinex.constants.Comparables;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.constants.PushUtils;
import com.artfonapps.familyinex.constants.RequestHeaders;
import com.artfonapps.familyinex.db.models.JsonModel;
import com.artfonapps.familyinex.db.models.User;
import com.artfonapps.familyinex.db.utils.BusProvider;
import com.artfonapps.familyinex.db.utils.UpdateEvent;
import com.artfonapps.familyinex.db.utils.InsertEvent;
import com.artfonapps.familyinex.db.utils.RemoveEvent;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by paperrose on 07.07.2016.
 * отвечает за отсылку и повторное отправление пушей
 * TODO дописать повторную отправку
 */
public class Communicator {

    PendingIntent pendingIntent;
    public static final String KEY = "key";
    public static final String DATA = "data";
    public static final String TO = "to";

    public Communicator(Context context) {
        this.context = context;
       // Intent alarmIntent = new Intent(context, GetDeviceIdService.class);
       // pendingIntent = PendingIntent.getService(context, 0, alarmIntent, 0);
       // manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ApplicationParameters.timerInterval, pendingIntent);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void finalize() {
        BusProvider.getInstance().unregister(this);
    }

    public static Context context;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    public static void sendPush(JsonModel jsonModel, int type) throws JSONException {
        List<User> users = (new Select()
                .from(User.class)
                .where(DbFields.MAIL + Comparables.NE, ApplicationParameters.currentUser.mail)
                .execute());
        for (User user: users)
            sendPush(jsonModel, user.deviceId, type);
    }

    public static void sendPush(JsonModel jsonModel, String id, int type) throws JSONException {
        //type - 0: insert, 1: update, -1: delete
        OkHttpClient client = new OkHttpClient();
        JSONObject obj = new JSONObject();
        Calendar now = Calendar.getInstance();
        JSONObject object = new JSONObject();

        obj.put(PushUtils.TITLE, jsonModel.getClass().getSimpleName());
        obj.put(PushUtils.ID, jsonModel.getRemoteId());
        obj.put(PushUtils.TYPE, Integer.toString(type));
        obj.put(PushUtils.DESC, jsonModel.getJSON());
        obj.put(PushUtils.TIME, now.getTimeInMillis());
        object.put(DATA, obj);
        object.put(TO, id);
        String str = object.toString();


        RequestBody body = RequestBody.create(JSON, str);
        Request request = new Request.Builder()
                .addHeader(RequestHeaders.HEADER_AUTHORIZATION, RequestHeaders.AUTHORIZATION + ApplicationParameters.GOOGLE_API_KEY)
                .addHeader(RequestHeaders.HEADER_CONTENT_LENGTH, Integer.toString(str.length()))
                .addHeader(RequestHeaders.HEADER_CONTENT_TYPE, RequestHeaders.CONTENT_TYPE)
                .addHeader(RequestHeaders.HEADER_CONNECTION, RequestHeaders.CONNECTION)
                .url(ApplicationParameters.GOOGLE_API_SERVER + ApplicationParameters.GOOGLE_API_URI)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("failure");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.e("response");
            }
        });
    }

    public static void saveToResend(JSONArray requests) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY, requests.toString());
        editor.commit();
    }

    public static void reSendPush() throws JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        JSONArray requests = new JSONArray(preferences.getString(KEY, "[]"));
        if (requests.length() == 0) return;
        final JSONObject object = requests.getJSONObject(0);
        final JSONArray newRequests = new JSONArray();
        for (int i = 1; i < requests.length(); i++)
            newRequests.put(requests.getJSONObject(i));
        final OkHttpClient client = new OkHttpClient();
        String str = object.toString();


        RequestBody body = RequestBody.create(JSON, str);
        Request request = new Request.Builder()
                .addHeader(RequestHeaders.HEADER_AUTHORIZATION, RequestHeaders.AUTHORIZATION + ApplicationParameters.GOOGLE_API_KEY)
                .addHeader(RequestHeaders.HEADER_CONTENT_LENGTH, Integer.toString(str.length()))
                .addHeader(RequestHeaders.HEADER_CONTENT_TYPE, RequestHeaders.CONTENT_TYPE)
                .addHeader(RequestHeaders.HEADER_CONNECTION, RequestHeaders.CONNECTION)
                .url(ApplicationParameters.GOOGLE_API_SERVER + ApplicationParameters.GOOGLE_API_URI)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                newRequests.put(object);
                saveToResend(newRequests);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() != 200) {
                    newRequests.put(object);
                    saveToResend(newRequests);
                } else {
                    saveToResend(newRequests);
                }
            }
        });
    }

    @Subscribe
    public void onUpdateEvent(UpdateEvent updateEvent){
        try {
            sendPush(updateEvent.getModelJson(), 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onInsertEvent(InsertEvent changeEvent){
        try {
            sendPush(changeEvent.getModelJson(), 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onRemoveEvent(RemoveEvent changeEvent){
        try {
            sendPush(changeEvent.getModelJson(), -1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
