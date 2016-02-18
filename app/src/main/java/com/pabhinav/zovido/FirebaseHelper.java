package com.pabhinav.zovido;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Date;
import java.util.Map;

/**
 * @author pabhinav
 */
public class FirebaseHelper {

    private Context context;
    private SharedPreferencesMap sharedPreferencesMap;

    public FirebaseHelper(Context context){
        this.context = context;
        sharedPreferencesMap = new SharedPreferencesMap(context);
    }

    /** Try to send feedback **/
    public void sendFeedback(final String feedback){

        /** Firebase linked **/
        final Firebase myFirebaseRef = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK);

        /** Need to know firebase connection presence **/
        Firebase connectedRef = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + ".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                    Date date = new Date(System.currentTimeMillis());
                    myFirebaseRef.child("Feedback " + date).setValue(feedback, new Firebase.CompletionListener() {

                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                sharedPreferencesMap.saveFeedback(feedback);
                            } else {
                                sharedPreferencesMap.removeFeedback();
                            }
                        }
                    });

                } else {
                    sharedPreferencesMap.saveFeedback(feedback);
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                sharedPreferencesMap.saveFeedback(feedback);
            }
        });

    }


    /** Try to send ACRA Map **/
    public void sendAcraMap(final Map<String,String> acraMap){

        /** Firebase linked **/
        final Firebase myFirebaseRef = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK);

        /** Need to know firebase connection presence **/
        Firebase connectedRef = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + ".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                    Date date = new Date(System.currentTimeMillis());
                    myFirebaseRef.child("ACRA " + date).setValue(acraMap, new Firebase.CompletionListener() {

                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                sharedPreferencesMap.saveAcraMap(acraMap);
                            } else {
                                sharedPreferencesMap.removeAcraMap();
                            }
                        }
                    });

                } else {
                    sharedPreferencesMap.saveAcraMap(acraMap);
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                sharedPreferencesMap.saveAcraMap(acraMap);
            }
        });

    }

}
