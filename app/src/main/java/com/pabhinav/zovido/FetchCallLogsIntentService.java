package com.pabhinav.zovido;

import android.Manifest;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telecom.Call;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author pabhinav
 */
public class FetchCallLogsIntentService extends IntentService {

    private boolean isSubPart;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetchCallLogsIntentService() {
        super("FetchCallLogsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        isSubPart = false;

        /** Query for data **/
        ArrayList<CallDataObjectParcel> callLogs = getCallDataFromCallLogs(intent);

        /** Send Broadcast **/
        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra(Constants.isSubPart, isSubPart);
        broadcastIntent.setAction(Constants.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putParcelableArrayListExtra(Constants.callData, callLogs);
        sendBroadcast(broadcastIntent);
    }

    public String getContactName(String phoneNumber) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        ContentResolver cr = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(!cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    private ArrayList<CallDataObjectParcel> getCallDataFromCallLogs(Intent intent){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        Cursor managedCursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.DATE + " DESC"
        );

        if(managedCursor == null){
            return null;
        }

        ArrayList<CallDataObjectParcel> callLogs = new ArrayList<>();

        CallDataObjectParcel firstCallDataObjectParcel = null;

        /** get the first data currently cached **/
        if(intent.getExtras() != null) {
            firstCallDataObjectParcel = intent.getExtras().getParcelable(Constants.firstCallDataObjectParse);
        }

        /** Get new call logs data **/
        ArrayList<CallDataObjectParcel> newlocalcalldata = newLocalCallData(managedCursor, firstCallDataObjectParcel);

        /** Add data to call logs data **/
        if(newlocalcalldata != null && newlocalcalldata.size() >0){
            callLogs.addAll(0,newlocalcalldata);
        }

        /** Limit size to 500 calls **/
        ArrayList<CallDataObjectParcel> subCallLogs = new ArrayList<>(500);
        for (int i = 0; i < 500 && i < callLogs.size(); i++) {
            subCallLogs.add(i, callLogs.get(i));
        }
        callLogs = subCallLogs;

        return callLogs;
    }


    private ArrayList<CallDataObjectParcel> newLocalCallData(Cursor managedCursor, CallDataObjectParcel savedCallDataObjectParcel){

        ArrayList<CallDataObjectParcel> callLogs = new ArrayList<>();

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        int index = 0;

        while (managedCursor.moveToNext() && index < 500 ) {

            CallDataObjectParcel callDataObjectParcel = new CallDataObjectParcel();

            callDataObjectParcel.setPhoneNumber(managedCursor.getString(number));

            String callDuration = managedCursor.getString(duration);
            int totalSecs = Integer.parseInt(callDuration);

            int hours = totalSecs / 3600;
            int minutes = (totalSecs % 3600) / 60;
            int seconds = totalSecs % 60;

            if(hours == 0 && minutes == 0){
                callDuration = String.format("%02d sec",seconds);
            } else if(hours == 0){
                callDuration = String.format("%02d min %02d sec", minutes, seconds);
            } else {
                callDuration = String.format("%02d hour %02d min %02d sec", hours, minutes, seconds);
            }
            callDataObjectParcel.setCallDuration(callDuration);
            callDataObjectParcel.setCallDate(new Date(Long.valueOf(managedCursor.getString(date))).toString());

            String dir = null;
            int callTypeCode = Integer.parseInt(managedCursor.getString(type));
            switch (callTypeCode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = Constants.outgoing;
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = Constants.incoming;
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = Constants.missed;
                    break;
            }
            callDataObjectParcel.setCallType(dir);
            callDataObjectParcel.setName(getContactName(managedCursor.getString(number)));

            /** If any of hte entries are null, continue to get next value **/
            if(!validated(callDataObjectParcel)){
                continue;
            }

            /** Check for top Parcel object, whenever same, no need to get data further. **/
            if(savedCallDataObjectParcel != null && savedCallDataObjectParcel.equals(callDataObjectParcel)){
                isSubPart = true;
                break;
            }

            /** Add to list always at the end **/
            callLogs.add(index , callDataObjectParcel);

            /** Increment counter **/
            index = index + 1;
        }

        /** Important to free resources **/
        managedCursor.close();


        return callLogs;
    }


    private boolean validated(CallDataObjectParcel callDataObjectParcel){

        return callDataObjectParcel.getCallDate() != null
                && callDataObjectParcel.getCallDuration() != null
                && callDataObjectParcel.getCallType() != null
                && callDataObjectParcel.getPhoneNumber() != null;
    }

}
