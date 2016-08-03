package com.artfonapps.familyinex.db.utils;

import com.artfonapps.familyinex.db.models.JsonModel;

/**
 * Created by paperrose on 07.07.2016.
 */
public class InsertEvent { //для пуша

    public InsertEvent(JsonModel model) {
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
