package com.artfonapps.familyinex.views.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artfonapps.familyinex.R;


public class PlannedExpensesFragment extends BaseFragment {

    public PlannedExpensesFragment() {}
        // Required empty public constructor

    public static PlannedExpensesFragment newInstance() {
        PlannedExpensesFragment fragment = new PlannedExpensesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_planned_expenses, container, false);
    }
}
