package com.artfonapps.familyinex;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.artfonapps.familyinex.constants.Comparables;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.db.models.Expense;
import com.artfonapps.familyinex.db.models.Income;
import com.artfonapps.familyinex.db.models.PayType;
import com.artfonapps.familyinex.db.models.PlannedExpenses;
import com.artfonapps.familyinex.db.models.User;
import com.artfonapps.familyinex.db.utils.BusProvider;
import com.artfonapps.familyinex.db.utils.UpdateEvent;
import com.artfonapps.familyinex.db.utils.InsertEvent;
import com.artfonapps.familyinex.db.utils.RemoveEvent;
import com.artfonapps.familyinex.network.Communicator;
import com.artfonapps.familyinex.network.FirebaseConnector;
import com.artfonapps.familyinex.network.GetDeviceIdService;
import com.artfonapps.familyinex.views.fragments.ExpensesFragment;
import com.artfonapps.familyinex.views.fragments.ListExpensesFragment;
import com.artfonapps.familyinex.views.fragments.ListIncomesFragment;
import com.artfonapps.familyinex.views.fragments.LoadingFragment;
import com.artfonapps.familyinex.views.fragments.MainFragment;
import com.artfonapps.familyinex.views.fragments.NewExpenseFragment;
import com.artfonapps.familyinex.views.fragments.NewExpenseToIncomeFragment;
import com.artfonapps.familyinex.views.fragments.NewIncomeFragment;
import com.artfonapps.familyinex.views.fragments.OnBaseFragmentInteractionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.otto.Subscribe;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements OnBaseFragmentInteractionListener {

    private static final String EXPENSE_TYPE = "expenseType";
    FirebaseConnector firebaseConnector;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    Communicator communicator;
    FragmentTransaction fTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseConnector = new FirebaseConnector(MainActivity.this);

        //AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        communicator = new Communicator(MainActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        newLoadingFragment();
        authUser();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("refresh"));
        LocalBroadcastManager.getInstance(this).registerReceiver(loadedReceiver, new IntentFilter("loaded"));
        generateRecords();


        getSupportFragmentManager().addOnBackStackChangedListener(getListener());
    }

    private FragmentManager.OnBackStackChangedListener getListener()
    {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener()
        {
            public void onBackStackChanged()
            {   try {
                    FragmentManager manager = getSupportFragmentManager();
                    if (manager != null)
                        manager.findFragmentById(R.id.fragment_container).onResume();
                } catch (NullPointerException e) {

                }
            }
        };
        return result;
    }

    LoadingFragment loadingScreen;

    /*TODO отрефакторить !!!*/

    public void newLoadingFragment() {
        loadingScreen = new LoadingFragment();
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.fragment_container, loadingScreen);
        fTrans.commit();
    }
    public void removeLoadingFragment() {
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.remove(loadingScreen);
        fTrans.commit();
    }


    public void newMainFragment() {
        MainFragment firstFragment = new MainFragment();
        firstFragment.setArguments(getIntent().getExtras());
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.fragment_container, firstFragment);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    public void newExpensesTypesFragment() {
        ExpensesFragment firstFragment = new ExpensesFragment();
        firstFragment.setArguments(getIntent().getExtras());
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.fragment_container, firstFragment);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    public void newExpensesListFragment(String expenseType) {
        ListExpensesFragment firstFragment = ListExpensesFragment.newInstance(expenseType);
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.fragment_container, firstFragment);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    public void newNewExpenseFragment(String expenseType) {
        NewExpenseFragment firstFragment = NewExpenseFragment.newInstance(expenseType);
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.fragment_container, firstFragment);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    public void newNewExpenseFragment(String expenseType, Long id) {
        NewExpenseFragment firstFragment = NewExpenseFragment.newInstance(expenseType, id);
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.fragment_container, firstFragment);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }




    public void newNewExpenseToIncomeFragment(String expenseType) {
        NewExpenseToIncomeFragment firstFragment = NewExpenseToIncomeFragment.newInstance(expenseType);
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.fragment_container, firstFragment);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    public void newNewIncomeFragment() {
        NewIncomeFragment firstFragment = NewIncomeFragment.newInstance();
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.fragment_container, firstFragment);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    public void newIncomesListFragment() {
        ListIncomesFragment firstFragment = ListIncomesFragment.newInstance();
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.fragment_container, firstFragment);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    public void removeIncomesListFragment() {
        getSupportFragmentManager().popBackStack();
    }

    public void removeNewIncomeFragment() {
        getSupportFragmentManager().popBackStack();
    }

    public void removeNewExpenseFragment() {
        getSupportFragmentManager().popBackStack();
    }

    public void removeNewExpenseToIncomeFragment() {
        getSupportFragmentManager().popBackStack();
    }

    public void removeExpensesListFragment() {
        getSupportFragmentManager().popBackStack();
    }

    public void removeListExpensesFragment(ListExpensesFragment fragment) {
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.remove(fragment);
        fTrans.commit();
    }

    public void removeNewExpenseFragment(NewExpenseFragment fragment) {
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.remove(fragment);
        fTrans.commit();
    }


    //TODO вынести работу с базой

    private void authUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 0);
            return;
        } else {
            ApplicationParameters.currentUser =
                    (new Select()
                            .from(User.class)
                            .where(DbFields.MAIL + Comparables.E, firebaseUser.getEmail()).executeSingle());
            GetDeviceIdService getDeviceIdService = new GetDeviceIdService();
            ApplicationParameters.currentUser.deviceId = getDeviceIdService.getDeviceId(getApplicationContext());
            ApplicationParameters.currentUser.save();
            try {
                firebaseConnector.setUser();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApplicationParameters.currentUser =
                    (new Select()
                            .from(User.class)
                            .where(DbFields.MAIL + Comparables.E, firebaseUser.getEmail()).executeSingle());

        }

    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FragmentManager manager = getSupportFragmentManager();
            if (manager != null)
                manager.findFragmentById(R.id.fragment_container).onResume();
        }
    };

    private BroadcastReceiver loadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            removeLoadingFragment();
            newMainFragment();
        }
    };

    public void generateRecords() {

        PayType payType2 = new PayType(2, "Наличные", -1, -1);
        PayType payType1 = new PayType(1, "Карта", -1, -1);

        payType2.save();
        payType1.save();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            try {
                firebaseConnector.setUser();

            } catch (JSONException e) {
                e.printStackTrace();
            }
         }

    }

    @Subscribe
    public void onChangeEvent(UpdateEvent updateEvent){
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null)
            manager.findFragmentById(R.id.fragment_container).onResume();
    }

    @Subscribe
    public void onInsertEvent(InsertEvent changeEvent){
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null)
            manager.findFragmentById(R.id.fragment_container).onResume();
    }

    @Subscribe
    public void onRemoveEvent(RemoveEvent changeEvent){
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null)
            manager.findFragmentById(R.id.fragment_container).onResume();
    }

    @Override
    public void onResume(){
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clear_expenses) {
            new Delete().from(Expense.class).execute();
            return true;
        } else if (id == R.id.action_clear_incomes) {
            new Delete().from(Income.class).execute();
            return true;
        } else if (id == R.id.action_clear_planned_expenses) {
            new Delete().from(PlannedExpenses.class).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
