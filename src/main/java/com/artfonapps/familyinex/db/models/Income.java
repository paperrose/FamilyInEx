package com.artfonapps.familyinex.db.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.artfonapps.familyinex.ApplicationParameters;
import com.artfonapps.familyinex.constants.Comparables;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.constants.DbTables;
import com.artfonapps.familyinex.db.utils.BusProvider;
import com.artfonapps.familyinex.db.utils.RemoveEvent;
import com.artfonapps.familyinex.db.utils.UpdateEvent;
import com.artfonapps.familyinex.network.Communicator;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.squareup.otto.Produce;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by paperrose on 23.06.2016.
 */
@Table(name = DbTables.INCOME)
public class Income extends JsonModel {

    public Income(long remoteId, User user, float sum, PayType type) {
        this.remoteId = remoteId;
        this.user = user;
        this.sum = sum;
        this.type = type;
        Calendar now = Calendar.getInstance();
        this.time = now.getTimeInMillis();
    }

    public Income(long remoteId, User user, float sum, long time, PayType type) {
        this.remoteId = remoteId;
        this.user = user;
        this.sum = sum;
        this.time = time;
        this.type = type;
    }

    @Column(name = DbFields.USER_ID)
    public User user; //чьи поступления
    @Column(name = DbFields.SUM)
    public float sum; //сумма поступлений
    @Column(name = DbFields.EXPENSE_REMOTE_ID)
    public Long expenseRemoteId; //сумма поступлений
    @Column(name = DbFields.TIME)
    public long time;
    @Column(name = DbFields.PAY_TYPE)
    public PayType type;

    public Income(float sum, PayType type, User user){
        super();
        this.sum = sum;
        this.type = type;
        this.user = user;
        this.time = System.currentTimeMillis();
        this.remoteId = this.time;
        this.expenseRemoteId = 1L;
    }

    public Income() {}

    public Income(float sum, PayType type, User user, long time){
        super();
        this.sum = sum;
        this.type = type;
        this.user = user;
        this.time = time;
        this.remoteId = getId();
    }

    public static List<Expense> getAllIncomesTo(Date dateMax) throws ParseException {
        long time = dateMax.getTime();
        return (new Select().from(Expense.class)
                .where(DbFields.TIME + Comparables.LE, time )).execute();
    }

    public static List<Income> getLastIncomes() throws ParseException {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        DateFormat dateFormat = new SimpleDateFormat(Comparables.DATE_FORMAT);
        Date dateMin = dateFormat.parse("01/"
                + (now.get(Calendar.MONTH))
                + "/" + now.get(Calendar.YEAR));
        long time = dateMin.getTime();
        return (new Select().from(Income.class)
                .where(DbFields.TIME + Comparables.GE, time )).execute();
    }

    public static float getLastSum() throws ParseException {
        float resultSum = 0;
        List<Income> incomes = getLastIncomes();
        for (Income income : incomes) {
            resultSum += income.sum;
        }
        return resultSum;
    }

    public static float getLastSum(List<Income> incomes) throws ParseException {
        float resultSum = 0;
        for (Income income : incomes) {
            resultSum += income.sum;
        }
        return resultSum;
    }

    @Produce
    public RemoveEvent produceRemoveEvent(JsonModel jsonModel) {
        return new RemoveEvent(jsonModel);
    }

    @Produce
    public UpdateEvent produceUpdateEvent(JsonModel jsonModel) {
        return new UpdateEvent(jsonModel);
    }

    public Long remove() {
        if (this.user.mail.equals(ApplicationParameters.currentUser.mail)) {
            try {
                BusProvider.getInstance().post(produceRemoveEvent(this));
                Communicator.sendPush(this, -1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.delete();

            return this.remoteId;
        }
        else
            return -1L;
    }

}
