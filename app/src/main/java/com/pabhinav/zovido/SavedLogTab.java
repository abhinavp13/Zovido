package com.pabhinav.zovido;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * @author pabhinav
 */
public class SavedLogTab extends Fragment {

    RecyclerView mRecyclerView;
    public static SavedLogRecyclerViewAdapter savedLogRecyclerViewAdapter;
    private MyApplication myApplication;
    private OnSavedLogsEventChangeListener savedLogsDataReadFromDBListener;
    private CallDetails attachedActivity;


    interface OnSavedLogsEventChangeListener {
        void onSavedLogsDataReadFromDB(ArrayList<DataParcel> dataParcels);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        attachedActivity = (CallDetails)activity;

        /** Make sure that parent activity implements above interface **/
        try{
            savedLogsDataReadFromDBListener = (OnSavedLogsEventChangeListener)activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnSavedLogsEventChangeListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** Inflate root view **/
        final View rootView = inflater.inflate(R.layout.saved_log_fragment, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.saved_log_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        savedLogRecyclerViewAdapter = new SavedLogRecyclerViewAdapter(getActivity(), mRecyclerView);
        mRecyclerView.setAdapter(savedLogRecyclerViewAdapter);

        /** Get data from DB **/
        myApplication = (MyApplication)getActivity().getApplicationContext();
        DatabaseHelper databaseHelper = myApplication.getDatabaseHelper();
        AsyncTaskForReadingDb asyncTaskForReadingDb = new AsyncTaskForReadingDb(databaseHelper);
        asyncTaskForReadingDb.setTaskComplete(new AsyncTaskForReadingDb.TaskComplete() {
            @Override
            public void onTaskComplete(ArrayList<DataParcel> dataParcels) {

                /** Remove loading in saved log fragment **/
                rootView.findViewById(R.id.loading_in_saved_log).setVisibility(View.GONE);
                rootView.findViewById(R.id.imageView_spinner_cover_in_saved_log).setVisibility(View.GONE);
                rootView.findViewById(R.id.spinner_saved_log).setVisibility(View.GONE);

                /** Show Empty saved logs symbol **/
                rootView.findViewById(R.id.imageView4).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.click_on_any_recent_logs).setVisibility(View.VISIBLE);

                if(dataParcels != null){
                    savedLogRecyclerViewAdapter.addAllItem(dataParcels,0);

                    /** inform attached activity that we have read data from db **/
                    savedLogsDataReadFromDBListener.onSavedLogsDataReadFromDB(savedLogRecyclerViewAdapter.getAllItems());
                }
            }
        });
        asyncTaskForReadingDb.execute();

        return rootView;
    }

    @Override
    public void onResume(){

        super.onResume();

        RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener();
        savedLogRecyclerViewAdapter.setOnItemClickListener(recyclerItemClickListener);
    }


    class RecyclerItemClickListener implements SavedLogRecyclerViewAdapter.MyClickListener{

        @Override
        public void onItemClick(int position, View v) {
        }

        @Override
        public void onDeleteForItemClicked(int position, View v){

            /** Delete from DB **/
            myApplication.deleteFromDb(savedLogRecyclerViewAdapter.getItem(position));

            savedLogRecyclerViewAdapter.deleteItem(position);
        }

        @Override
        public void onModifyForItemClicked(int position, View v) {

            DataParcel dataParcel = savedLogRecyclerViewAdapter.getItem(position);

            /** Get Data from feedback activity **/
            final Intent intent = new Intent(SavedLogTab.this.getActivity(), Feedback.class);
            intent.putExtra(Constants.phoneNumber, dataParcel.getUserMobileNumber());
            intent.putExtra(Constants.callEndTime, dataParcel.getTimestamp());
            intent.putExtra(Constants.userName, dataParcel.getName() == null ? "Unknown" : dataParcel.getName());
            intent.putExtra(Constants.agentName, (CallDetails.AGENT_NAME == null || CallDetails.AGENT_NAME.length() == 0)? "Your Name" : CallDetails.AGENT_NAME );
            intent.putExtra(Constants.callDuration, dataParcel.getCallDuration());
            intent.putExtra(Constants.userType, dataParcel.getUserType());
            intent.putExtra(Constants.productType, dataParcel.getProductType());
            intent.putExtra(Constants.callRemarks, dataParcel.getCallRemarks());

            startActivityForResult(intent, Constants.feedbackActivityRequestCode);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        /** Modified object received **/
        if(requestCode == Constants.feedbackActivityRequestCode && resultCode == Constants.feedbackActivityRequestCode){

            DataParcel dataParcel = data.getExtras().getParcelable(Constants.dataPojo);

            if(dataParcel != null) {

                ArrayList<DataParcel> dataParcelArrayList = savedLogRecyclerViewAdapter.getAllItems();
                for(int i = 0; i< dataParcelArrayList.size(); i++){
                    DataParcel dataParcelItem = dataParcelArrayList.get(i);
                    if(dataParcelItem != null
                            && dataParcelItem.getCallDuration().equals(dataParcel.getCallDuration())
                            && dataParcelItem.getTimestamp().equals(dataParcel.getTimestamp())) {
                        dataParcel.setId(dataParcelItem.getId());
                        dataParcelArrayList.set(i, dataParcel);
                        break;
                    }
                }
                savedLogRecyclerViewAdapter.notifyDataSetChanged();

                /** update to db **/
                myApplication.updateToDb(dataParcel);
            }
        }
    }
}
