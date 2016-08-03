package com.artfonapps.familyinex.db.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
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
@Table(name = DbTables.EXPENSE)
public class Expense extends StatisticModel{

    public Expense(long remoteId, float sum, long expenseType, PayType payType, User user, String comment) {
        this.remoteId = remoteId;
        this.sum = sum;
        this.expenseType = expenseType;
        this.user = user;
        this.payType = payType;
        this.comment = comment;
        this.time = System.currentTimeMillis();
    }

    public Expense() {}

    public Expense(long remoteId, float sum, long expenseType, PayType payType, User user, long time, String comment) {
        this.remoteId = remoteId;
        this.sum = sum;
        this.expenseType = expenseType;
        this.payType = payType;
        this.comment = comment;
        this.user = user;
        this.time = time;
    }

    @Column(name = DbFields.USER_ID)
    public User user;
    @Column(name = DbFields.TIME)
    public long time;
    @Column(name = DbFields.PAY_TYPE)
    public PayType payType;
    @Column(name = DbFields.COMMENT)
    public String comment;

    public Expense(float sum, long expenseType, PayType payType, User user, String comment){
        super();
        this.sum = sum;
        this.expenseType = expenseType;
        this.payType = payType;
        this.comment = comment;
        this.user = user;
        this.time = System.currentTimeMillis();
    }

    public Expense(float sum, long expenseType, PayType payType, User user, long time, String comment){
        super();
        this.sum = sum;
        this.expenseType = expenseType;
        this.payType = payType;
        this.comment = comment;
        this.user = user;
        this.time = time;
    }

    public Expense(float sum, long expenseType, PayType payType, User user, long time){
        super();
        this.sum = sum;
        this.expenseType = expenseType;
        this.payType = payType;
        this.user = user;
        this.time = time;
    }

    public Expense(String comment, long expenseType, PayType payType, float sum){
        super();
        this.comment = comment;
        this.expenseType = expenseType;
        this.payType = payType;
        this.user = ApplicationParameters.currentUser;
        this.time = System.currentTimeMillis();
        this.remoteId = this.time;
        this.sum = sum;

    }


    public static List<Expense> getAllExpenses() {
        return (new Select().from(Expense.class)).execute();
    }

    public static List<Expense> removeAllExpenses() {
        return new Delete().from(Expense.class).execute();
    }

    public static List<Expense> getLastExpenses() throws ParseException {
        Calendar now = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(Comparables.DATE_FORMAT);
        Date dateMin = dateFormat.parse("01/"
                + (now.get(Calendar.MONTH) + 1)
                + "/" + now.get(Calendar.YEAR));
        long time = dateMin.getTime();
        return (new Select()
                .from(Expense.class)
                .where(DbFields.TIME  + Comparables.GE, time ))
                .execute();
    }

    public static List<Expense> getExpensesBetweenDates(Date from, Date to) throws ParseException {
        long timeFrom = from.getTime();
        long timeTo = to.getTime()+24*3600;
        return (new Select().from(Expense.class)
                .where(DbFields.TIME  + Comparables.GE, timeFrom )
                .and(DbFields.TIME  + Comparables.L, timeTo)).execute();
    }

    public static List<Expense> getAllExpensesTo(Date dateMax) throws ParseException {
        long time = dateMax.getTime();
        return (new Select()
                .from(Expense.class)
                .where(DbFields.TIME  + Comparables.LE, time ))
                .execute();
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
