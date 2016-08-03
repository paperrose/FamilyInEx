package com.artfonapps.familyinex.views.utils;

import com.artfonapps.familyinex.R;

import java.util.ArrayList;

/**
 * Created by paperrose on 13.07.2016.
 */
public class SortItemClass {
    private int icon;

    public int getIcon() {
        return icon;
    }

    public int getGreenIcon() {
        return greenIcon;
    }

    public int getType() {
        return type;
    }

    public int getOrder() {
        return order;
    }

    private int greenIcon;
    private int type;
    private int order;
    private static int curNum;
    private static ArrayList<SortItemClass> sortTypes;

    public static void setCurNum(int i) {
        curNum = i;
    }

    public static int getCurNum() {
        return curNum;
    }

    public static ArrayList<SortItemClass> getSortTypes() {
        if (sortTypes != null) return sortTypes;
        sortTypes = new ArrayList<>();
        sortTypes.add(new SortItemClass(
                R.drawable.sort_down_date,
                R.drawable.sort_down_date_green,
                0,
                0
        ));
        sortTypes.add(new SortItemClass(
                R.drawable.sort_up_date,
                R.drawable.sort_up_date_green,
                0,
                1
        ));
        sortTypes.add(new SortItemClass(
                R.drawable.sort_down_alphabet,
                R.drawable.sort_down_alphabet_green,
                1,
                0
        ));
        sortTypes.add(new SortItemClass(
                R.drawable.sort_up_alphabet,
                R.drawable.sort_up_alphabet_green,
                1,
                1
        ));
        sortTypes.add(new SortItemClass(
                R.drawable.sort_down_sum,
                R.drawable.sort_down_sum_green,
                2,
                0
        ));
        sortTypes.add(new SortItemClass(
                R.drawable.sort_up_sum,
                R.drawable.sort_up_sum_green,
                2,
                1
        ));
        curNum = 0;
        return sortTypes;
    }

    private SortItemClass(int icon, int greenIcon, int type, int order) {
        this.icon = icon;
        this.greenIcon = greenIcon;
        this.type = type;
        this.order = order;
    }
}
