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
import com.artfonapps.familyinex.views.utils.SortItemClass;

/**
 * Created by paperrose on 13.07.2016.
 */
public class SortTypesAdapter extends ArrayAdapter<SortItemClass> {
    MainActivity context;

    private String [] types = {"По дате", "По алфавиту", "По сумме"};
    private String [] orders = {"(по убыванию)", "(по возрастанию)"};

    public SortTypesAdapter(Context context, int resource) {
        super(context, resource);
        this.context = (MainActivity)context;

    }

    @Override
    public int getCount() {
        return SortItemClass.getSortTypes().size();
    }

    @Override
    public SortItemClass getItem(int i) {
        return SortItemClass.getSortTypes().get(i);
    }

    public int getItemIndex(SortItemClass item) {
        return SortItemClass.getSortTypes().indexOf(item);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        if (view == null) {
            view = inflater.inflate(R.layout.sort_type_item, viewGroup, false);
        }
        SortItemClass p = SortItemClass.getSortTypes().get(i);
        ((TextView) view.findViewById(R.id.sort_type)).setText(types[p.getType()]);
        ((TextView) view.findViewById(R.id.sort_order)).setText(orders[p.getOrder()]);

        ((ImageView) view.findViewById(R.id.sort_icon)).setImageResource(p.getGreenIcon());
        if (SortItemClass.getCurNum() == i) {
            view.setBackgroundColor(context.getResources().getColor(R.color.colorDisabled));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        }
        return view;
    }

}
