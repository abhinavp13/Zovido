package com.pabhinav.zovido;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author pabhinav
 */
public class UploadingService extends Service {

    private NotificationManager notificationManager;
    private RemoteViews remoteViews;
    private Notification notification;
    public static UploadingService uploadingService;
    private SpreadsheetService service;
    private URL listFeedUrl;
    private ArrayList<DataParcel> dataParcels;
    private static boolean errorPosted = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        uploadingService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /** keep local copies **/
        listFeedUrl = AsyncTaskForListFeedInSpreadsheet.listFeedUrl;
        service = AsyncTaskForListFeedInSpreadsheet.service;
        dataParcels = copyDataParcels(AsyncTaskForListFeedInSpreadsheet.dataParcels);

        /** This should not happen **/
        if(listFeedUrl == null || service == null || dataParcels == null) {
            stopSelf();
        }

        remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        android.support.v4.app.NotificationCompat.Builder mBuilder = new android.support.v4.app.NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.noti_icon_white_96)
                .setContent(remoteViews)
                .setAutoCancel(false)
                .setOngoing(true);
        notification = mBuilder.build();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.notificationId, notification);

        /** Important for make it live, other wise killed by android os **/
        startForeground(Constants.notificationId, notification);

        /** Async task Execute   **/
        InsertAsyncTask insertAsyncTask = new InsertAsyncTask(dataParcels, this);
        insertAsyncTask.execute();

        /** Important to start sticky **/
        return START_STICKY;
    }

    public ArrayList<DataParcel> copyDataParcels(ArrayList<DataParcel> dataParcels){
        ArrayList<DataParcel> dataParcelsReturned = new ArrayList<>();
        dataParcelsReturned.addAll(dataParcels);
        return dataParcelsReturned;
    }

    /** Async task for inserting to spreadsheet **/
    class InsertAsyncTask extends AsyncTask {
        
        ArrayList<Boolean> completedInsert;
        ArrayList<DataParcel> dataParcels;
        Context context;
        boolean hasTimedOut;
        
        public InsertAsyncTask(ArrayList<DataParcel> dataParcels, Context context){
            this.dataParcels = dataParcels;
            this.context = context;
            hasTimedOut = false;
        }
        

        @Override
        protected Object doInBackground(Object[] params) {

            /** Service Started **/
            Log.d("Zovido : ", " Service Started ");

            completedInsert = new ArrayList<>();
            for(int i = 0; i<dataParcels.size(); i++){
                completedInsert.add(false);
            }

            /** Update notification **/
            Log.d("Zovido : ", "Service : showing normal notification");
            showNormalNotification(remoteViews, "Uploading ...");
            remoteViews.setProgressBar(R.id.firstBar, dataParcels.size(), 0, false);
            notificationManager.notify(Constants.notificationId, notification);

            int i = 0;
            for(int k = 0; k < dataParcels.size(); k++){

                DataParcel dataParcel = dataParcels.get(k);

                ListEntry row = new ListEntry();
                row.getCustomElements().setValueLocal("Name", dataParcel.getName());
                row.getCustomElements().setValueLocal("Phone", dataParcel.getUserMobileNumber());
                row.getCustomElements().setValueLocal("Agent", dataParcel.getAgentName());
                row.getCustomElements().setValueLocal("Time", dataParcel.getTimestamp());
                row.getCustomElements().setValueLocal("Duration", dataParcel.getCallDuration());
                row.getCustomElements().setValueLocal("Purpose", dataParcel.getPurpose());
                row.getCustomElements().setValueLocal("Product", dataParcel.getProduct());
                row.getCustomElements().setValueLocal("Sport", dataParcel.getSport());
                row.getCustomElements().setValueLocal("Remarks", dataParcel.getCallRemarks());

                try {
                    completedInsert.set(i, false);

                    /** Insert Timeout **/
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new RunnableClass(i), 5000);

                    service.insert(listFeedUrl, row);

                    if(hasTimedOut){
                        hasTimedOut = false;
                        return null;
                    }
                    completedInsert.set(i, true);


                    /** Delete the item **/
                    MyApplication myApplication = (MyApplication)getApplicationContext();
                    DatabaseHelper databaseHelper;
                    if(myApplication != null) {
                        databaseHelper = myApplication.getDatabaseHelper();
                    } else {
                        databaseHelper = new DatabaseHelper(context);
                    }
                    databaseHelper.deleteDataParcel(returnNewCopy(dataParcel));

                    /** Update Ui **/
                    Intent intent = new Intent();
                    intent.setAction("com.pabhinav.savedlog.ui.update");

                    /** Need to keep a local copy and pass as data parcel, (getting concurrent modification exception) **/
                    intent.putExtra(Constants.dataPojo, returnNewCopy(dataParcel));
                    sendBroadcast(intent);


                } catch (IOException | ServiceException e){

                    Log.d("Zovido : ", e.getMessage().toString());
                    e.printStackTrace();

                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                    /** if internet is connected, column mismatch for sure **/
                    if( activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

                        Log.d("Zovido : ", "Service : showing column mismatch");

                        /** column mismatch show in notification **/
                        showErrorNotification(remoteViews, "Uploading Failed : Column Mismatch", "Columns Mismatch :(", "Seems like your column headers are not what is expected by Zovido. Make sure that your first row has Zovido's database table headings, and then try again.");

                        notificationManager.notify(Constants.notificationId, notification);

                        return null;

                    }
                    /** else, its connectivity lost **/
                    else {

                        Log.d("Zovido : ", "Service : showing connection timeout error notification");

                        /** update notification **/
                        showErrorNotification(remoteViews, "Uploading Error : Timeout", "Connection Timeout", "Seems like you don't have proper connectivity or server is not responding, please try again later.");
                        notificationManager.notify(Constants.notificationId, notification);

                        return null;
                    }
                }

                i++;


                Log.d("Zovido : ", "Service : Updating progress : " + i + "/" + dataParcels.size());

                /** update notification **/
                remoteViews.setProgressBar(R.id.firstBar, dataParcels.size(), i, false);
                notificationManager.notify(Constants.notificationId, notification);
            }


            Log.d("Zovido : ", "Service : showing Uploaded Completed");

            /** Successfully Uploaded **/
            remoteViews.setProgressBar(R.id.firstBar, 100, 100, false);
            showDismissNotification(remoteViews, "Upload Completed Successfully");

            notificationManager.notify(Constants.notificationId, notification);
            return null;
        }



        /** pending intents **/
        public PendingIntent createPendingIntentForShowingDialog(String title, String mssg){

            Intent intent = new Intent(context, NotificationViewActivity.class);
            intent.putExtra("TITLE", title);
            intent.putExtra("MESSAGE", mssg);
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        public PendingIntent createPendingIntentForDismiss(){

            Intent intent1 = new Intent(context, NotificationViewActivity.class);
            intent1.putExtra("DISMISS", true);
            return PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        }



        /** Notifications Updates **/
        public void showErrorNotification(RemoteViews remoteViews, String hintText, String errorTitle, String errorMssg){

            errorPosted = true;

            Log.d("Zovido : ", "Show Error Notification");

            remoteViews.setViewVisibility(R.id.textView16, View.GONE);
            remoteViews.setViewVisibility(R.id.textView17, View.VISIBLE);

            remoteViews.setTextViewText(R.id.hint_below_first_bar, hintText);
            remoteViews.setTextColor(R.id.hint_below_first_bar, getResources().getColor(android.R.color.holo_red_light));

            remoteViews.setOnClickPendingIntent(R.id.textView17, createPendingIntentForShowingDialog(errorTitle, errorMssg));
        }

        public void showDismissNotification(RemoteViews remoteViews, String hintText){

            Log.d("Zovido : ", "Show Dismiss Notification");

            remoteViews.setViewVisibility(R.id.textView16, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.textView17, View.GONE);

            remoteViews.setTextViewText(R.id.hint_below_first_bar, hintText);
            remoteViews.setTextColor(R.id.hint_below_first_bar, getResources().getColor(R.color.heading_grey));

            remoteViews.setOnClickPendingIntent(R.id.textView16, createPendingIntentForDismiss());
        }

        public void showNormalNotification(RemoteViews remoteViews, String hintText){

            Log.d("Zovido : ", "Show Normal Notification");

            remoteViews.setViewVisibility(R.id.textView16, View.GONE);
            remoteViews.setViewVisibility(R.id.textView17, View.GONE);

            remoteViews.setTextViewText(R.id.hint_below_first_bar, hintText);
            remoteViews.setTextColor(R.id.hint_below_first_bar, getResources().getColor(R.color.heading_grey));
        }


        /** New DataParcel copied element **/
        public DataParcel returnNewCopy(DataParcel copyFrom){

            DataParcel copyTo;
            if(copyFrom == null){
                return null;
            }
            copyTo = new DataParcel();
            copyTo.setId(copyFrom.getId());
            copyTo.setName(copyFrom.getName());
            copyTo.setAgentName(copyFrom.getAgentName());
            copyTo.setUserMobileNumber(copyFrom.getUserMobileNumber());
            copyTo.setTimestamp(copyFrom.getTimestamp());
            copyTo.setCallDuration(copyFrom.getCallDuration());
            copyTo.setPurpose(copyFrom.getPurpose());
            copyTo.setProduct(copyFrom.getProduct());
            copyTo.setSport(copyFrom.getSport());
            copyTo.setCallRemarks(copyFrom.getCallRemarks());
            return copyTo;
        }

        /** Runnable class for insert timeouts **/
        class RunnableClass implements Runnable {

            private int index;

            public RunnableClass(int index){
                this.index = index;
            }

            @Override
            public void run() {

                if (!completedInsert.get(index)) {

                    Log.d("Zovido : ", "Service : showing connection timeout error notification");

                    if (errorPosted) {
                        errorPosted = false;
                        return;
                    }

                    /** update notification **/
                    showErrorNotification(remoteViews, "Uploading Error : Timeout", "Connection Timeout", "Seems like you don't have proper connectivity or server is not responding, please try again later.");
                    notificationManager.notify(Constants.notificationId, notification);

                    /** Tell everyone that it has timed out **/
                    hasTimedOut = true;
                }

            }
        }

    }



}
