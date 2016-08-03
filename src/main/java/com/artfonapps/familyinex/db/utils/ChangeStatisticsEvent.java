package com.artfonapps.familyinex.db.utils;

import com.artfonapps.familyinex.db.models.StatisticModel;

/**
 * Created by paperrose on 07.07.2016.
 */
public class ChangeStatisticsEvent { //для изменения индикаторов
    public StatisticModel getStatisticModel() {
        return statisticModel;
    }

    public void setStatisticModel(StatisticModel statisticModel) {
        this.statisticModel = statisticModel;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ChangeStatisticsEvent(StatisticModel statisticModel, int type) {
        this.statisticModel = statisticModel;
        this.type = type;
    }

    private StatisticModel statisticModel;
    private int type; //planned, real
}
