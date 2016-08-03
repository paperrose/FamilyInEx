package com.artfonapps.familyinex.views.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
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
import com.artfonapps.familyinex.constants.Titles;
import com.artfonapps.familyinex.db.models.Expense;
import com.artfonapps.familyinex.db.models.ExpenseType;
import com.artfonapps.familyinex.db.models.PlannedExpenses;
import com.artfonapps.familyinex.views.adapters.SortTypesAdapter;
import com.artfonapps.familyinex.views.adapters.SwipeExpenseAdapter;
import com.artfonapps.familyinex.views.utils.SortItemClass;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;

public class ListExpensesFragment extends BaseFragment implements View.OnTouchListener {


    private static final String EXPENSE_TYPE = "expenseType";
    private static final String RUB = " р.";
    private static final String ENTER_ALERT = "Введите сумму расхода";
    private static final String EXPENSE = "Расход: ";
    private static final String OK = "Ок";

    ArrayList<Expense> expenses;

    public String getExpenseType() {
        return expenseType;
    }

    private String expenseType;

    SwipeExpenseAdapter swipeExpenseAdapter;
    SortTypesAdapter sortTypesAdapter;
    LinearLayout sortTypesLayout;
    ImageButton sortButton;
    ImageButton newButton;
    private ExpenseType expenseTypeObj;
    TextView fact;
    TextView plan;
    TextView balance;

    public ListExpensesFragment() {
        // Required empty public constructor
    }

    public static ListExpensesFragment newInstance(String expenseType) {
        ListExpensesFragment fragment = new ListExpensesFragment();
        Bundle args = new Bundle();
        args.putString(EXPENSE_TYPE, expenseType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            expenseType = getArguments().getString(EXPENSE_TYPE);
        }
        refreshExpenses();

    }

    private void refreshExpenses() {
        try {
            expenses = new ArrayList<>();
            expenses.addAll(ExpenseType.getExpenses(expenseType));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void refreshSums() throws ParseException {
        float factSum = expenseTypeObj.getSum();
        float plannedSum = expenseTypeObj.getPlannedSum();
        float balanceSum = plannedSum - factSum;
        fact.setText(String.format("%.2f", factSum) + RUB);
        plan.setText(String.format("%.2f", plannedSum) + RUB);
        balance.setText(String.format("%.2f", balanceSum) + RUB);
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshExpenses();
        if (expenseTypeObj != null) {
            try {
                refreshSums();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        swipeExpenseAdapter.refresh(expenses);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_expenses, container, false);
        expenseTypeObj = ExpenseType.getObjectByName(expenseType);
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
                alert.setTitle(EXPENSE + expenseTypeObj.title);
                alert.setView(edittext);
                alert.setPositiveButton(OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (edittext.getText().toString().isEmpty()) {
                            return;
                        }
                        PlannedExpenses plannedExpense = new PlannedExpenses();
                        plannedExpense.expenseType = expenseTypeObj.remoteId;
                        plannedExpense.sum = Float.parseFloat(edittext.getText().toString());
                        plannedExpense.time = System.currentTimeMillis();
                        plannedExpense.remoteId = plannedExpense.time;
                        plannedExpense.saveObject();
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
       // View background = ;
        background.setAlpha(0.25f);
        //refreshExpenses();

        Picasso.with(getActivity()).load(expenseTypeObj.image).into(background);

        swipeExpenseAdapter = new SwipeExpenseAdapter(getActivity(),
                -1, expenses);
        ListView lv = (ListView)rootView.findViewById(R.id.expenses);
        lv.setAdapter(swipeExpenseAdapter);

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
        swipeExpenseAdapter.notifyDataSetChanged();

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
                if (expenseType.equals(Titles.EXPENSES_TO_INCOMES))
                    ((MainActivity)getActivity()).newNewExpenseToIncomeFragment(expenseType);
                else
                    ((MainActivity)getActivity()).newNewExpenseFragment(expenseType);
            }
        });
        return rootView;


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
