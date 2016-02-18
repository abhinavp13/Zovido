package com.pabhinav.zovido;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Map;

/**
 * @author pabhinav
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private FirebaseHelper firebaseHelper;

    @Override
    public void onReceive(Context context, Intent intent) {

        /** Check for internet connectivity **/
        if(checkInternet(context)) {

            firebaseHelper = new FirebaseHelper(context);
            SharedPreferencesMap sharedPreferencesMap = new SharedPreferencesMap(context);

            Map<String, String> acraMap = sharedPreferencesMap.loadAcraMap();

            if(acraMap != null && acraMap.size() > 0){
                /** Need to call Firebase to set value **/
                firebaseHelper.sendAcraMap(acraMap);
            }

            String feedback = sharedPreferencesMap.loadFeedback();
            if(feedback != null){
                /** Need to call Firebase to set value **/
                firebaseHelper.sendFeedback(feedback);
            }
        }
    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }
    }


    /** Service Manager for checking internet connectivity **/
    public class ServiceManager extends ContextWrapper {

        public ServiceManager(Context base) {
            super(base);
        }

        public boolean isNetworkAvailable() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
            return false;
        }
    }
}
