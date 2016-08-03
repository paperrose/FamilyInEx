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

import com.activeandroid.query.Select;
import com.artfonapps.familyinex.MainActivity;
import com.artfonapps.familyinex.R;
import com.artfonapps.familyinex.constants.Comparables;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.db.models.Expense;
import com.artfonapps.familyinex.db.models.ExpenseType;
import com.artfonapps.familyinex.db.models.PayType;
import com.squareup.picasso.Picasso;




//TODO вынести запросы из фрагмента

public class NewExpenseFragment extends BaseFragment {

    private static final String EXPENSE_TYPE = "ExpenseType";
    private static final String REMOTE_ID = "RemoteId";

    public String getExpenseType() {
        return expenseType;
    }

    private String expenseType;
    private Long remoteId = null;



    Button accept;
    EditText comment;
    EditText sum;
    ToggleButton payType;
    ImageView background;

    public NewExpenseFragment() {
        // Required empty public constructor
    }

    public static NewExpenseFragment newInstance(String expenseType) {
        NewExpenseFragment fragment = new NewExpenseFragment();
        Bundle args = new Bundle();
        args.putString(EXPENSE_TYPE, expenseType);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewExpenseFragment newInstance(String expenseType, long remoteId) {
        NewExpenseFragment fragment = new NewExpenseFragment();
        Bundle args = new Bundle();
        args.putString(EXPENSE_TYPE, expenseType);
        args.putLong(REMOTE_ID, remoteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            expenseType = getArguments().getString(EXPENSE_TYPE);
            remoteId = getArguments().getString(REMOTE_ID) == null ? -1 : getArguments().getLong(REMOTE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_expense, container, false);

        accept = (Button)rootView.findViewById(R.id.accept_button);
        comment = (EditText)rootView.findViewById(R.id.comment);
        sum = (EditText)rootView.findViewById(R.id.sum);
        payType = (ToggleButton)rootView.findViewById(R.id.payType);
        background = (ImageView)rootView.findViewById(R.id.backImage);

        final ExpenseType expenseTypeObj = ExpenseType.getObjectByName(expenseType);
        Picasso.with(getActivity()).load(expenseTypeObj.image).into(background);


        if (remoteId != -1) {
            Expense expenseEdit = new Select().from(Expense.class).where(DbFields.REMOTE_ID + Comparables.E, remoteId).executeSingle();
            comment.setText(expenseEdit.comment);
            sum.setText(Float.toString(expenseEdit.sum));
        }

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Expense expense;
                if (remoteId == -1) {
                    expense = new Expense(
                            comment.getText().toString(),
                            expenseTypeObj.remoteId,
                            payType.isChecked() ?
                                    PayType.getCashType() : PayType.getCardType(),
                            Float.parseFloat(sum.getText().toString()));
                } else {
                    expense = new Select().from(Expense.class).where(DbFields.REMOTE_ID + Comparables.E, remoteId).executeSingle();
                    expense.comment = comment.getText().toString();
                    expense.payType =  payType.isChecked() ?
                            PayType.getCashType() : PayType.getCardType();
                    expense.sum = Float.parseFloat(sum.getText().toString());
                }
                expense.saveObject();
                ((MainActivity)getActivity()).removeNewExpenseFragment();
            }
        });
        return rootView;
    }


}
