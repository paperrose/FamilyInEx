package com.artfonapps.familyinex.views.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.artfonapps.familyinex.MainActivity;
import com.artfonapps.familyinex.R;
import com.artfonapps.familyinex.db.models.Income;
import com.artfonapps.familyinex.views.adapters.SortTypesAdapter;
import com.artfonapps.familyinex.views.adapters.SwipeIncomeAdapter;
import com.artfonapps.familyinex.views.utils.SortItemClass;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;

public class ListIncomesFragment extends BaseFragment implements View.OnTouchListener {



    public static final String INCOME = "INCOME";
    public static final String RUB = " р.";
    private static final String ENTER_ALERT = "Введите сумму дохода";
    private static final String INCOME_TEXT = "Доход: ";
    private static final String OK = "Ок";


    ArrayList<Income> incomes;
    SwipeIncomeAdapter swipeIncomeAdapter;
    SortTypesAdapter sortTypesAdapter;
    LinearLayout sortTypesLayout;
    ImageButton sortButton;
    ImageButton newButton;
    TextView fact;
    TextView plan;
    TextView balance;

    public ListIncomesFragment() {

    }

    public static ListIncomesFragment newInstance() {
        ListIncomesFragment fragment = new ListIncomesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        refreshIncomes();

    }

    private void refreshIncomes() {
        try {
            incomes = new ArrayList<>();
            incomes.addAll(Income.getLastIncomes());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void refreshSums() throws ParseException {
        float factSum = Income.getLastSum();

        float plannedSum =  Float.parseFloat(PreferenceManager
                .getDefaultSharedPreferences(getActivity()).getString(INCOME, "0"));
        float balanceSum = plannedSum - factSum;
        fact.setText(String.format("%.2f", factSum) + RUB);
        plan.setText(String.format("%.2f", plannedSum) + RUB);
        balance.setText(String.format("%.2f", balanceSum) + RUB);
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshIncomes();
        try {
            refreshSums();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        swipeIncomeAdapter.refresh(incomes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_incomes, container, false);
        ImageView background = (ImageView)rootView.findViewById(R.id.back);
        fact = (TextView)rootView.findViewById(R.id.fact);
        plan = (TextView)rootView.findViewById(R.id.planned);
        plan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                final EditText edittext = new EditText(getActivity());
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setMessage(ENTER_ALERT);
                alert.setTitle(INCOME_TEXT);
                alert.setView(edittext);
                alert.setPositiveButton(OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (edittext.getText().toString().isEmpty()) {
                            return;
                        }
                        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(INCOME, edittext.getText().toString());
                        editor.commit();
                        try {
                            refreshSums();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                alert.show();
                return false;
            }
        });
        balance = (TextView)rootView.findViewById(R.id.balance);
        background.setAlpha(0.25f);

        Picasso.with(getActivity()).load(R.drawable.income_back).fit().centerCrop().into(background);

        swipeIncomeAdapter = new SwipeIncomeAdapter(getActivity(),
                -1, incomes);
        ListView lv = (ListView)rootView.findViewById(R.id.incomes);
        lv.setAdapter(swipeIncomeAdapter);

        sortTypesAdapter = new SortTypesAdapter(getActivity(), -1);

        ListView lv2 = (ListView)rootView.findViewById(R.id.sortTypes);
        lv2.setAdapter(sortTypesAdapter);
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeSortTypesVisibility();
                SortItemClass.setCurNum(position);
                sortButton.setImageResource(SortItemClass.getSortTypes().get(position).getIcon());
                sortTypesAdapter.notifyDataSetChanged();
            }
        });
        swipeIncomeAdapter.notifyDataSetChanged();

        sortTypesLayout = (LinearLayout)rootView.findViewById(R.id.sortTypesLayout);
        sortTypesLayout.setVisibility(View.GONE);
        sortButton = (ImageButton)rootView.findViewById(R.id.sort);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSortTypesVisibility();
            }
        });
        newButton = (ImageButton)rootView.findViewById(R.id.insert);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).newNewIncomeFragment();
            }
        });
        return rootView;


    }


    private void changeSortTypesVisibility() {
        if (sortTypesLayout.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_out_down);
            sortTypesLayout.startAnimation(animation);
            sortTypesLayout.setVisibility(View.GONE);
        }
        else {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_in_down);
            sortTypesLayout.startAnimation(animation);
            sortTypesLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (sortTypesLayout.getVisibility() == View.GONE) return false;
        if (sortTypesLayout.getVisibility() == View.VISIBLE) {
            if (event.getY() > sortTypesLayout.getTop() && event.getY() < sortTypesLayout.getBottom() &&
                    event.getX() < sortTypesLayout.getRight() && event.getX() > sortTypesLayout.getLeft())
                return true;
        }
        changeSortTypesVisibility();
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}
