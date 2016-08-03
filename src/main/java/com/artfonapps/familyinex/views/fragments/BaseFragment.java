package com.artfonapps.familyinex.views.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.artfonapps.familyinex.MainActivity;
import com.artfonapps.familyinex.constants.Titles;

/**
 * Created by paperrose on 30.07.2016.
 */
public class BaseFragment extends Fragment {
    protected OnBaseFragmentInteractionListener mListener;
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBaseFragmentInteractionListener) {
            mListener = (OnBaseFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        String title = null;
        if (this instanceof LoadingFragment) return;
        if (this instanceof MainFragment) {
            title = Titles.MAIN;
        } else if (this instanceof ExpensesFragment) {
            title = Titles.EXPENSES;
        } else if (this instanceof ListExpensesFragment) {
            title = ((ListExpensesFragment)this).getExpenseType();
        } else if (this instanceof ListIncomesFragment) {
            title = Titles.INCOMES;
        } else if (this instanceof NewExpenseFragment) {
            title = ((NewExpenseFragment)this).getExpenseType();
        } else if (this instanceof NewExpenseToIncomeFragment) {
            title = Titles.EXPENSES_TO_INCOMES;
        } else if (this instanceof NewIncomeFragment) {
            title = Titles.INCOMES;
        }
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(title);
    }
}
