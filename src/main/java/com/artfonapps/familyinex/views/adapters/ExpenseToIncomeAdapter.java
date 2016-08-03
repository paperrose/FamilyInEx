package com.artfonapps.familyinex.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.artfonapps.familyinex.MainActivity;
import com.artfonapps.familyinex.R;
import com.artfonapps.familyinex.views.utils.ExpenseToIncomeItem;
import com.artfonapps.familyinex.views.utils.SortItemClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paperrose on 28.07.2016.
 */
public class ExpenseToIncomeAdapter extends ArrayAdapter<ExpenseToIncomeItem> {
    private Context context;
    private List<ExpenseToIncomeItem> objects;
    private LayoutInflater inflater;
    public ExpenseToIncomeAdapter(Context context,List<ExpenseToIncomeItem> objects) {
        super(context, R.layout.expense_to_income_item, objects);
        this.objects = objects;
        this.context = context;
        inflater = ((MainActivity)context).getLayoutInflater();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public ExpenseToIncomeItem getItem(int i) {
        return objects.get(i);
    }

    public int getItemIndex(ExpenseToIncomeItem item) {
        return objects.indexOf(item);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate( R.layout.expense_to_income_item, viewGroup, false);
        }

        TextView textView = (TextView)view;
        ExpenseToIncomeItem expenseToIncomeItem = objects.get(i);
        textView.setText(expenseToIncomeItem.getUser().name
                + " - " + expenseToIncomeItem.getPayType().title);
        return view;
    }
}
