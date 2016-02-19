package com.pabhinav.zovido;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * @author pabhinav
 */
public class CallLogTab extends Fragment {

    CallDetailsReceiver callDetailsReceiver;
    View rootView;
    RecyclerView mRecyclerView;
    private static CallLogRecyclerViewAdapter mAdapter;
    private static ArrayList<CallDataObjectParcel> cachedCallLogs;
    public static boolean isEmptyCallLog = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** Initialize broadcast receiver object **/
        callDetailsReceiver = new CallDetailsReceiver();

        /** Inflate root view **/
        rootView = inflater.inflate(R.layout.call_log_fragment, container, false);

        /** Start the spinner **/
        startSpinning();

        if(cachedCallLogs == null){
            cachedCallLogs = new ArrayList<>();
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new CallLogRecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener();
        mAdapter.setOnItemClickListener(recyclerItemClickListener);

        registerAndStartService();

        /**
         * This is added becoz CallDetailsReceiver can be called
         * before even adapter has been loaded, so in order to
         * have consistency, we update on every onResume. **/
        updateAdapterIfDataAvailable();
    }

    private void updateAdapterIfDataAvailable(){

        ArrayList<CallDataObjectParcel> callLogs = cachedCallLogs;

        if(callLogs != null && mAdapter != null){
            mAdapter.updateAllAtOnce(callLogs);
        }
    }

    public void registerAndStartService(){

        IntentFilter filter = new IntentFilter(Constants.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(callDetailsReceiver, filter);

        Intent intent = new Intent(getActivity(), FetchCallLogsIntentService.class);
        if(cachedCallLogs != null && cachedCallLogs.size() > 0) {
            intent.putExtra(Constants.firstCallDataObjectParse, cachedCallLogs.get(0));
        }
        getActivity().startService(intent);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(callDetailsReceiver);
        super.onDestroy();
    }

    public static class CallDetailsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            /** New Complete list, need to update if Fragment is available **/
            ArrayList<CallDataObjectParcel> callLogs = intent.getExtras().getParcelableArrayList(Constants.callData);

            if(callLogs!=null && callLogs.size() >0) {

                /** User has some call logs **/
                isEmptyCallLog = false;

                /** Force stop updating on duplicate intent received **/
                if(cachedCallLogs != null && cachedCallLogs.size() > 0){
                    if(cachedCallLogs.get(0).equals(callLogs.get(0))){
                        return;
                    }
                }

                /** Check if returned is a subpart of call logs **/
                if(intent.getExtras().getBoolean(Constants.isSubPart)){
                    cachedCallLogs.addAll(0,callLogs);
                } else {
                    cachedCallLogs = new ArrayList<>();
                    cachedCallLogs.addAll(callLogs);

                    /** update ticks if saved logs data is available **/
                    if(SavedLogTab.savedLogRecyclerViewAdapter != null) {
                        updateTick(SavedLogTab.savedLogRecyclerViewAdapter.getAllItems());
                    }
                }

                if (mAdapter != null) {
                    mAdapter.updateAllAtOnce(cachedCallLogs);
                }
            } else if(callLogs != null){

                /** if call logs are not null but are empty and they are not a subpart of call logs **/
                if(!intent.getExtras().getBoolean(Constants.isSubPart)){
                    cachedCallLogs = new ArrayList<>();
                    isEmptyCallLog = true;
                    if(mAdapter != null){
                        mAdapter.updateAllAtOnce(cachedCallLogs);
                    }
                }
            }

        }
    }

    private void startSpinning(){

        final ImageView spinnerImageView = (ImageView) rootView.findViewById(R.id.spinner);

        /** Rotate spinner about center, when it has got its measures **/
        spinnerImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Animation rotateAnim = new RotateAnimation(0, 360,spinnerImageView.getWidth()/2, spinnerImageView.getHeight()/2);
                rotateAnim.setDuration(500);
                rotateAnim.setRepeatCount(Animation.INFINITE);
                rotateAnim.setInterpolator(new LinearInterpolator());
                spinnerImageView.startAnimation(rotateAnim);
            }
        });

    }

    class RecyclerItemClickListener implements CallLogRecyclerViewAdapter.MyClickListener{

        @Override
        public void onItemClick(int position, View v) {

            CallDataObjectParcel callDataObjectParcel = mAdapter.getItem(position);

            if(callDataObjectParcel == null){
                return;
            }

            /** Call feedback handling activity **/
            final Intent intent = new Intent(CallLogTab.this.getActivity(), Feedback.class);
            intent.putExtra(Constants.phoneNumber, callDataObjectParcel.getPhoneNumber());
            intent.putExtra(Constants.callEndTime, callDataObjectParcel.getCallDate());
            intent.putExtra(Constants.userName, callDataObjectParcel.getName() == null ? "Unknown" : callDataObjectParcel.getName());
            intent.putExtra(Constants.agentName, (CallDetails.AGENT_NAME == null || CallDetails.AGENT_NAME.length() == 0)? "Your Name" : CallDetails.AGENT_NAME );
            intent.putExtra(Constants.callDuration, callDataObjectParcel.getCallDuration());
            intent.putExtra(Constants.position, position);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    startActivityForResult(intent, Constants.feedbackActivityRequestCode);
                }
            }, 500);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        /** Need to save data into Db and update saved logs tab **/
        if(requestCode == Constants.feedbackActivityRequestCode && resultCode == Constants.feedbackActivityRequestCode){

            if(mAdapter != null){
                mAdapter.updateTick(data.getExtras().getInt(Constants.position));
            }

            DataParcel dataParcel = data.getExtras().getParcelable(Constants.dataPojo);

            if(dataParcel != null) {

                MyApplication myApplication = (MyApplication) getActivity().getApplicationContext();

                if(SavedLogTab.savedLogRecyclerViewAdapter != null) {

                    boolean matchesInSavedLogs = false;

                    /** Data already saved need to update **/
                    ArrayList<DataParcel> dataParcelArrayList = SavedLogTab.savedLogRecyclerViewAdapter.getAllItems();
                    for(int i = 0; i< dataParcelArrayList.size(); i++){
                        DataParcel dataParcelItem = dataParcelArrayList.get(i);
                        if(dataParcelItem != null
                                && dataParcelItem.getCallDuration().equals(dataParcel.getCallDuration())
                                && dataParcelItem.getTimestamp().equals(dataParcel.getTimestamp())){
                            matchesInSavedLogs = true;
                            dataParcel.setId(dataParcelItem.getId());
                            dataParcelArrayList.set(i, dataParcel);
                            SavedLogTab.savedLogRecyclerViewAdapter.notifyDataSetChanged();

                            Log.d("Zovido : ", "Before Update(CallLog)");
                            myApplication.printDb();

                            /** update to db **/
                            myApplication.updateToDb(dataParcel);

                            Log.d("Zovido : ", "After Update(CallLog)");
                            myApplication.printDb();
                            break;
                        }
                    }


                    if(!matchesInSavedLogs) {

                        Log.d("Zovido : ", "Before Write(CallLog)");
                        myApplication.printDb();

                        /** Write new data to db and update adapter **/
                        int id = myApplication.writeToDb(dataParcel);

                        Log.d("Zovido : ", "After Write(CallLog)");
                        myApplication.printDb();

                        dataParcel.setId(id);
                        if (SavedLogTab.savedLogRecyclerViewAdapter != null) {
                            SavedLogTab.savedLogRecyclerViewAdapter.addItem(dataParcel, 0);
                        }
                    }
                }
            }
        }
    }

    /** Update Ticks in all call logs **/
    public static void updateTick(ArrayList<DataParcel> dataParcelArrayList){

        /** Extract all dates for comparison **/
        ArrayList<String> dateList = new ArrayList<>();
        for(DataParcel dataParcel : dataParcelArrayList){
            if(dataParcel != null){
                dateList.add(dataParcel.getTimestamp());
            }
        }

        /** If we have call logs available update ticks. **/
        if(cachedCallLogs != null && cachedCallLogs.size() >0){
            for(int i = 0; i<cachedCallLogs.size(); i++) {
                CallDataObjectParcel callDataObjectParcel = cachedCallLogs.get(i);
                if(callDataObjectParcel != null){
                    if(dateList.indexOf(callDataObjectParcel.getCallDate()) != -1){
                        callDataObjectParcel.setShowTick(true);
                    }
                }
            }

            if(mAdapter != null){
                mAdapter.updateAllAtOnce(cachedCallLogs);
            }
        }

    }

}
