package com.pabhinav.zovido;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

import com.firebase.client.Firebase;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.ArrayList;


@ReportsCrashes(
        formUri = "t",
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast// optional. displays a Toast message when the user accepts to send a report.
)
public class MyApplication extends Application {

    private ArrayList<String> uploadedItemTimestamps;
    private DatabaseHelper databaseHelper;
    private boolean fetchedOnceUploadedItems;

    @Override
    public void onCreate() {

        /** Initialize ACRA error reports sending library **/
        ACRA.init(this);
        MyReportSender yourSender = new MyReportSender();
        ACRA.getErrorReporter().setReportSender(yourSender);

        super.onCreate();

        /** Initialize firebase **/
        Firebase.setAndroidContext(this);

        /** Start fetching call logs also add receiver to it **/
        CallLogTab.CallDetailsReceiver callDetailsReceiver = new CallLogTab.CallDetailsReceiver();
        IntentFilter filter = new IntentFilter(Constants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(callDetailsReceiver, filter);

        Intent intent = new Intent(this, FetchCallLogsIntentService.class);
        startService(intent);

        /** Read database and keep in local storage **/
        databaseHelper = new DatabaseHelper(this);

        /** Initialize the arraylist **/
        uploadedItemTimestamps = new ArrayList<>();
        fetchedOnceUploadedItems = false;
    }

    public ArrayList<String> getUploadedItemTimestamps() {
        return uploadedItemTimestamps;
    }

    public void setUploadedItemTimestamps(ArrayList<String> uploadedItemTimestamps) {
        this.uploadedItemTimestamps = uploadedItemTimestamps;
    }

    public boolean isFetchedOnceUploadedItems() {
        return fetchedOnceUploadedItems;
    }

    public void setFetchedOnceUploadedItems(boolean fetchedOnceUploadedItems) {
        this.fetchedOnceUploadedItems = fetchedOnceUploadedItems;
    }

    public DatabaseHelper getDatabaseHelper() {
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(this);
        }
        return databaseHelper;
    }

    public ArrayList<String> getAllUploadedTimestamps(){
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(this);
        }
        return databaseHelper.getAllUploadedItemsTimestamps();
    }

    public void addUploadingItemData(String timestamp){
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(this);
        }
        databaseHelper.addUploadedItem(timestamp);
    }

    public void deleteFromDb(DataParcel dataParcel){
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(this);
        }
        databaseHelper.deleteDataParcel(dataParcel);
    }

    public int writeToDb(DataParcel dataParcel){
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(this);
        }
        return databaseHelper.addDataParcel(dataParcel);
    }

    public void updateToDb(DataParcel dataParcel){
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(this);
        }
        int code = databaseHelper.updateDataParcel(dataParcel);
    }

    public void printDb(){
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(this);
        }
        databaseHelper.printWholeDatabase();
    }


}
