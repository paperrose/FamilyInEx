package com.artfonapps.familyinex.db.utils;

import com.artfonapps.familyinex.db.models.JsonModel;
import com.artfonapps.familyinex.db.models.StatisticModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by paperrose on 07.07.2016.
 */
public class UpdateEvent { //для пуша

    public UpdateEvent(JsonModel model) {
        this.modelJson = model;
    }

    public JsonModel getModelJson() {
        return modelJson;
    }

    public void setModelJson(JsonModel modelJson) {
        this.modelJson = modelJson;
    }
    private JsonModel modelJson;

}
