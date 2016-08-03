package com.artfonapps.familyinex.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.artfonapps.familyinex.db.models.Income;
import com.artfonapps.familyinex.views.widgets.swipelistitem.SwipeExpenseLayout;
import com.artfonapps.familyinex.views.widgets.swipelistitem.SwipeIncomeLayout;

import java.util.ArrayList;

/**
 * Created by paperrose on 12.07.2016.
 */
public class SwipeIncomeAdapter extends ArrayAdapter<Income> {

    Context context;
    private ArrayList<Income> items;

    public SwipeIncomeAdapter(Context context, int resource, ArrayList<Income> objects) {
        super(context, resource, objects);
        this.items = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void refresh(ArrayList<Income> items)
    {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public Income getItem(int i) {
        return items.get(i);
    }


    public int getItemIndex(Income item) {
        return items.indexOf(item);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SwipeIncomeLayout swp = (SwipeIncomeLayout) view;
        if (swp == null) {
            swp = new SwipeIncomeLayout(context);
        }
        swp.setState(SwipeIncomeLayout.State.HIDDEN);
        swp.setIncome(getItem(i));
        swp.setSwipe();
        view = swp;
        return view;
    }

}
