package com.artfonapps.familyinex.views.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.artfonapps.familyinex.ApplicationParameters;
import com.artfonapps.familyinex.MainActivity;
import com.artfonapps.familyinex.R;
import com.artfonapps.familyinex.db.models.Expense;
import com.artfonapps.familyinex.db.models.ExpenseType;
import com.artfonapps.familyinex.db.models.Income;
import com.artfonapps.familyinex.db.models.PayType;
import com.squareup.picasso.Picasso;

public class NewIncomeFragment extends BaseFragment {


    // TODO: Rename and change types of parameters
    private String expenseType;
    Button accept;
    EditText comment;
    EditText sum;
    ToggleButton payType;
    ImageView background;

    public NewIncomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NewIncomeFragment newInstance() {
        NewIncomeFragment fragment = new NewIncomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_income, container, false);
        accept = (Button)rootView.findViewById(R.id.accept_button);
        comment = (EditText)rootView.findViewById(R.id.comment);
        sum = (EditText)rootView.findViewById(R.id.sum);
        payType = (ToggleButton)rootView.findViewById(R.id.payType);
        background = (ImageView)rootView.findViewById(R.id.backImage);
        Picasso.with(getActivity()).load(R.drawable.income_back).fit().centerCrop().into(background);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Income income = new Income(
                        Float.parseFloat(sum.getText().toString()),
                        payType.isChecked() ?
                                PayType.getCashType() : PayType.getCardType(),
                        ApplicationParameters.currentUser
                );
                income.saveObject();
                ((MainActivity)getActivity()).removeNewExpenseFragment();
            }
        });
        return rootView;
    }

}
