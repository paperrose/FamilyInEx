package com.artfonapps.familyinex.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.artfonapps.familyinex.db.models.Expense;
import com.artfonapps.familyinex.views.widgets.swipelistitem.SwipeExpenseLayout;

import java.util.ArrayList;

/**
 * Created by paperrose on 12.07.2016.
 */
public class SwipeExpenseAdapter extends ArrayAdapter<Expense> {

    Context context;
    private ArrayList<Expense> items;

    public SwipeExpenseAdapter(Context context, int resource, ArrayList<Expense> objects) {
        super(context, resource, objects);
        this.items = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void refresh(ArrayList<Expense> items)
    {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public Expense getItem(int i) {
        return items.get(i);
    }


    public int getItemIndex(Expense item) {
        return items.indexOf(item);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SwipeExpenseLayout swp = (SwipeExpenseLayout)view;
        if (swp == null) {
            swp = new SwipeExpenseLayout(context);
        }
        swp.setState(SwipeExpenseLayout.State.HIDDEN);
        swp.setExpense(getItem(i));
        swp.setSwipe();
        view = swp;
        return view;
    }

}
