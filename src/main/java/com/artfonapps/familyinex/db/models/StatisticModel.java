package com.artfonapps.familyinex.db.models;

import com.activeandroid.annotation.Column;
import com.artfonapps.familyinex.constants.DbFields;

/**
 * Created by paperrose on 07.07.2016.
 */
public class StatisticModel extends JsonModel {
    @Column(name = DbFields.EXPENSE_TYPE)
    public long expenseType;
    @Column(name = DbFields.SUM)
    public float sum;

    public StatisticModel() {
    }

    public StatisticModel(long expenseType, float sum) {
        super();
        this.expenseType = expenseType;
        this.sum = sum;
    }

    public long getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(long expenseType) {
        this.expenseType = expenseType;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }
}
