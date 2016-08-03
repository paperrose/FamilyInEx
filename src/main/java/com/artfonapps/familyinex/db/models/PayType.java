package com.artfonapps.familyinex.db.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.constants.DbTables;

/**
 * Created by paperrose on 06.07.2016.
 */
@Table(name = DbTables.PAY_TYPE)
public class PayType extends JsonModel {
    public PayType(long remoteId, String title, int icon, int image) {
        this.remoteId = remoteId;
        this.title = title;
        this.icon = icon;
        this.image = image;
    }

    public PayType() {}

    @Column(name = DbFields.TITLE)
    public String title;
    @Column(name = DbFields.ICON)
    public int icon;
    @Column(name = DbFields.IMAGE)
    public int image;

    public static PayType getCardType() {
        return (new Select().from(PayType.class).where("RemoteId = ?", 1).executeSingle());
    }

    public static PayType getCashType() {
        return (new Select().from(PayType.class).where("RemoteId = ?", 2).executeSingle());
    }
}
