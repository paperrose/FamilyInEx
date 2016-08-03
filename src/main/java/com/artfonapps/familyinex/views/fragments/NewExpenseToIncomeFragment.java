package com.artfonapps.familyinex.views.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.activeandroid.query.Select;
import com.artfonapps.familyinex.ApplicationParameters;
import com.artfonapps.familyinex.MainActivity;
import com.artfonapps.familyinex.R;
import com.artfonapps.familyinex.db.models.Expense;
import com.artfonapps.familyinex.db.models.ExpenseType;
import com.artfonapps.familyinex.db.models.Income;
import com.artfonapps.familyinex.db.models.PayType;
import com.artfonapps.familyinex.db.models.User;
import com.artfonapps.familyinex.views.adapters.ExpenseToIncomeAdapter;
import com.artfonapps.familyinex.views.utils.ExpenseToIncomeItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewExpenseToIncomeFragment extends BaseFragment {

    private static final String EXPENSE_TYPE = "ExpenseType";
    public static final String CHANGE_TEXT = "Перевод денег: ";

    // TODO: Rename and change types of parameters
    private String expenseType;


    List<ExpenseToIncomeItem> expenseToIncomeItems;
    ArrayAdapter<String> userAdapter;
    ArrayList<String> eToIStrings;
    Button accept;
    Spinner comment;
    EditText sum;
    ToggleButton payType;
    ImageView background;
    public NewExpenseToIncomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NewExpenseToIncomeFragment newInstance(String expenseType) {
        NewExpenseToIncomeFragment fragment = new NewExpenseToIncomeFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_expense_to_income, container, false);
        accept = (Button)rootView.findViewById(R.id.accept_button);
        comment = (Spinner) rootView.findViewById(R.id.comment);
        sum = (EditText)rootView.findViewById(R.id.sum);
        payType = (ToggleButton)rootView.findViewById(R.id.payType);
        background = (ImageView)rootView.findViewById(R.id.backImage);
        final List<User> users = new Select().from(User.class)
                .where("Mail <> ?", ApplicationParameters.currentUser.mail).execute();
        final List<PayType> payTypes = new Select().from(PayType.class).execute();
        expenseToIncomeItems = new ArrayList<>(payTypes.size()*users.size()+1);
        eToIStrings = new ArrayList<>(payTypes.size()*users.size()+1);
        if (payType.isChecked()) {
            expenseToIncomeItems
                    .add(new ExpenseToIncomeItem(ApplicationParameters.currentUser, PayType.getCashType()));
        } else
            expenseToIncomeItems
                    .add(new ExpenseToIncomeItem(ApplicationParameters.currentUser, PayType.getCardType()));
        for (User user : users) {
            for (PayType payType : payTypes)
                expenseToIncomeItems.add(new ExpenseToIncomeItem(user, payType));
        }

        for (ExpenseToIncomeItem e : expenseToIncomeItems) {
            eToIStrings.add(e.getUser().name + " - " + e.getPayType().title);
        }

        userAdapter = new ArrayAdapter<String>(getActivity(), R.layout.expense_to_income_item, eToIStrings);
        comment.setAdapter(userAdapter);

        payType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                expenseToIncomeItems.clear();
                eToIStrings.clear();
                if (isChecked) {
                    expenseToIncomeItems
                            .add(new ExpenseToIncomeItem(ApplicationParameters.currentUser, PayType.getCashType()));
                } else
                    expenseToIncomeItems
                            .add(new ExpenseToIncomeItem(ApplicationParameters.currentUser, PayType.getCardType()));
                for (User user : users) {
                    for (PayType payType : payTypes)
                        expenseToIncomeItems.add(new ExpenseToIncomeItem(user, payType));
                }
                for (ExpenseToIncomeItem e : expenseToIncomeItems) {
                    eToIStrings.add(e.getUser().name + " - " + e.getPayType().title);
                }

                userAdapter.notifyDataSetChanged();

            }
        });
        final ExpenseType expenseTypeObj = ExpenseType.getObjectByName(expenseType);
        Picasso.with(getActivity()).load(expenseTypeObj.image).into(background);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseToIncomeItem expenseToIncomeItem = expenseToIncomeItems
                        .get(comment.getSelectedItemPosition());
                Expense expense = new Expense(
                        CHANGE_TEXT + expenseToIncomeItem.getUser().name
                                + " - " + expenseToIncomeItem.getPayType().title,
                        expenseTypeObj.remoteId,
                        payType.isChecked() ?
                                PayType.getCashType() : PayType.getCardType(),
                        Float.parseFloat(sum.getText().toString()));
                expense.saveObject();
                Income income = new Income(
                        Float.parseFloat(sum.getText().toString()),
                        expenseToIncomeItem.getPayType(),
                        expenseToIncomeItem.getUser()
                );
                income.saveObject();
                ((MainActivity)getActivity()).removeNewExpenseToIncomeFragment();
            }
        });
        return rootView;
    }

}
