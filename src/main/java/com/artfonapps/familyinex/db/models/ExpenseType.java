package com.artfonapps.familyinex.db.models;


import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.artfonapps.familyinex.constants.Comparables;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.constants.DbTables;
import com.artfonapps.familyinex.constants.Orders;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by paperrose on 05.07.2016.
 */
@Table(name = DbTables.EXPENSE_TYPE)
public class ExpenseType extends JsonModel{
    @Column(name = DbFields.TITLE, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String title;
    @Column(name = DbFields.ICON)
    public String icon;
    @Column(name = DbFields.IMAGE)
    public String image;

    public ExpenseType(long remoteId, String title, String icon, String image) {
        this.remoteId = remoteId;
        this.title = title;
        this.icon = icon;
        this.image = image;
    }


    public ExpenseType(String title, String icon, String image) {
        //this.remoteId =
        this.title = title;
        this.icon = icon;
        this.image = image;
    }

    public ExpenseType() {}

    public List<Expense> getExpenses(int month) throws ParseException {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MONTH, month);
        DateFormat dateFormat = new SimpleDateFormat(Comparables.DATE_FORMAT);
        Date dateMin = dateFormat.parse("01/"
                + (month)
                + "/" + now.get(Calendar.YEAR));

        Date dateMax = dateFormat.parse(now.getActualMaximum(Calendar.DAY_OF_MONTH)
                + "/" + (now.get(Calendar.MONTH) + 1)
                + "/" + now.get(Calendar.YEAR));
        long time = dateMin.getTime();
        return (new Select()
                .from(Expense.class)
                .where(DbFields.TIME + Comparables.GE, time )
                .and( DbFields.EXPENSE_TYPE + Comparables.E, remoteId))
                .execute();
    }

    public List<Expense> getExpenses() throws ParseException {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        DateFormat dateFormat = new SimpleDateFormat(Comparables.DATE_FORMAT);
        Date dateMin = dateFormat.parse("01/"
                + (now.get(Calendar.MONTH))
                + "/" + now.get(Calendar.YEAR));
        long time = dateMin.getTime();
        return (new Select()
                .from(Expense.class)
                .where(DbFields.TIME + Comparables.GE, time )
                .and( DbFields.EXPENSE_TYPE + Comparables.E, remoteId))
                .execute();
    }

    public static List<Expense> getExpenses(String title) throws ParseException {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        DateFormat dateFormat = new SimpleDateFormat(Comparables.DATE_FORMAT);
        Date dateMin = dateFormat.parse("01/"
                + (now.get(Calendar.MONTH))
                + "/" + now.get(Calendar.YEAR));
        long time = dateMin.getTime();
        ExpenseType curExpenseType = (new Select().from(ExpenseType.class)
                .where(DbFields.TITLE + Comparables.E, title))
                .executeSingle();
        List<Expense> expenses = (new Select()
                .from(Expense.class)
                .where(DbFields.TIME + Comparables.GE, time )
                .and(DbFields.EXPENSE_TYPE + Comparables.E, curExpenseType.remoteId))
                .execute();
        return expenses != null ? expenses : new ArrayList<Expense>();
    }

    public PlannedExpenses getPlannedExpense() throws ParseException {
        return new Select()
                .from(PlannedExpenses.class)
                .where(DbFields.EXPENSE_TYPE + Comparables.E, this.remoteId)
                .orderBy(DbFields.TIME + Orders.DESC)
                .executeSingle();
    }

    public float getPlannedSum() throws ParseException {
        PlannedExpenses plannedExpense = getPlannedExpense();
        return plannedExpense != null ? plannedExpense.sum : 0;
    }

    public float getSum(int month) throws ParseException {
        List<Expense> expenses = getExpenses(month);
        float sum = .0f;
        for (Expense expense : expenses) {
            sum += expense.sum;
        }
        return sum;
    }

    public float getSum() throws ParseException {
        List<Expense> expenses = getExpenses();
        float sum = .0f;
        for (Expense expense : expenses) {
            sum += expense.sum;
        }
        return sum;
    }

    public static ExpenseType getObjectByName(String name) {
        ExpenseType expenseType = new Select()
                .from(ExpenseType.class)
                .where(DbFields.TITLE + Comparables.E, name)
                .executeSingle();
        return expenseType;
    }

    public static long getByName(String name) {
        ExpenseType expenseType = new Select()
                .from(ExpenseType.class)
                .where(DbFields.TITLE + Comparables.E, name)
                .executeSingle();
        return expenseType.remoteId;
    }

}
