package com.artfonapps.familyinex.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.activeandroid.app.Application;
import com.activeandroid.query.Select;
import com.artfonapps.familyinex.ApplicationParameters;
import com.artfonapps.familyinex.LoginActivity;
import com.artfonapps.familyinex.MainActivity;
import com.artfonapps.familyinex.R;
import com.artfonapps.familyinex.constants.Comparables;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.db.models.User;
import com.artfonapps.familyinex.network.Communicator;
import com.artfonapps.familyinex.network.FirebaseConnector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;

import static android.app.Activity.*;

public class TestActivity extends AppCompatActivity {

    Button test;
    FirebaseConnector firebaseConnector;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirechatUser;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            ApplicationParameters.currentUser =
                    (new Select()
                            .from(User.class)
                            .where("Mail = ?", mFirechatUser.getEmail()).executeSingle());
            try {
                firebaseConnector.setUser();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        test = (Button)findViewById(R.id.test);
        //User user = new User(99999, "test", "device_id", "mail", "uid");
        //user.save();
        //ApplicationParameters.currentUser = user;

        firebaseConnector = new FirebaseConnector();



        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth = FirebaseAuth.getInstance();
                mFirechatUser = mFirebaseAuth.getCurrentUser();

                if (mFirechatUser == null) {
                    startActivityForResult(new Intent(TestActivity.this, LoginActivity.class), RESULT_OK);
                    finish();
                    return;
                } else {
                    ApplicationParameters.currentUser =
                            (new Select()
                                    .from(User.class)
                                    .where(DbFields.MAIL + Comparables.E, mFirechatUser.getEmail()).executeSingle());
                }
                try {
                    firebaseConnector.setUser();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
