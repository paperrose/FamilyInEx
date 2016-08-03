package com.artfonapps.familyinex.db.models;

import android.database.sqlite.SQLiteConstraintException;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.db.utils.BusProvider;
import com.artfonapps.familyinex.db.utils.InsertEvent;
import com.artfonapps.familyinex.db.utils.RemoveEvent;
import com.artfonapps.familyinex.network.Communicator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Produce;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by paperrose on 06.07.2016.
 */
public class JsonModel extends Model {
    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
    }

    public JsonModel() {

    }

    public JsonModel(long remoteId) {

        this.remoteId = remoteId;
    }

    @Column(name = DbFields.REMOTE_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long remoteId;

    public JSONObject getJSON() throws JSONException {
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(this);
        return new JSONObject(json);
    }

    public void setJSON(String jsonStr, String className ) throws JSONException {

    }



    @Override
    public Long save() {
        try {
            return super.save();
        } catch (SQLiteConstraintException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public final Long updateObject() {
        Long id = save();
        try {
            Communicator.sendPush(this, 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Produce
    public InsertEvent produceInsertEvent(JsonModel jsonModel) {
        return new InsertEvent(jsonModel);
    }

    public final Long saveObject() {
        Long id = save();
        BusProvider.getInstance().post(produceInsertEvent(this));
        return id;
    }

    public static <T extends Model> List<T> getUnique(String[] fields, Class<T> type) {
        return new Select(fields).distinct().from(type).execute();
    }
}
