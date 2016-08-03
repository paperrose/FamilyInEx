package com.artfonapps.familyinex.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.activeandroid.query.Select;
import com.artfonapps.familyinex.ApplicationParameters;
import com.artfonapps.familyinex.constants.Comparables;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.db.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by paperrose on 08.07.2016.
 */
public class GetDeviceIdService extends FirebaseInstanceIdService {

    //private final String SENDER_ID = "665149531559";
    public static final String PROPERTY_REG_ID = "PROPERTY_REG_ID";
    public static final String PROPERTY_APP_VERSION = "PROPERTY_APP_VERSION";

    public int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }




    public String getDeviceId(Context context) {
        String regid = getRegistrationId(context);
        if (regid == null || regid.isEmpty()) {
            regid = refreshToken(context);
        }
        return regid;
    }

    private String refreshToken(Context context) {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        saveSharedPreferences(refreshedToken, context);
        User u = null;
        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            FirebaseConnector firebaseConnector = new FirebaseConnector(context);
            u = new Select().from(User.class).where(DbFields.MAIL + Comparables.E, firebaseUser.getEmail()).executeSingle();
            u.deviceId = refreshedToken;
            u.save();
            firebaseConnector.setUserWithoutUploading();
        } catch (Exception e) {

        }
        try {
            if (u != null)
                ApplicationParameters.currentUser = u;
        } catch (Exception e) {

        }
        return refreshedToken;
    }

    public void saveSharedPreferences(String token, Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, token);
        editor.commit();
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        refreshToken(getApplicationContext());
        //sendRegistrationToServer(refreshedToken);
        return;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


}
