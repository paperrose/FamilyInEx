package com.artfonapps.familyinex.views.widgets.swipelistitem;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.artfonapps.familyinex.R;
import com.artfonapps.familyinex.db.models.Expense;
import com.artfonapps.familyinex.db.models.Income;
import com.artfonapps.familyinex.views.adapters.SwipeVPAdapter;
import com.artfonapps.familyinex.views.fragments.ListIncomesFragment;
import com.artfonapps.familyinex.views.utils.OnSwipeTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by paperrose on 11.07.2016.
 */
public class SwipeIncomeLayout extends RelativeLayout {

    ImageButton remove;
    ImageButton edit;
    private Context context;
    LinearLayout hideButtons;
    LayoutInflater inflater;

    TextView comment;
    TextView time;
    TextView sum;
    TextView user;
    ImageView payType;
    ViewPager viewPager;
    List<View> pages;

    public SwipeIncomeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate(context, R.layout.income_list_item, this);
        findItems();
        setSwipe();
    }


    public SwipeIncomeLayout(Context context) {
        super(context);
        this.context = context;
        inflate(context, R.layout.income_list_item, this);
        findItems();
        setSwipe();
    }

    public SwipeIncomeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate(context, R.layout.income_list_item, this);
        findItems();
        setSwipe();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
      //  setButtonsState(state);
    }

    private State state;

    public enum State {
        VISIBLE,
        HIDDEN
    }

    public Income getIncome() {
        return income;
    }

    public void setIncome(Income income) {
        this.income = income;
        setItems();
    }

    private Income income;



    public void findItems() {
        pages = new ArrayList<>();
        viewPager = (ViewPager)findViewById(R.id.hideButtons);

        FrameLayout layout = new FrameLayout(context);
        LayoutParams layoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParam);
        inflater = LayoutInflater.from(context);
        LinearLayout buttons = (LinearLayout) inflater.inflate(R.layout.expense_list_item_buttons_layout, null, false);

        pages.add(layout);
        pages.add(buttons);


        remove = (ImageButton)buttons.findViewById(R.id.remove);
        edit = (ImageButton)buttons.findViewById(R.id.edit);

        time = (TextView)findViewById(R.id.time);
        sum = (TextView)findViewById(R.id.sum);
        user = (TextView)findViewById(R.id.user);
        payType = (ImageView)findViewById(R.id.payType);

        remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Long id = income.remove();
                if (id == -1) {
                    //TODO сообщение о запрете удаления строки;
                } else {

                }
            }
        });

       // hideButtons = (LinearLayout)findViewById(R.id.hideButtons);

    }



    public void setButtonsState(State state) {
        if (state == State.HIDDEN) {
            hideButtons.setVisibility(GONE);
        } else
            hideButtons.setVisibility(VISIBLE);
    }



    public void setSwipe() {
        if (income == null) return;
        this.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {
                if (state == State.VISIBLE) return;
                setState(State.VISIBLE);
                final Animation swipeLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_right);
                hideButtons.startAnimation(swipeLeftAnimation);

            }

            @Override
            public void onSwipeRight() {
                if (state == State.HIDDEN) return;
                final Animation swipeRightAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_right);
                hideButtons.startAnimation(swipeRightAnimation);
                setState(State.HIDDEN);
            }
        });
    }

    private void setItems() {

        Date date = new Date(income.time);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
        time.setText(df.format(date));
        sum.setText(String.format("%.2f", income.sum) + ListIncomesFragment.RUB);
        user.setText(income.user.name);
        payType.setImageDrawable(context.getResources().getDrawable(
                income.type.remoteId == 1 ? R.drawable.cash : R.drawable.card));
        viewPager.setAdapter(new SwipeVPAdapter(pages));
    }
}
