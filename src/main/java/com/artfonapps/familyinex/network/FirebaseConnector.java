package com.artfonapps.familyinex.network;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.artfonapps.familyinex.ApplicationParameters;
import com.artfonapps.familyinex.constants.Comparables;
import com.artfonapps.familyinex.constants.DbFields;
import com.artfonapps.familyinex.constants.FirebaseFields;
import com.artfonapps.familyinex.db.models.ExpenseType;
import com.artfonapps.familyinex.db.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by paperrose on 19.07.2016.
 */
public class FirebaseConnector {
    private Context context;
    public static final String COMMAND = "loaded";
    private DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();


    public FirebaseConnector() {
    }

    public FirebaseConnector(Context context) {
        this.context = context;
    }


    private class ReferenceListener {
        public ReferenceListener() {

        }

        public ValueEventListener getChangeUserEventListener(final Query mailReference) {
            return new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                    boolean exists = false;
                    DatabaseReference newRef = null;
                    for (DataSnapshot snapshot : snapshots) {
                        exists = true;
                        newRef = mailReference.getRef()
                                .child(snapshot.getKey());
                        break;
                    }
                    if (!exists) {
                        newRef = mailReference.getRef().push();
                    }
                    newRef.child(FirebaseFields.DEVICE_ID).setValue(ApplicationParameters.currentUser.deviceId);
                    newRef.child(FirebaseFields.MAIL).setValue(ApplicationParameters.currentUser.mail);
                    newRef.child(FirebaseFields.NAME).setValue(ApplicationParameters.currentUser.name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }


        public ValueEventListener getCategoriesEventListener() {
            return new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> snapshots = dataSnapshot.child(FirebaseFields.CATEGORIES).getChildren();
                    int i = 0;
                    for (DataSnapshot snapshot: snapshots) {
                        i += 1;
                        (new ExpenseType(i,
                                snapshot.child(FirebaseFields.NAME).getValue().toString(),
                                snapshot.child(FirebaseFields.ICON).getValue().toString(),
                                snapshot.child(FirebaseFields.BACKGROUND).getValue().toString())).save();
                    }
                    Intent intent = new Intent(COMMAND);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }


        public ValueEventListener getUsersEventListener() {
            return new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                    int i = 1;
                    for (DataSnapshot snapshot: snapshots) {
                        if (snapshot.getKey().equals(FirebaseFields.REQUEST)) continue;
                        if (snapshot.child(FirebaseFields.MAIL).getValue().toString()
                                .equals(ApplicationParameters.currentUser.mail)) continue;
                        User u = (new User(snapshot.child(FirebaseFields.NAME).getValue().toString(),
                                snapshot.child(FirebaseFields.DEVICE_ID).getValue().toString(),
                                snapshot.child(FirebaseFields.MAIL).getValue().toString()));
                        u.save();
                        if (u.getId() == -1) {
                            new Update(User.class)
                                    .set(DbFields.DEVICE_ID + " = '" + snapshot.child(FirebaseFields.DEVICE_ID).getValue().toString() + "'")
                                    .where(DbFields.MAIL  + Comparables.E, snapshot.child(FirebaseFields.MAIL).getValue().toString())
                                    .execute();
                        }
                        i++;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
    }

    public void setUserWithoutUploading() throws JSONException {

        DatabaseReference userReference = rootReference.child(FirebaseFields.USERS);
        Query mailReference = userReference.orderByChild(FirebaseFields.MAIL)
                .equalTo(ApplicationParameters.currentUser.mail);

        ReferenceListener listener = new ReferenceListener();

        mailReference.addListenerForSingleValueEvent(listener.getChangeUserEventListener(mailReference));
        userReference.addListenerForSingleValueEvent(listener.getUsersEventListener());
        mailReference.getRef().child(FirebaseFields.REQUEST).setValue(System.currentTimeMillis());
    }

    public void setUser() throws JSONException {

        DatabaseReference userReference = rootReference.child(FirebaseFields.USERS);
        DatabaseReference groupReference = rootReference.child(FirebaseFields.GROUPS).child("HomeBudget");
        //Пока что в тесте работаем с одной группой
        //TODO сделать возможность добавления групп, категорий в группы и т.д.
        Query mailReference = userReference.orderByChild(FirebaseFields.MAIL)
                .equalTo(ApplicationParameters.currentUser.mail);

        ReferenceListener listener = new ReferenceListener();

        mailReference.addListenerForSingleValueEvent(listener.getChangeUserEventListener(mailReference));
        userReference.addListenerForSingleValueEvent(listener.getUsersEventListener());
        groupReference.addListenerForSingleValueEvent(listener.getCategoriesEventListener());



        mailReference.getRef().child(FirebaseFields.REQUEST).setValue(System.currentTimeMillis());
        groupReference.child(FirebaseFields.REQUEST).setValue(System.currentTimeMillis());
    }


}
