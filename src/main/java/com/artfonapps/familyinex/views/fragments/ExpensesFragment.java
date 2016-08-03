package com.artfonapps.familyinex.views.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artfonapps.familyinex.R;
import com.artfonapps.familyinex.db.models.ExpenseType;
import com.artfonapps.familyinex.db.models.PayType;
import com.artfonapps.familyinex.db.models.User;
import com.artfonapps.familyinex.views.adapters.SwipeExpensesPagerAdapter;

import java.util.List;

public class ExpensesFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXPENSE_TYPE = "ExpenseType";
    private static final String PAY_TYPE = "PayType";

    // TODO: Rename and change types of parameters
    private int expenseType;
    private int payType;
    ViewPager expensesPager;
    SwipeExpensesPagerAdapter expensesPagerAdapter;



    public ExpensesFragment() {

        // Required empty public constructor
    }

    public static ExpensesFragment newInstance(int expenseType, int payType) {
        ExpensesFragment fragment = new ExpensesFragment();
        Bundle args = new Bundle();
        args.putInt(EXPENSE_TYPE, expenseType);
        args.putInt(PAY_TYPE, payType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            expenseType = getArguments().getInt(EXPENSE_TYPE);
            payType = getArguments().getInt(PAY_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_expenses, container, false);
        expensesPager = (ViewPager)root.findViewById(R.id.typesScroller);
        expensesPagerAdapter =
                new SwipeExpensesPagerAdapter(ExpenseType.getUnique(new String[] { }, ExpenseType.class), getActivity());
        expensesPager.setAdapter(expensesPagerAdapter);
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



}
