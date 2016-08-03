package com.artfonapps.familyinex.views.utils;

import com.artfonapps.familyinex.db.models.PayType;
import com.artfonapps.familyinex.db.models.User;

/**
 * Created by paperrose on 28.07.2016.
 */
public class ExpenseToIncomeItem {
    private User user;

    public User getUser() {
        return user;
    }

    public ExpenseToIncomeItem setUser(User user) {
        this.user = user;
        return this;
    }

    public PayType getPayType() {
        return payType;
    }

    public ExpenseToIncomeItem setPayType(PayType payType) {
        this.payType = payType;
        return this;
    }

    public ExpenseToIncomeItem(User user, PayType payType) {

        this.user = user;
        this.payType = payType;
    }

    private PayType payType;

}
