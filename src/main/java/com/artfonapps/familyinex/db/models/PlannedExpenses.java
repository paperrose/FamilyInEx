package com.artfonapps.familyinex.db.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.constants.DbTables;
import com.artfonapps.familyinex.constants.Orders;
import com.artfonapps.familyinex.network.Communicator;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by paperrose on 07.07.2016.
 */
@Table(name = DbTables.PLANNED_EXPENSES)
public class PlannedExpenses extends StatisticModel{
    @Column(name = DbFields.TIME)
    public long time;
    public PlannedExpenses(long remoteId, long expenseType, long time) {
        this.time = time;
        this.expenseType = expenseType;
        this.remoteId = remoteId;
    }

    public static List<PlannedExpenses> lastPlannedExpenses() {
        return new Select()
                .distinct()
                .from(PlannedExpenses.class)
                .orderBy(DbFields.TIME + Orders.DESC)
                .groupBy(DbFields.EXPENSE_TYPE)
                .execute();
    }

    public static float lastPlannedExpensesSum() {
        List<PlannedExpenses> plannedExpenses = lastPlannedExpenses();
        float sum = 0;
        for (PlannedExpenses plannedExpense : plannedExpenses) {
            sum += plannedExpense.sum;
        }
        return sum;
    }

    public PlannedExpenses() {}

}
