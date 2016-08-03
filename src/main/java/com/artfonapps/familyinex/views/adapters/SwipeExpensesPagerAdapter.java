package com.artfonapps.familyinex.views.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.artfonapps.familyinex.MainActivity;
import com.artfonapps.familyinex.R;
import com.artfonapps.familyinex.db.models.ExpenseType;
import com.artfonapps.familyinex.db.models.PlannedExpenses;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paperrose on 25.07.2016.
 */
public class SwipeExpensesPagerAdapter extends PagerAdapter{
    List<ExpenseType> expenseTypes = null;
    List<PlannedExpenses> plannedExpenseTypes = null;
    private Context context;
    private LayoutInflater layoutInflater;
    public SwipeExpensesPagerAdapter(List<ExpenseType> expenseTypes, Context context){
        this.expenseTypes = expenseTypes;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public Object instantiateItem (ViewGroup container, int position) {
        View view = this.layoutInflater.inflate(R.layout.content_expense, container, false);
        List<ItemHolder> holders = new ArrayList<>(6);
        ItemHolder holder1 = new ItemHolder();
        ItemHolder holder2 = new ItemHolder();
        ItemHolder holder3 = new ItemHolder();
        ItemHolder holder4 = new ItemHolder();
        ItemHolder holder5 = new ItemHolder();
        ItemHolder holder6 = new ItemHolder();

        holder1.expenseType = (ImageButton)view.findViewById(R.id.expenseType1);
        holder2.expenseType = (ImageButton)view.findViewById(R.id.expenseType2);
        holder3.expenseType = (ImageButton)view.findViewById(R.id.expenseType3);
        holder4.expenseType = (ImageButton)view.findViewById(R.id.expenseType4);
        holder5.expenseType = (ImageButton)view.findViewById(R.id.expenseType5);
        holder6.expenseType = (ImageButton)view.findViewById(R.id.expenseType6);
        holder1.expenseTypeProgress = (ProgressBar)view.findViewById(R.id.expenseTypeProgress1);
        holder2.expenseTypeProgress = (ProgressBar)view.findViewById(R.id.expenseTypeProgress2);
        holder3.expenseTypeProgress = (ProgressBar)view.findViewById(R.id.expenseTypeProgress3);
        holder4.expenseTypeProgress = (ProgressBar)view.findViewById(R.id.expenseTypeProgress4);
        holder5.expenseTypeProgress = (ProgressBar)view.findViewById(R.id.expenseTypeProgress5);
        holder6.expenseTypeProgress = (ProgressBar)view.findViewById(R.id.expenseTypeProgress6);

        holders.add(holder1);
        holders.add(holder2);
        holders.add(holder3);
        holders.add(holder4);
        holders.add(holder5);
        holders.add(holder6);
        for (int i = 0, j = 6*position; i < 6 && j < expenseTypes.size(); i++, j++) {
            Picasso.with(context).load(expenseTypes.get(j).icon).fit().into(holders.get(i).expenseType);
            /*try {
                holders.get(i).expenseTypeProgress
                        .setProgress((int)(100*(expenseTypes.get(j).getSum()/plannedExpenseTypes.get(j).sum)));
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
            holders.get(i).expenseType.setTag(j);
            holders.get(i).expenseType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)context).newExpensesListFragment(expenseTypes.get((int)v.getTag()).title);
                }
            });
        }
        for (int i = expenseTypes.size();  i < 6*(position+1); i++) {
            holders.get(6 - 6*(position+1) + i).expenseTypeProgress.setVisibility(View.GONE);
            holders.get(6 - 6*(position+1) + i).expenseType.setVisibility(View.INVISIBLE);
        }
        container.addView(view);
        return view;
    }

    public class ItemHolder {
        ImageButton expenseType;
        ProgressBar expenseTypeProgress;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount(){
        return (int)Math.ceil(expenseTypes.size()/6.0);
    }

    @Override
    public boolean isViewFromObject(View view, Object object){
        return view.equals(object);
    }


}
