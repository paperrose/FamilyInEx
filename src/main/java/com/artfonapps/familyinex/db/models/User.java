package com.artfonapps.familyinex.db.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.constants.DbTables;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by paperrose on 23.06.2016.
 */
@Table(name = DbTables.USER)
public class User extends JsonModel{

    public User(long remoteId, String name) {
        super(remoteId);
        this.name = name;
        this.deviceId = "";
    }

    public User() {}

    public User(long remoteId, String name, String deviceId) {
        super(remoteId);
        this.name = name;
        this.deviceId = deviceId;
    }


    @Column(name = DbFields.NAME)
    public String name;
    @Column(name = DbFields.DEVICE_ID)
    public String deviceId;
    @Column(name = DbFields.MAIL, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String mail;
    @Column(name = DbFields.UID)
    public String uid;

    public User(long remoteId, String name, String deviceId, String mail, String uid) {
        super(remoteId);
        this.name = name;
        this.deviceId = deviceId;
        this.mail = mail;
        this.uid = uid;
    }

    public User(String name, String deviceId, String mail) {
        super();
        this.remoteId = System.currentTimeMillis();
        this.name = name;
        this.deviceId = deviceId;
        this.mail = mail;
    }

    public User(long remoteId, String name, String deviceId, String mail) {
        super(remoteId);
        this.name = name;
        this.deviceId = deviceId;
        this.mail = mail;
    }



    public User(String name) {
        this.name = name;
    }

    public Map<String, Object> getDataForFirebase() throws JSONException {
        HashMap<String, Object> result = new HashMap<>();
        result.put(DbFields.NAME, name);
        result.put(DbFields.DEVICE_ID, deviceId);
        result.put(DbFields.MAIL, mail);
        return result;
    }
}
